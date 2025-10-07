package com.duongpt.expensetracker.features.money.di

import com.duongpt.expensetracker.features.money.data.data_source.local.dao.BudgetDao
import com.duongpt.expensetracker.features.money.data.data_source.local.dao.CategoryDao
import com.duongpt.expensetracker.features.money.data.data_source.local.dao.WalletDao
import com.duongpt.expensetracker.features.money.data.repository.WalletLocalRepository
import com.duongpt.expensetracker.features.money.domain.repository.WalletRepository
import com.duongpt.expensetracker.features.money.data.data_source.local.dao.CurrencyDao
import com.duongpt.expensetracker.features.money.data.data_source.local.dao.TransactionDao
import com.duongpt.expensetracker.features.money.data.repository.BudgetRepositoryImpl
import com.duongpt.expensetracker.features.money.data.repository.CategoryRepositoryImpl
import com.duongpt.expensetracker.features.money.data.repository.CurrencyLocalRepository
import com.duongpt.expensetracker.features.money.data.repository.TransactionRepositoryImpl
import com.duongpt.expensetracker.features.money.data.repository.OnboardingRepositoryImpl
import com.duongpt.expensetracker.features.money.domain.repository.BudgetRepository
import com.duongpt.expensetracker.features.money.domain.repository.CategoryRepository
import com.duongpt.expensetracker.features.money.domain.repository.CurrencyRepository
import com.duongpt.expensetracker.features.money.domain.repository.TransactionRepository
import com.duongpt.expensetracker.features.money.domain.repository.OnboardingRepository
import com.duongpt.expensetracker.utils.CurrencyConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MoneyRepositoryModule {

    @Provides
    @Singleton
    fun provideWalletRepository(walletDao: WalletDao): WalletRepository {
        return WalletLocalRepository(walletDao)
    }

    @Provides
    @Singleton
    fun provideCurrencyRepository(currencyDao: CurrencyDao): CurrencyRepository {
        return CurrencyLocalRepository(currencyDao)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(transactionDao: TransactionDao): TransactionRepository {
        return TransactionRepositoryImpl(transactionDao)
    }
    
    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao)
    }
    
    @Provides
    @Singleton
    fun provideBudgetRepository(
        budgetDao: BudgetDao,
        categoryDao: CategoryDao,
        walletDao: WalletDao,
        currencyConverter: CurrencyConverter
    ): BudgetRepository {
        return BudgetRepositoryImpl(budgetDao, categoryDao, walletDao, currencyConverter)
    }

    @Provides
    @Singleton
    fun provideOnboardingRepository(
        onboardingRepositoryImpl: OnboardingRepositoryImpl
    ): OnboardingRepository {
        return onboardingRepositoryImpl
    }
}