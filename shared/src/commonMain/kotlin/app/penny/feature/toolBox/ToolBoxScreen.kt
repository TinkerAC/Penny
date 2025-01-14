// ToolBoxScreen.kt
package app.penny.feature.toolBox

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.penny.feature.currencyConvater.CurrencyConverterScreen
import app.penny.feature.loanCalculator.LoanCalculatorScreen
import app.penny.feature.toolBox.component.ToolCard
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

data class Tool(
    val displayName: StringResource,
    val description: StringResource? = null,
    val icon: ImageVector,
    val screen: Screen? = null
)


class ToolBoxScreen : Screen {
    @Composable
    override fun Content() {
        val localNavigator = LocalNavigator.currentOrThrow

        // 示例工具列表
        val toolList = listOf(
            Tool(
                displayName = SharedRes.strings.tool_currency_converter,
                icon = Icons.Filled.CurrencyExchange,
                screen = CurrencyConverterScreen()
            ),
            Tool(
                displayName = SharedRes.strings.tool_loan_calculator,
                icon = Icons.Filled.AttachMoney,
                screen = LoanCalculatorScreen()
            ),
        )

        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(title = stringResource(SharedRes.strings.pennys_box),
                    onNavigateBack = {
                        localNavigator.pop()
                    })
            },
        ) { innerPadding ->

            Column(
                modifier = Modifier.padding(innerPadding).padding(16.dp)
            ) {

                toolList.forEach { tool ->
                    ToolCard(
                        toolName = stringResource(tool.displayName), icon = tool.icon, onClick = {
                            tool.screen?.let { screen ->
                                localNavigator.push(screen)
                            }
                        }, toolDescription = ""

                    )
                }

            }
        }

    }
}

