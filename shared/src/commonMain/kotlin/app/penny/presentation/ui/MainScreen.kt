// MainScreen.kt
package app.penny.presentation.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.feature.transactions.TransactionScreen
import app.penny.presentation.ui.screens.BottomNavItem
import app.penny.presentation.viewmodel.MainViewModel
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.*

@OptIn(ExperimentalVoyagerApi::class)
class MainScreen : Screen {

    @Composable
    override fun Content() {

        val viewModel = koinScreenModel<MainViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        val rootNavigator = LocalNavigator.currentOrThrow

        Navigator(
            screen = remember { BottomNavItem.Dashboard.screen },
        ) { navigator ->

            val items = listOf(
                BottomNavItem.Dashboard,
                BottomNavItem.Analytics,
                BottomNavItem.Transactions,
                BottomNavItem.Profile
            )

            var selectedItem by remember { mutableStateOf(0) }

            Scaffold(
                topBar = {
                    // Implement a TopAppBar if needed
                },
                bottomBar = {
                    BottomAppBarWithFAB(
                        items = items,
                        selectedItem = selectedItem,
                        onItemSelected = { index, item ->
                            selectedItem = index
                            when (item) {
                                BottomNavItem.Dashboard -> navigator.replaceAll(item.screen)
                                BottomNavItem.Analytics -> navigator.replaceAll(item.screen)
                                BottomNavItem.Transactions -> rootNavigator.push(TransactionScreen())
                                BottomNavItem.Profile -> navigator.replaceAll(item.screen)
                            }
                        },
                        onFabClick = {
                            // Navigate to the new screen or perform action
                            rootNavigator.push(TransactionScreen())
                        }
                    )
                },
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        CurrentScreen()
                    }
                }
            )
        }
    }

}

@Composable
fun BottomAppBarWithFAB(
    items: List<BottomNavItem>,
    selectedItem: Int,
    onItemSelected: (Int, BottomNavItem) -> Unit,
    onFabClick: () -> Unit
) {
    Box {
        // Bottom navigation bar
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier
                .height(56.dp)
                .align(Alignment.BottomCenter)
        ) {
            items.forEachIndexed { index, item ->
                // Insert a spacer at the middle index to leave space for the FAB
                if (index == items.size / 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = when (item) {
                                BottomNavItem.Dashboard -> Icons.Filled.Home
                                BottomNavItem.Analytics -> Icons.Filled.PieChart
                                BottomNavItem.Transactions -> Icons.Filled.SwapHoriz
                                BottomNavItem.Profile -> Icons.Filled.Person
                                else -> Icons.Filled.Home
                            },
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    selected = selectedItem == index,
                    onClick = {
                        onItemSelected(index, item)
                    }
                )
            }
        }

        // Floating action button
        FloatingActionButton(
            onClick = onFabClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-28).dp) // Adjust the offset to position the FAB above the bar
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Action"
            )
        }
    }
}