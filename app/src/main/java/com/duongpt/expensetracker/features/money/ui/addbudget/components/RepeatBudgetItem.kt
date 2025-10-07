package com.duongpt.expensetracker.features.money.ui.addbudget.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duongpt.expensetracker.ui.theme.AppColor

@Composable
fun RepeatBudgetItem(
    isRepeating: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppColor.Dark.PrimaryColor.cardColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isRepeating,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.Green,
                    uncheckedColor = Color.Gray
                )
            )
            
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "Repeat this budget",
                    color = Color.White,
                    fontSize = 16.sp
                )
                
                Text(
                    text = "Budget will renew automatically.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
} 