package app.penny.feature.analytics

import AnalyticsTopBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import app.penny.presentation.ui.components.LedgerSelectionDialog
import app.penny.presentation.ui.components.LoadingScreen
import app.penny.feature.analytics.component.AssetChangeTable
import app.penny.feature.analytics.component.CategoryPieChart
import app.penny.feature.analytics.component.IncomeExpenseTrendChartCard
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

class AnalyticScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AnalyticViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        // 为 Column 添加 Modifier.fillMaxSize()
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

            // 将 when 语句放入 Column 内
            when (uiState.isLoading) {
                true -> {
                    // 显示加载中
                    LoadingScreen(modifier = Modifier.fillMaxSize())
                }

                false -> {
                    // 显示图表
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            IncomeExpenseTrendChartCard(
                                xAxisData = uiState.incomeExpenseTrendChartData.xAxisData,
                                incomeValues = uiState.incomeExpenseTrendChartData.incomeValues,
                                expenseValues = uiState.incomeExpenseTrendChartData.expenseValues
                            )
                        }
                        item {
                            CategoryPieChart(
                                incomePieChartData = uiState.incomePieChartData,
                                expensePieChartData = uiState.expensePieChartData
                            )
                        }
                        item {

                            AssetChangeTable(
                                assetChangeData = uiState.assetChangeTableData
                            )
                        }
                        item {
//                            AssetTrendChart(
//                                xAxisData = uiState.assetTrendLineChartData.first,
//                                assetValues = uiState.assetTrendLineChartData.second
//                            )
                        }
                    }
                }
            }
        }

        if (uiState.ledgerSelectionDialogVisible) {
            // 显示账本选择对话框
            LedgerSelectionDialog(
                ledgers = uiState.ledgers,
                selectedLedger = uiState.selectedLedger,
                onLedgerSelected = { ledger ->
                    viewModel.handleIntent(AnalyticIntent.SelectLedger(ledger))
                },
                onDismiss = {
                    viewModel.handleIntent(AnalyticIntent.DismissLedgerSelectionDialog)
                }
            )
        }
    }
}