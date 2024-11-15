package app.penny.presentation.ui.screens.analytics

import AnalyticsTopBar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.penny.presentation.ui.components.LedgerSelectionDialog
import app.penny.presentation.ui.components.LoadingScreen
import app.penny.presentation.ui.screens.analytics.chart.CategoryPieChart
import app.penny.presentation.ui.screens.analytics.chart.IncomeExpenseTrendChartCard
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

class AnalyticScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AnalyticViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        Column {
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

        }

        when (uiState.isLoading) {
            true -> {
                // 显示加载中
                LoadingScreen()
            }

            false -> {
                // 显示图表
                LazyColumn {
                    item {
//                        Text(text = "chartData: ${uiState.incomeExpenseTrendChartData}")
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