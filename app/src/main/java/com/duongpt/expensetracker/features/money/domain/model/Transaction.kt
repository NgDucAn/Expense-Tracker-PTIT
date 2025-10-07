package com.duongpt.expensetracker.features.money.domain.model

import java.util.Date

// Represents a single financial transaction
data class Transaction(
    val id: Int,
    val wallet: Wallet,
    val transactionType: TransactionType,
    val amount: Double,
    val transactionDate: Date,
    val description: String?,
    val category: Category,
    val withPerson: String? = null,
    val eventName: String? = null,
    val hasReminder: Boolean = false,
    val excludeFromReport: Boolean = false,
    val photoUri: String? = null,
    val parentDebtId: Int? = null,  // Link to original debt transaction
    val debtReference: String? = null, // Unique identifier for debt relationship
    val debtMetadata: String? = null // Additional debt-related metadata as JSON
) {
    // For compatibility with repositories and other code still using walletId
    val walletId: Int
        get() = wallet.id
        
    /**
     * Check if this transaction is debt-related
     */
    val isDebtRelated: Boolean get() = DebtCategoryMetadata.ALL_DEBT_CATEGORIES.contains(category.metaData)
    
    /**
     * Get the debt type if this is a debt transaction
     */
    val debtType: DebtType? get() = when (category.metaData) {
        in DebtCategoryMetadata.PAYABLE_ORIGINAL,
        in DebtCategoryMetadata.PAYABLE_REPAYMENT -> DebtType.PAYABLE
        in DebtCategoryMetadata.RECEIVABLE_ORIGINAL,
        in DebtCategoryMetadata.RECEIVABLE_REPAYMENT -> DebtType.RECEIVABLE
        else -> null
    }
    
    /**
     * Check if this is an original debt transaction (not a repayment)
     */
    val isOriginalDebt: Boolean get() = category.metaData in 
        (DebtCategoryMetadata.PAYABLE_ORIGINAL + DebtCategoryMetadata.RECEIVABLE_ORIGINAL)
    
    /**
     * Check if this is a repayment transaction
     */
    val isRepayment: Boolean get() = category.metaData in 
        (DebtCategoryMetadata.PAYABLE_REPAYMENT + DebtCategoryMetadata.RECEIVABLE_REPAYMENT)
}

// Enum to define the type of transaction more clearly
enum class TransactionType {
    INFLOW,
    OUTFLOW
}
