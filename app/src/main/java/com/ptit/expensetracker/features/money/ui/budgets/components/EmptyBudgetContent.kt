package com.ptit.expensetracker.features.money.ui.budgets.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptit.expensetracker.R
import com.ptit.expensetracker.ui.theme.AppColor
import com.ptit.expensetracker.ui.theme.TextMain
import com.ptit.expensetracker.ui.theme.TextSecondary

/**
 * Empty state content for the Budget screen
 *
 * @param paddingValues PaddingValues from the parent Scaffold
 * @param onCreateBudgetClick Callback when the Create Budget button is clicked
 * @param onHowToUseClick Callback when the "How to use your budget" text is clicked
 */
@Composable
fun EmptyBudgetContent(
    paddingValues: PaddingValues,
    onCreateBudgetClick: () -> Unit,
    onHowToUseClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
//        // Illustration
//        Image(
//            painter = painterResource(id = R.drawable.ic_help_wallet_info),
//            contentDescription = "Empty Budget Illustration",
//            modifier = Modifier
//                .size(120.dp)
//                .padding(bottom = 24.dp),
//            contentScale = ContentScale.Fit
//        )
        
        // Title
        Text(
            text = "No Running Budgets",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextMain,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Description
        Text(
            text = "Start planning and tracking your expenses by creating a budget.",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 30.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Create Budget Button
        Button(
            onClick = onCreateBudgetClick,
            modifier = Modifier
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColor.Light.PrimaryColor.TextButtonColor,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Create Budget",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // How to use text
//        Text(
//            text = "How to use your budget",
//            fontSize = 14.sp,
//            color = Color.White,
//            textAlign = TextAlign.Center,
//            modifier = Modifier
//                .padding(vertical = 8.dp)
//                .clickable { onHowToUseClick() }
//        )
    }
} 