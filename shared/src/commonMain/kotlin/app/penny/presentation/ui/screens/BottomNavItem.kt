// shared/src/commonMain/kotlin/app/penny/presentation/ui/screens/BottomNavItem.kt

package app.penny.presentation.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import app.penny.feature.analytics.AnalyticScreen
import app.penny.feature.dashBoard.DashboardScreen
import app.penny.feature.profile.ProfileScreen
import app.penny.feature.transactions.TransactionScreen
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.StringResource

sealed class BottomNavItem(
    val topBar: @Composable () -> Unit,
    val icon: ImageVector,
    val titleStringResource: StringResource,
    val screen: Screen,
    val statusBarColor: @Composable () -> Color
) {
    data object Dashboard :
        BottomNavItem(
            {},
            Icons.Outlined.Home,
            SharedRes.strings.dashboard,
            DashboardScreen(),
            { MaterialTheme.colorScheme.primaryContainer }
        )

    data object Analytics :
        BottomNavItem(
            {},
            Icons.Outlined.PieChart,
            SharedRes.strings.analytics,
            AnalyticScreen(),
            { MaterialTheme.colorScheme.surface }
        )

    data object Profile : BottomNavItem(
        {},
        Icons.Outlined.Person,
        SharedRes.strings.profile,
        ProfileScreen(),
        { MaterialTheme.colorScheme.surface }
    )

    data object Transactions :
        BottomNavItem(
            {},
            Icons.AutoMirrored.Outlined.ReceiptLong,
            SharedRes.strings.transaction,
            TransactionScreen(),
            { MaterialTheme.colorScheme.surfaceVariant }
        )

    companion object {
        val items = listOf(Dashboard, Transactions, Analytics, Profile)
    }
}


