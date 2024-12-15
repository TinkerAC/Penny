package app.penny.feature.analytics

import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.valueObject.YearMonth
import kotlinx.datetime.LocalDate

sealed class AnalyticIntent {
    data class OnTabSelected(val tab: AnalyticTab) : AnalyticIntent()
    data class OnYearSelected(val year: Int) : AnalyticIntent()
    data class OnYearMonthSelected(val yearMonth: YearMonth) : AnalyticIntent()
    data class OnStartDateSelected(val date: LocalDate) : AnalyticIntent()
    data class OnEndDateSelected(val date: LocalDate) : AnalyticIntent()

    object ShowLedgerSelectionDialog : AnalyticIntent()
    object DismissLedgerSelectionDialog : AnalyticIntent()
    data class SelectLedger(val ledger: LedgerModel) : AnalyticIntent()
}