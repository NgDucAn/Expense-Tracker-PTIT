package com.duongpt.expensetracker.features.money.ui.debtmanagement.components

import com.duongpt.expensetracker.features.money.domain.model.DebtType

/**
 * Represents the main tabs in debt management screen
 */
enum class DebtTab(val displayName: String) {
    PAYABLE("Phải trả"),
    RECEIVABLE("Được nhận")
}

/**
 * Represents sub-sections within each debt tab
 */
enum class DebtSection(val displayName: String) {
    UNPAID("Chưa trả"),
    PAID("Đã trả")
}

/**
 * Filter options for debt management
 */
data class DebtFilterOptions(
    val sortBy: DebtSortBy = DebtSortBy.AMOUNT_DESC,
    val showPaidDebts: Boolean = true,
    val showUnpaidDebts: Boolean = true,
    val minAmount: Double? = null,
    val maxAmount: Double? = null,
    val searchQuery: String = ""
) {
    fun isDefault(): Boolean {
        return sortBy == DebtSortBy.AMOUNT_DESC &&
               showPaidDebts &&
               showUnpaidDebts &&
               minAmount == null &&
               maxAmount == null &&
               searchQuery.isEmpty()
    }
}

/**
 * Sorting options for debt summaries
 */
enum class DebtSortBy(val displayName: String) {
    AMOUNT_ASC("Số tiền (Thấp đến cao)"),
    AMOUNT_DESC("Số tiền (Cao đến thấp)"),
    NAME_ASC("Tên (A-Z)"),
    NAME_DESC("Tên (Z-A)"),
    DATE_ASC("Ngày (Cũ đến mới)"),
    DATE_DESC("Ngày (Mới đến cũ)")
}

/**
 * UI model for debt summary cards
 */
data class DebtCardUiModel(
    val personName: String,
    val personInitial: String,
    val originalAmount: String,
    val remainingAmount: String,
    val paidAmount: String,
    val progressPercentage: Float,
    val isFullyPaid: Boolean,
    val lastPaymentDate: String?,
    val currencySymbol: String,
    val debtType: DebtType,
    val paymentCount: Int,
    val daysSinceLastPayment: Int?
)

/**
 * UI model for debt statistics
 */
data class DebtStatsUiModel(
    val totalAmount: String,
    val unpaidAmount: String,
    val paidAmount: String,
    val totalCount: Int,
    val unpaidCount: Int,
    val paidCount: Int,
    val currencySymbol: String,
    val debtType: DebtType
)

/**
 * UI model for payment history items
 */
data class PaymentHistoryUiModel(
    val date: String,
    val amount: String,
    val description: String?,
    val isLatest: Boolean,
    val currencySymbol: String
)

/**
 * UI state for wallet selector
 */
data class WalletSelectorUiModel(
    val walletName: String,
    val balance: String,
    val currencySymbol: String,
    val isSelected: Boolean,
    val isAllWallets: Boolean = false
) 