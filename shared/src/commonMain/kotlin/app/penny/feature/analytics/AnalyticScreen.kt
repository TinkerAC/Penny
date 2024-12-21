// file: shared/src/commonMain/kotlin/app/penny/feature/analytics/AnalyticScreen.kt
package app.penny.feature.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.feature.analytics.component.AnalyticsTopBar
import app.penny.feature.analytics.component.CategoryPieChart
import app.penny.feature.analytics.component.AssetChangeTable
import app.penny.feature.analytics.component.IncomeExpenseTrendChartCard
import app.penny.presentation.ui.components.LedgerSelectDialog
import app.penny.presentation.ui.components.LoadingScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

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
                            .padding(bottom = 16.dp)
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
