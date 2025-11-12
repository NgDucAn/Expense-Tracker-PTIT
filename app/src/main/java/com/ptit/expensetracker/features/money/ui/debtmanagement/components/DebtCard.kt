package com.ptit.expensetracker.features.money.ui.debtmanagement.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptit.expensetracker.features.money.domain.model.DebtSummary
import com.ptit.expensetracker.features.money.domain.model.DebtCategoryMetadata
import com.ptit.expensetracker.features.money.domain.model.PaymentRecord
import com.ptit.expensetracker.ui.theme.*
import com.ptit.expensetracker.utils.formatAmount
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtSummaryCard(
    debtSummary: DebtSummary,
    onCardClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onCardClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColor.Dark.PrimaryColor.cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with person info and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Person info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar
                    PersonAvatar(
                        name = debtSummary.personName,
                        modifier = Modifier.size(40.dp)
                    )
                    
                    Column {
                        Text(
                            text = debtSummary.personName,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        Text(
                            text = if (DebtCategoryMetadata.PAYABLE_ORIGINAL.contains(debtSummary.originalTransaction.category.metaData)) {
                                "Bạn nợ"
                            } else {
                                "Nợ bạn"
                            },
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                
                // Status badge
                StatusBadge(
                    isFullyPaid = debtSummary.isPaid,
                    isPayable = DebtCategoryMetadata.PAYABLE_ORIGINAL.contains(debtSummary.originalTransaction.category.metaData)
                )
            }
            
            // Amount information
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Original and remaining amounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AmountInfo(
                        label = "Gốc",
                        amount = debtSummary.totalAmount,
                        currencySymbol = getCurrencySymbol(debtSummary),
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                    
                    AmountInfo(
                        label = if (DebtCategoryMetadata.PAYABLE_ORIGINAL.contains(debtSummary.originalTransaction.category.metaData)) {
                            "Còn nợ"
                        } else {
                            "Còn thu"
                        },
                        amount = debtSummary.remainingAmount,
                        currencySymbol = getCurrencySymbol(debtSummary),
                        textColor = if (DebtCategoryMetadata.PAYABLE_ORIGINAL.contains(debtSummary.originalTransaction.category.metaData)) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
                
                // Progress bar
                if (debtSummary.totalAmount > 0) {
                    val progressPercentage = debtSummary.progressPercentage
                    
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (DebtCategoryMetadata.PAYABLE_ORIGINAL.contains(debtSummary.originalTransaction.category.metaData)) {
                                    "Đã trả"
                                } else {
                                    "Đã thu"
                                },
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${(progressPercentage * 100).toInt()}%",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        LinearProgressIndicator(
                            progress = progressPercentage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = if (DebtCategoryMetadata.PAYABLE_ORIGINAL.contains(debtSummary.originalTransaction.category.metaData)) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
            
            // Last payment info
            debtSummary.lastPaymentDate?.let { lastPaymentDate ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Thanh toán cuối: ${formatDate(lastPaymentDate)}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    
                    if (debtSummary.paymentHistory.isNotEmpty()) {
                        Text(
                            text = "${debtSummary.paymentHistory.size} lần",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Payment button
                if (!debtSummary.isPaid) {
                    OutlinedButton(
                        onClick = onPaymentClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (DebtCategoryMetadata.PAYABLE_ORIGINAL.contains(debtSummary.originalTransaction.category.metaData)) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (DebtCategoryMetadata.PAYABLE_ORIGINAL.contains(debtSummary.originalTransaction.category.metaData)) {
                                "Trả nợ"
                            } else {
                                "Thu nợ"
                            },
                            fontSize = 12.sp
                        )
                    }
                }
                
                // History button
                OutlinedButton(
                    onClick = onHistoryClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.outline
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Lịch sử",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonAvatar(
    name: String,
    modifier: Modifier = Modifier
) {
    val initial = name.take(1).uppercase()
    
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatusBadge(
    isFullyPaid: Boolean,
    isPayable: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isFullyPaid) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            if (isPayable) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.secondaryContainer
            }
        }
    ) {
        Text(
            text = if (isFullyPaid) "Hoàn thành" else "Chưa hoàn thành",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = if (isFullyPaid) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                if (isPayable) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSecondaryContainer
                }
            },
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AmountInfo(
    label: String,
    amount: Double,
    currencySymbol: String,
    textColor: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${formatAmount(amount)} $currencySymbol",
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun getCurrencySymbol(debtSummary: DebtSummary): String {
    return if (debtSummary.paymentHistory.isNotEmpty()) {
        debtSummary.repaymentTransactions.first().wallet.currency.symbol
    } else {
        "đ"
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(date)
}

