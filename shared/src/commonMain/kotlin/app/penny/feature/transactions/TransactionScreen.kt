// File: shared/src/commonMain/kotlin/app/penny/feature/transactions/TransactionScreen.kt
package app.penny.feature.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.feature.transactions.component.CalendarViewContent
import app.penny.feature.transactions.component.GroupByBottomAppBar
import app.penny.feature.transactions.component.ListViewContent
import app.penny.presentation.ui.contentColorFor
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
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
                    title = {
                        Text(
                            stringResource(SharedRes.strings.transaction)
                        )
                    },
                    actions = {
                        //navigation icon

                        IconButton(onClick = { rootNavigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = stringResource(SharedRes.strings.back)
                            )
                        }

                        IconButton(onClick = { isCalendarView = !isCalendarView }) {
                            Icon(
                                imageVector = if (isCalendarView) Icons.AutoMirrored.Filled.List else Icons.Filled.CalendarToday,
                                contentDescription = stringResource(SharedRes.strings.toggle_view)
                            )
                        }

                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )

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
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
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
