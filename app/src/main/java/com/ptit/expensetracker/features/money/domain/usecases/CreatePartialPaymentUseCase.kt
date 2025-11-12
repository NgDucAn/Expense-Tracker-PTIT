package com.ptit.expensetracker.features.money.domain.usecases

import android.util.Log
import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.Transaction
import com.ptit.expensetracker.features.money.domain.repository.TransactionRepository
import javax.inject.Inject

/**
 * Use case for creating partial debt payments
 * This creates a payment transaction linked to an original debt transaction
 */
class CreatePartialPaymentUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) : UseCase<Transaction, CreatePartialPaymentUseCase.Params>() {

    companion object {
        private const val TAG = "CreatePartialPaymentUseCase"
    }

    override suspend fun run(params: Params): Either<Failure, Transaction> {
        return try {
            Log.d(TAG, "Creating partial payment for debt: ${params.originalDebtId}")
            
            // Create the payment transaction
            val paymentTransaction = params.paymentTransaction.copy(
                parentDebtId = params.originalDebtId,
                debtReference = params.debtReference
            )
            
            // Save the payment transaction
            transactionRepository.saveTransaction(paymentTransaction)
            
            Log.d(TAG, "Created partial payment transaction with ID: ${paymentTransaction.id}")
            Either.Right(paymentTransaction)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error creating partial payment", e)
            Either.Left(Failure.ServerError)
        }
    }

    data class Params(
        val originalDebtId: Int,
        val debtReference: String,
        val paymentTransaction: Transaction
    )
} 