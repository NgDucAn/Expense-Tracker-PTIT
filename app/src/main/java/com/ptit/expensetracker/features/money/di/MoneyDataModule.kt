package com.ptit.expensetracker.features.money.di

import android.content.Context
import com.ptit.expensetracker.features.money.data.data_source.local.CategoryDataSource
import com.ptit.expensetracker.features.money.data.data_source.local.CurrencyDataSource
import com.ptit.expensetracker.utils.CurrencyConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MoneyDataModule {

    @Provides
    @Singleton
    fun provideCurrencyDataSource(): CurrencyDataSource {
        return CurrencyDataSource()
    }
    
    @Provides
    @Singleton
    fun provideCategoryDataSource(): CategoryDataSource {
        return CategoryDataSource()
    }
    
    @Provides
    @Singleton
    fun provideCurrencyConverter(@ApplicationContext context: Context): CurrencyConverter {
        return CurrencyConverter(context)
    }
}