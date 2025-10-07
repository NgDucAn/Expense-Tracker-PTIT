package com.duongpt.expensetracker.features.money.ui.debtmanagement

import androidx.lifecycle.viewModelScope
import com.duongpt.expensetracker.core.interactor.UseCase
import com.duongpt.expensetracker.core.platform.BaseViewModel

import com.duongpt.expensetracker.features.money.domain.model.DebtCategoryMetadata
import com.duongpt.expensetracker.features.money.domain.model.DebtSummary
import com.duongpt.expensetracker.features.money.domain.model.Wallet
import com.duongpt.expensetracker.features.money.domain.usecases.GetDebtSummaryUseCase
import com.duongpt.expensetracker.features.money.domain.usecases.GetWalletsUseCase
import com.duongpt.expensetracker.features.money.ui.debtmanagement.components.DebtFilterOptions
import com.duongpt.expensetracker.features.money.ui.debtmanagement.components.DebtSortBy
import com.duongpt.expensetracker.features.money.ui.debtmanagement.components.DebtTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the DebtManagement screen.
 * Handles business logic and manages the UI state for debt management.
 * Follows MVI architecture pattern.
 */
@HiltViewModel
class DebtManagementViewModel @Inject constructor(
    private val getDebtSummaryUseCase: GetDebtSummaryUseCase,
    private val getWalletsUseCase: GetWalletsUseCase
) : BaseViewModel<DebtManagementState, DebtManagementIntent, DebtManagementEvent>() {

    override val _viewState = MutableStateFlow(DebtManagementState())

    init {
        processIntent(DebtManagementIntent.LoadInitialData)
    }

    /**
     * Handles intents triggered from the UI.
     */
    override fun processIntent(intent: DebtManagementIntent) {
        when (intent) {
            DebtManagementIntent.LoadInitialData -> loadInitialData()
            DebtManagementIntent.RefreshData -> refreshData()
            
            // Wallet selection
            DebtManagementIntent.ShowWalletSelector -> showWalletSelector()
            DebtManagementIntent.HideWalletSelector -> hideWalletSelector()
            is DebtManagementIntent.SelectWallet -> selectWallet(intent.wallet)
            
            // Tab management
            is DebtManagementIntent.SelectTab -> selectTab(intent.tab)
            
            // Debt management
            is DebtManagementIntent.ViewDebtDetails -> viewDebtDetails(intent.debtSummary)
            is DebtManagementIntent.AddPartialPayment -> addPartialPayment(intent.debtSummary)
            is DebtManagementIntent.ViewPaymentHistory -> viewPaymentHistory(intent.debtSummary)
            
            // Filter management
            DebtManagementIntent.ShowFilterDialog -> showFilterDialog()
            DebtManagementIntent.HideFilterDialog -> hideFilterDialog()
            is DebtManagementIntent.ApplyFilter -> applyFilter(intent.filterOptions)
            DebtManagementIntent.ClearFilter -> clearFilter()
            
            // Error handling
            DebtManagementIntent.ClearError -> clearError()
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _viewState.update { it.copy(isLoading = true, error = null) }
            
            try {
                // First load wallets
                getWalletsUseCase(UseCase.None()) { walletsResult ->
                    walletsResult.fold(
                        { 
                            _viewState.update { 
                                it.copy(
                                    isLoading = false,
                                    error = "Không thể tải danh sách ví"
                                ) 
                            }
                        },
                        { wallets ->
                            viewModelScope.launch {
                                wallets.collect() { listWallets ->
                                    // Update available wallets in state
                                    _viewState.update {
                                        it.copy(availableWallets = listWallets)
                                    }

                                    // Then load debt data
                                    getDebtSummaryUseCase(GetDebtSummaryUseCase.Params(null)) { debtResult ->
                                        debtResult.fold(
                                            {
                                                _viewState.update {
                                                    it.copy(
                                                        isLoading = false,
                                                        error = "Không thể tải dữ liệu nợ"
                                                    )
                                                }
                                            },
                                            { debtInfo ->
                                                val allDebts = debtInfo.payableDebts + debtInfo.receivableDebts
                                                updateStateWithData(listWallets, allDebts)
                                            }
                                        )
                                    }
                                }
                            }

                        }
                    )
                }
            } catch (e: Exception) {
                _viewState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Đã xảy ra lỗi: ${e.message}"
                    ) 
                }
            }
        }
    }

    private fun refreshData() {
        _viewState.update { it.copy(isRefreshing = true) }
        loadInitialData()
    }

    private fun showWalletSelector() {
        _viewState.update { it.copy(showWalletSelector = true) }
    }

    private fun hideWalletSelector() {
        _viewState.update { it.copy(showWalletSelector = false) }
    }

    private fun selectWallet(wallet: Wallet?) {
        _viewState.update { 
            it.copy(
                selectedWallet = wallet,
                showWalletSelector = false
            ) 
        }
        
        // Reload debt data for selected wallet
        loadDebtDataForWallet(wallet?.id)
    }

    private fun selectTab(tab: DebtTab) {
        _viewState.update { it.copy(selectedTab = tab) }
    }

    private fun viewDebtDetails(debtSummary: DebtSummary) {
        emitEvent(DebtManagementEvent.NavigateToDebtDetails(debtSummary))
    }

    private fun addPartialPayment(debtSummary: DebtSummary) {
        if (debtSummary.isPaid) {
            emitEvent(DebtManagementEvent.ShowToast("Nợ này đã được thanh toán đầy đủ"))
            return
        }
        emitEvent(DebtManagementEvent.NavigateToAddPartialPayment(debtSummary))
    }

    private fun viewPaymentHistory(debtSummary: DebtSummary) {
        emitEvent(DebtManagementEvent.NavigateToPaymentHistory(debtSummary))
    }

    private fun showFilterDialog() {
        _viewState.update { it.copy(showFilterDialog = true) }
    }

    private fun hideFilterDialog() {
        _viewState.update { it.copy(showFilterDialog = false) }
    }

    private fun applyFilter(filterOptions: DebtFilterOptions) {
        _viewState.update { 
            it.copy(
                filterOptions = filterOptions,
                showFilterDialog = false
            ) 
        }
        
        // Apply filter to current debt data
        applyFilterToDebtData()
    }

    private fun clearFilter() {
        _viewState.update { 
            it.copy(
                filterOptions = DebtFilterOptions(),
                showFilterDialog = false
            ) 
        }
        
        // Reset to unfiltered data
        applyFilterToDebtData()
    }

    private fun clearError() {
        _viewState.update { it.copy(error = null) }
    }

    // Private helper methods

    private fun loadDebtDataForWallet(walletId: Int?) {
        viewModelScope.launch {
            try {
                getDebtSummaryUseCase(GetDebtSummaryUseCase.Params(walletId)) { result ->
                    result.fold(
                        { failure ->
                            _viewState.update { 
                                it.copy(error = "Không thể tải dữ liệu nợ: ${failure}") 
                            }
                        },
                        { debtInfo ->
                            val currentState = _viewState.value
                            val allDebts = debtInfo.payableDebts + debtInfo.receivableDebts
                            updateStateWithData(currentState.availableWallets, allDebts)
                        }
                    )
                }
            } catch (e: Exception) {
                _viewState.update { 
                    it.copy(error = "Đã xảy ra lỗi khi tải dữ liệu: ${e.message}") 
                }
            }
        }
    }

    private fun updateStateWithData(wallets: List<Wallet>, allDebtSummaries: List<DebtSummary>) {
        // We need to determine debt type from the original transaction category
        val payableDebts = allDebtSummaries.filter { debt ->
            DebtCategoryMetadata.PAYABLE_ORIGINAL.contains(debt.originalTransaction.category.metaData)
        }
        val receivableDebts = allDebtSummaries.filter { debt ->
            DebtCategoryMetadata.RECEIVABLE_ORIGINAL.contains(debt.originalTransaction.category.metaData)
        }
        
        // Apply current filter
        val filteredPayableDebts = applyFilterToDebts(payableDebts)
        val filteredReceivableDebts = applyFilterToDebts(receivableDebts)
        
        // Separate paid and unpaid debts using isPaid property
        val unpaidPayableDebts = filteredPayableDebts.filter { !it.isPaid }
        val paidPayableDebts = filteredPayableDebts.filter { it.isPaid }
        val unpaidReceivableDebts = filteredReceivableDebts.filter { !it.isPaid }
        val paidReceivableDebts = filteredReceivableDebts.filter { it.isPaid }
        
        // Calculate statistics
        val totalPayableAmount = payableDebts.sumOf { it.remainingAmount }
        val totalReceivableAmount = receivableDebts.sumOf { it.remainingAmount }
        val totalUnpaidPayable = unpaidPayableDebts.sumOf { it.remainingAmount }
        val totalUnpaidReceivable = unpaidReceivableDebts.sumOf { it.remainingAmount }
        
        // Get currency symbol from selected wallet or default
        val currencySymbol = _viewState.value.selectedWallet?.currency?.symbol 
            ?: wallets.firstOrNull()?.currency?.symbol 
            ?: "đ"

        _viewState.update { currentState ->
            currentState.copy(
                availableWallets = wallets,
                payableDebts = filteredPayableDebts,
                receivableDebts = filteredReceivableDebts,
                unpaidPayableDebts = unpaidPayableDebts,
                paidPayableDebts = paidPayableDebts,
                unpaidReceivableDebts = unpaidReceivableDebts,
                paidReceivableDebts = paidReceivableDebts,
                totalPayableAmount = totalPayableAmount,
                totalReceivableAmount = totalReceivableAmount,
                totalUnpaidPayable = totalUnpaidPayable,
                totalUnpaidReceivable = totalUnpaidReceivable,
                currencySymbol = currencySymbol,
                isLoading = false,
                isRefreshing = false,
                error = null
            )
        }
    }

    private fun applyFilterToDebtData() {
        val currentState = _viewState.value
        
        // Apply filter to current debt data
        val filteredPayableDebts = applyFilterToDebts(currentState.payableDebts)
        val filteredReceivableDebts = applyFilterToDebts(currentState.receivableDebts)
        
        // Update state with filtered data
        updateStateWithData(currentState.availableWallets, filteredPayableDebts + filteredReceivableDebts)
    }

    private fun applyFilterToDebts(debts: List<DebtSummary>): List<DebtSummary> {
        val filter = _viewState.value.filterOptions
        
        var filteredDebts = debts
        
        // Filter by paid/unpaid status
        if (!filter.showPaidDebts) {
            filteredDebts = filteredDebts.filter { !it.isPaid }
        }
        if (!filter.showUnpaidDebts) {
            filteredDebts = filteredDebts.filter { it.isPaid }
        }
        
        // Filter by amount range
        filter.minAmount?.let { min ->
            filteredDebts = filteredDebts.filter { it.remainingAmount >= min }
        }
        filter.maxAmount?.let { max ->
            filteredDebts = filteredDebts.filter { it.remainingAmount <= max }
        }
        
        // Filter by search query
        if (filter.searchQuery.isNotBlank()) {
            filteredDebts = filteredDebts.filter { 
                it.personName.contains(filter.searchQuery, ignoreCase = true)
            }
        }
        
        // Sort debts (simplified sorting without DebtMapper)
        return when (filter.sortBy) {
            DebtSortBy.AMOUNT_ASC ->
                filteredDebts.sortedBy { it.remainingAmount }
            DebtSortBy.AMOUNT_DESC ->
                filteredDebts.sortedByDescending { it.remainingAmount }
            DebtSortBy.NAME_ASC ->
                filteredDebts.sortedBy { it.personName }
           DebtSortBy.NAME_DESC ->
                filteredDebts.sortedByDescending { it.personName }
            DebtSortBy.DATE_ASC ->
                filteredDebts.sortedBy { it.originalTransaction.transactionDate }
           DebtSortBy.DATE_DESC ->
                filteredDebts.sortedByDescending { it.originalTransaction.transactionDate }
        }
    }
} 