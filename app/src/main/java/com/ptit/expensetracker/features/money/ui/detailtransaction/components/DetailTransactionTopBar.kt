package com.ptit.expensetracker.features.money.ui.detailtransaction.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.ptit.expensetracker.R
import com.ptit.expensetracker.ui.theme.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailTransactionTopBar(
    onCloseClick: () -> Unit,
    onCopyClick: () -> Unit,
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onCloseClick) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onCopyClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_w_add),
                    contentDescription = "Copy",
                    tint = Color.White
                )
            }
            IconButton(onClick = onShareClick) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = Color.White
                )
            }
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = Color.White
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = AppColor.Dark.PrimaryColor.containerColor
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun DetailTransactionTopBarPreview() {
    DetailTransactionTopBar(
        onCloseClick = {},
        onCopyClick = {},
        onShareClick = {},
        onEditClick = {},
        onDeleteClick = {}
    )
}