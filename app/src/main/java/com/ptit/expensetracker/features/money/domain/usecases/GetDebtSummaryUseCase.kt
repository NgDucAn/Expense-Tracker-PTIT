package com.ptit.expensetracker.features.money.domain.usecases

import android.util.Log
import com.ptit.expensetracker.core.failure.Failure
import com.ptit.expensetracker.core.functional.Either
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.features.money.domain.model.DebtCategoryMetadata
import com.ptit.expensetracker.features.money.domain.model.DebtInfo
import com.ptit.expensetracker.features.money.domain.model.DebtPerson
import com.ptit.expensetracker.features.money.domain.model.DebtSummary
import com.ptit.expensetracker.features.money.domain.model.Transaction
import com.ptit.expensetracker.features.money.domain.model.Wallet
import com.ptit.expensetracker.features.money.domain.repository.TransactionRepository
import com.ptit.expensetracker.features.money.domain.repository.WalletRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetDebtSummaryUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) : UseCase<DebtInfo, GetDebtSummaryUseCase.Params>() {

    companion object {
        private const val TAG = "GetDebtSummaryUseCase"
    }

    override suspend fun run(params: Params): Either<Failure, DebtInfo> {
        return try {
            // Get the selected wallet
            val walletWithCurrencyEntity = if (params.walletId != null) {
                walletRepository.getWalletById(params.walletId).first()
            } else  null

            if (walletWithCurrencyEntity == null) {
                Log.w(TAG, "No wallet found for ID: ${params.walletId}")
                return Either.Left(Failure.DatabaseError)
            }

            // Get all debt-related transactions for the wallet
            val allTransactions = if (params.walletId != null) {
                transactionRepository.getTransactionsByWalletId(params.walletId).first()
            } else {
                transactionRepository.getAllTransactions().first()
            }

            // Filter debt-related transactions
            val debtTransactions = allTransactions.filter { transaction ->
                DebtCategoryMetadata.ALL_DEBT_CATEGORIES.contains(transaction.category.metaData)
            }

            Log.d(TAG, "Found ${debtTransactions.size} debt transactions")

            // Calculate payable debts (money we owe)
            val payableDebts = calculateDebtSummaries(
                transactions = debtTransactions,
                originalCategories = DebtCategoryMetadata.PAYABLE_ORIGINAL,
                repaymentCategories = DebtCategoryMetadata.PAYABLE_REPAYMENT
            )

            // Calculate receivable debts (money owed to us)
            val receivableDebts = calculateDebtSummaries(
                transactions = debtTransactions,
                originalCategories = DebtCategoryMetadata.RECEIVABLE_ORIGINAL,
                repaymentCategories = DebtCategoryMetadata.RECEIVABLE_REPAYMENT
            )

            val debtInfo = DebtInfo(
                wallet = Wallet.fromEntity(walletWithCurrencyEntity!!),
                payableDebts = payableDebts,
                receivableDebts = receivableDebts
            )

            Log.d(TAG, "Calculated debt info: ${payableDebts.size} payable, ${receivableDebts.size} receivable")

            Either.Right(debtInfo)
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating debt summary", e)
            Either.Left(Failure.ServerError)
        }
    }

    private suspend fun calculateDebtSummaries(
        transactions: List<Transaction>,
        originalCategories: List<String>,
        repaymentCategories: List<String>
    ): List<DebtSummary> {
        
        // Group transactions by debtReference first, then fallback to person-based grouping
        val groupedTransactions = groupTransactionsByDebt(transactions, originalCategories, repaymentCategories)
        
        return groupedTransactions.mapNotNull { (debtKey, debtTransactions) ->
            createDebtSummary(debtKey, debtTransactions, originalCategories, repaymentCategories)
        }
    }

    private fun groupTransactionsByDebt(
        transactions: List<Transaction>,
        originalCategories: List<String>,
        repaymentCategories: List<String>
    ): Map<String, List<Transaction>> {
        
        val relevantTransactions = transactions.filter { tx ->
            originalCategories.contains(tx.category.metaData) || 
            repaymentCategories.contains(tx.category.metaData)
        }
        
        // First, try to group by debtReference
        val withDebtReference = relevantTransactions.filter { it.debtReference != null }
        val withoutDebtReference = relevantTransactions.filter { it.debtReference == null }
        
        val debtReferenceGroups = withDebtReference.groupBy { it.debtReference!! }
        
        // For transactions without debtReference, group by person and try to match them
        val personGroups = withoutDebtReference.groupBy { tx ->
            val persons = parseDebtPersonFromJson(tx.withPerson)
            persons?.firstOrNull()?.let { person ->
                "${person.id}_${person.name}" // Create a unique key for person
            } ?: "unknown_${tx.id}"
        }
        
        return debtReferenceGroups + personGroups
    }

    private fun createDebtSummary(
        debtKey: String,
        debtTransactions: List<Transaction>,
        originalCategories: List<String>,
        repaymentCategories: List<String>
    ): DebtSummary? {
        
        // Find the original debt transaction
        val originalTransaction = debtTransactions.find { tx ->
            originalCategories.contains(tx.category.metaData)
        } ?: return null
        
        // Find all repayment transactions
        val repaymentTransactions = debtTransactions.filter { tx ->
            repaymentCategories.contains(tx.category.metaData)
        }.sortedBy { it.transactionDate }
        
        // Parse person information
        val personInfo = parseDebtPersonFromJson(originalTransaction.withPerson)?.firstOrNull()
            ?: return null
        
        val totalAmount = originalTransaction.amount
        val paidAmount = repaymentTransactions.sumOf { it.amount }
        val currency = originalTransaction.wallet.currency.currencyCode
        
        return DebtSummary(
            debtId = originalTransaction.debtReference ?: "legacy_${originalTransaction.id}",
            personName = personInfo.name,
            personId = personInfo.id,
            originalTransaction = originalTransaction,
            repaymentTransactions = repaymentTransactions,
            totalAmount = totalAmount,
            paidAmount = paidAmount,
            currency = currency
        )
    }

    private fun parseDebtPersonFromJson(withPersonJson: String?): List<DebtPerson>? {
        return try {
            if (withPersonJson.isNullOrEmpty()) return null
            
            val gson = Gson()
            val listType = object : TypeToken<List<DebtPerson>>() {}.type
            gson.fromJson(withPersonJson, listType)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to parse debt person JSON, trying fallback", e)
            // Fallback: try to parse as simple contact format
            try {
                val gson = Gson()
                val contactListType = object : TypeToken<List<Map<String, String>>>() {}.type
                val contacts: List<Map<String, String>> = gson.fromJson(withPersonJson, contactListType)
                
                contacts?.map { contact ->
                    DebtPerson(
                        id = contact["id"] ?: "unknown",
                        name = contact["name"] ?: "Unknown",
                        initial = contact["initial"] ?: contact["name"]?.firstOrNull()?.toString() ?: "?"
                    )
                }
            } catch (fallbackException: Exception) {
                Log.e(TAG, "Failed to parse person JSON with fallback", fallbackException)
                null
            }
        }
    }

    data class Params(
        val walletId: Int? = null  // null means all wallets
    )
} 