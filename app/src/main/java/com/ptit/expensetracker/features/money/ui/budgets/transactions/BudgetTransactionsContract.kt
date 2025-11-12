package com.ptit.expensetracker.features.money.ui.budgets.transactions

import com.ptit.expensetracker.core.platform.MviStateBase
import com.ptit.expensetracker.core.platform.MviIntentBase
import com.ptit.expensetracker.core.platform.MviEventBase
import com.ptit.expensetracker.features.money.domain.model.Category
import com.ptit.expensetracker.features.money.ui.transactions.DailyTransactions

// State for BudgetTransactions screen
data class BudgetTransactionsState(
    val budgetId: Int,
    val category: Category,
    val groupedTransactions: List<DailyTransactions> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) : MviStateBase

// Intents representing user actions
sealed interface BudgetTransactionsIntent : MviIntentBase {
    object LoadTransactions : BudgetTransactionsIntent
    object RetryLoad : BudgetTransactionsIntent
    data class TransactionClicked(val transactionId: Int) : BudgetTransactionsIntent
    object BackClicked : BudgetTransactionsIntent
}

// Events for one-time actions like navigation or error
sealed interface BudgetTransactionsEvent : MviEventBase {
    data class NavigateToTransactionDetail(val transactionId: Int) : BudgetTransactionsEvent
    object NavigateBack : BudgetTransactionsEvent
    data class ShowError(val message: String) : BudgetTransactionsEvent
} 