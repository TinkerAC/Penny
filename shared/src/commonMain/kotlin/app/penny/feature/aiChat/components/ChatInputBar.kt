package app.penny.feature.aiChat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import app.penny.core.data.enumerate.MessageType
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ChatInputBar(
    isRecording: Boolean = false,
    inputMode: MessageType = MessageType.TEXT,
    inputText: String,
    onSendClicked: (String) -> Unit,
    onModeToggle: () -> Unit, // 切换输入模式
    onStartRecord: () -> Unit,
    onStopRecord: () -> Unit,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(inputText)) }

    Surface(
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 模式切换按钮
                IconButton(onClick = onModeToggle) {
                    Icon(
                        imageVector = if (inputMode == MessageType.TEXT) Icons.Default.Mic else Icons.Default.Keyboard,
                        contentDescription = "Toggle Input Mode"
                    )
                }

                // 统一的输入组件样式
                when (inputMode) {
                    MessageType.TEXT -> {
                        TextField(
                            value = textFieldValue,
                            onValueChange = {
                                textFieldValue = it
                            },
                            placeholder = { Text(stringResource(SharedRes.strings.type_a_message)) },
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    onSendClicked(textFieldValue.text)
                                    textFieldValue = TextFieldValue("")
                                }
                            )
                        )
                    }

                    MessageType.AUDIO -> {
                        // 音频输入模式的按住说话按钮，统一样式
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            onStartRecord()
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(
                                    if (isRecording) SharedRes.strings.recording else SharedRes.strings.tap_to_speak
                                )
                            )

                        }
                    }
                }

                // 发送按钮
                IconButton(
                    onClick = {
                        onSendClicked(textFieldValue.text)
                        textFieldValue = TextFieldValue("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message"
                    )
                }
            }
        }
    }
}
