// AnalyticsTopBar.kt


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.core.utils.localDateNow
import app.penny.feature.analytics.AnalyticTab
import app.penny.feature.analytics.AnalyticUiState
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource
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
    val tabs = AnalyticTab.entries

    Surface(
        shadowElevation = 4.dp,
        shape = RectangleShape,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOf(selectedTab),
                edgePadding = 0.dp,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        text = { Text(stringResource(tab.displayNameStringResource)) }
                    )
                }
            }
            IconButton(
                onClick = onLedgerSelectionClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = stringResource(SharedRes.strings.switch_ledger),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
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
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
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
        Text(
            text =
            stringResource(SharedRes.strings.start_date)
        )
        DatePicker(
            selectedDate = startDate,
            onDateSelected = onStartDateSelected
        )
        Spacer(modifier = Modifier.height(8.dp))
        // 结束日期选择
        Text(text = stringResource(SharedRes.strings.end_date))
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
