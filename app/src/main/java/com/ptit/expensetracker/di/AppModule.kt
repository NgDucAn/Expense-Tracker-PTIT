package com.ptit.expensetracker.di

import com.ptit.expensetracker.core.di.CoreModule
import com.ptit.expensetracker.features.ai.di.AiModule
import com.ptit.expensetracker.features.money.di.MoneyModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Application module that aggregates all singleton-scoped feature modules
 *
 * Note: ViewModelComponent scoped modules (like MoneyUseCaseModule) are not included here
 * as they are automatically managed by Hilt and injected into ViewModels
 */
@Module(
    includes = [
        CoreModule::class,
        MoneyModule::class,
        AiModule::class
    ]
)
@InstallIn(SingletonComponent::class)
object AppModule