package com.ptit.expensetracker.features.money.domain.usecases

import android.util.Log
import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.DebtCategoryMetadata
import com.ptit.expensetracker.features.money.domain.model.PaymentRecord
import com.ptit.expensetracker.features.money.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for getting payment history for a specific debt
 */
class GetDebtPaymentHistoryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) : UseCase<List<PaymentRecord>, GetDebtPaymentHistoryUseCase.Params>() {

    companion object {
        private const val TAG = "GetDebtPaymentHistoryUseCase"
    }

    override suspend fun run(params: Params): Either<Failure, List<PaymentRecord>> {
        return try {
            Log.d(TAG, "Getting payment history for debt: ${params.debtReference}")
            
            // Get transactions by debt reference
            val relatedTransactions = if (params.debtReference.isNotBlank()) {
                transactionRepository.getTransactionsByDebtReference(params.debtReference).first()
            } else {
                // Fallback: get by parent debt ID
                transactionRepository.getTransactionsByParentDebtId(params.originalDebtId).first()
            }
            
            // Filter to only include payment/repayment transactions
            val paymentCategories = DebtCategoryMetadata.PAYABLE_REPAYMENT + 
                                  DebtCategoryMetadata.RECEIVABLE_REPAYMENT
            
            val paymentTransactions = relatedTransactions.filter { transaction ->
                paymentCategories.contains(transaction.category.metaData)
            }
            
            // Convert to PaymentRecord objects
            val paymentRecords = paymentTransactions.map { transaction ->
                PaymentRecord(
                    date = transaction.transactionDate,
                    amount = transaction.amount,
                    description = transaction.description,
                    transactionId = transaction.id
                )
            }.sortedByDescending { it.date } // Latest payments first
            
            Log.d(TAG, "Found ${paymentRecords.size} payment records")
            Either.Right(paymentRecords)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error getting payment history", e)
            Either.Left(Failure.ServerError)
        }
    }

    data class Params(
        val originalDebtId: Int,
        val debtReference: String = ""
    )
}