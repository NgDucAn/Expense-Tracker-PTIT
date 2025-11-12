package com.ptit.expensetracker.features.money.ui.navigation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.ptit.expensetracker.features.money.domain.model.Currency
import com.ptit.expensetracker.features.money.ui.account.AccountScreen
import com.ptit.expensetracker.features.money.ui.addwallet.AddWalletScreen
import com.ptit.expensetracker.features.money.ui.budgets.BudgetScreen
import com.ptit.expensetracker.features.money.ui.currency.CurrencyScreen
import com.ptit.expensetracker.features.money.ui.enteramount.EnterAmountScreen
import com.ptit.expensetracker.features.money.ui.home.HomeScreen
import com.ptit.expensetracker.features.money.ui.navigation.screen.Screen
import com.ptit.expensetracker.features.money.ui.transactions.TransactionScreen
import com.ptit.expensetracker.features.money.ui.addtransaction.AddTransactionScreen
import com.ptit.expensetracker.features.money.ui.category.CategoryScreen
import com.ptit.expensetracker.features.money.ui.contacts.ContactsScreen
import com.ptit.expensetracker.features.money.ui.detailtransaction.DetailTransactionScreen

import com.ptit.expensetracker.features.money.ui.mywallets.MyWalletsScreen
import com.ptit.expensetracker.features.money.ui.transfermoney.TransferMoneyScreen
import com.ptit.expensetracker.features.money.ui.choosewallet.ChooseWalletScreen
import com.ptit.expensetracker.features.money.ui.addbudget.AddBudgetScreen
import com.ptit.expensetracker.features.money.ui.addbudget.AddBudgetViewModel
import com.ptit.expensetracker.features.money.domain.model.Category
import com.ptit.expensetracker.features.money.domain.model.Wallet
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.navDeepLink
import com.ptit.expensetracker.features.money.ui.addbudget.AddBudgetIntent
import com.ptit.expensetracker.ui.theme.AppColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.graphics.Color
import com.ptit.expensetracker.features.money.ui.addwallet.AddWalletViewModel
import com.ptit.expensetracker.ui.theme.AppColor.Dark.SecondaryColor.color1
import com.ptit.expensetracker.features.money.ui.onboarding.splash.SplashScreen
import com.ptit.expensetracker.features.money.ui.onboarding.walletsetup.WalletSetupScreen
import com.ptit.expensetracker.features.money.ui.onboarding.iconpicker.IconPickerScreen
import com.ptit.expensetracker.features.money.ui.budgetdetails.BudgetDetailsScreen
import com.ptit.expensetracker.features.money.ui.budgets.transactions.BudgetTransactionsScreen
import com.ptit.expensetracker.features.money.ui.search.SearchTransactionsScreen
import com.ptit.expensetracker.features.money.ui.debtmanagement.DebtManagementScreen

// 1. Define Navigation Destinations

// List of items for the bottom bar (excluding Add, handled by FAB)
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Transactions,
    Screen.Budget,
    Screen.Account,
)

