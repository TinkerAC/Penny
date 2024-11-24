package app.penny.feature.dashboard

sealed class DashboardIntent {
    data object InsertRandomTransaction : DashboardIntent()
    data object UploadUpdatedLedgers : DashboardIntent()
    data object ClearUserData : DashboardIntent()
}