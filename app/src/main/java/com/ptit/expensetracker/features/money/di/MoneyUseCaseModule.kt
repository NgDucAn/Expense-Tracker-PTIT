package com.ptit.expensetracker.features.money.di

import com.ptit.expensetracker.features.money.domain.repository.BudgetRepository
import com.ptit.expensetracker.features.money.domain.repository.CurrencyRepository
import com.ptit.expensetracker.features.money.domain.repository.TransactionRepository
import com.ptit.expensetracker.features.money.domain.repository.WalletRepository
import com.ptit.expensetracker.features.money.domain.usecases.CheckCurrenciesExistUseCase
import com.ptit.expensetracker.features.money.domain.usecases.CreateWalletUseCase
import com.ptit.expensetracker.features.money.domain.usecases.DeleteWalletUseCase
import com.ptit.expensetracker.features.money.domain.usecases.UpdateWalletUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetCurrencyByCodeUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetDefaultWalletUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetTransactionByIdUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetTransactionsByCategoryAndWalletUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetTransactionsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetWalletsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.ObserveTransactionsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.ObserveWalletByIdUseCase
import com.ptit.expensetracker.features.money.domain.usecases.SaveBudgetUseCase
import com.ptit.expensetracker.features.money.domain.usecases.SaveCurrenciesUseCase
import com.ptit.expensetracker.features.money.domain.repository.CategoryRepository
import com.ptit.expensetracker.features.money.domain.usecases.CheckBudgetExistsByCategoryUseCase
import com.ptit.expensetracker.features.money.domain.usecases.DeleteTransactionUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetBudgetsByWalletUseCase
import com.ptit.expensetracker.features.money.domain.usecases.TransferMoneyUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetCategoryByNameUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetWeeklyExpenseUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetBudgetByIdUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetTransactionsForBudgetUseCase
import com.ptit.expensetracker.features.money.domain.usecases.DeleteBudgetUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetBudgetTransactionsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetTransactionsByMonthUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetFutureTransactionsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.ObserveTransactionsByMonthUseCase
import com.ptit.expensetracker.features.money.domain.usecases.ObserveFutureTransactionsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.SearchTransactionsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.QuickSearchTransactionsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetSearchSuggestionsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetUsedCategoriesUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetDebtSummaryUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetDebtTransactionsUseCase
import com.ptit.expensetracker.features.money.domain.usecases.CreatePartialPaymentUseCase
import com.ptit.expensetracker.features.money.domain.usecases.GetDebtPaymentHistoryUseCase
import com.ptit.expensetracker.utils.CurrencyConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MoneyUseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideCheckCurrenciesExistUseCase(currencyRepository: CurrencyRepository): CheckCurrenciesExistUseCase {
        return CheckCurrenciesExistUseCase(currencyRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideSaveCurrenciesUseCase(currencyRepository: CurrencyRepository): SaveCurrenciesUseCase {
        return SaveCurrenciesUseCase(currencyRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCreateWalletUseCase(walletRepository: WalletRepository): CreateWalletUseCase {
        return CreateWalletUseCase(walletRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCurrencyByCodeUseCase(currencyRepository: CurrencyRepository): GetCurrencyByCodeUseCase {
        return GetCurrencyByCodeUseCase(currencyRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetWalletsUseCase(walletRepository: WalletRepository): GetWalletsUseCase {
        return GetWalletsUseCase(walletRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideDeleteWalletUseCase(walletRepository: WalletRepository): DeleteWalletUseCase {
        return DeleteWalletUseCase(walletRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideUpdateWalletUseCase(walletRepository: WalletRepository): UpdateWalletUseCase {
        return UpdateWalletUseCase(walletRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideGetTransactionsUseCase(transactionRepository: TransactionRepository): GetTransactionsUseCase {
        return GetTransactionsUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTransactionByIdUseCase(transactionRepository: TransactionRepository): GetTransactionByIdUseCase {
        return GetTransactionByIdUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideObserveTransactionsUseCase(transactionRepository: TransactionRepository): ObserveTransactionsUseCase {
        return ObserveTransactionsUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideObserveWalletByIdUseCase(walletRepository: WalletRepository): ObserveWalletByIdUseCase {
        return ObserveWalletByIdUseCase(walletRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteTransactionUseCase(
        transactionRepository: TransactionRepository,
        walletRepository: WalletRepository
    ): DeleteTransactionUseCase {
        return DeleteTransactionUseCase(transactionRepository, walletRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideTransferMoneyUseCase(
        transactionRepository: TransactionRepository,
        walletRepository: WalletRepository,
        categoryRepository: CategoryRepository
    ): TransferMoneyUseCase {
        return TransferMoneyUseCase(transactionRepository, walletRepository, categoryRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideSaveBudgetUseCase(budgetRepository: BudgetRepository): SaveBudgetUseCase {
        return SaveBudgetUseCase(budgetRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideGetDefaultWalletUseCase(walletRepository: WalletRepository): GetDefaultWalletUseCase {
        return GetDefaultWalletUseCase(walletRepository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideGetTransactionsByCategoryAndWalletUseCase(
        transactionRepository: TransactionRepository
    ): GetTransactionsByCategoryAndWalletUseCase {
        return GetTransactionsByCategoryAndWalletUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetBudgetsByWalletUseCase(budgetRepository: BudgetRepository): GetBudgetsByWalletUseCase {
        return GetBudgetsByWalletUseCase(budgetRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCheckBudgetExistsByCategoryUseCase(budgetRepository: BudgetRepository): CheckBudgetExistsByCategoryUseCase {
        return CheckBudgetExistsByCategoryUseCase(budgetRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetCategoryByNameUseCase(categoryRepository: CategoryRepository): GetCategoryByNameUseCase {
        return GetCategoryByNameUseCase(categoryRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetWeeklyExpenseUseCase(
        transactionRepository: TransactionRepository
    ): GetWeeklyExpenseUseCase {
        return GetWeeklyExpenseUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetBudgetByIdUseCase(budgetRepository: BudgetRepository): GetBudgetByIdUseCase {
        return GetBudgetByIdUseCase(budgetRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTransactionsForBudgetUseCase(
        budgetRepository: BudgetRepository,
        transactionRepository: TransactionRepository
    ): GetTransactionsForBudgetUseCase {
        return GetTransactionsForBudgetUseCase(budgetRepository, transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideDeleteBudgetUseCase(budgetRepository: BudgetRepository): DeleteBudgetUseCase {
        return DeleteBudgetUseCase(budgetRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetBudgetTransactionsUseCase(
        budgetRepository: BudgetRepository,
        getTxnsByCategoryAndWallet: GetTransactionsByCategoryAndWalletUseCase,
        currencyConverter: CurrencyConverter
    ): GetBudgetTransactionsUseCase {
        return GetBudgetTransactionsUseCase(budgetRepository, getTxnsByCategoryAndWallet, currencyConverter)
    }

    @Provides
    @ViewModelScoped
    fun provideGetTransactionsByMonthUseCase(
        transactionRepository: TransactionRepository
    ): GetTransactionsByMonthUseCase {
        return GetTransactionsByMonthUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetFutureTransactionsUseCase(
        transactionRepository: TransactionRepository
    ): GetFutureTransactionsUseCase {
        return GetFutureTransactionsUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideObserveTransactionsByMonthUseCase(
        transactionRepository: TransactionRepository
    ): ObserveTransactionsByMonthUseCase {
        return ObserveTransactionsByMonthUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideObserveFutureTransactionsUseCase(
        transactionRepository: TransactionRepository
    ): ObserveFutureTransactionsUseCase {
        return ObserveFutureTransactionsUseCase(transactionRepository)
    }

    // ==================== SEARCH FUNCTIONALITY USE CASES ====================

    @Provides
    @ViewModelScoped
    fun provideSearchTransactionsUseCase(
        transactionRepository: TransactionRepository
    ): SearchTransactionsUseCase {
        return SearchTransactionsUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideQuickSearchTransactionsUseCase(
        transactionRepository: TransactionRepository
    ): QuickSearchTransactionsUseCase {
        return QuickSearchTransactionsUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetSearchSuggestionsUseCase(
        transactionRepository: TransactionRepository
    ): GetSearchSuggestionsUseCase {
        return GetSearchSuggestionsUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetUsedCategoriesUseCase(
        transactionRepository: TransactionRepository
    ): GetUsedCategoriesUseCase {
        return GetUsedCategoriesUseCase(transactionRepository)
    }

    // ==================== DEBT MANAGEMENT USE CASES ====================

    @Provides
    @ViewModelScoped
    fun provideGetDebtSummaryUseCase(
        transactionRepository: TransactionRepository,
        walletRepository: WalletRepository
    ): GetDebtSummaryUseCase {
        return GetDebtSummaryUseCase(transactionRepository, walletRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetDebtTransactionsUseCase(
        transactionRepository: TransactionRepository
    ): GetDebtTransactionsUseCase {
        return GetDebtTransactionsUseCase(transactionRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideCreatePartialPaymentUseCase(
        transactionRepository: TransactionRepository,
        walletRepository: WalletRepository
    ): CreatePartialPaymentUseCase {
        return CreatePartialPaymentUseCase(transactionRepository, walletRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetDebtPaymentHistoryUseCase(
        transactionRepository: TransactionRepository
    ): GetDebtPaymentHistoryUseCase {
        return GetDebtPaymentHistoryUseCase(transactionRepository)
    }
}