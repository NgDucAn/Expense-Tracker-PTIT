package com.ptit.expensetracker.features.ai.ui.chat

import androidx.lifecycle.viewModelScope
import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.platform.BaseViewModel
import com.ptit.expensetracker.features.ai.domain.usecase.ChatAiUseCase
import com.ptit.expensetracker.features.ai.domain.usecase.AiPlanUseCase
import com.ptit.expensetracker.features.ai.domain.usecase.AiAnswerUseCase
import com.ptit.expensetracker.features.ai.domain.usecase.ParseTransactionAiUseCase
import com.ptit.expensetracker.features.ai.domain.usecase.AiHistoryUseCase
import com.ptit.expensetracker.features.ai.domain.usecase.AiClearHistoryUseCase
import com.ptit.expensetracker.features.ai.analytics.AnalyticsQueryEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatAiViewModel @Inject constructor(
    private val chatAiUseCase: ChatAiUseCase,
    private val parseTransactionAiUseCase: ParseTransactionAiUseCase,
    private val aiPlanUseCase: AiPlanUseCase,
    private val aiAnswerUseCase: AiAnswerUseCase,
    private val analyticsQueryEngine: AnalyticsQueryEngine,
    private val aiHistoryUseCase: AiHistoryUseCase,
    private val aiClearHistoryUseCase: AiClearHistoryUseCase,
) : BaseViewModel<ChatAiState, ChatAiIntent, ChatAiEvent>() {

    override val _viewState = MutableStateFlow(ChatAiState())

    init {
        loadHistory()
    }

    override fun processIntent(intent: ChatAiIntent) {
        when (intent) {
            is ChatAiIntent.UpdateInput -> _viewState.update { it.copy(input = intent.text) }
            ChatAiIntent.Send -> send()
            ChatAiIntent.ClearHistory -> clearHistory()
            is ChatAiIntent.SelectMode -> _viewState.update { it.copy(mode = intent.mode) }
            is ChatAiIntent.ApplySuggestion -> applySuggestion(intent.text, intent.sendImmediately)
        }
    }

    private fun send() {
        val text = viewState.value.input.trim()
        if (text.isBlank()) return
        when (viewState.value.mode) {
            ChatAiMode.CHAT -> sendChat(text)
            ChatAiMode.TRANSACTION -> parseTransaction(text)
        }
    }

    private fun sendChat(text: String) {
        // If this looks like an analysis question, use plan -> fetch -> answer flow.
        if (looksLikeAnalysis(text)) {
            analyze(text)
            return
        }
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    messages = it.messages + ChatMessage(text = text, isUser = true),
                    input = ""
                )
            }
            chatAiUseCase(ChatAiUseCase.Params(message = text)) { result ->
                result.fold(
                    { failure -> handleFailure(failure, "Gửi chat thất bại") },
                    { resp ->
                        _viewState.update { state ->
                            state.copy(
                                isLoading = false,
                                messages = state.messages + ChatMessage(
                                    text = resp.reply,
                                    isUser = false
                                )
                            )
                        }
                    }
                )
            }
        }
    }

    private fun analyze(text: String) {
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    messages = it.messages + ChatMessage(text = text, isUser = true),
                    input = ""
                )
            }
            aiPlanUseCase(AiPlanUseCase.Params(prompt = text)) { planResult ->
                planResult.fold(
                    { failure -> handleFailure(failure, "Lập kế hoạch phân tích thất bại") },
                    { plan ->
                        viewModelScope.launch {
                            try {
                                val dataResponse = analyticsQueryEngine.execute(plan.dataRequest)
                                aiAnswerUseCase(
                                    AiAnswerUseCase.Params(
                                        prompt = text,
                                        dataRequest = plan.dataRequest,
                                        dataResponse = dataResponse
                                    )
                                ) { answerResult ->
                                    answerResult.fold(
                                        { failure -> handleFailure(failure, "Phân tích thất bại") },
                                        { answer ->
                                            _viewState.update { state ->
                                                state.copy(
                                                    isLoading = false,
                                                    messages = state.messages + ChatMessage(
                                                        text = answer.answerMarkdown,
                                                        isUser = false
                                                    )
                                                )
                                            }
                                        }
                                    )
                                }
                            } catch (e: Exception) {
                                _viewState.update { it.copy(isLoading = false, error = "Không thể truy vấn dữ liệu") }
                                emitEvent(ChatAiEvent.ShowError("Không thể truy vấn dữ liệu"))
                            }
                        }
                    }
                )
            }
        }
    }

    private fun parseTransaction(text: String) {
        viewModelScope.launch {
            _viewState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    messages = it.messages + ChatMessage(text = text, isUser = true),
                    input = ""
                )
            }
            parseTransactionAiUseCase(ParseTransactionAiUseCase.Params(text = text)) { result ->
                result.fold(
                    { failure -> handleFailure(failure, "Phân tích giao dịch thất bại") },
                    { parsed ->
                        _viewState.update { it.copy(isLoading = false) }
                        emitEvent(ChatAiEvent.PrefillTransaction(parsed))
                    }
                )
            }
        }
    }

    private fun clearHistory() {
        viewModelScope.launch {
            aiClearHistoryUseCase(com.ptit.expensetracker.core.interactor.UseCase.None()) { result ->
                result.fold(
                    { failure -> handleFailure(failure, "Xóa lịch sử thất bại") },
                    {
                        _viewState.update { it.copy(messages = emptyList(), error = null) }
                        emitEvent(ChatAiEvent.HistoryCleared)
                    }
                )
            }
        }
    }

    private fun applySuggestion(text: String, sendImmediately: Boolean) {
        _viewState.update { it.copy(input = text) }
        if (sendImmediately) {
            processIntent(ChatAiIntent.Send)
        }
    }

    private fun handleFailure(failure: Failure, defaultMsg: String) {
        val msg = when (failure) {
            is Failure.NetworkConnection -> "Không có kết nối mạng"
            is Failure.ServerError -> "Lỗi máy chủ"
            is Failure.NotFound -> "Không tìm thấy"
            else -> defaultMsg
        }
        _viewState.update { it.copy(isLoading = false, error = msg) }
        emitEvent(ChatAiEvent.ShowError(msg))
    }

    private fun looksLikeAnalysis(text: String): Boolean {
        val t = text.lowercase()
        return t.contains("so sánh") ||
                t.contains("phân tích") ||
                t.contains("xu hướng") ||
                t.contains("dự đoán") ||
                t.contains("báo cáo") ||
                t.contains("thống kê")
    }

    private fun loadHistory() {
        viewModelScope.launch {
            aiHistoryUseCase(com.ptit.expensetracker.core.interactor.UseCase.None()) { result ->
                result.fold(
                    { /* ignore on cold start */ },
                    { history ->
                        val mapped = history.map { h ->
                            ChatMessage(
                                text = h.content,
                                isUser = h.role.equals("USER", ignoreCase = true)
                            )
                        }
                        _viewState.update { it.copy(messages = mapped) }
                    }
                )
            }
        }
    }
}


