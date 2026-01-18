package com.ptit.expensetracker.features.ai.data

import com.ptit.expensetracker.features.ai.data.remote.dto.ChatRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.ChatResponseDto
import com.ptit.expensetracker.features.ai.data.remote.dto.AiAnswerRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.AiAnswerResponseDto
import com.ptit.expensetracker.features.ai.data.remote.dto.AiPlanRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.AiPlanResponseDto
import com.ptit.expensetracker.features.ai.data.remote.dto.InsightsRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.InsightsResponseDto
import com.ptit.expensetracker.features.ai.data.remote.dto.ParseTransactionRequestDto
import com.ptit.expensetracker.features.ai.data.remote.dto.ParsedTransactionDto
import com.ptit.expensetracker.features.ai.data.remote.dto.ChatHistoryDto
import com.ptit.expensetracker.features.ai.data.remote.dto.context.FinancialContextDto

interface AiRepository {
    suspend fun chat(request: ChatRequestDto): ChatResponseDto
    suspend fun parseTransaction(request: ParseTransactionRequestDto): ParsedTransactionDto
    suspend fun insights(request: InsightsRequestDto): InsightsResponseDto
    suspend fun plan(request: AiPlanRequestDto): AiPlanResponseDto
    suspend fun answer(request: AiAnswerRequestDto): AiAnswerResponseDto
    suspend fun history(): List<ChatHistoryDto>
    suspend fun clearHistory()
    suspend fun syncContext(body: FinancialContextDto)
    suspend fun getContext(userId: String): FinancialContextDto
}


