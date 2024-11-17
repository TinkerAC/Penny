package app.penny.presentation.ui.screens.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.penny.domain.model.TransactionModel
import app.penny.utils.getDaysInMonth
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn

class CalendarViewScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<TransactionViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        val currentMonth = remember {
            mutableStateOf(
                Clock.System.todayIn(TimeZone.currentSystemDefault())
            )

        }

        Column(modifier = Modifier.fillMaxSize()) {
            CalendarHeader(
                currentMonth = currentMonth.value,
                onMonthChange = { offset ->
                    currentMonth.value = currentMonth.value.plus(offset, DateTimeUnit.MONTH)
                }
            )
            CalendarView(
                transactions = uiState.transactions,
                currentMonth = currentMonth.value,
                onDateSelected = { date ->
                    viewModel.handleIntent(TransactionIntent.SelectDate(date))
                }
            )
            val selectedDate = uiState.selectedDate
            if (selectedDate != null) {
                Text(
                    text = "Transactions on ${selectedDate.toString()}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )
                val transactionsForSelectedDate = uiState.transactions.filter {
                    it.transactionDate.toLocalDateTime(TimeZone.currentSystemDefault()).date == selectedDate
                }
                TransactionListForDate(transactions = transactionsForSelectedDate)
            }
        }
    }
}

@Composable
fun CalendarHeader(currentMonth: LocalDate, onMonthChange: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onMonthChange(-1) }) {
            Icon(imageVector = Icons.Default.ArrowBackIos, contentDescription = "Previous Month")
        }
        Text(
            text = "${currentMonth.year} - ${currentMonth.month.name}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(onClick = { onMonthChange(1) }) {
            Icon(imageVector = Icons.Default.ArrowForwardIos, contentDescription = "Next Month")
        }
    }
}

@Composable
fun CalendarView(
    transactions: List<TransactionModel>,
    currentMonth: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = getDaysInMonth(currentMonth.year, currentMonth.monthNumber)
//    val firstDayOfWeek = currentMonth.withDayOfMonth(1).dayOfWeek.isoDayNumber % 7
    val firstDayOfWeek = currentMonth.dayOfWeek.isoDayNumber % 7

    val dates = buildList<LocalDate?> {
        // 添加空白日期以对齐星期
        repeat(firstDayOfWeek) {
            add(null)
        }
        // 添加当前月份的所有日期
        for (day in 1..daysInMonth) {
            add(LocalDate(currentMonth.year, currentMonth.monthNumber, day))
        }
        // 填充剩余的空白
        while (size % 7 != 0) {
            add(null)
        }
    }

    val incomeExpensesByDate = transactions.groupBy {
        it.transactionDate.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }.mapValues { entry ->
        val income = entry.value.filter { !it.amount.isNegative }
            .fold(BigDecimal.ZERO) { acc, transaction -> acc + transaction.amount }

        val expense = entry.value.filter { it.amount.isNegative }
            .fold(BigDecimal.ZERO) { acc, transaction -> acc + transaction.amount }
        Pair(income, expense)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // 星期标题
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // 日期网格
        dates.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clickable(enabled = date != null) {
                                date?.let { onDateSelected(it) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = date.dayOfMonth.toString())
                                val incomeExpense = incomeExpensesByDate[date]
                                if (incomeExpense != null) {
                                    Text(
                                        text = "In: ${incomeExpense.first}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                    Text(
                                        text = "Ex: ${incomeExpense.second}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionListForDate(transactions: List<TransactionModel>) {
    LazyColumn {
        items(transactions) { transaction ->
            TransactionItem(transaction)
        }
    }
}