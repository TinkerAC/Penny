package app.penny.feature.analytics

import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.valueObject.YearMonth
import com.aay.compose.donutChart.model.PieChartData
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
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

    val filteredTransactions: List<TransactionModel> = emptyList(),

    val incomeExpenseTrendChartData: IncomeExpenseTrendChartData = IncomeExpenseTrendChartData.empty,

    val incomePieChartData: List<PieChartData> = emptyList(),
    val expensePieChartData: List<PieChartData> = emptyList(),

    val assetChangeTableData: List<Pair<LocalDate, Triple<BigDecimal, BigDecimal, BigDecimal>>> = emptyList(),
    val assetTrendLineChartData: Pair<List<String>, List<Double>> = Pair(emptyList(), emptyList()),


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




data class IncomeExpenseTrendChartData(
    val xAxisData: List<String>,
    val incomeValues: List<Double>,
    val expenseValues: List<Double>
) {
    companion object {
        val empty = IncomeExpenseTrendChartData(emptyList(), emptyList(), emptyList())
    }
}