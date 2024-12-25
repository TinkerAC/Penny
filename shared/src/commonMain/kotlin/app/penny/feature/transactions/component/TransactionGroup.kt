// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/components/TransactionGroup.kt
package app.penny.feature.transactions.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.GroupIdentifier
import app.penny.core.domain.model.Summary
import app.penny.feature.transactions.GroupByType
import app.penny.feature.transactions.GroupedTransaction
import app.penny.presentation.ui.components.TransactionItem
import app.penny.presentation.utils.formatBigDecimal
import app.penny.presentation.utils.getLocalizedMonth
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TransactionGroup(group: GroupedTransaction) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize()
            .padding(horizontal = 8.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.clickable { isExpanded = !isExpanded }.padding(16.dp) // 增加间距
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                // 日期部分
                TransactionGroupIdentifier(
                    identifier = group.groupIdentifier
                )

                Spacer(modifier = Modifier.weight(1f))

                // 交易汇总 (收入、支出、余额)
                Summary(group.summary)

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier
                        .border(
                            width = (1.5f).dp, // 边框宽度
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), // 边框颜色
                            shape = RoundedCornerShape(4.dp) // 边框形状，可根据需要调整
                        )
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary, thickness = 2.dp
                )
                Spacer(modifier = Modifier.height(8.dp))

                group.transactions.forEach { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}

/**
 * 日期部分:
 * - 上行: 用 labelLarge 显示格式化后的日期 (如 "1st","2nd")
 * - 下行: 用 labelSmall + onSurface 较低的 alpha 显示年月 (如 "2024-12")
 */
@Composable
private fun TransactionGroupIdentifier(
    identifier: GroupIdentifier
) {

    when (identifier) {
        is GroupIdentifier.TimeGroupIdentifier -> {

            if (identifier.groupOption == GroupByType.Time.GroupOption.Year) {
                //年份
                Text(
                    text = identifier.year.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            } else {

                Column {
                    //the upper part of the identifier (1st/Q1/W01)
                    Text(
                        text = formatTimeIdentifierPart1(identifier),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Text(
                        text = identifier.year.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        is GroupIdentifier.CategoryGroupIdentifier -> {
            Icon(
                imageVector = identifier.category.categoryIcon,
                contentDescription = "Category",
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(identifier.category.categoryName),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * 汇总信息: 收入、支出、余额
 */
@Composable
private fun Summary(summary: Summary) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.End
    ) {
        // 余额
        Row(
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = stringResource(SharedRes.strings.balance_abbr),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = formatBigDecimal(summary.balance),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 收入 & 支出
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(SharedRes.strings.income_abbr),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = formatBigDecimal(summary.income),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            VerticalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), thickness = 2.dp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(SharedRes.strings.expense_abbr),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                // 将支出的颜色调整为一个更柔和但仍代表支出的色系（以 tertiary 为例）
                Text(
                    text = formatBigDecimal(summary.expense),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}


@Composable
private fun formatTimeIdentifierPart1(identifier: GroupIdentifier.TimeGroupIdentifier): String {
    when (identifier.groupOption) {
        GroupByType.Time.GroupOption.Day -> {
            return "${
                identifier.month?.let {
                    getLocalizedMonth(
                        month = it,
                        short = true
                    )
                }
            }-${
                when (identifier.day) {
                    1 -> "1st"
                    2 -> "2nd"
                    3 -> "3rd"
                    else -> "${identifier.day}th"
                }
            }"
        }

        GroupByType.Time.GroupOption.Week -> {
            return "W${identifier.weekOfYear}"
        }

        GroupByType.Time.GroupOption.Month -> {
            return "${
                identifier.month?.let {
                    getLocalizedMonth(
                        month = it,
                        short = true
                    )
                }
            }"
        }

        GroupByType.Time.GroupOption.Season -> {
            return "Q${identifier.quarter}"
        }


        //should not reach here
        GroupByType.Time.GroupOption.Year -> {
            TODO()
        }
    }

}
