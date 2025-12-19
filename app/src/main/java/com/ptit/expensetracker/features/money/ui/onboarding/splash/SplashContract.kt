package com.ptit.expensetracker.features.money.ui.onboarding.splash

import com.ptit.expensetracker.core.platform.MviIntentBase
import com.ptit.expensetracker.core.platform.MviStateBase
import com.ptit.expensetracker.core.platform.MviEventBase

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
    object GoToOnboarding : SplashEvent
} 