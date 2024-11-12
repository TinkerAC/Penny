package app.penny.presentation.ui.screens.analytics

import AnalyticsTopBar
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.penny.presentation.ui.components.LedgerSelectionDialog
import app.penny.presentation.ui.screens.analytics.chart.IncomeExpenseTrendChart
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

class AnalyticScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AnalyticViewModel>()
        val uiState by viewModel.uiState.collectAsState()

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

        Column {
            // 显示图表
            if (uiState.chartData.xAxisData.isNotEmpty()) {
                IncomeExpenseTrendChart(
                    xAxisData = uiState.chartData.xAxisData,
                    incomeValues = uiState.chartData.incomeValues,
                    expenseValues = uiState.chartData.expenseValues
                )
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