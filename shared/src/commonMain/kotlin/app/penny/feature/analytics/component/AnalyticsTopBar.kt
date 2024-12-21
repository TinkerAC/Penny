// AnalyticsTopBar.kt
package app.penny.feature.analytics.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.valueObject.YearMonth
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
    Column(modifier = Modifier.fillMaxWidth()) {
        TopTabRow(
            selectedTab = uiState.selectedTab,
            onTabSelected = onTabSelected,
            onLedgerSelectionClick = onLedgerSelectionClick
        )

        Spacer(modifier = Modifier.height(16.dp))

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
private fun TopTabRow(
    selectedTab: AnalyticTab,
    onTabSelected: (AnalyticTab) -> Unit,
    onLedgerSelectionClick: () -> Unit
) {
    val tabs = AnalyticTab.entries

    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.surface
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
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        text = {
                            Text(
                                text = stringResource(tab.displayNameStringResource),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }
            IconButton(
                onClick = onLedgerSelectionClick,
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 8.dp)
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
