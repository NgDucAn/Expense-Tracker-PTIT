package com.ptit.expensetracker.features.ai.domain.usecase

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.functional.toLeft
import com.ptit.expensetracker.core.functional.toRight
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.ai.data.AiRepository
import com.ptit.expensetracker.features.ai.data.remote.dto.ChatHistoryDto
import com.ptit.expensetracker.features.ai.domain.AiFailure
import javax.inject.Inject

class AiHistoryUseCase @Inject constructor(
    private val repository: AiRepository
) : UseCase<List<ChatHistoryDto>, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, List<ChatHistoryDto>> {
        return try {
            repository.history().toRight()
        } catch (e: Exception) {
            AiFailure.Unknown(e.message ?: "Load history failed").toLeft()
        }
    }
}


