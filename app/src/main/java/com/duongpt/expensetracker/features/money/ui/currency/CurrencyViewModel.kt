package com.duongpt.expensetracker.features.money.ui.currency

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.core.platform.BaseViewModel
import com.duongpt.expensetracker.features.money.data.data_source.local.CurrencyDataSource
import com.duongpt.expensetracker.features.money.domain.usecases.CheckCurrenciesExistUseCase
import com.duongpt.expensetracker.features.money.domain.usecases.SaveCurrenciesUseCase
import com.duongpt.expensetracker.features.money.domain.usecases.GetAllCurrenciesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val checkCurrenciesExistUseCase: CheckCurrenciesExistUseCase,
    private val saveCurrenciesUseCase: SaveCurrenciesUseCase,
    private val getAllCurrenciesUseCase: GetAllCurrenciesUseCase,
    private val currencyDataSource: CurrencyDataSource,
    @ApplicationContext private val context: Context
) : BaseViewModel<CurrencyState, CurrencyIntent, CurrencyEvent>() {

    override val _viewState = MutableStateFlow(CurrencyState())

    override fun processIntent(intent: CurrencyIntent) {
        when (intent) {
            CurrencyIntent.LoadCurrencies -> loadCurrencies()
            is CurrencyIntent.SelectCurrency -> selectCurrency(intent.currency)
        }
    }

    private fun loadCurrencies() {
        _viewState.value = _viewState.value.copy(isLoading = true, error = null)
        checkCurrenciesExistUseCase(UseCase.None(), viewModelScope) { result ->
            result.fold(
                {
                    _viewState.value = _viewState.value.copy(isLoading = false, error = "Failed to check currencies")
                    emitEvent(CurrencyEvent.ShowError("Failed to check currencies"))
                },
                { exists ->
                    if (!exists) {
                        viewModelScope.launch {
                            try {
                                val list = currencyDataSource.loadCurrencies(context)
                                saveCurrenciesUseCase(SaveCurrenciesUseCase.Params(list), viewModelScope) { saveResult ->
                                    saveResult.fold(
                                        {
                                            _viewState.value = _viewState.value.copy(isLoading = false, error = "Failed to save currencies")
                                            emitEvent(CurrencyEvent.ShowError("Failed to save currencies"))
                                        },
                                        {
                                            loadFromDb()
                                        }
                                    )
                                }
                            } catch (e: Exception) {
                                _viewState.value = _viewState.value.copy(isLoading = false, error = e.message)
                                emitEvent(CurrencyEvent.ShowError("Failed to load currencies: ${e.message}"))
                            }
                        }
                    } else {
                        loadFromDb()
                    }
                }
            )
        }
    }

    private fun loadFromDb() {
        getAllCurrenciesUseCase(UseCase.None(), viewModelScope) { result ->
            result.fold(
                {
                    _viewState.value = _viewState.value.copy(isLoading = false, error = "Failed to load currencies")
                    emitEvent(CurrencyEvent.ShowError("Failed to load currencies"))
                },
                { flow ->
                    flow.onEach { list ->
                        _viewState.value = _viewState.value.copy(isLoading = false, currencies = list)
                    }.launchIn(viewModelScope)
                }
            )
        }
    }

    private fun selectCurrency(currency: com.duongpt.expensetracker.features.money.domain.model.Currency) {
        _viewState.value = _viewState.value.copy(selectedCurrencyCode = currency.currencyCode)
        emitEvent(CurrencyEvent.CurrencySelected(currency))
    }
} 