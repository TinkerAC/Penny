// shared/src/commonMain/kotlin/app/penny/presentation/ui/screens/BottomNavItem.kt

package app.penny.presentation.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector
import app.penny.feature.analytics.AnalyticScreen
import app.penny.feature.dashboard.DebugScreen
import app.penny.feature.profile.ProfileScreen
import cafe.adriel.voyager.core.screen.Screen
import org.jetbrains.compose.resources.stringResource

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String,
    val screen: Screen
) {
    data object Dashboard :
        BottomNavItem("dashboard", Icons.Filled.Home,
            stringResource(Res.)
            DebugScreen())

    data object Analytics :
        BottomNavItem("analytics", Icons.Filled.PieChart, "Analytics", AnalyticScreen())

    data object Profile : BottomNavItem("profile", Icons.Filled.Person, "Profile", ProfileScreen())

    data object Transactions :
        BottomNavItem("transactions", Icons.Filled.Person, "Transactions", ProfileScreen())

    companion object {
        val items = listOf(Dashboard, Analytics, Profile)
    }
}


