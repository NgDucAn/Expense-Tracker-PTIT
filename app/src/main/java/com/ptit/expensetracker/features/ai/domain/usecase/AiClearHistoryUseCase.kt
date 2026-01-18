package com.ptit.expensetracker.features.ai.domain.usecase

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.functional.toLeft
import com.ptit.expensetracker.core.functional.toRight
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.ai.data.AiRepository
import com.ptit.expensetracker.features.ai.domain.AiFailure
import javax.inject.Inject

class AiClearHistoryUseCase @Inject constructor(
    private val repository: AiRepository
) : UseCase<Unit, UseCase.None>() {

    override suspend fun run(params: None): Either<Failure, Unit> {
        return try {
            repository.clearHistory()
            Unit.toRight()
        } catch (e: Exception) {
            AiFailure.Unknown(e.message ?: "Clear history failed").toLeft()
        }
    }
}


