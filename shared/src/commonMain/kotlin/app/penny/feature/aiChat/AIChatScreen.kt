package app.penny.feature.aiChat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.penny.feature.aiChat.components.ChatInputBar
import app.penny.feature.aiChat.components.messageBubble.MessageRow
import app.penny.presentation.ui.components.LedgerSelectDialog
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.compose.stringResource
import kotlin.uuid.ExperimentalUuidApi

class AIChatScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
    @Composable
    override fun Content() {
        Logger.d { "AIChatScreen Content composed" }
        val rootNavigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<AIChatViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(topBar = {
            TopAppBar(title = {
                Text(
                    stringResource(SharedRes.strings.chat_with_penny)
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    rootNavigator.pop()
                }) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                }
            }, actions = {
                // Placeholder for any future actions
            },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }, bottomBar = {
            ChatInputBar(
                inputMode = uiState.inputMode,
                inputText = uiState.inputText,
                onModeToggle = {
                    viewModel.handleIntent(AIChatIntent.ToggleInputMode)
                },
                onSendClicked = { text ->
                    viewModel.handleIntent(AIChatIntent.SendMessage(text))
                },
                onStartRecord = {
                    viewModel.handleIntent(AIChatIntent.StartRecording)
                },
                onStopRecord = {},
            )
        }) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues).fillMaxSize().background(
                    MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            reverseLayout = true,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(uiState.messages.reversed()) { message ->
                                MessageRow(
                                    message = message,
                                    onActionConfirm = { msg, baseEntityDto ->
                                        viewModel.handleIntent(
                                            AIChatIntent.ConfirmPendingAction(
                                                message = msg.copy(
                                                    userIntent = msg.userIntent.copy(
                                                        dto = baseEntityDto,
                                                    )
                                                )
                                            )
                                        )
                                    },
                                    onActionDismiss = { m ->
                                        viewModel.handleIntent(
                                            AIChatIntent.DismissFunctionalMessage(
                                                m
                                            )
                                        )
                                    },
                                    userAvatarUrl = uiState.userAvatarUrl
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // 提升遮罩层和取消按钮到父组件
                if (uiState.isRecording) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray.copy(alpha = 0.5f))
                            .clickable(enabled = false) { /* 防止点击穿透 */ },
                        contentAlignment = Alignment.BottomCenter
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) { // 圆形取消按钮
                                IconButton(
                                    onClick = {
                                        viewModel.handleIntent(AIChatIntent.CancelRecording)
                                    },
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(Color.White, shape = CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Cancel Recording",
                                        tint = Color.Red
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("点击取消")
                            }
                            //confirm button
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.handleIntent(AIChatIntent.StopRecording)
                                    },
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(Color.White, shape = CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Stop Recording",
                                        tint = Color.Green
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("点击发送")


                            }


                        }


                    }
                }

                // 显示 Ledger Select Dialog
                if (uiState.ledgerSelectDialogVisible) {
                    LedgerSelectDialog(
                        allLedgers = emptyList(),
                        currentLedger = uiState.selectedLedger!!,
                        onLedgerSelected = { ledger ->
                            viewModel.handleIntent(AIChatIntent.SelectLedger(ledger))
                        },
                        onDismissRequest = {
                            viewModel.handleIntent(AIChatIntent.HideLedgerSelectDialog)
                        }
                    )
                }
            }
        }
    }

}
