package com.duongpt.expensetracker.features.money.ui.enteramount

import com.duongpt.expensetracker.core.platform.MviEventBase
import com.duongpt.expensetracker.core.platform.MviIntentBase
import com.duongpt.expensetracker.core.platform.MviStateBase
import com.duongpt.expensetracker.features.money.domain.model.Currency
import com.duongpt.expensetracker.features.money.ui.addtransaction.components.NumpadButton

data class EnterAmountState(
    val amount: String = "0",
    val formattedAmount: String = "0",
    val currency: Currency = Currency(
        id = 0,
        currencyName = "US Dollar",
        currencyCode = "USD",
        symbol = "$"
    )
) : MviStateBase

sealed class EnterAmountIntent : MviIntentBase {
    data class NumpadButtonPressed(val button: NumpadButton) : EnterAmountIntent()
    object SaveAmount : EnterAmountIntent()
}

sealed class EnterAmountEvent : MviEventBase {
    data class ShowError(
        val message: String,
        val level: ErrorLevel = ErrorLevel.ERROR
    ) : EnterAmountEvent()
    
    data class NavigateBack(
        val amount: String,
        val formattedAmount: String
    ) : EnterAmountEvent()
    
    enum class ErrorLevel {
        INFO,    // Just information, not an error
        WARNING, // Warning but can continue
        ERROR    // Error that prevents continuation
    }
} 