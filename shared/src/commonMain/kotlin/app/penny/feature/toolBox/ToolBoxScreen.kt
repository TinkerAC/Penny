// ToolBoxScreen.kt
package app.penny.feature.toolBox

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
    val icon: ImageVector
)


class ToolBoxScreen : Screen {
    @Composable
    override fun Content() {
        val localNavigator = LocalNavigator.currentOrThrow

        // 示例工具列表
        val toolList = listOf(
            Tool(
                displayName = SharedRes.strings.tool_data_export,
                icon = Icons.Filled.IosShare
            ),
            Tool(
                displayName = SharedRes.strings.tool_exchange_rate_conversion,
                icon = Icons.Filled.CurrencyExchange,
            ),
            Tool(
                displayName = SharedRes.strings.tool_debt_calculator,
                icon = Icons.Filled.AttachMoney,
            ),
        )

        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = stringResource(SharedRes.strings.pennys_box),
                    onNavigateBack = {
                        localNavigator.pop()
                    }
                )
            },
        ) { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(36.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp)
            ) {
                items(toolList) { tool ->
                    ToolCard(
                        toolName = stringResource(tool.displayName),
                        icon = tool.icon,
                        onClick = {
                            // TODO:
                        },
                        toolDescription = ""

                    )
                }
            }
        }
    }
}
