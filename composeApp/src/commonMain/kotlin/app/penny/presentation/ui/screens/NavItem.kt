// shared/src/commonMain/kotlin/app/penny/presentation/ui/screens/NavItem.kt

package app.penny.presentation.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

sealed class NavItem(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val screen: Screen
) {
    data object Dashboard : NavItem("dashboard", Icons.Filled.Home, "Dashboard", DashboardScreen())
    data object Transactions :
        NavItem("transactions", Icons.Filled.ThumbUp, "Transactions", TransactionScreen())

    data object Analytics : NavItem("analytics", Icons.Filled.Info, "Analytics", AnalyticsScreen())
    data object Profile : NavItem("profile", Icons.Filled.Person, "Profile", ProfileScreen())

    companion object {
        val items = listOf(Dashboard, Transactions, Analytics, Profile)
    }


}

val NavItemSaver: Saver<NavItem, String> = Saver(
    save = {
        it.route
    },
    restore = {
        NavItem.items.first { item -> item.route == it }
    }
)

