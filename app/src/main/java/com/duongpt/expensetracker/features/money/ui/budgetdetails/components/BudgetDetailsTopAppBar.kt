package com.duongpt.expensetracker.features.money.ui.budgetdetails.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailsTopAppBar(
    onNavigateBack: () -> Unit,
    onEditBudget: () -> Unit,
    onDeleteBudget: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text("Budget details", color = Color.White) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        },
        actions = {
            IconButton(onClick = onEditBudget) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Budget", tint = Color.White)
            }
            IconButton(onClick = onDeleteBudget) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Budget", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF1C1C1E)
        ),
        modifier = modifier
    )
} 