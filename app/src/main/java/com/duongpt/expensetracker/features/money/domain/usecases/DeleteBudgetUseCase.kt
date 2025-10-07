package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.repository.BudgetRepository
import javax.inject.Inject

class DeleteBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) : UseCase<Unit, Int>() {

    override suspend fun run(params: Int): Either<Failure, Unit> {
        return try {
            budgetRepository.deleteBudget(params)
            Either.Right(Unit)
        } catch (e: Exception) {
            Either.Left(Failure.DatabaseError)
        }
    }
} 