// shared/src/commonMain/kotlin/app/penny/presentation/ui/screens/BottomNavItem.kt

package app.penny.presentation.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import app.penny.presentation.ui.screens.dashboard.DashboardScreen
import cafe.adriel.voyager.core.screen.Screen

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val screen: Screen
) {
    data object Dashboard :
        BottomNavItem("dashboard", Icons.Filled.Home, "Dashboard", DashboardScreen())

    data object Analytics :
        BottomNavItem("analytics", Icons.Filled.Info, "Analytics", AnalyticsScreen())

    data object Profile : BottomNavItem("profile", Icons.Filled.Person, "Profile", ProfileScreen())

    companion object {
        val items = listOf(Dashboard, Analytics, Profile)
    }
}


