// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/components/FunctionalBubble.kt
package app.penny.feature.aiChat.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.servershared.EditableField
import app.penny.servershared.FieldType
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.Action
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime


/**
 * 通用的功能气泡。
 * 根据传入的dto，动态生成可编辑字段（通过dto.getEditableFields()），
 * 用户修改后点击确认，将编辑后的字段通过onConfirm返回。
 *
 * @param show 是否显示此气泡。
 * @param action 要执行的Action。
 * @param dto 待编辑的dto，可为空（如果为空表示无法解析dto，需要用户手动输入全部字段）。
 * @param onConfirm 用户点击确认时回调 (editedFields: Map<String, String?>)。
 * @param onDismiss 用户点击取消时回调。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FunctionalBubble(
    show: Boolean,
    action: Action?,
    dto: BaseEntityDto?,
    onConfirm: (Map<String, String?>) -> Unit,
    onDismiss: () -> Unit
) {
    if (!show || action == null) return

    // 获取可编辑字段
    val fields: List<EditableField> = dto?.getEditableFields() ?: emptyList()

    // 为每个字段创建状态
    val fieldStates = remember(fields) {
        fields.associate { it.name to mutableStateOf(it.value ?: "") }.toMutableMap()
    }

    // Surface 包裹整个气泡
    Surface(
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "准备执行操作: ${action.actionName}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 动态渲染每个字段
                fields.forEach { field ->
                    when (field.type) {
                        FieldType.TEXT -> {
                            val state = fieldStates[field.name]!!
                            OutlinedTextField(
                                value = state.value,
                                onValueChange = { state.value = it },
                                label = { Text(field.label) },
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
                                        val localDateTime =
                                            instant.toLocalDateTime(TimeZone.currentSystemDefault())
                                        "${localDateTime.year}-${
                                            localDateTime.monthNumber.toString().padStart(2, '0')
                                        }-${localDateTime.dayOfMonth.toString().padStart(2, '0')}"
                                    } else ""
                                } else ""
                            }

                            OutlinedTextField(
                                value = date,
                                onValueChange = {},
                                label = { Text(field.label) },
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clickable { expanded = true },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.CalendarToday,
                                        contentDescription = "选择日期"
                                    )
                                }
                            )
                            if (expanded) {

                                val datePickerState = rememberDatePickerState()
                                DatePickerDialog(
                                    // Dismiss the dialog when the user clicks outside the dialog or on the back button.
                                    // To disable this functionality, simply use an empty onDismissRequest.
                                    onDismissRequest = {
                                        expanded = false
                                    },

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
                                                        // Parse the date and update state
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
                                            enabled = true // Enable the confirm button by default
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
                            val categories =
                                listOf("Food", "Transport", "Utilities", "Entertainment") // 示例分类
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedTextField(
                                    value = state.value,
                                    onValueChange = { },
                                    label = { Text(field.label) },
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .clickable { expanded = true },
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = "选择分类"
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
                            val currencies = listOf("USD", "EUR", "CNY", "JPY") // 示例货币
                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                OutlinedTextField(
                                    value = state.value,
                                    onValueChange = { },
                                    label = { Text(field.label) },
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .clickable { expanded = true },
                                    trailingIcon = {
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = "选择货币"
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
                        // 可根据需要添加更多字段类型的处理
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val editedFields = fieldStates.mapValues { it.value.value }
                            onConfirm(editedFields)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("确认")
                    }
                }
            }
        }
    }
}
