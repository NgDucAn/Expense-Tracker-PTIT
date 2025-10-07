package com.duongpt.expensetracker.features.money.domain.usecases

import com.duongpt.expensetracker.core.failure.Failure
import com.duongpt.expensetracker.core.functional.Either
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.features.money.domain.model.Transaction
import com.duongpt.expensetracker.features.money.domain.repository.TransactionRepository
import javax.inject.Inject

class GetTransactionByIdUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) : UseCase<Transaction, Int>() {

    override suspend fun run(params: Int): Either<Failure, Transaction> {
        return try {
            val transaction = transactionRepository.getTransactionById(params)
            if (transaction != null) {
                Either.Right(transaction)
            } else {
                Either.Left(Failure.NotFound)
            }
        } catch (e: Exception) {
            Either.Left(Failure.ServerError)
        }
    }
}