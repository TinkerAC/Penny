package app.penny.presentation.ui.screens.analytics

import AnalyticsTopBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

class AnalyticScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<AnalyticViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        AnalyticsTopBar(
            uiState = uiState,
            onTabSelected = { tab ->
                viewModel.handleIntent(AnalyticIntent.OnTabSelected(tab))
            },
            onLedgerIconClicked = { /* 处理Ledger切换逻辑 */ },
            onYearSelected = { year -> viewModel.handleIntent(AnalyticIntent.OnYearSelected(year)) },
            onYearMonthSelected = { yearMonth ->
                viewModel.handleIntent(
                    AnalyticIntent.OnYearMonthSelected(
                        yearMonth
                    )
                )
            },
            onStartDateSelected = { date ->
                viewModel.handleIntent(
                    AnalyticIntent.OnStartDateSelected(
                        date
                    )
                )
            },
            onEndDateSelected = { date ->
                viewModel.handleIntent(
                    AnalyticIntent.OnEndDateSelected(
                        date
                    )
                )
            }
        )


    }
}

