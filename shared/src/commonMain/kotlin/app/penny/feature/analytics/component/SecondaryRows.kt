package app.penny.feature.analytics.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.core.utils.localDateNow


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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "选择月份",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
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
