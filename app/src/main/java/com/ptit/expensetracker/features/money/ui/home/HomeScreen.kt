package com.ptit.expensetracker.features.money.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ptit.expensetracker.R
import com.ptit.expensetracker.ui.theme.*
import com.ptit.expensetracker.features.money.data.data_source.local.model.WalletWithCurrencyEntity
import com.ptit.expensetracker.features.money.data.data_source.local.model.WalletEntity
import com.ptit.expensetracker.features.money.data.data_source.local.model.CurrencyEntity
import com.ptit.expensetracker.features.money.ui.navigation.screen.Screen
import com.ptit.expensetracker.features.money.ui.home.components.HomeReportTabs
import com.ptit.expensetracker.features.money.ui.home.components.MainTab
import com.ptit.expensetracker.features.money.ui.home.components.SpendingSubTab
import com.ptit.expensetracker.features.money.ui.home.components.TrendingSubTab
import com.ptit.expensetracker.utils.getDrawableResId

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    showBalanceSheet: Boolean = false,
    onDismissBalanceSheet: () -> Unit = {},
    showWeeklyExpenseSheet: Boolean = false,
    onDismissWeeklyExpenseSheet: () -> Unit = {}
) {
    val uiState by viewModel.viewState.collectAsState()
    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    HomeScreenContent(
        modifier = modifier
            .padding(top = insetsTop)
            .padding(horizontal = 16.dp),
        state = uiState,
        onToggleBalanceVisibility = {
            viewModel.processIntent(
                HomeScreenIntent.ToggleBalanceVisibility
            )
        },
        onSeeAllWalletsClick = {
            navController.navigate(Screen.MyWallets.route)
        },
        onAiChatClick = {
            navController.navigate(Screen.AiChat.route)
        },
        onSearchClick = {
            navController.navigate(Screen.SearchTransaction.route)
        },
        onSelectMainTab = { viewModel.processIntent(HomeScreenIntent.SelectMainTab(it)) },
        onSelectTrendingTab = { viewModel.processIntent(HomeScreenIntent.SelectTrendingTab(it)) },
        onSelectSpendingTab = { viewModel.processIntent(HomeScreenIntent.SelectSpendingTab(it)) }
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    state: HomeScreenState,
    onToggleBalanceVisibility: () -> Unit,
    onSeeAllWalletsClick: () -> Unit = {},
    onAiChatClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onSelectMainTab: (MainTab) -> Unit,
    onSelectTrendingTab: (TrendingSubTab) -> Unit,
    onSelectSpendingTab: (SpendingSubTab) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppColor.Light.PrimaryColor.containerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TopBalanceBar(
                balance = state.totalBalance,
                isVisible = state.isBalanceVisible,
                onToggleVisibility = onToggleBalanceVisibility,
                onAiChatClick = onAiChatClick,
                onSearchClick = onSearchClick
            )
            MyWalletsSection(
                wallets = state.wallets,
                onSeeAllClick = onSeeAllWalletsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ReportSectionHeader()
                StatisticsPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ) {
                    HomeReportTabs(
                        selectedMainTab = state.selectedMainTab,
                        onSelectMainTab = onSelectMainTab,
                        selectedTrendingTab = state.selectedTrendingTab,
                        onSelectTrendingTab = onSelectTrendingTab,
                        selectedSpendingTab = state.selectedSpendingTab,
                        onSelectSpendingTab = onSelectSpendingTab,
                        transactions = state.transactions,
                        totalSpent = state.totalSpent,
                        totalIncome = state.totalIncome,
                        currentSpent = state.currentSpent,
                        previousSpent = state.previousSpent,
                        currentMonthSpent = state.currentMonthSpent,
                        previousMonthSpent = state.previousMonthSpent,
                        currencySymbol = state.wallets.find { it.wallet.isMainWallet }?.currency?.symbol ?: "",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun TopBalanceBar(
    balance: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    onAiChatClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween // Đẩy các phần tử ra xa nhau
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Total balance",
                color = Color(0xFF505D6D), // Màu chữ phụ
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isVisible) balance else "••••••• đ", // Hiển thị hoặc ẩn số dư
                    color = Color.Black,
                    fontSize = 28.sp, // Kích thướ c lớn hơn
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier.size(24.dp)
                ) { // Nút ẩn/hiện
                    Icon(
                        imageVector = if (isVisible) Icons.Filled.AccountBox else Icons.Filled.AccountBox,
                        contentDescription = if (isVisible) "Hide balance" else "Show balance",
                        tint = Color.Black
                    )
                }
            }
        }

        Row() {
            // Icon chatbot
            IconButton(onClick = onAiChatClick) {
                Image(
                    painter = painterResource(id = R.drawable.ic_ai_chatbot),
                    contentDescription = "Chat Bot"
                )
            }
            // Icon tìm kiếm
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF1E2A36)
                )
            }
        }
    }
}

