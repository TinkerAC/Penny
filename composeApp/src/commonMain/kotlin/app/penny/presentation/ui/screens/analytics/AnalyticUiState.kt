package app.penny.presentation.ui.screens.analytics

import app.penny.domain.model.LedgerModel
import app.penny.domain.model.TransactionModel
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


data class AnalyticUiState(
    val selectedTab: AnalyticTab = AnalyticTab.Recent,
    val ledgers: List<LedgerModel> = emptyList(),
    val selectedLedger: LedgerModel? = null,
    val selectedYear: Int = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()
    ).year,
    val selectedYearMonth: YearMonth = YearMonth(
        Clock.System.now().toLocalDateTime(
            TimeZone.currentSystemDefault()
        ).year,
        Clock.System.now().toLocalDateTime(
            TimeZone.currentSystemDefault()
        ).monthNumber
    ),

    val startDate: LocalDate = LocalDate(1970, 1, 1),
    val endDate: LocalDate = LocalDate(2099, 12, 31),

    val transactions: List<TransactionModel> = emptyList(),
    val ledgerSelectionDialogVisible: Boolean = false
)

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

}