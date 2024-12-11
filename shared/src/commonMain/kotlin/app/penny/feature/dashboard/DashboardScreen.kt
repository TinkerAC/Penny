// DashboardScreen.kt
package app.penny.feature.dashboard

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.feature.myLedger.MyLedgerScreen
import app.penny.feature.newTransaction.NewTransactionScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class DashboardScreen : Screen {

    @Composable
    override fun Content() {
        val dashboardViewModel = koinScreenModel<DashboardViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val uiState = dashboardViewModel.uiState.collectAsState()
        val rootNavigator = navigator.parent
        var count by remember { mutableStateOf(0) }
        var tier by remember { mutableStateOf(0) }
        Button(
            onClick = {
                rootNavigator?.push(NewTransactionScreen())
            }
        ) {
            Text("Add Transaction")
        }

        Button(
            onClick = {
                rootNavigator?.push(MyLedgerScreen())
            }
        ) {
            Text("My Ledger")
        }

        TextField(
            value = count.toString(),
            onValueChange = {
                count = it.toIntOrNull() ?: count // 只接受合法数字，否则保持不变
            },
            placeholder = { Text("Type a count") },
            modifier = Modifier
                .height(56.dp),
            colors = TextFieldDefaults.colors()
        )

        TextField(
            value = tier.toString(),
            onValueChange = {
                tier = it.toIntOrNull() ?: tier // 只接受合法数字，否则保持不变
            },
            placeholder = { Text("Type a tier") },
            modifier = Modifier
                .height(56.dp),
            colors = TextFieldDefaults.colors()
        )

        Button(
            onClick = {
                dashboardViewModel.insertRandomTransaction(count, tier)
            }
        ) {
            Text("Insert Random Transaction")
        }

        Button(
            onClick = {
                dashboardViewModel.uploadUpdatedLedgers()
            }
        ) {
            Text("Upload Ledgers")
        }

        Button(
            onClick = {
                dashboardViewModel.clearUserData()
            }
        ) {
            Text("Clear User Data(Token, User Name, User Email)")
        }
        Button(
            onClick = {
                dashboardViewModel.downloadUnsyncedLedgers()
            }
        ) {
            Text("Download Unsynced Ledgers")
        }


        Button(
            onClick = {
                dashboardViewModel.syncAllData()
            }
        ) {
            Text("Sync All Data")
        }




        Text("Last Synced At: ${dashboardViewModel.uiState.value.lastSyncedAt ?: "Never"}")

        Text("Active User: ${dashboardViewModel.uiState.value.activeUser?: "No Active User"})")
        Text(uiState.value.message ?: "")



    }
}
