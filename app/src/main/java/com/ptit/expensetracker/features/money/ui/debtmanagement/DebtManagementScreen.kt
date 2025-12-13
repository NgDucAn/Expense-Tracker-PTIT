package com.ptit.expensetracker.features.money.ui.debtmanagement

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ptit.expensetracker.R
import com.ptit.expensetracker.features.money.domain.model.DebtSummary
import com.ptit.expensetracker.features.money.domain.model.DebtType
import com.ptit.expensetracker.features.money.ui.debtmanagement.components.DebtStatsCard
import com.ptit.expensetracker.features.money.ui.debtmanagement.components.DebtSummaryCard
import com.ptit.expensetracker.features.money.ui.debtmanagement.components.DebtTab
import com.ptit.expensetracker.features.money.ui.debtmanagement.components.WalletSelector
import com.ptit.expensetracker.ui.theme.*
import com.ptit.expensetracker.utils.getDrawableResId
import com.ptit.expensetracker.utils.formatAmount
import com.ptit.expensetracker.features.money.domain.model.PaymentRecord
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtManagementScreen(
    modifier: Modifier = Modifier,
    viewModel: DebtManagementViewModel = hiltViewModel(),
    navController: NavController = rememberNavController(),
    onNavigateBack: () -> Unit = { navController.popBackStack() }
) {
    val uiState by viewModel.viewState.collectAsState()
    val context = LocalContext.current

    // Handle one-time events
    LaunchedEffect(viewModel) {
        viewModel.event.collect { event ->
            when (event) {
                is DebtManagementEvent.NavigateToDebtDetails -> {
                    // TODO: Navigate to debt details screen
                }
                is DebtManagementEvent.NavigateToAddPartialPayment -> {
                    // TODO: Navigate to add partial payment screen
                }
                is DebtManagementEvent.NavigateToPaymentHistory -> {
                    // TODO: Navigate to payment history screen
                }
                is DebtManagementEvent.ShowToast -> {
                    // TODO: Show toast message
                }
                is DebtManagementEvent.ShowError -> {
                    // TODO: Show error message
                }
                is DebtManagementEvent.ShowSuccessMessage -> {
                    // TODO: Show success message
                }
                is DebtManagementEvent.ShareDebtSummary -> {
                    // TODO: Share debt summary
                }
                is DebtManagementEvent.ExportDebtReport -> {
                    // TODO: Export debt report
                }

                else -> {}
            }
        }
    }

    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    DebtManagementScreenContent(
        modifier = modifier,
        state = uiState,
        onIntent = viewModel::processIntent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtManagementScreenContent(
    modifier: Modifier = Modifier,
    state: DebtManagementState,
    onIntent: (DebtManagementIntent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = AppColor.Light.PrimaryColor.containerColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Text(
                        text = "Quản lý nợ",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = IconTint
                        )
                    }
                },
                actions = {
                    // Wallet selector button
                    IconButton(
                        onClick = { onIntent(DebtManagementIntent.ShowWalletSelector) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_wallet),
                            contentDescription = "Select Wallet",
                            tint = IconTint
                        )
                    }
                    
                    // Filter button
                    IconButton(
                        onClick = { onIntent(DebtManagementIntent.ShowFilterDialog) }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sort),
                            contentDescription = "Filter",
                            tint = if (!state.filterOptions.isDefault()) MaterialTheme.colorScheme.primary else IconTint
                        )
                    }
                    
                    // Refresh button
                    IconButton(
                        onClick = { onIntent(DebtManagementIntent.RefreshData) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = IconTint
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColor.Light.PrimaryColor.containerColor
                )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Wallet selector info
                WalletInfoCard(
                    selectedWallet = state.selectedWallet,
                    currencySymbol = state.currencySymbol,
                    onWalletClick = { onIntent(DebtManagementIntent.ShowWalletSelector) }
                )

                // Debt tabs
                DebtTabRow(
                    selectedTab = state.selectedTab,
                    onTabSelected = { onIntent(DebtManagementIntent.SelectTab(it)) }
                )

                // Content based on selected tab
                when (state.selectedTab) {
                    DebtTab.PAYABLE -> {
                        PayableDebtContent(
                            state = state,
                            onIntent = onIntent
                        )
                    }
                    DebtTab.RECEIVABLE -> {
                        ReceivableDebtContent(
                            state = state,
                            onIntent = onIntent
                        )
                    }
                }
            }
        }

        // Loading overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // Error handling
        state.error?.let { error ->
            LaunchedEffect(error) {
                // TODO: Show error snackbar
            }
        }
    }

    // Wallet selector bottom sheet
    if (state.showWalletSelector) {
        WalletSelector(
            wallets = state.availableWallets,
            selectedWallet = state.selectedWallet,
            onWalletSelected = { onIntent(DebtManagementIntent.SelectWallet(it)) },
            onDismiss = { onIntent(DebtManagementIntent.HideWalletSelector) }
        )
    }

    // Filter dialog
    if (state.showFilterDialog) {
        // TODO: Implement filter dialog
    }

    // Payment bottom sheet
    if (state.showPaymentSheet && state.paymentTarget != null) {
        PaymentSheet(
            debt = state.paymentTarget,
            amountText = state.paymentAmountInput,
            noteText = state.paymentNoteInput,
            isSubmitting = state.paymentSubmitting,
            currencySymbol = state.currencySymbol,
            onAmountChange = { onIntent(DebtManagementIntent.UpdatePaymentAmount(it)) },
            onNoteChange = { onIntent(DebtManagementIntent.UpdatePaymentNote(it)) },
            onConfirm = { onIntent(DebtManagementIntent.ConfirmPayment) },
            onDismiss = { onIntent(DebtManagementIntent.DismissPaymentSheet) }
        )
    }

    // History bottom sheet
    if (state.showHistorySheet) {
        PaymentHistorySheet(
            title = state.historyTitle ?: "Lịch sử",
            items = state.historyItems,
            isLoading = state.historyLoading,
            error = state.historyError,
            debtType = state.historyDebtType,
            onDismiss = { onIntent(DebtManagementIntent.ClosePaymentHistory) }
        )
    }
}

