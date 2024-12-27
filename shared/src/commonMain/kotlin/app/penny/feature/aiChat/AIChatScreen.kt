package app.penny.feature.aiChat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import app.penny.feature.aiChat.components.messageBubble.MessageBubble
import app.penny.presentation.ui.components.LedgerSelectDialog
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.compose.stringResource

class AIChatScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
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
                IconButton(onClick = {
                    viewModel.handleIntent(AIChatIntent.ShowLedgerSelectDialog)
                }) {
                    Icon(
                        Icons.Outlined.AccountBalanceWallet,
                        contentDescription = "Select Ledger"
                    )
                }
            })
        }, bottomBar = {
            ChatInputBar(inputText = uiState.inputText, onTextChanged = { text ->
                viewModel.updateInputText(text)
            }, onSendClicked = { message ->
                viewModel.handleIntent(AIChatIntent.SendMessage(message))
            }, onAttachClicked = {
                // 附件功能留空
            }, onAudioClicked = {
                // 语音功能留空
            })
        }) { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues).fillMaxSize()
            ) {
                // 应用自定义的聊天背景
//                ChatBackground()

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


                                MessageBubble(message = message,
                                    onActionConfirm = { msg, editableFields ->
                                        viewModel.handleIntent(
                                            AIChatIntent.ConfirmPendingAction(
                                                message = msg
                                            )
                                        )
                                    },
                                    onActionDismiss = { m ->
                                        viewModel.handleIntent(
                                            AIChatIntent.DismissFunctionalMessage(
                                                m
                                            )
                                        )
                                    })
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }


                if (uiState.ledgerSelectDialogVisible) {
                    LedgerSelectDialog(allLedgers = emptyList(),
                        currentLedger = uiState.selectedLedger!!,
                        onLedgerSelected = { ledger ->
                            viewModel.handleIntent(AIChatIntent.SelectLedger(ledger))
                        },
                        onDismissRequest = {
                            viewModel.handleIntent(AIChatIntent.HideLedgerSelectDialog)
                        })
                }
            }
        }
    }


}

@Composable
fun ChatInputBar(
    inputText: String,
    onTextChanged: (String) -> Unit,
    onSendClicked: (String) -> Unit,
    onAttachClicked: () -> Unit,
    onAudioClicked: () -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(inputText)) }

    Surface(
        tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAttachClicked) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach File")
            }
            TextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    onTextChanged(it.text)
                },
                placeholder = { Text("Type a message") },
                modifier = Modifier.weight(1f).height(56.dp),
//                colors = TextFieldDefaults.colors(
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                    textColor = MaterialTheme.colorScheme.onSurface,
//                    placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
//                )
            )
            IconButton(onClick = onAudioClicked) {
                Icon(Icons.Default.Mic, contentDescription = "Record Audio")
            }
            IconButton(onClick = {
                onSendClicked(textFieldValue.text)
                textFieldValue = TextFieldValue("")
            }) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Message")
            }
        }
    }
}
