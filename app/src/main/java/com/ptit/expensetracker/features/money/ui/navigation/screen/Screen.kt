package com.ptit.expensetracker.features.money.ui.navigation.screen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Chat
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Splash       : Screen("splash",      "Splash",       Icons.Default.Home)
    data object Onboarding   : Screen("onboarding",  "Onboarding",   Icons.Default.Home)
    data object GoogleLogin  : Screen("google_login","Google Login", Icons.Default.AccountCircle)
    data object WalletSetup  : Screen("wallet_setup?allowSkip={allowSkip}","Setup Wallet",Icons.Default.AccountCircle) {
        fun createRoute(allowSkip: Boolean = false) = "wallet_setup?allowSkip=$allowSkip"
    }
    data object IconPicker    : Screen("icon_picker","Select Icon", Icons.Default.Add)
    data object Home        : Screen("home",        "Home",         Icons.Default.Home)
    data object Transactions:
        Screen("transactions", "Transactions", Icons.AutoMirrored.Filled.List)
    data object Budget      : Screen("budget",      "Budget",       Icons.Default.ShoppingCart)
    data object Account     : Screen("account",     "Account",      Icons.Default.AccountCircle)
    data object AddTransaction : Screen("add_transaction?transactionId={transactionId}", "Add",      Icons.Default.Add) {
        fun createRoute(transactionId: Int? = null) =
            if (transactionId != null) "add_transaction?transactionId=$transactionId" else "add_transaction"
    }
    data object Category    : Screen("category",    "Category",      Icons.Default.Add)
    data object MyWallets   : Screen("my_wallets",   "My Wallets",    Icons.Default.Add)
    data object AddWallet   : Screen("add_wallet?walletId={walletId}",   "Add Wallet",     Icons.Default.Add) {
        fun createRoute(walletId: Int? = null) =
            if (walletId != null) "add_wallet?walletId=$walletId" else "add_wallet"
    }
    data object Currency    : Screen("currency",    "Currency",      Icons.Default.Add)
    data object AddBudget   : Screen("add_budget?budgetId={budgetId}",   "Add Budget",     Icons.Default.Add) {
        fun createRoute(budgetId: Int? = null) =
            if (budgetId != null) "add_budget?budgetId=$budgetId" else "add_budget"
    }
    data object Contacts    : Screen("contacts",    "Contacts",      Icons.Default.AccountCircle)
    data object ChooseWallet: 
        Screen("choose_wallet?walletId={walletId}", "Choose Wallet", Icons.Default.Add) {
        fun createRoute(walletId: Int? = null) = 
            if (walletId != null) "choose_wallet?walletId=$walletId" else "choose_wallet"
    }
    data object EnterAmount : 
        Screen("enter_amount?currencyCode={currencyCode}&currencySymbol={currencySymbol}&currencyId={currencyId}", 
              "Enter Amount", 
              Icons.Default.Add) {
        fun createRoute(currencyId: Int, currencyCode: String, currencySymbol: String) = 
            "enter_amount?currencyId=$currencyId&currencyCode=$currencyCode&currencySymbol=$currencySymbol"
    }
    data object DetailTransaction :
        Screen("detail_transaction/{transactionId}", "Transaction Detail", Icons.Default.Add) {
        fun createRoute(transactionId: Int) = "detail_transaction/$transactionId"
    }

    data object TransferMoney :
        Screen("transfer_money?walletId={walletId}", "Transfer Money", Icons.Default.Add) {
        fun createRoute(walletId: Int? = null) = if (walletId != null) "transfer_money?walletId=$walletId" else "transfer_money"
    }
    data object BudgetDetails :
        Screen("budget_details/{budgetId}", "Budget Details", Icons.Default.ShoppingCart) {
        fun createRoute(budgetId: Int) = "budget_details/$budgetId"
    }
    data object BudgetTransactions :
        Screen("budget_transactions/{budgetId}", "Budget Transactions", Icons.AutoMirrored.Filled.List) {
        fun createRoute(budgetId: Int) = "budget_transactions/$budgetId"
    }
    
    // Search Transaction Screen
    data object SearchTransaction : Screen("search_transaction", "Search Transaction", Icons.Default.Search)
    
    // Debt Management Screen
    data object DebtManagement : Screen("debt_management", "Quản lý nợ", Icons.Default.Search)

    // Monthly Report Screen
    data object MonthlyReport : Screen("monthly_report", "Reports", Icons.Default.ShoppingCart)
    data object AiChat : Screen("ai_chat", "AI Chat", Icons.Default.Chat)
}