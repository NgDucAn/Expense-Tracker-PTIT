package com.ptit.expensetracker.features.money.ui.onboarding.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ptit.expensetracker.features.money.ui.navigation.screen.Screen
import kotlinx.coroutines.flow.collect
import androidx.compose.ui.tooling.preview.Preview
import com.ptit.expensetracker.ui.theme.ExpenseTrackerTheme
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    // Trigger onboarding status check at start
    LaunchedEffect(Unit) {
        viewModel.processIntent(SplashIntent.CheckStatus)
    }

    // Collect navigation events
    LaunchedEffect(viewModel.event) {
        viewModel.event.collect { event ->
            when (event) {
                is SplashEvent.GoToHome -> {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0)
                    }
                }
                is SplashEvent.GoToWalletSetup -> {
                    // Navigate to wallet setup screen (to be implemented)
                    navController.navigate(Screen.WalletSetup.route) {
                        popUpTo(0)
                    }
                }
                else -> {}
            }
        }
    }

    // Simple splash UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Expense Tracker",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun SplashScreenContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Expense Tracker",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenContentPreview() {
    ExpenseTrackerTheme {
        SplashScreenContent()
    }
}
