// file: shared/src/commonMain/kotlin/app/penny/feature/transactions/TransactionScreen.kt
package app.penny.feature.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.feature.transactions.component.CalendarViewContent
import app.penny.feature.transactions.component.ListViewContent
import app.penny.feature.transactions.component.TransactionTopBar
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class TransactionScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<TransactionViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        var isCalendarView by remember { mutableStateOf(false) }

        val currentMonth = remember {
            mutableStateOf(
                YearMonth(
                    Clock.System.todayIn(TimeZone.currentSystemDefault()).year,
                    Clock.System.todayIn(TimeZone.currentSystemDefault()).monthNumber
                )
            )
        }

        LaunchedEffect(Unit) {
            viewModel.refreshData()
        }


        Scaffold(
            topBar = {
                TransactionTopBar(
                    isCalendarView = isCalendarView,
                    onToggleView = { isCalendarView = !isCalendarView },
                    selectedGroupByType = uiState.selectedGroupByType,
                    selectedGroupByOption = uiState.selectedGroupByOption
                        ?: GroupByType.Time.GroupOption.Month,
                    onGroupByOptionSelected = { groupByType, groupOption ->
                        viewModel.handleIntent(TransactionIntent.SelectGroupByOption(groupOption))
                    }
                )
            },
            // 移除BottomBar
        ) { innerPadding ->
            Box(
                modifier =
                Modifier
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (isCalendarView) {
                    CalendarViewContent(
                        currentYearMonth = currentMonth.value,
                        onDateSelected = { date ->
                            viewModel.handleIntent(TransactionIntent.SelectDate(date))
                        },
                        onMonthChange = { offset ->
                            currentMonth.value = currentMonth.value.plusMonths(offset)
                        }, uiState = uiState
                    )
                } else {
                    ListViewContent(
                        uiState = uiState,
                    )
                }
            }
        }
    }
}