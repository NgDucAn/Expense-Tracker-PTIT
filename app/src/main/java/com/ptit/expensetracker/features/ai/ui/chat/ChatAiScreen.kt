package com.ptit.expensetracker.features.ai.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAiScreen(
    onPrefillTransaction: (parsed: com.ptit.expensetracker.features.ai.data.remote.dto.ParsedTransactionDto) -> Unit = {},
    viewModel: ChatAiViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val state by viewModel.viewState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val isRecordingState = remember { mutableStateOf(false) }
    val baseInputBeforeVoice = remember { mutableStateOf("") }
    val pendingStartAfterPermission = remember { mutableStateOf(false) }

    val voiceController = remember(context) {
        VoiceToTextController(
            context = context,
            onPartial = { partial ->
                // Keep user's typed prefix and replace the voice part while recording.
                val prefix = baseInputBeforeVoice.value
                val combined = if (prefix.isBlank()) partial else "$prefix $partial"
                viewModel.processIntent(ChatAiIntent.UpdateInput(combined.trim()))
            },
            onFinal = { finalText ->
                val prefix = baseInputBeforeVoice.value
                val combined = if (prefix.isBlank()) finalText else "$prefix $finalText"
                viewModel.processIntent(ChatAiIntent.UpdateInput(combined.trim()))
                isRecordingState.value = false
            },
            onError = { msg ->
                isRecordingState.value = false
                scope.launch { snackbarHostState.showSnackbar(msg) }
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose { voiceController.destroy() }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingStartAfterPermission.value) {
            pendingStartAfterPermission.value = false
            baseInputBeforeVoice.value = state.input.trim()
            isRecordingState.value = true
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            voiceController.start(locale = "vi-VN")
        } else {
            pendingStartAfterPermission.value = false
            scope.launch { snackbarHostState.showSnackbar("Cần quyền micro để dùng Voice-to-text") }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is ChatAiEvent.PrefillTransaction -> onPrefillTransaction(event.parsed)
                is ChatAiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                ChatAiEvent.HistoryCleared -> snackbarHostState.showSnackbar("Đã xóa lịch sử")
                else -> {}
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") },
                actions = {
                    IconButton(onClick = { viewModel.processIntent(ChatAiIntent.ClearHistory) }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear history")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            // Message list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (state.isLoading) {
                    item {
                        BotBubbleTyping()
                    }
                }
                items(state.messages.asReversed()) { msg ->
                    if (msg.isUser) {
                        UserBubble(text = msg.text)
                    } else {
                        BotBubbleMarkdown(text = msg.text)
                    }
                }
            }

            // Suggestion chips
            SuggestionRow(
                onSuggestion = { text ->
                    viewModel.processIntent(ChatAiIntent.ApplySuggestion(text = text, sendImmediately = true))
                }
            )

            // Input area
            InputArea(
                input = state.input,
                mode = state.mode,
                isLoading = state.isLoading,
                onInputChange = { viewModel.processIntent(ChatAiIntent.UpdateInput(it)) },
                onSelectMode = { viewModel.processIntent(ChatAiIntent.SelectMode(it)) },
                onSend = { viewModel.processIntent(ChatAiIntent.Send) },
                isRecording = isRecordingState.value,
                onMicPressAndHold = {
                    // Start recording on press if permission granted; stop on release.
                    if (state.isLoading) return@InputArea
                    if (!voiceController.isAvailable) {
                        scope.launch { snackbarHostState.showSnackbar("Voice-to-text không khả dụng trên thiết bị này") }
                        return@InputArea
                    }
                    val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.RECORD_AUDIO
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    if (!hasPermission) {
                        pendingStartAfterPermission.value = true
                        permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        return@InputArea
                    }
                    baseInputBeforeVoice.value = state.input.trim()
                    isRecordingState.value = true
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    voiceController.start(locale = "vi-VN")
                },
                onMicRelease = {
                    if (isRecordingState.value) {
                        voiceController.stop()
                        isRecordingState.value = false
                    }
                }
            )
        }
    }
}

@Composable
private fun UserBubble(text: String) {
    val bg = MaterialTheme.colorScheme.primaryContainer
    val fg = MaterialTheme.colorScheme.onPrimaryContainer
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 6.dp))
                .background(bg)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(text = text, color = fg, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun BotBubbleMarkdown(text: String) {
    val bg = MaterialTheme.colorScheme.surfaceVariant
    val fg = MaterialTheme.colorScheme.onSurfaceVariant

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text("AI", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 6.dp, bottomEnd = 18.dp))
                .background(bg)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Bot can return markdown → render it.
            androidx.compose.runtime.CompositionLocalProvider {
                // keep as plain call; MarkdownText handles minimal markdown.
            }
            MarkdownText(markdown = text)
        }
    }
}

@Composable
private fun BotBubbleTyping() {
    val bg = MaterialTheme.colorScheme.surfaceVariant
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text("AI", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 6.dp, bottomEnd = 18.dp))
                .background(bg)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            TypingIndicator()
        }
    }
}

@Composable
private fun SuggestionRow(
    onSuggestion: (String) -> Unit
) {
    val suggestions = listOf(
        "Báo cáo chi tiêu 3 tháng gần đây",
        "Phân tích xu hướng chi tiêu",
        "Cách quản lý chi tiêu hiệu quả",
        "Thống kê chi tiêu tháng này",
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(suggestions) { s ->
            SuggestionChip(
                onClick = { onSuggestion(s) },
                label = { Text(s, maxLines = 1) }
            )
        }
    }
}

@Composable
private fun InputArea(
    input: String,
    mode: ChatAiMode,
    isLoading: Boolean,
    isRecording: Boolean,
    onInputChange: (String) -> Unit,
    onSelectMode: (ChatAiMode) -> Unit,
    onMicPressAndHold: () -> Unit,
    onMicRelease: () -> Unit,
    onSend: () -> Unit,
) {
    val sendEnabled = input.trim().isNotEmpty() && !isLoading

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = mode == ChatAiMode.CHAT,
                onClick = { onSelectMode(ChatAiMode.CHAT) },
                label = { Text("Chat") }
            )
            FilterChip(
                selected = mode == ChatAiMode.TRANSACTION,
                onClick = { onSelectMode(ChatAiMode.TRANSACTION) },
                label = { Text("Giao dịch") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = if (mode == ChatAiMode.TRANSACTION)
                            "Nhập mô tả giao dịch (vd: Hôm nay ăn phở 50k)..."
                        else
                            "Nhập tin nhắn..."
                    )
                },
                minLines = 1,
                maxLines = 5,
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            onMicPressAndHold()
                                            try {
                                                awaitRelease()
                                            } finally {
                                                onMicRelease()
                                            }
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val tint = if (isRecording) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            Icon(imageVector = Icons.Default.Mic, contentDescription = "Mic", tint = tint)
                        }
                        IconButton(onClick = onSend, enabled = sendEnabled) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                        }
                    }
                }
            )
        }

        Text(
            text = if (mode == ChatAiMode.TRANSACTION) "Mô tả giao dịch của bạn" else "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )
    }
}


