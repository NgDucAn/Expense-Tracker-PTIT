package com.duongpt.expensetracker.features.money.domain.model

import java.util.Date

/**
 * Represents a person involved in debt transactions
 * Used for parsing and storing person information in withPerson JSON field
 */
data class DebtPerson(
    val id: String,
    val name: String,
    val initial: String,
    val debtId: String? = null,
    val originalAmount: Double? = null,
    val notes: String? = null
)

/**
 * Represents a record of debt payment
 */
data class PaymentRecord(
    val date: Date,
    val amount: Double,
    val description: String?,
    val transactionId: Int
)

/**
 * Comprehensive debt summary for a specific person and debt relationship
 */
data class DebtSummary(
    val debtId: String,
    val personName: String,
    val personId: String,
    val originalTransaction: Transaction,
    val repaymentTransactions: List<Transaction>,
    val totalAmount: Double,
    val paidAmount: Double,
    val currency: String
) {
    val remainingAmount: Double get() = totalAmount - paidAmount
    val isPaid: Boolean get() = remainingAmount <= 0.0
    val progressPercentage: Float get() = if (totalAmount > 0) (paidAmount / totalAmount).toFloat() else 0f
    
    val paymentHistory: List<PaymentRecord> get() = repaymentTransactions.map { transaction ->
        PaymentRecord(
            date = transaction.transactionDate,
            amount = transaction.amount,
            description = transaction.description,
            transactionId = transaction.id
        )
    }.sortedByDescending { it.date }
    
    /**
     * Check if this debt has recent activity (within last 30 days)
     */
    val hasRecentActivity: Boolean get() {
        val thirtyDaysAgo = Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)
        return repaymentTransactions.any { it.transactionDate.after(thirtyDaysAgo) }
    }
    
    /**
     * Get the last payment date, null if no payments made
     */
    val lastPaymentDate: Date? get() = repaymentTransactions
        .maxByOrNull { it.transactionDate }?.transactionDate
}

/**
 * Complete debt information for a specific wallet or all wallets
 */
data class DebtInfo(
    val wallet: Wallet?,
    val payableDebts: List<DebtSummary>,
    val receivableDebts: List<DebtSummary>
) {
    val totalPayableAmount: Double get() = payableDebts.sumOf { it.remainingAmount }
    val totalReceivableAmount: Double get() = receivableDebts.sumOf { it.remainingAmount }
    
    val totalPayableCount: Int get() = payableDebts.count { !it.isPaid }
    val totalReceivableCount: Int get() = receivableDebts.count { !it.isPaid }
    
    val hasAnyDebts: Boolean get() = payableDebts.isNotEmpty() || receivableDebts.isNotEmpty()
}

/**
 * Type of debt relationship
 */
enum class DebtType {
    PAYABLE,    // Money we owe to others (IS_LOAN, IS_REPAYMENT)
    RECEIVABLE  // Money others owe to us (IS_DEBT, IS_DEBT_COLLECTION)
}

/**
 * UI tab types for debt management screen
 */
enum class DebtTab {
    PAYABLE,
    RECEIVABLE
}

/**
 * Status of debt payment
 */
enum class DebtStatus {
    UNPAID,     // No payments made yet
    PARTIAL,    // Some payments made but not fully paid
    PAID        // Fully paid
}

/**
 * Debt category metadata constants for easy reference
 */
object DebtCategoryMetadata {
    // Payable debt categories (expenses)
    const val LOAN = "IS_LOAN"
    const val REPAYMENT = "IS_REPAYMENT"
    const val PAY_INTEREST = "IS_PAY_INTEREST"
    
    // Receivable debt categories (income)
    const val DEBT = "IS_DEBT"
    const val DEBT_COLLECTION = "IS_DEBT_COLLECTION"
    const val COLLECT_INTEREST = "IS_COLLECT_INTEREST"
    
    val PAYABLE_ORIGINAL = listOf(LOAN)
    val PAYABLE_REPAYMENT = listOf(REPAYMENT)
    val RECEIVABLE_ORIGINAL = listOf(DEBT)
    val RECEIVABLE_REPAYMENT = listOf(DEBT_COLLECTION)
    
    val ALL_DEBT_CATEGORIES = listOf(LOAN, REPAYMENT, PAY_INTEREST, DEBT, DEBT_COLLECTION, COLLECT_INTEREST)
} 