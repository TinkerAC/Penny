package app.penny.feature.notification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enumerate.Notification
import app.penny.feature.notification.component.NestedSwitchSetting
import app.penny.feature.setting.component.SettingSection
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class NotificationScreen : Screen {

    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<NotificationViewModel>()


        val uiState = viewModel.uiState.collectAsState()

        // 初始化时刷新数据
        LaunchedEffect(Unit) {
            viewModel.refreshData()
        }

        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = "Notifications",
                    onNavigateBack = {
                        rootNavigator.pop()
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).padding(16.dp)
            ) {
                SettingSection(
                    settingItems = listOf({
                        NestedSwitchSetting(
                            settingName = "Notifications",
                            checked = uiState.value.notificationEnabled,
                            onCheckedChange = {
                                viewModel.toggleNotification()
                            },
                            childSettings = listOf(
                                Notification.ScheduledNotification to uiState.value.scheduledNotificationEnabled,
                                Notification.BudgetReachedNotification to uiState.value.budgetReachedNotificationEnabled
                            ),
                            onChildCheckedChange = { notification, checked ->
                                when (notification) {
                                    Notification.ScheduledNotification -> viewModel.toggleScheduledNotification()
                                    Notification.BudgetReachedNotification -> viewModel.toggleBudgetReachedNotification()
                                }
                            }
                        )
                    })
                )
            }
        }
    }
}
