package app.penny.presentation.ui.screens.dashboard

sealed class DashboardIntent {
    data object InsertRandomTransaction: DashboardIntent()
}