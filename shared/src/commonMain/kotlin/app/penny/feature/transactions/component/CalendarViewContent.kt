// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/components/CalendarViewContent.kt
package app.penny.feature.transactions.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.core.utils.getDaysInMonth
import app.penny.feature.transactions.TransactionUiState
import app.penny.presentation.ui.components.TransactionItem
import kotlinx.datetime.LocalDate

@Composable
fun CalendarViewContent(
    currentYearMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (Long) -> Unit,
    uiState: TransactionUiState,
) {
    // 正确获取当月的总天数
    val daysInMonth = getDaysInMonth(currentYearMonth)
    // 或者如果 YearMonth.toLocalDate() 返回第一天
    // val daysInMonth = currentMonth.toLocalDate().daysInMonth

    val firstDayOfThisMonth = LocalDate(currentYearMonth.year, currentYearMonth.month, 1)
    val firstDayOfWeek = firstDayOfThisMonth.dayOfWeek.ordinal // Monday=0 ... Sunday=6

    val dates = buildList {
        repeat(firstDayOfWeek) { add(null) }
        for (day in 1..daysInMonth) {
            add(LocalDate(currentYearMonth.year, currentYearMonth.month, day))
        }
        while (size % 7 != 0) {
            add(null)
        }
    }


    val transactionOfSelectedDate = uiState.calendarViewTransactionOfDate

    Column(modifier = Modifier.fillMaxWidth()) {
        // 移动 CalendarHeader 到这里，确保每次 CalendarViewContent 都有头部
        CalendarHeader(
            currentMonth = currentYearMonth,
            onMonthChange = onMonthChange
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("一", "二", "三", "四", "五", "六", "日").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        dates.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clickable(enabled = date != null) {
                                date?.let { onDateSelected(it) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                uiState.calendarViewSummaryByDate[date]?.income?.let {
                                    Text(
                                        text =
                                        it.toPlainString(),

                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                uiState.calendarViewSummaryByDate[date]?.expense?.let {
                                    Text(
                                        text =
                                        it.toPlainString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        LazyColumn {
            uiState.calendarViewTransactionOfDate.forEach { transaction ->
                item {
                    TransactionItem(
                        transaction = transaction,
                        onClick = {
                            //todo:navigate to detail page
                        }
                    )
                }
            }
        }
    }


}

@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onMonthChange: (Long) -> Unit
) {
    val monthName = currentMonth.toLocalDate().month.name.lowercase()
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onMonthChange(-1) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "上个月",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        val monthText = "${currentMonth.year}年 $monthName"
        Text(
            text = monthText,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(onClick = { onMonthChange(1) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                contentDescription = "下个月",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}