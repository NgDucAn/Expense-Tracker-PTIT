package com.ptit.expensetracker.features.money.ui.addbudget

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ptit.expensetracker.features.money.domain.model.Category
import com.ptit.expensetracker.features.money.domain.model.CategoryType
import com.ptit.expensetracker.features.money.domain.model.Currency
import com.ptit.expensetracker.features.money.domain.model.Wallet
import com.ptit.expensetracker.features.money.ui.addbudget.components.AmountItem
import com.ptit.expensetracker.features.money.ui.addbudget.components.CategoryItem
import com.ptit.expensetracker.features.money.ui.addbudget.components.DatePickerItem
import com.ptit.expensetracker.features.money.ui.addbudget.components.RepeatBudgetItem
import com.ptit.expensetracker.features.money.ui.addbudget.components.SaveButton
import com.ptit.expensetracker.features.money.ui.addbudget.components.WalletPickerItem
import com.ptit.expensetracker.features.money.ui.addtransaction.components.NumpadButton
import com.ptit.expensetracker.features.money.ui.addtransaction.components.TransactionNumpad
import com.ptit.expensetracker.ui.theme.AppColor
import java.util.Calendar
import java.util.Date

// Enum to distinguish which date is being picked
private enum class DatePickerType { START, END }

@Composable
fun AddBudgetScreen(
    viewModel: AddBudgetViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToSelectCategory: () -> Unit,
    onNavigateToSelectDate: () -> Unit,
    onNavigateToSelectWallet: () -> Unit,
    onShowError: (String) -> Unit
) {
    val state by viewModel.viewState.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var categoryIdToOverride by remember { mutableStateOf(0) }
    // Date range picker state
    var showDatePicker by remember { mutableStateOf(false) }
    var datePickerType by remember { mutableStateOf(DatePickerType.START) }
    val context = LocalContext.current

    // Determine which date to show initially
    val calendarInstance = Calendar.getInstance().apply {
        time = if (datePickerType == DatePickerType.START) state.startDate else state.endDate
    }

    // Show native date picker for start or end date
    if (showDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val pickedDate = Calendar.getInstance().apply { set(year, month, dayOfMonth, 0, 0, 0) }.time
                if (datePickerType == DatePickerType.START) {
                    viewModel.processIntent(AddBudgetIntent.SelectStartDate(pickedDate))
                } else {
                    viewModel.processIntent(AddBudgetIntent.SelectEndDate(pickedDate))
                }
                showDatePicker = false
            },
            calendarInstance.get(Calendar.YEAR),
            calendarInstance.get(Calendar.MONTH),
            calendarInstance.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is AddBudgetEvent.NavigateBack -> onNavigateBack()
                is AddBudgetEvent.NavigateToSelectCategory -> onNavigateToSelectCategory()
                is AddBudgetEvent.NavigateToSelectDate -> onNavigateToSelectDate()
                is AddBudgetEvent.NavigateToSelectWallet -> onNavigateToSelectWallet()
                is AddBudgetEvent.ShowError -> onShowError(event.message)
                is AddBudgetEvent.BudgetSaved -> { /* Success handling */ }
                is AddBudgetEvent.ShowInfo -> { /* Show info message */ }
                is AddBudgetEvent.ShowConfirmOverride -> {
                    categoryIdToOverride = event.categoryId
                    showConfirmDialog = true
                }
                null -> { /* No event */ }
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Budget Already Exists") },
            text = { Text("A budget for this category already exists. Do you want to override it?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.processIntent(AddBudgetIntent.ConfirmOverride)
                    }
                ) {
                    Text("Yes, Override")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    AddBudgetScreenContent(
        state = state,
        onCategoryClick = { 
            viewModel.processIntent(AddBudgetIntent.NavigateToSelectCategory)
        },
        onAmountClick = { 
            viewModel.processIntent(AddBudgetIntent.ToggleNumpad)
        },
        onNumpadButtonClick = { button ->
            viewModel.processIntent(AddBudgetIntent.NumpadButtonPressed(button))
        },
        // Pick start date
        onStartDateClick = {
            datePickerType = DatePickerType.START
            showDatePicker = true
        },
        // Pick end date
        onEndDateClick = {
            datePickerType = DatePickerType.END
            showDatePicker = true
        },
        onWalletClick = { viewModel.processIntent(AddBudgetIntent.NavigateToSelectWallet) },
        onTotalClick = { viewModel.processIntent(AddBudgetIntent.ToggleTotal(true)) },
        onRepeatChange = { viewModel.processIntent(AddBudgetIntent.ToggleRepeat(it)) },
        onSaveClick = { viewModel.processIntent(AddBudgetIntent.SaveBudget) },
        onBackClick = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreenContent(
    state: AddBudgetState,
    onCategoryClick: () -> Unit,
    onAmountClick: () -> Unit,
    onNumpadButtonClick: (NumpadButton) -> Unit,
    onStartDateClick: () -> Unit,
    onEndDateClick: () -> Unit,
    onWalletClick: () -> Unit,
    onTotalClick: () -> Unit,
    onRepeatChange: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val backgroundColor = Color.Black
    val scrollState = rememberScrollState()
    val bottomSheetState = rememberModalBottomSheetState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Edit Budget" else "Add Budget", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColor.Dark.PrimaryColor.cardColor
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        CategoryItem(
                            category = state.selectedCategory,
                            onClick = onCategoryClick
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        AmountItem(
                            amount = state.amount,
                            amountText = state.formattedAmount,
                            displayExpression = state.displayExpression,
                            currencyCode = state.selectedWallet?.currency?.currencyCode ?: "VND",
                            onClick = onAmountClick
                        )
                    }
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = AppColor.Dark.PrimaryColor.cardColor
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        DatePickerItem(
                            label = "From Date",
                            date = state.startDate,
                            onClick = onStartDateClick
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DatePickerItem(
                            label = "To Date",
                            date = state.endDate,
                            onClick = onEndDateClick
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        WalletPickerItem(
                            wallet = state.selectedWallet,
                            onClick = onWalletClick
                        )
                        
//                        Spacer(modifier = Modifier.height(16.dp))
//
//                        TotalItem(
//                            onClick = onTotalClick
//                        )
                    }
                }
                
                RepeatBudgetItem(
                    isRepeating = state.isRepeating,
                    onCheckedChange = onRepeatChange
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                SaveButton(
                    onClick = onSaveClick
                )
            }
            
            // Show numpad in bottom sheet when activated
            if (state.showNumpad) {
                ModalBottomSheet(
                    onDismissRequest = { onAmountClick() },
                    sheetState = bottomSheetState,
                    containerColor = Color.Transparent
                ) {
                    TransactionNumpad(
                        onButtonClick = onNumpadButtonClick,
                        showSaveButton = false
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddBudgetScreenPreview() {
    val previewCategory = Category(
        id = 1,
        metaData = "food",
        title = "Food & Drinks",
        icon = "🍔",
        type = CategoryType.EXPENSE
    )
    
    val previewCurrency = Currency(
        id = 1,
        currencyName = "Vietnamese Dong",
        currencyCode = "VND",
        symbol = "₫"
    )
    
    val previewWallet = Wallet(
        id = 1,
        walletName = "Main Wallet",
        currentBalance = 1000000.0,
        currency = previewCurrency,
        isMainWallet = true
    )
    
    val previewState = AddBudgetState(
        selectedCategory = previewCategory,
        amount = 500000.0,
        amountInput = "500000",
        formattedAmount = "500,000",
        displayExpression = "250,000 + 250,000",
        startDate = Date(),
        endDate = Date(),
        isRepeating = false,
        selectedWallet = previewWallet,
        isTotal = false,
        availableWallets = listOf(previewWallet)
    )
    
    AddBudgetScreenContent(
        state = previewState,
        onCategoryClick = {},
        onAmountClick = {},
        onNumpadButtonClick = {},
        onStartDateClick = {},
        onEndDateClick = {},
        onWalletClick = {},
        onTotalClick = {},
        onRepeatChange = {},
        onSaveClick = {},
        onBackClick = {}
    )
} 