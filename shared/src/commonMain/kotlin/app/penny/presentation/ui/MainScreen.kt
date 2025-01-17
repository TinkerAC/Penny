// MainScreen.kt
package app.penny.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.feature.aiChat.AIChatScreen
import app.penny.presentation.ui.components.SafeAreaBackgrounds
import app.penny.presentation.ui.screens.BottomNavItem
import app.penny.presentation.viewmodel.MainIntent
import app.penny.presentation.viewmodel.MainViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource

class MainScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<MainViewModel>()
        val uiState by viewModel.uiState.collectAsState()
        val rootNavigator = LocalNavigator.currentOrThrow

        SafeAreaBackgrounds(
            topColor = uiState.selectedNavigationItem.statusBarColor(),
            bottomColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Scaffold(
                bottomBar = {
                    BottomAppBarWithFAB(
                        items = uiState.navigationItems,
                        selectedItem = uiState.selectedNavigationItem,
                        onItemSelected = { _, item ->
                            viewModel.handleIntent(MainIntent.SelectBottomNavigationItem(item))
                        },
                        onFabClick = {
                            rootNavigator.push(AIChatScreen())
                        }
                    )
                },

                content = { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        uiState.selectedNavigationItem.screen.Content()
                    }
                }
            )
        }
    }
}

@Composable
fun BottomAppBarWithFAB(
    items: List<BottomNavItem>,
    selectedItem: BottomNavItem,
    onItemSelected: (Int, BottomNavItem) -> Unit,
    onFabClick: () -> Unit,
) {
    Box {
        // 底部导航栏
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.primary,
//            tonalElevation = 2.dp,
            modifier = Modifier
                .height(56.dp)
                .align(Alignment.BottomCenter)


        ) {
            items.forEachIndexed { index, item ->
                if (index == items.size / 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }

                println(item)
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.titleStringResource)
                        )
                    },
                    selected = item == selectedItem,
                    onClick = {
                        onItemSelected(index, item)
                    }
                )
            }
        }

        // 悬浮操作按钮
        FloatingActionButton(
            onClick = onFabClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-28).dp) // offset the FAB to be above the navigation bar
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "New UserIntent"
            )
        }
    }
}