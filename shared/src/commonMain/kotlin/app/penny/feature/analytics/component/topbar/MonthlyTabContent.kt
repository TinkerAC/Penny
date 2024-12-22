package app.penny.feature.analytics.component.topbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.core.utils.localDateNow
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource


@Composable
fun MonthlyTabContent(
    selectedYearMonth: YearMonth,
    onYearMonthSelected: (YearMonth) -> Unit
) {
    val currentDate = localDateNow()
    val currentYear = currentDate.year
    val currentMonth = currentDate.monthNumber

    // 过去10年的YearMonth
    val yearMonths = (currentYear downTo currentYear - 10).flatMap { year ->
        if (year == currentYear) {
            // 当前年份，仅生成到当前月份
            (1..currentMonth).map { month -> YearMonth(year, month) }.reversed()
        } else {
            // 其他年份，生成所有12个月份
            (1..12).map { month -> YearMonth(year, month) }.reversed()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Text(
            text = stringResource(SharedRes.strings.select_month),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            LazyRow(
                contentPadding = PaddingValues(
                    vertical = 8.dp,
                    horizontal = 16.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(yearMonths) { yearMonth ->
                    YearMonthCard(
                        yearMonth = yearMonth,
                        isSelected = selectedYearMonth == yearMonth,
                        onClick = { onYearMonthSelected(yearMonth) }
                    )
                }
            }
        }

    }
}

@Composable
fun YearMonthCard(
    yearMonth: YearMonth,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val monthName = yearMonth.month
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .width(100.dp)
            .height(60.dp)
    ) {
        Box(
            contentAlignment = androidx.compose.ui.Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Text(
                    text = monthName.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = yearMonth.year.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
