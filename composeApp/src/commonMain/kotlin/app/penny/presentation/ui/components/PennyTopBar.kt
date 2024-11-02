package app.penny.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.penny.domain.model.LedgerModel
import app.penny.presentation.uiState.MainUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PennyTopBar(
    title: String,
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    onLedgerSelected: (LedgerModel) -> Unit // 回调函数用于选择Ledger
) {
    var expanded by remember { mutableStateOf(false) } // 控制下拉菜单的状态
    TopAppBar(
        title = {
            Row {
                Text(text = title)

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    uiState.ledgers.forEach { ledger ->
                        DropdownMenuItem(
                            text = { Text(text = ledger.name) },
                            onClick = {
                                expanded = false
                                onLedgerSelected(ledger) // 调用选择Ledger的回调函数
                            }
                        )
                    }
                }
            }


        },
        modifier = modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            scrolledContainerColor = MaterialTheme.colorScheme.primary,
            navigationIconContentColor = Color.White,
        )
    )

    // 显示并选择当前的Ledger 的下拉菜单


}
