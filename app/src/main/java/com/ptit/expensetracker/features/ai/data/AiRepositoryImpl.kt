package com.ptit.expensetracker.features.ai.data

import com.ptit.expensetracker.features.ai.data.remote.AiApiService
import com.ptit.expensetracker.features.ai.data.remote.dto.AiAnswerRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.AiAnswerResponseDto
import com.ptit.expensetracker.features.ai.data.remote.dto.AiPlanRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.AiPlanResponseDto
import com.ptit.expensetracker.features.ai.data.remote.dto.ChatRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.ChatResponseDto
import com.ptit.expensetracker.features.ai.data.remote.dto.ChatHistoryDto
import com.ptit.expensetracker.features.ai.data.remote.dto.InsightsRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.InsightsResponseDto
import com.ptit.expensetracker.features.ai.data.remote.dto.ParseTransactionRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.ParsedTransactionDto
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val api: AiApiService
) : AiRepository {

    override suspend fun chat(request: ChatRequestDto): ChatResponseDto = api.chat(request)

    override suspend fun parseTransaction(request: ParseTransactionRequestDto): ParsedTransactionDto =
        api.parseTransaction(request)

    override suspend fun insights(request: InsightsRequestDto): InsightsResponseDto =
        api.insights(request)

    override suspend fun plan(request: AiPlanRequestDto): AiPlanResponseDto =
        api.plan(request)

    override suspend fun answer(request: AiAnswerRequestDto): AiAnswerResponseDto =
        api.answer(request)

    override suspend fun history(): List<ChatHistoryDto> = api.history()

    override suspend fun clearHistory() {
        api.clearHistory()
    }
}


