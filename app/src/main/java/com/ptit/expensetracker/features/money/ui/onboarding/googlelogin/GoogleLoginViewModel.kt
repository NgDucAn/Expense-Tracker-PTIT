package com.ptit.expensetracker.features.money.ui.onboarding.googlelogin

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ptit.expensetracker.R
import com.ptit.expensetracker.core.platform.BaseViewModel
import com.ptit.expensetracker.features.money.domain.repository.OnboardingRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleLoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val onboardingRepository: OnboardingRepository,
) : BaseViewModel<GoogleLoginState, GoogleLoginIntent, GoogleLoginEvent>() {

    override val _viewState = MutableStateFlow(GoogleLoginState())

    override fun processIntent(intent: GoogleLoginIntent) {
        when (intent) {
            GoogleLoginIntent.SignIn -> emitEvent(GoogleLoginEvent.LaunchSignInFlow)
            is GoogleLoginIntent.GoogleSignIn -> handleGoogleSignIn(intent.idToken)
            GoogleLoginIntent.Back -> emitEvent(GoogleLoginEvent.NavigateBack)
        }
    }

    private fun handleGoogleSignIn(idToken: String?) {
        viewModelScope.launch {
            if (idToken.isNullOrBlank()) {
                emitEvent(GoogleLoginEvent.ShowError(context.getString(R.string.google_login_error_no_token)))
                return@launch
            }

            _viewState.value = _viewState.value.copy(isLoading = true, error = null)

            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            // Consider onboarding completed once user has logged in
                            onboardingRepository.setOnboardingCompleted(true)
                            _viewState.value = _viewState.value.copy(isLoading = false)
                            emitEvent(GoogleLoginEvent.SignInSuccess)
                        }
                    }
                    .addOnFailureListener { e ->
                        _viewState.value = _viewState.value.copy(isLoading = false)
                        val msg = e.localizedMessage
                        if (msg.isNullOrBlank()) {
                            emitEvent(GoogleLoginEvent.ShowError(context.getString(R.string.google_login_sign_in_error)))
                        } else {
                            emitEvent(
                                GoogleLoginEvent.ShowError(
                                    context.getString(R.string.google_login_error_auth_failed_format, msg)
                                )
                            )
                        }
                    }
            } catch (e: Exception) {
                _viewState.value = _viewState.value.copy(isLoading = false)
                emitEvent(
                    GoogleLoginEvent.ShowError(
                        e.localizedMessage ?: context.getString(R.string.google_login_sign_in_error)
                    )
                )
            }
        }
    }
}


