// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/components/TransactionGroup.kt
package app.penny.feature.transactions.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.TransactionModel
import app.penny.feature.transactions.GroupedTransaction
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun TransactionItem(transaction: TransactionModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标或文字说明
            Icon(
                imageVector = when (transaction.category.categoryName) {
                    "工资收入" -> Icons.Default.ArrowDownward // 示例图标，可根据需求更换
                    else -> Icons.Default.ArrowUpward
                },
                contentDescription = transaction.category.categoryName,
                tint = if (transaction.amount > BigDecimal.ZERO) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.category.categoryName, // 交易类型或名称
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTransactionTime(transaction.transactionInstant),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatAmount(transaction.amount),
                style = MaterialTheme.typography.titleMedium,
                color = if (transaction.amount > BigDecimal.ZERO) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}

private fun formatTransactionTime(timestamp: kotlinx.datetime.Instant): String {
    val localDateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDateTime.year}-${
        localDateTime.monthNumber.toString().padStart(2, '0')
    }-${localDateTime.dayOfMonth.toString().padStart(2, '0')} " +
            "${localDateTime.hour.toString().padStart(2, '0')}:${
                localDateTime.minute.toString().padStart(2, '0')
            }"
}

private fun formatAmount(amount: BigDecimal): String {
    return if (amount > BigDecimal.ZERO) {
        "+${amount.toPlainString()}"
    } else {
        "-${amount.abs().toPlainString()}"
    }
}

@Composable
fun TransactionGroup(group: GroupedTransaction) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Increased elevation,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable { isExpanded = !isExpanded }
                .padding(16.dp) // Increased padding for better spacing
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = group.groupKey,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            // Summary Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem(
                    label = "Balance",
                    value = group.balance.toPlainString(),
                    color = MaterialTheme.colorScheme.primary
                )
                SummaryItem(
                    label = "Income",
                    value = group.income.toPlainString(),
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                SummaryItem(
                    label = "Expense",
                    value = group.expense.toPlainString(),
                    color = MaterialTheme.colorScheme.errorContainer
                )
            }
            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 2.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                group.transactions.forEach { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}