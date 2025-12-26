package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.BudgetAlertSettings
import com.ptit.expensetracker.features.money.domain.repository.BudgetAlertRepository
import javax.inject.Inject

class GetBudgetAlertSettingsUseCase @Inject constructor(
    private val budgetAlertRepository: BudgetAlertRepository
) : UseCase<BudgetAlertSettings, GetBudgetAlertSettingsUseCase.Params>() {
    
    override suspend fun run(params: Params): Either<Failure, BudgetAlertSettings> {
        return try {
            val settings = when (params) {
                is Params.Global -> {
                    budgetAlertRepository.getGlobalSettings() ?: BudgetAlertSettings.default()
                }
                is Params.ForBudget -> {
                    budgetAlertRepository.getSettingsForBudget(params.budgetId) 
                        ?: budgetAlertRepository.getGlobalSettings() 
                        ?: BudgetAlertSettings.default()
                }
                is Params.Effective -> {
                    budgetAlertRepository.getEffectiveSettings(params.budgetId)
                }
            }
            Either.Right(settings)
        } catch (e: Exception) {
            Either.Left(Failure.ServerError)
        }
    }
    
    sealed class Params {
        data object Global : Params()
        data class ForBudget(val budgetId: Int) : Params()
        data class Effective(val budgetId: Int?) : Params()
    }
}