@Composable
fun MyWalletsSection(
    modifier: Modifier = Modifier,
    wallets: List<WalletWithCurrencyEntity>,
    onSeeAllClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "My Wallets",
                color = Color(0xFF1E2A36),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "See all",
                color = Color(0xFFFCA419),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onSeeAllClick() }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // LazyColumn để hiển thị danh sách ví
       LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(wallets.take(3)) { wallet ->
                WalletItem(wallet = wallet)
            }
        }
    }
}

@Composable
fun WalletItem(wallet: WalletWithCurrencyEntity) {
    // Define background colors based on wallet ID for this example
    val iconBackgroundColor = if (wallet.wallet.id == 1) WalletIconBgOrange else WalletIconBgTeal

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon của ví
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = getDrawableResId(LocalContext.current, wallet.wallet.icon)),
                contentDescription = wallet.wallet.walletName,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        // Tên ví
        Text(
            text = wallet.wallet.walletName,
            modifier = Modifier.weight(1f),
            color = Color(0xFF1E2A36),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        // Số dư của ví
        Text(
            text = String.format(
                "%,.0f %s",
                wallet.wallet.currentBalance,
                wallet.currency.currencyCode
            ),
            color = Color(0xFF1E2A36),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenContentPreview() {
    ExpenseTrackerTheme {
        // Create sample data for preview
        val sampleWallets = listOf(
            WalletWithCurrencyEntity(
                wallet = WalletEntity(
                    id = 1,
                    walletName = "Cash Wallet",
                    currentBalance = 1500000.0,
                    currencyId = 1,
                    isMainWallet = true
                ),
                currency = CurrencyEntity(
                    id = 1,
                    currencyCode = "VND",
                    currencyName = "Vietnamese Dong",
                    symbol = "₫"
                )
            ),
            WalletWithCurrencyEntity(
                wallet = WalletEntity(
                    id = 2,
                    walletName = "Bank Account",
                    currentBalance = 5000000.0,
                    currencyId = 1,
                    isMainWallet = false
                ),
                currency = CurrencyEntity(
                    id = 1,
                    currencyCode = "VND",
                    currencyName = "Vietnamese Dong",
                    symbol = "₫"
                )
            )
        )

        val previewState = HomeScreenState(
            totalBalance = "6,500,000 ₫",
            isBalanceVisible = true,
            wallets = sampleWallets
        )

        HomeScreenContent(
            state = previewState,
            onToggleBalanceVisibility = {},
            onSeeAllWalletsClick = {},
            onAiChatClick = {},
            onSearchClick = {},
            onSelectMainTab = {},
            onSelectTrendingTab = {},
            onSelectSpendingTab = {}
        )
    }
}

@Composable
fun ReportSectionHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Report this month",
            color = Color(0xFF2B3B48),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "See reports",
            color = Color(0xFFFCA419),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun StatisticsPlaceholder(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopCenter
    ) {
        content()
    }
}