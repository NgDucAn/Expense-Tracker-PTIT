package com.ptit.expensetracker.features.money.ui.monthlyreport

import com.ptit.expensetracker.core.platform.MviEventBase
import com.ptit.expensetracker.core.platform.MviIntentBase
import com.ptit.expensetracker.core.platform.MviStateBase
import com.ptit.expensetracker.features.money.domain.usecases.GetMonthlyReportUseCase
import com.ptit.expensetracker.features.money.ui.transactions.MonthItem

/**
 * Contract for MonthlyReportScreen following MVI pattern.
 * Defines State, Intent, and Event.
 */

/**
 * Represents the UI state for MonthlyReportScreen.
 */
data class MonthlyReportState(
    val selectedTabIndex: Int = 0,
    val months: List<MonthItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    
    // Report data for selected month
    val openingBalance: Double = 0.0,
    val closingBalance: Double = 0.0,
    val netIncome: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    
    // Category breakdown for pie charts
    val incomeByCategory: List<CategoryPieSlice> = emptyList(),
    val expenseByCategory: List<CategoryPieSlice> = emptyList(),
    
    // Currency symbol for formatting
    val currencySymbol: String = "đ"
) : MviStateBase

/**
 * Represents user actions or intents on MonthlyReportScreen.
 */
sealed interface MonthlyReportIntent : MviIntentBase {
    data object LoadInitialData : MonthlyReportIntent
    data class SelectTab(val index: Int) : MonthlyReportIntent
}

/**
 * Represents one-time events that can occur on MonthlyReportScreen.
 */
sealed interface MonthlyReportEvent : MviEventBase {
    // No events needed for now, but keeping for future extensibility
}

/**
 * Represents a category pie slice with metadata, display name, amount, color, and percentage.
 * This is used for displaying pie charts in the monthly report.
 */
data class CategoryPieSlice(
    val categoryMetadata: String,
    val categoryDisplayName: String,
    val amount: Double,
    val color: androidx.compose.ui.graphics.Color,
    val percentage: Float = 0f
)