@Composable
fun WalletInfoCard(
    selectedWallet: com.ptit.expensetracker.features.money.domain.model.Wallet?,
    currencySymbol: String,
    onWalletClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        onClick = onWalletClick,
        colors = CardDefaults.cardColors(
            containerColor = AppColor.Light.PrimaryColor.cardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Ví được chọn",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedWallet?.walletName ?: "Tất cả ví",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            selectedWallet?.let {
                Icon(
                    painter = painterResource(id = getDrawableResId(LocalContext.current, it.icon)),
                    contentDescription = "Selected Wallet",
                    tint = MaterialTheme.colorScheme.primary
                )
            } ?: run {
                Icon(
                    painter = painterResource(id = R.drawable.ic_wallet),
                    contentDescription = "Wallet",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun DebtTabRow(
    selectedTab: DebtTab,
    onTabSelected: (DebtTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        containerColor = AppColor.Light.PrimaryColor.cardColor,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        DebtTab.values().forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = tab.displayName,
                        fontSize = 14.sp,
                        fontWeight = if (selectedTab == tab) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            )
        }
    }
}

@Composable
fun PayableDebtContent(
    state: DebtManagementState,
    onIntent: (DebtManagementIntent) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Statistics card
        item {
            DebtStatsCard(
                debtType = DebtType.PAYABLE,
                totalAmount = state.totalPayableAmount,
                unpaidAmount = state.totalUnpaidPayable,
                totalCount = state.payableDebts.size,
                unpaidCount = state.unpaidPayableDebts.size,
                currencySymbol = state.currencySymbol
            )
        }

        // Unpaid debts section
        if (state.unpaidPayableDebts.isNotEmpty()) {
            item {
                SectionHeader(title = "Chưa trả (${state.unpaidPayableDebts.size})")
            }
            
            items(state.unpaidPayableDebts) { debt ->
                DebtSummaryCard(
                    debtSummary = debt,
                    onCardClick = { onIntent(DebtManagementIntent.ViewDebtDetails(debt)) },
                    onPaymentClick = { onIntent(DebtManagementIntent.AddPartialPayment(debt)) },
                    onHistoryClick = { onIntent(DebtManagementIntent.ViewPaymentHistory(debt)) }
                )
            }
        }

        // Paid debts section
        if (state.paidPayableDebts.isNotEmpty()) {
            item {
                SectionHeader(title = "Đã trả (${state.paidPayableDebts.size})")
            }
            
            items(state.paidPayableDebts) { debt ->
                DebtSummaryCard(
                    debtSummary = debt,
                    onCardClick = { onIntent(DebtManagementIntent.ViewDebtDetails(debt)) },
                    onPaymentClick = { onIntent(DebtManagementIntent.AddPartialPayment(debt)) },
                    onHistoryClick = { onIntent(DebtManagementIntent.ViewPaymentHistory(debt)) }
                )
            }
        }

        // Empty state
        if (state.payableDebts.isEmpty() && !state.isLoading) {
            item {
                EmptyDebtState(
                    debtType = DebtType.PAYABLE,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ReceivableDebtContent(
    state: DebtManagementState,
    onIntent: (DebtManagementIntent) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Statistics card
        item {
            DebtStatsCard(
                debtType = DebtType.RECEIVABLE,
                totalAmount = state.totalReceivableAmount,
                unpaidAmount = state.totalUnpaidReceivable,
                totalCount = state.receivableDebts.size,
                unpaidCount = state.unpaidReceivableDebts.size,
                currencySymbol = state.currencySymbol
            )
        }

        // Unpaid debts section
        if (state.unpaidReceivableDebts.isNotEmpty()) {
            item {
                SectionHeader(title = "Chưa thu (${state.unpaidReceivableDebts.size})")
            }
            
            items(state.unpaidReceivableDebts) { debt ->
                DebtSummaryCard(
                    debtSummary = debt,
                    onCardClick = { onIntent(DebtManagementIntent.ViewDebtDetails(debt)) },
                    onPaymentClick = { onIntent(DebtManagementIntent.AddPartialPayment(debt)) },
                    onHistoryClick = { onIntent(DebtManagementIntent.ViewPaymentHistory(debt)) }
                )
            }
        }

        // Paid debts section
        if (state.paidReceivableDebts.isNotEmpty()) {
            item {
                SectionHeader(title = "Đã thu (${state.paidReceivableDebts.size})")
            }
            
            items(state.paidReceivableDebts) { debt ->
                DebtSummaryCard(
                    debtSummary = debt,
                    onCardClick = { onIntent(DebtManagementIntent.ViewDebtDetails(debt)) },
                    onPaymentClick = { onIntent(DebtManagementIntent.AddPartialPayment(debt)) },
                    onHistoryClick = { onIntent(DebtManagementIntent.ViewPaymentHistory(debt)) }
                )
            }
        }

        // Empty state
        if (state.receivableDebts.isEmpty() && !state.isLoading) {
            item {
                EmptyDebtState(
                    debtType = DebtType.RECEIVABLE,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentSheet(
    debt: DebtSummary,
    amountText: String,
    noteText: String,
    isSubmitting: Boolean,
    currencySymbol: String,
    onAmountChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Thanh toán nợ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${debt.personName} • Còn lại ${formatAmount(debt.remainingAmount)} $currencySymbol",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = onAmountChange,
                label = { Text("Số tiền") },
                placeholder = { Text("Nhập số tiền cần thanh toán") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = noteText,
                onValueChange = onNoteChange,
                label = { Text("Ghi chú") },
                placeholder = { Text("Ví dụ: trả một phần") },
                singleLine = false,
                minLines = 2,
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss
                ) {
                    Text("Huỷ")
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onConfirm,
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Xác nhận")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentHistorySheet(
    title: String,
    items: List<PaymentRecord>,
    isLoading: Boolean,
    error: String?,
    debtType: DebtType?,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                error != null -> {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                items.isEmpty() -> {
                    Text(
                        text = "Chưa có lịch sử",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items, key = { it.transactionId }) { record ->
                            PaymentHistoryRow(
                                record = record,
                                debtType = debtType
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PaymentHistoryRow(
    record: PaymentRecord,
    debtType: DebtType?
) {
    val amountColor = when (debtType) {
        DebtType.PAYABLE -> MaterialTheme.colorScheme.error // Trả nợ = tiền ra
        DebtType.RECEIVABLE -> MaterialTheme.colorScheme.primary // Thu nợ = tiền vào
        else -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatDate(record.date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!record.description.isNullOrBlank()) {
                Text(
                    text = record.description ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = formatAmount(record.amount),
            style = MaterialTheme.typography.bodyMedium,
            color = amountColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(date)
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun EmptyDebtState(
    debtType: DebtType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (debtType) {
                DebtType.PAYABLE -> "Không có khoản nợ phải trả"
                DebtType.RECEIVABLE -> "Không có khoản nợ được nhận"
            },
            color = TextSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = when (debtType) {
                DebtType.PAYABLE -> "Tất cả khoản vay đã được thanh toán"
                DebtType.RECEIVABLE -> "Tất cả khoản cho vay đã được thu hồi"
            },
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
} 