package com.ptit.expensetracker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ptit.expensetracker.features.money.data.worker.ProcessRecurringBudgetsWorker
import com.ptit.expensetracker.features.money.data.worker.DailyReminderWorker
import com.google.firebase.auth.FirebaseAuth
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Force Firebase Auth to use English to avoid Vietnamese locale issues
        FirebaseAuth.getInstance().setLanguageCode("en")
        scheduleRecurringBudgets()
        scheduleDailyTransactionReminder()
    }

    /**
     * Lên lịch worker xử lý ngân sách lặp lại (đã có sẵn từ trước).
     */
    private fun scheduleRecurringBudgets() {
        val workRequest =
            PeriodicWorkRequestBuilder<ProcessRecurringBudgetsWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(1, TimeUnit.HOURS) // initial delay to stagger
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ProcessRecurringBudgets",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    /**
     * Lên lịch worker gửi notification nhắc người dùng ghi lại giao dịch lúc ~21h mỗi ngày.
     *
     * WorkManager không đảm bảo chính xác từng phút nhưng đủ cho use case nhắc nhẹ.
     */
    private fun scheduleDailyTransactionReminder() {
        val now = LocalDateTime.now()
        val targetToday = LocalDate.now().atTime(LocalTime.of(21, 0))

        val nextRun = if (now.isBefore(targetToday)) {
            targetToday
        } else {
            // Nếu đã qua 21h hôm nay -> chuyển sang 21h ngày mai
            LocalDate.now().plusDays(1).atTime(LocalTime.of(21, 0))
        }

        val initialDelayMinutes = Duration.between(now, nextRun).toMinutes().coerceAtLeast(0)

        val workRequest =
            PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            DailyReminderWorker.DAILY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
