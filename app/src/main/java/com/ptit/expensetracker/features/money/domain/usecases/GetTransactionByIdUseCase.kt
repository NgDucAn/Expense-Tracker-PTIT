package com.ptit.expensetracker.features.money.domain.usecases

import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.Transaction
import com.ptit.expensetracker.features.money.domain.repository.TransactionRepository
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