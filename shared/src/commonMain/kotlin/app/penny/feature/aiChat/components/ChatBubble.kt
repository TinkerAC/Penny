// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/components/ChatBubble.kt
package app.penny.feature.aiChat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.ChatMessage
import app.penny.servershared.EditableField
import app.penny.servershared.FieldType
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.Action
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.avatar_boy
import penny.shared.generated.resources.avatar_girl
import penny.shared.generated.resources.avatar_penny
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatBubble(
    message: ChatMessage,
    // 以下参数为可选的功能气泡展示所需
    showFunctionalBubble: Boolean = false,
    action: Action? = null,
    dto: BaseEntityDto? = null,
    onConfirm: ((Map<String, String?>) -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    // 判断发送者是否为用户
    val isUser = message.sender.uuid != Uuid.fromLongs(0, 0)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI头像
            Image(
                painter = painterResource(
                    Res.drawable.avatar_penny
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Card(
            shape = MaterialTheme.shapes.medium.copy(
                topStart = CornerSize(16.dp),
                topEnd = CornerSize(16.dp),
                bottomStart = if (isUser) CornerSize(16.dp) else CornerSize(0.dp),
                bottomEnd = if (isUser) CornerSize(0.dp) else CornerSize(16.dp)
            ),
            colors = if (isUser) {
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            } else {
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            },
            modifier = Modifier.widthIn(max = 240.dp)
        ) {
            if (showFunctionalBubble && action != null && dto != null && onConfirm != null && onDismiss != null) {
                // 功能气泡渲染逻辑
                FunctionalBubbleContent(
                    isUser = isUser,
                    action = action,
                    dto = dto,
                    onConfirm = onConfirm,
                    onDismiss = onDismiss
                )
            } else {
                // 普通文本消息渲染逻辑
                when (message) {
                    is ChatMessage.TextMessage -> {
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(
                    if (Random.nextBoolean()) Res.drawable.avatar_girl else Res.drawable.avatar_boy
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FunctionalBubbleContent(
    isUser: Boolean,
    action: Action,
    dto: BaseEntityDto,
    onConfirm: (Map<String, String?>) -> Unit,
    onDismiss: () -> Unit
) {
    // 假设此为AI气泡
    val fields: List<EditableField> = dto.getEditableFields()
    val fieldStates = remember(fields) {
        fields.associate { it.name to mutableStateOf(it.value ?: "") }.toMutableMap()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "准备执行操作: ${action.actionName}",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        fields.forEach { field ->
            when (field.type) {
                FieldType.TEXT -> {
                    val state = fieldStates[field.name]!!
                    OutlinedTextField(
                        value = state.value,
                        onValueChange = { state.value = it },
                        label = { Text(field.label) },
                        colors = outlinedTextFieldColors(isUser),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                FieldType.DATE -> {
                    val state = fieldStates[field.name]!!
                    var expanded by remember { mutableStateOf(false) }
                    val date = remember(state.value) {
                        if (state.value.isNotBlank()) {
                            val epochSeconds = state.value.toLongOrNull() ?: 0L
                            if (epochSeconds > 0) {
                                val instant = Instant.fromEpochSeconds(epochSeconds)
                                val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
                                "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')}"
                            } else ""
                        } else ""
                    }

                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        label = { Text(field.label) },
                        readOnly = true,
                        colors = outlinedTextFieldColors(isUser),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { expanded = true },
                        trailingIcon = {
                            Icon(
                                Icons.Default.CalendarToday,
                                contentDescription = "选择日期",
                                tint = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    if (expanded) {
                        val datePickerState = rememberDatePickerState()
                        DatePickerDialog(
                            onDismissRequest = { expanded = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        expanded = false
                                        val parts = date.split("-")
                                        if (parts.size == 3) {
                                            val year = parts[0].toIntOrNull() ?: 0
                                            val month = parts[1].toIntOrNull() ?: 0
                                            val day = parts[2].toIntOrNull() ?: 0

                                            if (year > 0 && month in 1..12 && day in 1..31) {
                                                val localDate = LocalDate(year, month, day)
                                                val epochSeconds = localDate
                                                    .atStartOfDayIn(TimeZone.currentSystemDefault())
                                                    .epochSeconds
                                                state.value = epochSeconds.toString()
                                            } else {
                                                state.value = "0"
                                            }
                                        } else {
                                            state.value = "0"
                                        }
                                    },
                                    enabled = true
                                ) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { expanded = false }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }

                FieldType.CATEGORY -> {
                    val state = fieldStates[field.name]!!
                    val categories = listOf("Food", "Transport", "Utilities", "Entertainment")
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedTextField(
                            value = state.value,
                            onValueChange = { },
                            label = { Text(field.label) },
                            readOnly = true,
                            colors = outlinedTextFieldColors(isUser),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { expanded = true },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "选择分类",
                                    tint = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        state.value = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                FieldType.CURRENCY -> {
                    val state = fieldStates[field.name]!!
                    val currencies = listOf("USD", "EUR", "CNY", "JPY")
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedTextField(
                            value = state.value,
                            onValueChange = { },
                            label = { Text(field.label) },
                            readOnly = true,
                            colors = outlinedTextFieldColors(isUser),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { expanded = true },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "选择货币",
                                    tint = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            currencies.forEach { currency ->
                                DropdownMenuItem(
                                    text = { Text(currency) },
                                    onClick = {
                                        state.value = currency
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(
                onClick = { onDismiss() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("取消")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    val editedFields = fieldStates.mapValues { it.value.value }
                    onConfirm(editedFields)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("确认")
            }
        }
    }
}

@Composable
private fun outlinedTextFieldColors(isUser: Boolean): TextFieldColors {
    return OutlinedTextFieldDefaults.colors().copy(
//        textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        focusedLabelColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedLabelColor = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
//        focusedBorderColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
//        unfocusedBorderColor = if (isUser) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        cursorColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
//        trailingIconColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    )
}