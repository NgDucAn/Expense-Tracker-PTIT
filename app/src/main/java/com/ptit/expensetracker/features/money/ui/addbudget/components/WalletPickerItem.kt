package com.ptit.expensetracker.features.money.ui.addbudget.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptit.expensetracker.R
import com.ptit.expensetracker.features.money.domain.model.Wallet

@Composable
fun WalletPickerItem(
    wallet: Wallet?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_wallet),
            contentDescription = "Wallet",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = wallet?.walletName ?: "Select wallet",
            color = Color.White,
            fontSize = 16.sp
        )
        
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Select",
            tint = Color.Gray
        )
    }
} 