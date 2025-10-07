package com.duongpt.expensetracker.features.money.ui.addbudget.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duongpt.expensetracker.features.money.domain.model.Category
import com.duongpt.expensetracker.utils.getDrawableResId
import com.duongpt.expensetracker.utils.getStringResId

@Composable
fun CategoryItem(
    category: Category?,
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
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            if (category != null) {
                Image(
                    painter = painterResource(getDrawableResId(LocalContext.current, category.icon)),
                    contentDescription = null,
                )
            }
        }
        
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = category?.title?.let {
                 stringResource(id = getStringResId(LocalContext.current, it))
            } ?: "Select category",
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