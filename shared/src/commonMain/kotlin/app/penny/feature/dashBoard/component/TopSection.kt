import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.feature.dashBoard.DashboardUiState
import app.penny.feature.dashBoard.component.OverViewCard
import app.penny.feature.newTransaction.NewTransactionScreen
import app.penny.presentation.ui.components.PennyLogo
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
fun TopSection(scale: Float, yOffset: Dp, uiState: DashboardUiState) {
    val topSurfaceHeight = 250.dp
    val rootNavigator = LocalNavigator.currentOrThrow

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(topSurfaceHeight),
        contentAlignment = Alignment.BottomCenter,

        ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter
            ) {
                PennyLogo(
                    fontSize = 56.sp, modifier = Modifier
                        .scale(scale) // 应用缩放因子
                        .offset(y = (-150).dp - yOffset) // 应用垂直偏移量（向上移动）
                )
            }
        }
        OverViewCard(
            modifier = Modifier.offset(0.dp, 130.dp),
            currencySymbol = "$",
            incomeOfMonth =uiState.incomeOfMonth,
            expenseOfMonth = uiState.expenseOfMonth,
            balanceOfMonth = uiState.balanceOfMonth,
            onAddTransactionClick = {
                rootNavigator.push(NewTransactionScreen())
            }

        )
    }
}