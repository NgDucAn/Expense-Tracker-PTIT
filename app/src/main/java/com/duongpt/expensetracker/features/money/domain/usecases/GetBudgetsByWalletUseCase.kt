package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.model.Budget
import com.duongpt.expensetracker.features.money.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetsByWalletUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) : UseCase<Flow<List<Budget>>, GetBudgetsByWalletUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, Flow<List<Budget>>> {
        return try {
            when (params) {
                is Params.ByWalletId -> Either.Right(budgetRepository.getBudgetsByWallet(params.walletId))
                is Params.TotalWallets -> {

                    Either.Right(budgetRepository.getBudgetsFromTotalWallet())
                }
            }
        } catch (e: Exception) {
            Either.Left(Failure.ServerError)
        }
    }

    sealed class Params {
        data class ByWalletId(val walletId: Int) : Params()
        data object TotalWallets : Params()
    }
}