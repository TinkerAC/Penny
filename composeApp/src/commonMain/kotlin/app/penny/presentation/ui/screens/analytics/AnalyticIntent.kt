package app.penny.presentation.ui.screens.analytics

import kotlinx.datetime.LocalDate

sealed class AnalyticIntent {
    class OnTabSelected(val tab: AnalyticTab) : AnalyticIntent()
    data object OnLedgerIconClicked : AnalyticIntent()

    class OnYearSelected(val year: Int) : AnalyticIntent()
    class OnYearMonthSelected(val yearMonth: YearMonth) : AnalyticIntent()
    class OnStartDateSelected(val date: LocalDate) : AnalyticIntent()
    class OnEndDateSelected(val date: LocalDate) : AnalyticIntent()
}