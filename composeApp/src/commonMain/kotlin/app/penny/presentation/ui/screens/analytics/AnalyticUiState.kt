package app.penny.presentation.ui.screens.analytics

import app.penny.domain.model.LedgerModel
import app.penny.domain.model.TransactionModel
import app.penny.utils.getDaysInMonth
import app.penny.utils.localDateNow
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

data class AnalyticUiState(
    val isLoading: Boolean = false,
    val selectedTab: AnalyticTab = AnalyticTab.Recent,
    val ledgers: List<LedgerModel> = emptyList(),
    val selectedLedger: LedgerModel? = null,
    val selectedYear: Int = currentYear,
    val selectedYearMonth: YearMonth = currentYearMonth,
    val startDate: LocalDate = defaultStartDate,
    val endDate: LocalDate = defaultEndDate,
    val timeFilter: TimeFilter = TimeFilter.default,
    val allTransactions: List<TransactionModel> = emptyList(),
    val filteredTransactions: List<TransactionModel> = emptyList(),
    val incomeExpenseTrendChartData: IncomeExpenseTrendChartData = IncomeExpenseTrendChartData.empty,
    val ledgerSelectionDialogVisible: Boolean = false
) {
    companion object {
        private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        private val currentYear = now.year
        private val currentMonth = now.monthNumber
        val currentYearMonth = YearMonth(currentYear, currentMonth)

        val defaultStartDate: LocalDate = now.date.minus(DatePeriod(days = 7))
        val defaultEndDate: LocalDate = now.date
    }
}

enum class AnalyticTab {
    Recent,
    Monthly,
    Yearly,
    Custom
}

data class YearMonth(
    val year: Int,
    val month: Int
) {
    override fun toString(): String {
        return "$year-$month"
    }

    fun getLocalDateSequence(): List<LocalDate> {
        val daysInMonth = getDaysInMonth(year, month)

        return (1..daysInMonth step 2).map { LocalDate(year, month, it) }

    }
}

data class TimeFilter(
    val start: Instant,
    val end: Instant
) {
    override fun toString(): String {
        return "${start}-${end}"
    }

    companion object {
        val default: TimeFilter
            get() {
                val timeZone = TimeZone.currentSystemDefault()
                val now = localDateNow()
                val start = now.minus(DatePeriod(days = 7)).atStartOfDayIn(timeZone)
                val end = now.atStartOfDayIn(timeZone)
                return TimeFilter(start, end)
            }
    }
}

data class IncomeExpenseTrendChartData(
    val xAxisData: List<String>,
    val incomeValues: List<Double>,
    val expenseValues: List<Double>
) {
    companion object {
        val empty = IncomeExpenseTrendChartData(emptyList(), emptyList(), emptyList())
    }
}