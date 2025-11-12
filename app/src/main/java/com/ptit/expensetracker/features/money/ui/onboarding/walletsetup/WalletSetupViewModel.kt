package com.ptit.expensetracker.features.money.ui.onboarding.walletsetup

import androidx.lifecycle.viewModelScope
import com.ptit.expensetracker.core.interactor.UseCase
import com.ptit.expensetracker.core.platform.BaseViewModel
import com.ptit.expensetracker.features.money.data.data_source.local.model.WalletEntity
import com.ptit.expensetracker.features.money.data.data_source.local.model.CurrencyEntity
import com.ptit.expensetracker.features.money.data.data_source.local.model.WalletWithCurrencyEntity
import com.ptit.expensetracker.features.money.domain.usecases.CreateWalletUseCase
import com.ptit.expensetracker.features.money.domain.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import com.ptit.expensetracker.features.money.ui.addtransaction.components.NumpadButton
import com.ptit.expensetracker.utils.CalculatorUtil
import com.ptit.expensetracker.utils.CalculatorUtil.CalculatorCallback

@HiltViewModel
class WalletSetupViewModel @Inject constructor(
    private val createWalletUseCase: CreateWalletUseCase,
    private val onboardingRepository: OnboardingRepository
) : BaseViewModel<WalletSetupState, WalletSetupIntent, WalletSetupEvent>(), CalculatorCallback {

    override val _viewState = MutableStateFlow(WalletSetupState())
    // Calculator utility for amount input
    private val calculator = CalculatorUtil(this)

    override fun processIntent(intent: WalletSetupIntent) {
        when (intent) {
            WalletSetupIntent.ChangeIcon -> emitEvent(WalletSetupEvent.NavigateToIconPicker)
            is WalletSetupIntent.IconPicked -> _viewState.value = _viewState.value.copy(
                selectedIcon = intent.iconRes
            )
            is WalletSetupIntent.UpdateName -> _viewState.value = _viewState.value.copy(
                walletName = intent.name
            )
            WalletSetupIntent.ChangeCurrency -> emitEvent(WalletSetupEvent.NavigateToCurrencyPicker)
            // Toggle bottom-sheet numpad
            WalletSetupIntent.ToggleNumpad -> _viewState.value = _viewState.value.copy(
                showNumpad = !_viewState.value.showNumpad
            )
            is WalletSetupIntent.CurrencySelected -> _viewState.value = _viewState.value.copy(
                selectedCurrency = intent.currency
            ).also {
                // Update calculator format for the selected currency
                calculator.setCurrency(intent.currency)
            }
            WalletSetupIntent.ChangeBalance -> _viewState.value = _viewState.value.copy(
                showNumpad = !_viewState.value.showNumpad
            )
            is WalletSetupIntent.BalanceEntered -> _viewState.value = _viewState.value.copy(
                enteredAmount = intent.amount
            )
            is WalletSetupIntent.NumpadButtonPressed -> calculator.handleNumpadButtonPressed(intent.button)
            WalletSetupIntent.ConfirmSetup -> handleConfirmSetup()
        }
    }

    private fun handleConfirmSetup() {
        // Assemble and execute create wallet use case
        val state = _viewState.value
        state.selectedCurrency?.let { currency ->
            viewModelScope.launch {
                _viewState.value = state.copy(isCreating = true, error = null)
                // Create entity for insertion
                val walletEntity = WalletEntity(
                    id = 0,
                    walletName = state.walletName,
                    currentBalance = state.amountInput.toDoubleOrNull() ?: 0.0,
                    currencyId = currency.id,
                    icon = state.selectedIcon.toString(),
                    isMainWallet = true
                )
                val currencyEntity = CurrencyEntity(
                    id = currency.id,
                    currencyName = currency.currencyName,
                    currencyCode = currency.currencyCode,
                    symbol = currency.symbol,
                    displayType = currency.displayType,
                    image = currency.image
                )
                val params = CreateWalletUseCase.Params(
                    WalletWithCurrencyEntity(walletEntity, currencyEntity)
                )
                // Invoke use case
                createWalletUseCase(params, viewModelScope) { result ->
                    result.fold(
                        { failure ->
                            _viewState.value = state.copy(
                                isCreating = false,
                                error = failure.javaClass.simpleName
                            )
                        },
                        {
                            // On success, save onboarding flag and navigate
                            viewModelScope.launch {
                                onboardingRepository.setOnboardingCompleted(true)
                                emitEvent(WalletSetupEvent.NavigateToHome)
                            }
                        }
                    )
                }
            }
        }
    }

    // Calculator callback implementations
    override fun onCalculatorUpdate(amount: String, formattedAmount: String, displayExpression: String) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(
                amountInput = amount,
                formattedAmount = formattedAmount
            )
        }
    }

    override fun onSaveAmount(amount: String, formattedAmount: String) {
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(
                amountInput = amount,
                formattedAmount = formattedAmount,
                showNumpad = false
            )
        }
    }

    override fun onError(message: String, level: CalculatorUtil.CalculatorError) {
        // Update error state
        viewModelScope.launch {
            _viewState.value = _viewState.value.copy(error = message)
        }
    }
} 