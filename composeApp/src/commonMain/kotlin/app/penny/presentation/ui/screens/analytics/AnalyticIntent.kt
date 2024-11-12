package app.penny.presentation.ui.screens.analytics

import app.penny.domain.model.LedgerModel
import kotlinx.datetime.LocalDate

sealed class AnalyticIntent {
    class OnTabSelected(val tab: AnalyticTab) : AnalyticIntent()
    class OnYearSelected(val year: Int) : AnalyticIntent()
    class OnYearMonthSelected(val yearMonth: YearMonth) : AnalyticIntent()
    class OnStartDateSelected(val date: LocalDate) : AnalyticIntent()
    class OnEndDateSelected(val date: LocalDate) : AnalyticIntent()


    class ShowLedgerSelectionDialog : AnalyticIntent()
    class DismissLedgerSelectionDialog : AnalyticIntent()
    class SelectLedger(val ledger: LedgerModel) : AnalyticIntent()

}