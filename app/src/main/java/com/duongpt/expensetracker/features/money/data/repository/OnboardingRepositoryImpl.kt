package com.duongpt.expensetracker.features.money.data.repository

import com.duongpt.expensetracker.core.data.datastore.OnboardingPreferences
import com.duongpt.expensetracker.features.money.domain.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences
) : OnboardingRepository {

    override fun isOnboardingCompleted(): Flow<Boolean> =
        onboardingPreferences.isOnboardingCompleted

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        onboardingPreferences.setOnboardingCompleted(completed)
    }
} 