package com.duongpt.expensetracker.features.money.ui.onboarding.splash

import com.duongpt.expensetracker.core.platform.MviIntentBase
import com.duongpt.expensetracker.core.platform.MviStateBase
import com.duongpt.expensetracker.core.platform.MviEventBase

sealed interface SplashIntent : MviIntentBase {
    object CheckStatus : SplashIntent
}

/**
 * UI state for SplashScreen
 */
data class SplashState(
    val isLoading: Boolean = true
) : MviStateBase

sealed interface SplashEvent : MviEventBase {
    object GoToHome : SplashEvent
    object GoToWalletSetup : SplashEvent
} 