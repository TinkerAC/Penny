// DebugScreen.kt
package app.penny.feature.debugBoard

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import app.penny.presentation.ui.components.DisplayColorScheme
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class DebugScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val debugViewModel = koinScreenModel<DebugViewModel>()
        val uiState = debugViewModel.uiState.collectAsState()
        val rootNavigator = LocalNavigator.currentOrThrow
        var count by remember { mutableStateOf(0) }
        var tier by remember { mutableStateOf(0) }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Debug")

                    },
                    actions =
                    {
                        Button(
                            onClick = {
                                rootNavigator.pop()
                            }
                        ) {
                            Text("Back")
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.surface,
        ) { innerPadding ->

            LazyColumn(
                modifier = Modifier.padding(innerPadding)
            ) {
                item {
                    Button(
                        onClick = {
                            rootNavigator.push(NewTransactionScreen())
                        }
                    ) {
                        Text("Add Transaction")
                    }


                    Button(
                        onClick = {
                            rootNavigator.push(MyLedgerScreen())
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
                            debugViewModel.insertRandomTransaction(count, tier)
                        }
                    ) {
                        Text("Insert Random Transaction")
                    }

                    Button(
                        onClick = {
                            debugViewModel.uploadUpdatedLedgers()
                        }
                    ) {
                        Text("Upload Ledgers")
                    }

                    Button(
                        onClick = {
                            debugViewModel.clearUserData()
                        }
                    ) {
                        Text("Clear User Data(Token, User Name, User Email)")
                    }
                    Button(
                        onClick = {
                            debugViewModel.downloadUnsyncedLedgers()
                        }
                    ) {
                        Text("Download Unsynced Ledgers")
                    }


                    Button(
                        onClick = {
                            debugViewModel.syncAllData()
                        }
                    ) {
                        Text("Sync All Data")
                    }

                    Button(
                        onClick = {
                            debugViewModel.clearLastSyncedAt()
                        }
                    ) {
                        Text("Clear Last Synced At")
                    }

                    Button(
                        onClick = {
                            debugViewModel.sendNotification()
                        }
                    ){
                        Text("send Notification")
                    }



                    Text("Last Synced At: ${debugViewModel.uiState.value.lastSyncedAt ?: "Never"}")

                    Text("Active User: ${debugViewModel.uiState.value.activeUser ?: "No Active User"})")
                    Text(uiState.value.message ?: "")

                    Text("Default Ledger: ${debugViewModel.uiState.value.defaultLedger ?: "No Default Ledger"})")

                }

                item {
                    DisplayColorScheme()
                }
            }
        }
    }
}
