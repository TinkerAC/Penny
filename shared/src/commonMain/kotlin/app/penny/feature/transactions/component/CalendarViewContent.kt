// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/components/CalendarViewContent.kt

package app.penny.feature.transactions.component

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.core.utils.getDaysInMonth
import app.penny.core.utils.localDateNow
import app.penny.feature.transactionDetail.TransactionDetailScreen
import app.penny.feature.transactions.TransactionUiState
import app.penny.presentation.ui.components.TransactionItem
import app.penny.shared.SharedRes
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import app.penny.feature.transactions.TransactionScreen


/**
 * The main content of the calendar view of [TransactionScreen], showing a grid of dates for the current month,
 * And the list of transactions for the selected date .
 */

@OptIn(ExperimentalUuidApi::class)
@Composable
fun CalendarViewContent(
    currentYearMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (Long) -> Unit,
    uiState: TransactionUiState,
) {

    val localNavigator = LocalNavigator.currentOrThrow
    // get the number of days in the month
    val daysInMonth = getDaysInMonth(currentYearMonth)

    // get the first day of the month
    val firstDayOfThisMonth = LocalDate(currentYearMonth.year, currentYearMonth.month, 1)
    // Monday=0, ..., Sunday=6
    val firstDayOfWeek = firstDayOfThisMonth.dayOfWeek.ordinal

    //prepare the list of dates to render, ensuring alignment by "week"
    val dates = buildList {
        //padding for the first week
        repeat(firstDayOfWeek) { add(null) }
        // add all the days in the month

        for (day in 1..daysInMonth) {
            add(LocalDate(currentYearMonth.year, currentYearMonth.month, day))
        }

        while (size % 7 != 0) {
            add(null)
        }
    }

    // header + calendar + list
    Column(modifier = Modifier.fillMaxWidth()) {
        // 1) Header
        CalendarHeader(
            currentMonth = currentYearMonth,
            onMonthChange = onMonthChange
        )

        // 2) Weekday labels
        val weekdayLabels = listOf(
            stringResource(SharedRes.strings.monday_short),
            stringResource(SharedRes.strings.tuesday_short),
            stringResource(SharedRes.strings.wednesday_short),
            stringResource(SharedRes.strings.thursday_short),
            stringResource(SharedRes.strings.friday_short),
            stringResource(SharedRes.strings.saturday_short),
            stringResource(SharedRes.strings.sunday_short),
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            weekdayLabels.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // 3) Calendar grid
        dates.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    // box for each date
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clickable(enabled = date != null) {
                                date?.let { onDateSelected(it) }
                            }.background(
                                color = if (uiState.selectedDate == date) {
                                    MaterialTheme.colorScheme.surfaceVariant
                                } else if (date == localDateNow()) {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            ).clip(MaterialTheme.shapes.small),

                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            val daySummary = uiState.calendarViewSummaryByDate[date]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${date.dayOfMonth}${if (date == localDateNow()) "\uD83D\uDCCD" else ""}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.87f
                                    )
                                )

                                // 收支概要 - 收入
                                daySummary?.income?.let { income ->
                                    Text(
                                        text = income.toPlainString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        // 收入用更明显的“正向颜色”来区分，也可使用 colorScheme.primary
                                        color = if (income > "0".toBigDecimal()) {
                                            /* 例如绿色 */ MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                                // 收支概要 - 支出
                                daySummary?.expense?.let { expense ->
                                    Text(
                                        text = (-expense).toPlainString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        // 支出用更明显的“负向颜色”来区分，也可使用 colorScheme.error
                                        color = if (-expense < "0".toBigDecimal()) {
                                            MaterialTheme.colorScheme.error
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4) Transaction list
        LazyColumn {
            uiState.calendarViewTransactionOfDate.forEach { transaction ->
                item {
                    TransactionItem(
                        transaction = transaction,
                        onClick = {
                            localNavigator.push(TransactionDetailScreen(transaction.uuid))
                        }
                    )
                }
            }
        }
    }
}

/**
 * The header of the calendar component, showing the current month and year,with buttons to navigate to the previous and next month.
 */
@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onMonthChange: (Long) -> Unit
) {
    val localDate = currentMonth.toLocalDate()
    val monthName = localDate.month
        .name.lowercase()
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
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(SharedRes.strings.previous_month),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        // 月份标题示例：2025年 January
        val monthText = "${currentMonth.year}-$monthName"


        Text(
            text = monthText,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        IconButton(onClick = { onMonthChange(1) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForward,
                contentDescription = stringResource(SharedRes.strings.next_month),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
