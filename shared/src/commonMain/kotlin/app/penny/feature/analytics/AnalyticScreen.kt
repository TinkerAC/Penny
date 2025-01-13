// file: shared/src/commonMain/kotlin/app/penny/feature/analytics/AnalyticScreen.kt
package app.penny.feature.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.penny.feature.analytics.component.ChartCard
import app.penny.feature.analytics.component.chart.CategoryPieChartContent
import app.penny.feature.analytics.component.chart.IncomeExpenseChartContent
import app.penny.feature.analytics.component.table.AssetDailyChangeContent
import app.penny.feature.analytics.component.topbar.AnalyticsTopBar
import app.penny.presentation.ui.components.LedgerSelectDialog
import app.penny.presentation.ui.components.LoadingScreen
import app.penny.presentation.ui.components.SliderToggleButton
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import dev.icerock.moko.resources.compose.stringResource

class AnalyticScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AnalyticViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.refreshData()
        }

        Column(modifier = Modifier.fillMaxSize()) {
            AnalyticsTopBar(
                uiState = uiState,
                onTabSelected = { tab ->
                    viewModel.handleIntent(AnalyticIntent.OnTabSelected(tab))
                },
                onYearSelected = { year ->
                    viewModel.handleIntent(AnalyticIntent.OnYearSelected(year))
                },
                onYearMonthSelected = { yearMonth ->
                    viewModel.handleIntent(AnalyticIntent.OnYearMonthSelected(yearMonth))
                },
                onStartDateSelected = { date ->
                    viewModel.handleIntent(AnalyticIntent.OnStartDateSelected(date))
                },
                onEndDateSelected = { date ->
                    viewModel.handleIntent(AnalyticIntent.OnEndDateSelected(date))
                },
                onLedgerSelectionClick = {
                    viewModel.handleIntent(AnalyticIntent.ShowLedgerSelectionDialog)
                }
            )

            when {
                uiState.isLoading -> {
                    LoadingScreen(modifier = Modifier.fillMaxSize())
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        item {
                            ChartCard(
                                title = stringResource(SharedRes.strings.income_expense_trend),
                                chartContent = {
                                    IncomeExpenseChartContent(
                                        incomeExpenseTrendChartData = uiState.incomeExpenseTrendChartData
                                    )
                                }

                            )
                        }
                        item {
                            var isExpense by remember { mutableStateOf(false) }
                            ChartCard(
                                title = stringResource(SharedRes.strings.category_pie_chart),
                                chartContent = {
                                    CategoryPieChartContent(
                                        incomePieChartData = uiState.incomePieChartData,
                                        expensePieChartData = uiState.expensePieChartData,
                                        isExpense = isExpense
                                    )
                                },
                                actions = {
                                    SliderToggleButton(
                                        options = listOf(
                                            stringResource(SharedRes.strings.expense),
                                            stringResource(SharedRes.strings.income),
                                        ),
                                        selectedIndex = if (isExpense) 0 else 1,
                                        onToggle = {
                                            // 切换 isExpense 的状态
                                            isExpense = !isExpense
                                        },
                                        unselectedBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.12f
                                        ),
                                    )
                                }
                            )
                        }

                        item {

                            ChartCard(
                                title = stringResource(SharedRes.strings.asset_daily_change),
                                chartContent = {
                                    AssetDailyChangeContent(
                                        assetChangeData = uiState.assetChangeTableData
                                    )
                                }
                            )
                        }
                        // 可以根据需要添加更多图表或表格
                    }
                }
            }
        }

        if (uiState.ledgerSelectionDialogVisible) {
            LedgerSelectDialog(
                onLedgerSelected = { ledger ->
                    viewModel.handleIntent(AnalyticIntent.SelectLedger(ledger))
                },
                onDismissRequest = {
                    viewModel.handleIntent(AnalyticIntent.DismissLedgerSelectionDialog)
                },
                allLedgers = uiState.ledgers,
                currentLedger = uiState.selectedLedger!!
            )
        }
    }
}
