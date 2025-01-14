// file: /Users/tinker/StudioProjects/Penny/shared/src/commonMain/kotlin/app/penny/feature/debugBoard/DebugScreen.kt
package app.penny.feature.debugBoard

import androidx.compose.foundation.layout.Column
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

        // Navigator
        val rootNavigator = LocalNavigator.currentOrThrow

        // 输入状态：count & tier
        var count by remember { mutableStateOf(0) }
        var tier by remember { mutableStateOf(0) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Debug") },
                    actions = {
                        Button(
                            onClick = { rootNavigator.pop() }
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
                /**
                 * 1. 交易相关 (Transaction Section)
                 */
                item {
                    TransactionSection(
                        onAddTransactionClick = {
                            rootNavigator.push(NewTransactionScreen())
                        },
                        onMyLedgerClick = {
                            rootNavigator.push(MyLedgerScreen())
                        }
                    )
                }

                /**
                 * 2. 插入随机交易 (Insert Random Transaction Section)
                 */
                item {
                    InsertRandomTransactionSection(
                        onInsertRandomTransaction = { count, recentDays ->
                            debugViewModel.insertRandomTransaction(count, recentDays)
                        }
                    )
                }

                /**
                 * 3. 同步相关操作 (Sync Section)
                 */
                item {
                    SyncSection(
                        onUploadUpdatedLedgers = { debugViewModel.uploadUpdatedLedgers() },
                        onDownloadUnsyncedLedgers = { debugViewModel.downloadUnsyncedLedgers() },
                        onSyncAllData = { debugViewModel.syncAllData() },
                        onClearLastSyncedAt = { debugViewModel.clearLastSyncedAt() }
                    )
                }

                /**
                 * 4. 用户数据相关操作 (User Data Section)
                 */
                item {
                    UserDataSection(
                        onClearUserData = { debugViewModel.clearUserData() },
                        onSendNotification = { debugViewModel.sendNotification() }
                    )
                }

                /**
                 * 5. 输出调试信息 (Debug Info Section)
                 */
                item {
                    DebugInfoSection(
                        lastSyncedAt = uiState.value.lastSyncedAt?.toString() ?: "Never",
                        activeUser = uiState.value.activeUser?.toString() ?: "No Active User",
                        message = uiState.value.message ?: "",
                        defaultLedger = uiState.value.defaultLedger?.toString() ?: "No Default Ledger",
                        databasePath = uiState.value.databasePath?.toString() ?: "Unknown",
                        settingStorePath = uiState.value.settingStorePath?.toString() ?: "Unknown",
                        accessToken = uiState.value.accessToken ?: "Unknown",
                        refreshToken = uiState.value.refreshToken ?: "Unknown"
                    )
                }

                /**
                 * 6. 颜色方案预览 (DisplayColorScheme)
                 */
                item {
                    DisplayColorScheme()
                }
            }
        }
    }
}

/**
 * 交易相关模块
 */
@Composable
fun TransactionSection(
    onAddTransactionClick: () -> Unit,
    onMyLedgerClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Transaction Section")
        Button(onClick = onAddTransactionClick, modifier = Modifier.padding(top = 8.dp)) {
            Text("Add Transaction")
        }
        Button(onClick = onMyLedgerClick, modifier = Modifier.padding(top = 8.dp)) {
            Text("My Ledger")
        }
    }
}


/**
 * 插入随机交易模块
 * 将原先的 tier 改为 recentDays，并在输入框前使用 Text 提示
 */
@Composable
fun InsertRandomTransactionSection(
    onInsertRandomTransaction: (count: Int, recentDays: Int) -> Unit
) {
    // 这两个值代表用户输入：插入条数 (count) 和过去多少天内 (recentDays)
    var count by remember { mutableStateOf(0) }
    var recentDays by remember { mutableStateOf(0) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Insert Random Transaction")

        // 提示文字，说明“插入多少条随机记录”
        Text(text = "Number of random transactions to insert:")
        TextField(
            value = count.toString(),
            onValueChange = { input ->
                // 只接受数字
                val parsed = input.toIntOrNull()
                if (parsed != null) {
                    count = parsed
                }
            },
            placeholder = { Text("e.g. 10") },
            modifier = Modifier
                .height(56.dp)
                .padding(top = 4.dp),
            colors = TextFieldDefaults.colors()
        )

        Text(
            text = "Insert records in the last N days:",
            modifier = Modifier.padding(top = 16.dp)
        )
        TextField(
            value = recentDays.toString(),
            onValueChange = { input ->
                val parsed = input.toIntOrNull()
                if (parsed != null) {
                    recentDays = parsed
                }
            },
            placeholder = { Text("e.g. 7") },
            modifier = Modifier
                .height(56.dp)
                .padding(top = 4.dp),
            colors = TextFieldDefaults.colors()
        )

        // 插入随机交易按钮
        Button(
            onClick = { onInsertRandomTransaction(count, recentDays) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Insert Random Transaction")
        }
    }
}

/**
 * 同步相关操作
 */
@Composable
fun SyncSection(
    onUploadUpdatedLedgers: () -> Unit,
    onDownloadUnsyncedLedgers: () -> Unit,
    onSyncAllData: () -> Unit,
    onClearLastSyncedAt: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Sync Section")

        Button(onClick = onUploadUpdatedLedgers, modifier = Modifier.padding(top = 8.dp)) {
            Text("Upload Ledgers")
        }
        Button(onClick = onDownloadUnsyncedLedgers, modifier = Modifier.padding(top = 8.dp)) {
            Text("Download Unsynced Ledgers")
        }
        Button(onClick = onSyncAllData, modifier = Modifier.padding(top = 8.dp)) {
            Text("Sync All Data")
        }
        Button(onClick = onClearLastSyncedAt, modifier = Modifier.padding(top = 8.dp)) {
            Text("Clear Last Synced At")
        }
    }
}

/**
 * 用户数据相关操作
 */
@Composable
fun UserDataSection(
    onClearUserData: () -> Unit,
    onSendNotification: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "User Data Section")

        Button(onClick = onClearUserData, modifier = Modifier.padding(top = 8.dp)) {
            Text("Clear User Data(Token, User Name, User Email)")
        }
        Button(onClick = onSendNotification, modifier = Modifier.padding(top = 8.dp)) {
            Text("Send Notification")
        }
    }
}

/**
 * 输出调试信息
 */
@Composable
fun DebugInfoSection(
    lastSyncedAt: String,
    activeUser: String,
    message: String,
    defaultLedger: String,
    databasePath: String,
    settingStorePath: String,
    accessToken: String,
    refreshToken: String
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Debug Info Section")
        Text("Last Synced At: $lastSyncedAt")
        Text("Active User: $activeUser")
        Text("Message: $message")
        Text("Default Ledger: $defaultLedger")
        Text("DataBasePath: $databasePath")
        Text("Setting Store Path: $settingStorePath")
        Text("AccessToken: $accessToken")
        Text("RefreshToken: $refreshToken")
    }
}
