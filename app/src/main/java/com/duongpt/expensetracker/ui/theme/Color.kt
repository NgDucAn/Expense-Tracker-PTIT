package com.duongpt.expensetracker.ui.theme

import androidx.compose.ui.graphics.Color

object AppColor {
    object Dark {
        object PrimaryColor {
            val disabledContentColor = Color(0xFFB0B0B0) // Light gray for disabled text
            val disabledContainerColor = Color(0xFF2A2A2A) // Dark gray for disabled state
            val containerColor = Color.Black
            val contentColor = Color.White
            val cardColor = ScreenBackground
            val TextButtonColor = TextAccent
            val containerColorSecondary = Color(0xFF2A2A2A)
        }

        object SecondaryColor {
            val color1 = Color(0x99ffffff)
            val color2 = Color(0xff1c1c1e)
        }

        // Colors from the Transaction Screen screenshot
        val WalletSelectorBackground = Color(0xFF3A3A3C)
        val InflowAmountColor = Color(0xFF4FC3F7)
        val OutflowAmountColor = Color(0xFFF05252)
        val ReportButtonBackground = Color(0xFF4CAF50)
        val ExpenseAmountColor = Color(0xFFF05252)
        val IncomeAmountColor = Color(0xFF4CAF50)
        val CategoryIconBackgroundLoan = Color(0xFFEF5350)
        val CategoryIconBackgroundFood = Color(0xFF26A69A)
        val DividerColor = Color(0x1AFFFFFF)
        val TabIndicatorColor = Color.White
        val TransactionItemBackground = Color(0xFF2C2C2E)

        // Colors for the TransactionNumpad component
        object NumpadColors {
            val ButtonBackgroundColor = Color(0xFF2C2C2E) // Dark gray button background
            val ButtonContentColor = Color.White // White text for numbers
            val OperatorContentColor = TextAccent // Green text/icons for operators
            val ActionBackgroundColor = TextAccent // Blue background for action button
            val DividerColor = Color.Black // Black divider between buttons
            val SaveButtonBackgroundColor = Color.DarkGray // Background for Save button
            val SaveButtonTextColor = Color.Gray // Text color for Save button
        }
    }
}

// Custom colors based on the screenshot
val ScreenBackground = Color(0xFF1F1F1F)
val CardBackground = Color(0xFF2C2C2E)
val TextPrimary = Color.White
val TextSecondary = Color(0xFFB0B0B0)
val TextAccent = Color(0xFF4CAF50)
val IconTint = Color.White
val BalanceColor = Color.White
val WalletIconBgOrange = Color(0xFFE67E22)
val WalletIconBgTeal = Color(0xFF1ABC9C)
val ChartPlaceholderColor = Color(0xFF3A3A3C)
val ReportHighlightColor = Color(0xFFE91E63)
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val greenIndicator = Color(0xFF00C853)
val darkSurface = Color(0xFF1E1E1E) // A slightly lighter dark for cards
val onDarkSurface = Color.White
val fabColor = Color(0xFF00C853) // Green FAB