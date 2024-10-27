// MainScreen.kt
package app.penny.presentation.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.penny.presentation.ui.components.PennyTopBar
import app.penny.presentation.ui.screens.NavItem
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator

@Composable
fun MainScreen() {

    // The main navigation
    Navigator(
        screen = remember { NavItem.items.first().screen }, // Set initial screen
    ) { navigator ->
        Scaffold(
            topBar = {
                PennyTopBar(
                    title = "Penny",
                )
            },
            bottomBar = {
                BottomNavigation {
                    NavItem.items.forEach { item ->
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
                                println(
                                    "Current screen: ${navigator.lastItem}"
                                )
                            }
                        )
                    }
                }
            },
            content = {


                //wrap the current screen in a column to apply "innerPadding"
                Column(
                    modifier = Modifier.padding(it)
                ) {
                    CurrentScreen()
                }
            }
        )
    }
}
