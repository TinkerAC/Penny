package app.penny.feature.notification

import app.penny.core.data.repository.UserPreferenceRepository
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationViewModel(
    private val userPreferenceRepository: UserPreferenceRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        NotificationUiState()
    )
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        _uiState.value = NotificationUiState(
            notificationEnabled = userPreferenceRepository.getNotificationEnabled(),
            scheduledNotificationEnabled = userPreferenceRepository.getScheduledNotificationEnabled(),
            budgetReachedNotificationEnabled = userPreferenceRepository.getBudgetReachedNotificationEnabled()
        )
    }


    fun toggleNotification() {

        val currentNotificationEnabled = _uiState.value.notificationEnabled

        userPreferenceRepository.setNotificationEnabled(!currentNotificationEnabled)

        _uiState.update {
            it.copy(
                notificationEnabled = !currentNotificationEnabled
            )
        }


    }


    fun toggleScheduledNotification() {

        val currentScheduledNotificationEnabled = _uiState.value.scheduledNotificationEnabled


        userPreferenceRepository.setScheduledNotificationEnabled(!currentScheduledNotificationEnabled)

        _uiState.update {
            it.copy(
                scheduledNotificationEnabled = !currentScheduledNotificationEnabled
            )
        }
    }


    fun toggleBudgetReachedNotification() {

        val currentBudgetReachedNotificationEnabled =
            _uiState.value.budgetReachedNotificationEnabled

        userPreferenceRepository.setBudgetReachedNotificationEnabled(!currentBudgetReachedNotificationEnabled)

        _uiState.update {
            it.copy(
                budgetReachedNotificationEnabled = !currentBudgetReachedNotificationEnabled
            )
        }
    }


}
