package app.penny.feature.dashboard

sealed class DashboardIntent {
    data object InsertRandomTransaction : DashboardIntent()
}