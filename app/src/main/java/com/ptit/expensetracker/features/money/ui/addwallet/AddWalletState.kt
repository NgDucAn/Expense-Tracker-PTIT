package com.ptit.expensetracker.features.money.ui.addwallet

import com.ptit.expensetracker.core.platform.MviEventBase
import com.ptit.expensetracker.core.platform.MviIntentBase
import com.ptit.expensetracker.core.platform.MviStateBase
import com.ptit.expensetracker.features.money.domain.model.Currency
import com.ptit.expensetracker.features.money.domain.model.Wallet
import com.ptit.expensetracker.features.money.ui.addtransaction.components.NumpadButton
import com.ptit.expensetracker.R

data class AddWalletState(
    val walletName: String = "",
    val currency: Currency? = null,
    val initialBalance: String = "0",
    val enableNotification: Boolean = false,
    val excludedFromTotal: Boolean = false,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val showNumpad: Boolean = false,
    val amountInput: String = "0",
    val formattedAmount: String = "0",
    val displayExpression: String = "0",
    val iconString: String = "img_wallet_default_widget",
    val walletToEdit: Wallet? = null,
    val isEditMode: Boolean = false
) : MviStateBase

interface AddWalletIntent : MviIntentBase {
    data class UpdateWalletName(val name: String) : AddWalletIntent
    data class SelectCurrency(val currency: Currency) : AddWalletIntent
    data class UpdateInitialBalance(val balance: String) : AddWalletIntent
    data class ToggleNotification(val enable: Boolean) : AddWalletIntent
    data class ToggleExcludedFromTotal(val excluded: Boolean) : AddWalletIntent
    data class LoadWallet(val walletId: Int) : AddWalletIntent
    object SaveWallet : AddWalletIntent
    object NavigateBack : AddWalletIntent
    object NavigateToCurrencyScreen : AddWalletIntent
    object ToggleNumpad : AddWalletIntent
    data class NumpadButtonPressed(val button: NumpadButton) : AddWalletIntent
    object NavigateToIconPicker : AddWalletIntent
    data class SelectIcon(val iconName: String) : AddWalletIntent
    data class SetInitialWallet(val wallet: Wallet) : AddWalletIntent
}

interface AddWalletEvent : MviEventBase {
    object NavigateBack : AddWalletEvent
    object NavigateToCurrencyScreen : AddWalletEvent
    object NavigateToIconPicker : AddWalletEvent
    object ShowSuccess : AddWalletEvent
    data class ShowError(val message: String) : AddWalletEvent
    data class ShowInfo(val message: String) : AddWalletEvent
} 