// 3. Main App Navigation Composable
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    shouldNavigateToAddTransaction: MutableState<Boolean>,
    incomingIntent: MutableState<Intent?>,
    showBalanceSheet: MutableState<Boolean>,
    onDismissBalanceSheet: () -> Unit,
    showWeeklyExpenseSheet: MutableState<Boolean>,
    onDismissWeeklyExpenseSheet: () -> Unit
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Check if we're on a full-screen page that should hide bottom nav
    val isFullScreenPage =
            currentRoute == Screen.AddTransaction.route ||
            currentRoute == Screen.DetailTransaction.route ||
            currentRoute == Screen.EnterAmount.route ||
            currentRoute == Screen.TransferMoney.route ||
            currentRoute == Screen.MyWallets.route ||
            currentRoute == Screen.AddWallet.route ||
            currentRoute == Screen.Currency.route ||
            currentRoute == Screen.Category.route ||
            currentRoute == Screen.AddBudget.route ||
            currentRoute == Screen.Contacts.route ||
            currentRoute == Screen.ChooseWallet.route ||
            currentRoute == Screen.Splash.route ||
            currentRoute == Screen.WalletSetup.route ||
            currentRoute == Screen.IconPicker.route ||
            currentRoute == Screen.BudgetDetails.route ||
            currentRoute == Screen.BudgetTransactions.route ||
            currentRoute == Screen.SearchTransaction.route ||
            currentRoute == Screen.DebtManagement.route

    LaunchedEffect(shouldNavigateToAddTransaction.value) {
        if (shouldNavigateToAddTransaction.value) {
            navController.navigate(Screen.AddTransaction.createRoute())
            shouldNavigateToAddTransaction.value = false
        }
    }

    LaunchedEffect(incomingIntent.value) {
        incomingIntent.value?.let { intent ->
            // Handle OPEN_APP_FEATURE intent
            if (intent.action == Intent.ACTION_VIEW && intent.hasExtra("feature")) {
                val feature = intent.getStringExtra("feature")

                // Navigate to Add Transaction screen through deep link
                val openFeatureRoute =
                    if (feature?.equals("show_add_transaction", ignoreCase = true) == true) {
//                    android.net.Uri.parse("expensetracker://add_transaction")
                        Screen.AddTransaction.route
                    } else if (feature?.equals("show_all_wallets", ignoreCase = true) == true) {
//                    android.net.Uri.parse("expensetracker://show_my_wallets")
                        Screen.MyWallets.route
                    } else if (feature?.equals("show_list_transaction", ignoreCase = true) == true) {
                        Screen.Transactions.route
                    } else if (feature?.equals("show_budget_screen", ignoreCase = true) == true) {
                        Screen.Budget.route
                    } else if (feature?.equals("show_account_screen", ignoreCase = true) == true) {
                        Screen.Account.route
                    } else if (feature?.equals("show_currency_screen", ignoreCase = true) == true) {
                        Screen.Currency.route
                    } else if (feature?.equals("show_debt_management_screen", ignoreCase = true) == true) {
                        Screen.DebtManagement.route
                    } else {
                        // Default to Home screen if no valid feature is provided
//                    android.net.Uri.parse("expensetracker://home")
                        Screen.Home.route
                    }
//
//                val deepLinkIntent = Intent(Intent.ACTION_VIEW, openFeatureUri)
//                navController.handleDeepLink(deepLinkIntent)

                navController.createDeepLink()
                    .setDestination(openFeatureRoute)
                    .createTaskStackBuilder()
                    .startActivities()
            }

            incomingIntent.value = null
        }
    }

    Scaffold(
        bottomBar = {
            // Only show the bottom bar when not on full-screen pages
            AnimatedVisibility(
                visible = !isFullScreenPage,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = color1
                    )
                    NavigationBar(
                        containerColor = AppColor.Dark.PrimaryColor.containerColor,
                        contentColor = AppColor.Dark.PrimaryColor.contentColor
                    ) {
                        val currentDestination = currentBackStackEntry?.destination

                        // Navigation items before center Add button
                        bottomNavItems.take(2).forEach { screen ->
                            AppBottomNavigationItem(
                                screen = screen,
                                currentDestination = currentDestination,
                                navController = navController,
                            )
                        }

                        // Center Add button
                        Box(
                            modifier = Modifier.padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { navController.navigate(Screen.AddTransaction.createRoute()) },
                                modifier = Modifier
                                    .background(
                                        color = AppColor.Dark.PrimaryColor.TextButtonColor,
                                        shape = CircleShape
                                    )
                                    .padding(2.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Add Expense",
                                    tint = AppColor.Dark.PrimaryColor.contentColor
                                )
                            }
                        }

                        // Navigation items after center Add button
                        bottomNavItems.drop(2).forEach { screen ->
                            AppBottomNavigationItem(
                                screen = screen,
                                currentDestination = currentDestination,
                                navController = navController
                            )
                        }
                    }
                }
            }
        },
        containerColor = AppColor.Dark.PrimaryColor.containerColor,
    ) { innerPadding ->
        // Apply padding only when not on full-screen pages
        val contentModifier = if (isFullScreenPage) {
            Modifier.fillMaxSize()
        } else {
            Modifier.padding(innerPadding)
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(navController = navController)
            }
            composable(Screen.WalletSetup.route) {
                WalletSetupScreen(navController = navController)
            }
            composable(Screen.IconPicker.route) {
                IconPickerScreen(navController = navController)
            }
            composable(route = Screen.Home.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "expensetracker://home"
                    }
                )
            ) {
                HomeScreen(
                    modifier = Modifier,
                    navController = navController,
                    showBalanceSheet = showBalanceSheet.value,
                    onDismissBalanceSheet = onDismissBalanceSheet,
                    showWeeklyExpenseSheet = showWeeklyExpenseSheet.value,
                    onDismissWeeklyExpenseSheet = onDismissWeeklyExpenseSheet
                )
            }
            composable(Screen.Transactions.route) {
                TransactionScreen(
                    navController = navController,
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                )
            }
            composable(Screen.Budget.route) { backStackEntry ->
                BudgetScreen(
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                    savedStateHandle = backStackEntry.savedStateHandle,
                    navController = navController,
                    onCreateBudgetClick = { navController.navigate(Screen.AddBudget.createRoute()) },
                    onHowToUseClick = { /* Handle how to use click */ }
                )
            }
            composable(
                route = Screen.BudgetDetails.route,
                arguments = listOf(navArgument("budgetId") { type = NavType.IntType })
            ) { backStackEntry ->
                val budgetId = backStackEntry.arguments?.getInt("budgetId") ?: return@composable
                BudgetDetailsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onEditBudget = { navController.navigate(Screen.AddBudget.createRoute(budgetId)) },
                    onShowTransactions = { navController.navigate(Screen.BudgetTransactions.createRoute(budgetId)) },
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                )
            }
            composable(
                route = Screen.BudgetTransactions.route,
                arguments = listOf(navArgument("budgetId") { type = NavType.IntType })
            ) { backStackEntry ->
                val budgetId = backStackEntry.arguments?.getInt("budgetId") ?: return@composable
                BudgetTransactionsScreen(
                    budgetId = budgetId,
                    navController = navController,
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                )
            }
            composable(Screen.Account.route) { 
                AccountScreen(
                    modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
                    onNavigateToDebts = { navController.navigate(Screen.DebtManagement.route) },
                    onNavigateToWallets = { navController.navigate(Screen.MyWallets.route) },
                    onNavigateToCategories = { navController.navigate(Screen.Category.route) }
                ) 
            }
            composable(
                route = Screen.AddTransaction.route,
                arguments = listOf(
                    navArgument("transactionId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                ),
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern =
                            "expensetracker://addexpense?amount={amount}&category={category}&date={date}"
                    },
                    navDeepLink {
                        uriPattern = "expensetracker://add_transaction"
                    },
                )
            ) { backStackEntry ->
                val amount = backStackEntry.arguments?.getString("amount")
                val category = backStackEntry.arguments?.getString("category")
                val date = backStackEntry.arguments?.getString("date")
                val transactionId = backStackEntry.arguments?.getInt("transactionId")?.takeIf { it != -1 }

                AddTransactionScreen(
                    onCloseClick = { navController.popBackStack() },
                    navController = navController,
                    transactionId = transactionId,
                    initialAmount = amount,
                    initialCategory = category,
                    initialDate = date
                )
            }
            composable(Screen.Category.route) {
                CategoryScreen(
                    onBackClick = { navController.popBackStack() },
                    onCategorySelected = { selectedCategory ->
                        // Always set the selected category in the previous back stack entry
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_category",
                            selectedCategory
                        )
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.MyWallets.route,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "expensetracker://show_my_wallets"
                    }
                )
            ) {
                MyWalletsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddWallet = { navController.navigate(Screen.AddWallet.createRoute()) },
                    onNavigateToWalletDetail = { walletId -> /* TODO: implement detail navigation */ },
                    onNavigateToEditWallet = { walletId ->
                        navController.navigate(Screen.AddWallet.createRoute(walletId))
                    },
                    onNavigateToTransferMoney = { walletId ->
                        navController.navigate(Screen.TransferMoney.createRoute(walletId))
                    }
                )
            }
            composable(
                route = Screen.AddWallet.route,
                arguments = listOf(navArgument("walletId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { backStackEntry ->
                // Obtain the ViewModel scoped to this backStackEntry so SavedStateHandle has walletId
                val viewModel: AddWalletViewModel = hiltViewModel(backStackEntry)
                AddWalletScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() },
                    navController = navController
                )
            }
            composable(Screen.Currency.route) {
                CurrencyScreen(
                    onBackClick = { navController.popBackStack() },
                    onCurrencySelected = { selectedCurrency ->
                        // Set the selected currency in the previous back stack entry
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_currency",
                            selectedCurrency
                        )
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Contacts.route) {
                ContactsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onDone = { selectedContacts ->
                        // Handle selected contacts - could set them in previous back stack entry
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_contacts",
                            selectedContacts
                        )
                    },
                    navController = navController
                )
            }
            composable(
                route = Screen.AddBudget.route,
                arguments = listOf(navArgument("budgetId") {
                    type = NavType.IntType
                    nullable = false
                    defaultValue = -1 // Using -1 as a marker for null
                })
            ) { backStackEntry ->
                val viewModel = hiltViewModel<AddBudgetViewModel>(backStackEntry)

                // Observe for selected category from CategoryScreen
                navController.currentBackStackEntry?.savedStateHandle?.get<Category>("selected_category")
                    ?.let { category ->
                        LaunchedEffect(category) {
                            viewModel.processIntent(AddBudgetIntent.SelectCategory(category))
                            // Remove the value to avoid processing it multiple times
                            navController.currentBackStackEntry?.savedStateHandle?.remove<Category>(
                                "selected_category"
                            )
                        }
                    }

                // Observe for selected wallet from ChooseWalletScreen
                val selectedWallet =
                    navController.currentBackStackEntry?.savedStateHandle?.get<Wallet>("selected_wallet")
                val isTotal =
                    navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>("is_total_wallet")
                        ?: false

                LaunchedEffect(selectedWallet) {
                    selectedWallet?.let {
                        viewModel.processIntent(AddBudgetIntent.SelectWallet(it))
                        if (isTotal) {
                            viewModel.processIntent(AddBudgetIntent.ToggleTotal(true))
                        }
                        // Remove the values to avoid processing them multiple times
                        navController.currentBackStackEntry?.savedStateHandle?.remove<Wallet>(
                            "selected_wallet"
                        )
                        navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("is_total_wallet")
                    }
                }

                AddBudgetScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSelectCategory = { navController.navigate(Screen.Category.route) },
                    onNavigateToSelectDate = { /* TODO: Implement date picker navigation */ },
                    onNavigateToSelectWallet = {
                        // Pass current selection (wallet ID or -1 for total) to pre-select
                        val state = viewModel.viewState.value
                        val paramId = if (state.isTotal) -1 else state.selectedWallet?.id
                        navController.navigate(Screen.ChooseWallet.createRoute(paramId))
                    },
                    onShowError = { errorMessage ->
                        /* TODO: Implement error handling, such as showing a snackbar */
                    }
                )
            }
            composable(
                route = Screen.ChooseWallet.route,
                arguments = listOf(
                    navArgument("walletId") {
                        type = NavType.IntType
                        nullable = false
                        defaultValue = -1 // Using -1 as a marker for null
                    }
                )
            ) { backStackEntry ->
                val walletId = backStackEntry.arguments?.getInt("walletId")
                // Keep raw walletId (−1 indicates All Wallets)
                val initialWalletId = walletId

                android.util.Log.d(
                    "Navigation",
                    "Opening ChooseWalletScreen with initialWalletId: $initialWalletId"
                )
                // Get previous entry to determine where to navigate back to
                val previousRoute = navController.previousBackStackEntry?.destination?.route
                android.util.Log.d("Navigation", "Previous route: $previousRoute")

                ChooseWalletScreen(
                    initialWalletId = initialWalletId,
                    showTotalWallet = !isFromAddTransaction(previousRoute),
                    onWalletSelected = { selectedWallet, isTotalWallet ->
                        // Set both the wallet ID and the wallet object in the previous back stack entry
                        android.util.Log.d(
                            "Navigation",
                            "Wallet selected in Navigation: ${selectedWallet.id} - ${selectedWallet.walletName}, isTotalWallet: $isTotalWallet"
                        )
                        android.util.Log.d(
                            "Navigation",
                            "Setting wallet in previous entry: $previousRoute"
                        )

                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_wallet_id",
                            selectedWallet.id
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "is_total_wallet",
                            isTotalWallet
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selected_wallet",
                            selectedWallet
                        )

                        // Let the ChooseWalletScreen handle navigation back
                    },
                    onNavigateBack = {
                        // Navigate back - ensure we get back to the correct screen
                        if (previousRoute == Screen.Transactions.route) {
                            android.util.Log.d("Navigation", "Navigating back to Transactions")
                            navController.popBackStack()
                        } else if (previousRoute == Screen.AddBudget.route) {
                            android.util.Log.d("Navigation", "Navigating back to Add Budget")
                            navController.popBackStack()
                        } else {
                            android.util.Log.d(
                                "Navigation",
                                "Previous route is neither Transactions nor AddBudget, simple popBackStack"
                            )
                            navController.popBackStack()
                        }
                    },
                    navController = navController
                )
            }
            composable(
                route = Screen.EnterAmount.route,
                arguments = listOf(
                    navArgument("currencyId") {
                        type = NavType.IntType
                        defaultValue = 0
                    },
                    navArgument("currencyCode") {
                        type = NavType.StringType
                        defaultValue = "USD"
                    },
                    navArgument("currencySymbol") {
                        type = NavType.StringType
                        defaultValue = "$"
                    }
                )
            ) { backStackEntry ->
                val currencyId = backStackEntry.arguments?.getInt("currencyId") ?: 0
                val currencyCode = backStackEntry.arguments?.getString("currencyCode") ?: "USD"
                val currencySymbol = backStackEntry.arguments?.getString("currencySymbol") ?: "$"

                // Create Currency object from arguments
                val currency = Currency(
                    id = currencyId,
                    currencyName = "", // We don't need this for the amount screen
                    currencyCode = currencyCode,
                    symbol = currencySymbol
                )

                EnterAmountScreen(
                    onNavigateBack = { amount, formattedAmount ->
                        // Set both raw and formatted amounts in the previous back stack entry
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "entered_amount", amount
                        )
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "formatted_amount", formattedAmount
                        )
                        navController.popBackStack()
                    },
                    initialCurrency = currency
                )
            }
            composable(
                route = Screen.DetailTransaction.route,
                arguments = listOf(
                    navArgument("transactionId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
                DetailTransactionScreen(
                    transactionId = transactionId,
                    navController = navController
                )
            }

            composable(
                route = Screen.TransferMoney.route,
                arguments = listOf(
                    navArgument("walletId") {
                        type = NavType.IntType
                        defaultValue = 0
                    }
                )
            ) { backStackEntry ->
                val walletId = backStackEntry.arguments?.getInt("walletId")
                TransferMoneyScreen(
                    navController = navController,
                    walletId = walletId
                )
            }
            composable(Screen.SearchTransaction.route) {
                SearchTransactionsScreen(
                    navController = navController
                )
            }
            composable(Screen.DebtManagement.route) {
                DebtManagementScreen(
                    navController = navController,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// Helper function to check if coming from AddTransaction screen
private fun isFromAddTransaction(previousRoute: String?): Boolean {
    return previousRoute?.startsWith("add_transaction") == true
}

// Helper composable for creating each Bottom Navigation Item
@Composable
fun RowScope.AppBottomNavigationItem(
    screen: Screen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        icon = { Icon(screen.icon, contentDescription = screen.label) },
        label = { Text(screen.label, fontSize = 10.sp) },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = {
            // Navigate to the selected screen
            navController.navigate(screen.route) {
                // Pop up to the start destination, avoiding building up a large back stack
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true // Save state of screens
                }
                // Avoid multiple copies of the same destination when reselecting
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = AppColor.Dark.PrimaryColor.contentColor,
            selectedTextColor = AppColor.Dark.PrimaryColor.contentColor,
            unselectedIconColor = AppColor.Dark.PrimaryColor.containerColorSecondary,
            unselectedTextColor = AppColor.Dark.PrimaryColor.containerColorSecondary,
            indicatorColor = Color.Transparent
        )
    )
}