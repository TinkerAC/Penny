package app.penny.feature.debugBoard

sealed class DashboardIntent {
    data object InsertRandomTransaction : DashboardIntent()
    data object UploadUpdatedLedgers : DashboardIntent()
    data object ClearUserData : DashboardIntent()
    data object DownloadUnsyncedLedgers : DashboardIntent()


}