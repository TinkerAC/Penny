// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/TransactionScreen.kt
package app.penny.feature.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.feature.transactions.component.CalendarViewContent
import app.penny.feature.transactions.component.GroupByBottomAppBar
import app.penny.feature.transactions.component.ListViewContent
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class TransactionScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<TransactionViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val rootNavigator = LocalNavigator.currentOrThrow
        var isCalendarView by remember { mutableStateOf(false) }

        val currentMonth = remember {
            mutableStateOf(
                YearMonth(
                    Clock.System.todayIn(TimeZone.currentSystemDefault()).year,
                    Clock.System.todayIn(TimeZone.currentSystemDefault()).monthNumber
                )
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("交易记录") },
                    actions = {
                        //navigation icon

                        IconButton(onClick = { rootNavigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = "返回"
                            )
                        }

                        IconButton(onClick = { isCalendarView = !isCalendarView }) {
                            Icon(
                                imageVector = if (isCalendarView) Icons.Filled.List else Icons.Filled.CalendarToday,
                                contentDescription = "切换视图"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                if (!isCalendarView) {
                    GroupByBottomAppBar(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier =
                Modifier
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (isCalendarView) {
                    CalendarViewContent(
                        transactions = uiState.transactions,
                        currentYearMonth = currentMonth.value,
                        onDateSelected = { date ->
                            viewModel.handleIntent(TransactionIntent.SelectDate(date))
                        },
                        onMonthChange = { offset ->
                            currentMonth.value = currentMonth.value.plusMonths(offset)
//                            viewModel.fetchTransactionsForMonth(currentMonth.value)
                        }
                    )
                } else {
                    ListViewContent(
                        viewModel = viewModel,
                        uiState = uiState,
                    )
                }
            }
        }
    }
}
