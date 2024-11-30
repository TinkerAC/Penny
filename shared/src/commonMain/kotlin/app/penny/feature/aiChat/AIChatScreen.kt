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
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

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
                        viewModel.handleIntent(AIChatIntent.SendMessage(text))
                    },
                    onSendClicked = { message ->
                        viewModel.handleIntent(AIChatIntent.SendMessage(message))
                    },
                    onAttachClicked = {
                        // Handle file attachment (functionality left blank)
                    },
                    onAudioClicked = {
                        // Handle audio input (functionality left blank)
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (uiState.isLoading) {
                    // Show a loading indicator
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