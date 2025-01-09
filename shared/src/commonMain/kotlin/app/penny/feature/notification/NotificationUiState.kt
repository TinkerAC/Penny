package app.penny.feature.notification

data class NotificationUiState(
    val notificationEnabled: Boolean = false,
    val scheduledNotificationEnabled: Boolean = false,
    val budgetReachedNotificationEnabled: Boolean = false,
)

