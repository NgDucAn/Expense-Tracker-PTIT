package com.ptit.expensetracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ptit.expensetracker.features.money.data.worker.ProcessRecurringBudgetsWorker
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // Schedule recurring budgets processing once a day
        val workRequest = PeriodicWorkRequestBuilder<ProcessRecurringBudgetsWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(1, TimeUnit.HOURS) // initial delay to stagger
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ProcessRecurringBudgets",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}