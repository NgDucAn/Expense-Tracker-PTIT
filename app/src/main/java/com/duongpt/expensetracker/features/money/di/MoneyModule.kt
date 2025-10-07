package com.duongpt.expensetracker.features.money.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Aggregate module for Money feature repositories
 *
 * Note: Use cases are provided by MoneyUseCaseModule with ViewModelComponent scope
 */
@Module(
    includes = [
        MoneyRepositoryModule::class,
        MoneyDataModule::class,
        RecurringBudgetWorkerModule::class,
    ]
)
@InstallIn(SingletonComponent::class)
object MoneyModule