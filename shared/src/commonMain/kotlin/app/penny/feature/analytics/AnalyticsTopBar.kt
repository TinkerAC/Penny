// AnalyticsTopBar.kt


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.feature.analytics.AnalyticTab
import app.penny.feature.analytics.AnalyticUiState
import app.penny.feature.analytics.YearMonth
import app.penny.core.utils.localDateNow
import kotlinx.datetime.LocalDate


@Composable
fun AnalyticsTopBar(
    uiState: AnalyticUiState,
    onTabSelected: (AnalyticTab) -> Unit,
    onYearSelected: (Int) -> Unit,
    onYearMonthSelected: (YearMonth) -> Unit,
    onStartDateSelected: (LocalDate) -> Unit,
    onEndDateSelected: (LocalDate) -> Unit,
    onLedgerSelectionClick: () -> Unit
) {
    Column {
        TopTabRow(
            selectedTab = uiState.selectedTab,
            onTabSelected = onTabSelected,
            onLedgerSelectionClick = onLedgerSelectionClick
        )
        when (uiState.selectedTab) {
            AnalyticTab.Monthly -> {
                MonthlyTabContent(
                    selectedYearMonth = uiState.selectedYearMonth,
                    onYearMonthSelected = onYearMonthSelected,
                )
            }

            AnalyticTab.Yearly -> {
                YearlyTabContent(
                    selectedYear = uiState.selectedYear,
                    onYearSelected = onYearSelected
                )
            }

            AnalyticTab.Custom -> {
                CustomTabContent(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    onStartDateSelected = onStartDateSelected,
                    onEndDateSelected = onEndDateSelected
                )
            }

            else -> {
                // 对于Recent选项，无需额外操作
            }
        }
    }
}

@Composable
fun TopTabRow(
    selectedTab: AnalyticTab,
    onTabSelected: (AnalyticTab) -> Unit,
    onLedgerSelectionClick: () -> Unit
) {
    val tabs = listOf(
        AnalyticTab.Recent,
        AnalyticTab.Monthly,
        AnalyticTab.Yearly,
        AnalyticTab.Custom
    )

    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = { Text(tab.name) }
            )
        }
        Spacer(modifier = Modifier)
        IconButton(
            onClick =
            onLedgerSelectionClick

        ) {
            Icon(
                imageVector = Icons.Filled.NoteAlt,
                contentDescription = "Switch Ledger"
            )
        }
    }
}

@Composable
fun MonthlyTabContent(
    selectedYearMonth: YearMonth,
    onYearMonthSelected: (YearMonth) -> Unit
) {
    val currentYear = localDateNow().year
    val currentMonth = localDateNow().monthNumber

    //过去10年的YearMonth
    val yearMonths = (currentYear downTo currentYear - 10).flatMap { year ->
        if (year == currentYear) {
            // 当前年份，仅生成到当前月份
            (1..currentMonth).map { month -> YearMonth(year, month) }.reversed()
        } else {
            // 其他年份，生成所有12个月份f
            (1..12).map { month -> YearMonth(year, month) }.reversed()
        }
    }


    Row {
        Column(
            modifier = Modifier.padding(16.dp).weight(3f)
        )
        {
            Text(text = selectedYearMonth.year.toString())
        }
        Column(
            modifier = Modifier.padding(16.dp).weight(7f)
        ) {
            LazyRow(
//                modifier = Modifier.weight(7f) //TAKES ME 3DAYS to find this bug!!! weight can't be used in LazyRow

            ) {
                items(yearMonths) { yearMonth ->
                    YearMonthItem(
                        yearMonth = yearMonth,
                        isSelected = selectedYearMonth == yearMonth,
                        onClick = {
                            onYearMonthSelected(yearMonth)
                        })
                }
            }
        }

    }
}

@Composable
fun YearMonthItem(yearMonth: YearMonth, isSelected: Boolean, onClick: () -> Unit) {
    val month = yearMonth.month


    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = month.toString(),
//            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun YearlyTabContent(
    selectedYear: Int,
    onYearSelected: (Int) -> Unit
) {
    val currentYear = localDateNow().year
    val years = (currentYear downTo currentYear - 10).toList()

    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(years) { year ->
            YearItem(
                year = year,
                isSelected = selectedYear == year,
                onClick = { onYearSelected(year) }
            )
        }
    }
}

@Composable
fun YearItem(year: Int, isSelected: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(
            text = year.toString(),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun CustomTabContent(
    startDate: LocalDate,
    endDate: LocalDate,
    onStartDateSelected: (LocalDate) -> Unit,
    onEndDateSelected: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // 开始日期选择
        Text(text = "Start Date")
        DatePicker(
            selectedDate = startDate,
            onDateSelected = onStartDateSelected
        )
        Spacer(modifier = Modifier.height(8.dp))
        // 结束日期选择
        Text(text = "End Date")
        DatePicker(
            selectedDate = endDate,
            onDateSelected = onEndDateSelected
        )
    }
}

@Composable
fun DatePicker(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // 简化处理，使用TextButton模拟日期选择
    TextButton(onClick = {
        // 实际应用中，应使用日期选择器对话框
        onDateSelected(localDateNow())
    }) {
        Text(text = selectedDate.toString())
    }
}