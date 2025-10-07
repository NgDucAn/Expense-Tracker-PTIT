package com.duongpt.expensetracker.features.money.ui.currency

import com.duongpt.expensetracker.core.platform.MviIntentBase
import com.duongpt.expensetracker.core.platform.MviStateBase
import com.duongpt.expensetracker.core.platform.MviEventBase
import com.duongpt.expensetracker.features.money.domain.model.Currency

sealed interface CurrencyIntent : MviIntentBase {
    object LoadCurrencies : CurrencyIntent
    data class SelectCurrency(val currency: Currency) : CurrencyIntent
}

/**
 * UI state for CurrencyScreen
 */
data class CurrencyState(
    val isLoading: Boolean = false,
    val currencies: List<Currency> = emptyList(),
    val selectedCurrencyCode: String? = null,
    val error: String? = null
) : MviStateBase

sealed interface CurrencyEvent : MviEventBase {
    data class ShowError(val message: String) : CurrencyEvent
    data class CurrencySelected(val currency: Currency) : CurrencyEvent
} 