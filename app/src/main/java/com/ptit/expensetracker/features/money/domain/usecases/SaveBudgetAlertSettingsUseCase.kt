package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.BudgetAlertSettings
import com.ptit.expensetracker.features.money.domain.repository.BudgetAlertRepository
import javax.inject.Inject

class SaveBudgetAlertSettingsUseCase @Inject constructor(
    private val budgetAlertRepository: BudgetAlertRepository
) : UseCase<Long, SaveBudgetAlertSettingsUseCase.Params>() {
    
    override suspend fun run(params: Params): Either<Failure, Long> {
        return try {
            val result = budgetAlertRepository.saveSettings(params.settings)
            Either.Right(result)
        } catch (e: Exception) {
            Either.Left(Failure.ServerError)
        }
    }
    
    data class Params(val settings: BudgetAlertSettings)
}

