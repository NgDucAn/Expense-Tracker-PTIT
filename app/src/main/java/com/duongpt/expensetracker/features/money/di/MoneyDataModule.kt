package com.duongpt.expensetracker.features.money.di

import android.content.Context
import com.duongpt.expensetracker.features.money.data.data_source.local.CategoryDataSource
import com.duongpt.expensetracker.features.money.data.data_source.local.CurrencyDataSource
import com.duongpt.expensetracker.utils.CurrencyConverter
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