// MainScreen.kt
package app.penny.presentation.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.presentation.ui.components.PennyTopBar
import app.penny.presentation.ui.screens.BottomNavItem
import app.penny.presentation.viewmodel.MainViewModel
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScreenTransition
import co.touchlab.kermit.Logger

@OptIn(ExperimentalVoyagerApi::class)
class MainScreen : Screen, ScreenTransition {

    @Composable
    override fun Content() {

        val viewModel = koinScreenModel<MainViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        Navigator(
            screen = remember { BottomNavItem.items.first().screen },
        ) { navigator ->

            Scaffold(
                topBar = {
                    PennyTopBar(
                        title = "Penny",
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
                        uiState = uiState,
                        onLedgerSelected = { ledger ->
                            Logger.d("Selected ledger: $ledger")
                        }
                    )
                },
                bottomBar = {
                    BottomNavigation(
                        modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
                        elevation = 8.dp
                    ) {
                        BottomNavItem.items.forEach { item ->
                            val isSelected = navigator.lastItem == item.screen
                            BottomNavigationItem(
                                icon = { Icon(item.icon, contentDescription = item.route) },
                                label = { Text(item.title) },
                                selected = isSelected,
                                onClick = {
                                    if (!isSelected) {
                                        // Replace the entire stack with the new screen
                                        navigator.replaceAll(item.screen)
                                    }
                                    Logger.d(
                                        "Current screen: ${navigator.lastItem}"
                                    )
                                }
                            )
                        }
                    }
                },
                content = { paddingValues ->
                    Column(
                        modifier = Modifier.padding(paddingValues)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        CurrentScreen() // 显示当前的子屏幕
                    }
                }
            )
        }
    }

    override fun enter(lastEvent: StackEvent): EnterTransition? {
        return null
    }
}
