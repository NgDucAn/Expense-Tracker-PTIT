package com.duongpt.expensetracker.features.money.ui.addtransaction.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.duongpt.expensetracker.ui.theme.AppColor



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionTopAppBar(
    title: String,
    onCloseClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontSize = 17.sp, fontWeight = FontWeight.Medium) },
        navigationIcon = {
            TextButton (onClick = onCloseClick, colors = ButtonDefaults.textButtonColors(
                contentColor = AppColor.Dark.PrimaryColor.contentColor,
                containerColor = Color.Transparent
            )) {
                Text(text = "Cancel", fontSize = 16.sp, fontWeight = FontWeight.Normal)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColor.Dark.PrimaryColor.containerColor,
            titleContentColor = AppColor.Dark.PrimaryColor.contentColor,
            navigationIconContentColor = AppColor.Dark.PrimaryColor.contentColor
        )
    )
}