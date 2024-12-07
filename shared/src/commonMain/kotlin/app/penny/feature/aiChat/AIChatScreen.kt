// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/AIChatScreen.kt
package app.penny.feature.aiChat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import app.penny.feature.aiChat.components.ChatBubble
import app.penny.feature.aiChat.components.FunctionalBubble
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch

class AIChatScreen : Screen {
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<AIChatViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = "Penny",
                    onNavigateBack = {
                        rootNavigator.pop()
                    }
                )
            },
            bottomBar = {
                ChatInputBar(
                    inputText = uiState.inputText,
                    onTextChanged = { text ->
                        // 不在这里直接sendMessage，否则会重复发消息，改为只更新输入框
                    },
                    onSendClicked = { message ->
                        viewModel.handleIntent(AIChatIntent.SendMessage(message))
                    },
                    onAttachClicked = {
                        // 附件功能留空
                    },
                    onAudioClicked = {
                        // 语音功能留空
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            reverseLayout = true,
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(uiState.messages.reversed()) { message ->
                                ChatBubble(message = message)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    // 功能性气泡
                    FunctionalBubble(
                        show = uiState.showFunctionalBubble,
                        action = uiState.pendingAction,
                        dto = uiState.pendingDto,
                        onConfirm = { editedFields ->
                            uiState.pendingAction?.let { action ->
                                viewModel.handleIntent(AIChatIntent.ConfirmPendingAction(action, editedFields))
                            }
                        },
                        onDismiss = {
                            // 用户取消，关闭bubble
                            // 这里简单处理：关闭bubble，但保留pendingAction/dto以便以后需要？
                            // 实际可根据需求清空
                            viewModel.screenModelScope.launch {
                                viewModel.cancelBubble()
                            }
                        }
                    )
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
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp),
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
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = TextFieldDefaults.colors()
            )
            IconButton(onClick = onAudioClicked) {
                Icon(Icons.Default.Mic, contentDescription = "Record Audio")
            }
            IconButton(
                onClick = {
                    onSendClicked(textFieldValue.text)
                    textFieldValue = TextFieldValue("")
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send Message")
            }
        }
    }
}