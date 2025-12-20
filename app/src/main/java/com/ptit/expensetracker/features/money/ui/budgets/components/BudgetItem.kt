@file:OptIn(ExperimentalMaterial3Api::class)

package com.ptit.expensetracker.features.money.ui.budgets.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptit.expensetracker.R
import com.ptit.expensetracker.ui.theme.AppColor
import com.ptit.expensetracker.ui.theme.TextMain
import com.ptit.expensetracker.ui.theme.TextSecondary

@Composable
fun BudgetItem(
    modifier: Modifier = Modifier,
    categoryIcon: Int,
    categoryName: String,
    amount: String,
    leftAmount: String,
    progress: Float
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColor.Light.PrimaryColor.cardColor
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category with icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = categoryIcon),
                        contentDescription = categoryName,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = categoryName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMain
                    )
                }

                // Amount
                Text(
                    text = amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMain
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Left amount text
            Text(
                text = stringResource(R.string.budgets_left_label, leftAmount),
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color(0xFFFCA419),
                trackColor = Color(0xFFE4E7EC),
                strokeCap = StrokeCap.Round,
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Today text
            Text(
                text = stringResource(R.string.budgets_today),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
} 