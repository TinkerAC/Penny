package app.penny.feature.aiChat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.ActionStatus
import app.penny.core.domain.model.UserModel
import app.penny.servershared.EditableField
import app.penny.servershared.FieldType
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.Action
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.avatar_boy
import penny.shared.generated.resources.avatar_girl
import penny.shared.generated.resources.avatar_penny

@OptIn(ExperimentalUuidApi::class)
@Composable
fun ChatBubble(
    message: ChatMessage,
    onActionConfirm: (ChatMessage, Map<String, String?>) -> Unit,
    onActionDismiss: (ChatMessage) -> Unit
) {
    val isUser = message.sender.uuid != Uuid.fromLongs(0, 0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI Avatar
            Image(
                painter = painterResource(Res.drawable.avatar_penny),
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
                bottomStart = CornerSize(16.dp),
                bottomEnd = CornerSize(16.dp),
                topStart = if (isUser) CornerSize(16.dp) else CornerSize(0.dp),
                topEnd = if (isUser) CornerSize(0.dp) else CornerSize(16.dp)
            ),
            colors = if (isUser) {
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            } else {
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            },
            modifier = Modifier.widthIn(max = 240.dp)
        ) {
            // 普通文本
            Text(
                text = message.content ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(8.dp)
            )
            if (message.action !== null) {
                // 有action的消息，根据status来展示不同的UI
                when (message.actionStatus) {
                    ActionStatus.Pending -> {
                        ActionPendingContent(
                            message = message,
                            onConfirm = {
                                onActionConfirm(message, it)
                            },
                            onDismiss = {
                                onActionDismiss(message)
                            }
                        )
                    }
                    ActionStatus.Completed -> {
                        ActionCompletedContent(
                            action = message.action,
                        )
                    }
                    ActionStatus.Cancelled -> {
                        ActionCancelledContent(
                            action = message.action
                        )
                    }
                    else -> {
                        // 理论上不会出现
                        Text(
                            text = "未知状态",
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

@Composable
private fun ActionPendingContent(
    message: ChatMessage,
    onConfirm: (Map<String, String?>) -> Unit,
    onDismiss: () -> Unit
) {
    val fields: List<EditableField> = message.action?.dto?.getEditableFields() ?: emptyList()
    val fieldStates = remember(fields) {
        fields.associate { it.name to mutableStateOf(it.value ?: "") }.toMutableMap()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "准备执行操作: ${message.action?.actionName}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        fields.forEach { field ->
            when (field.type) {
                FieldType.TEXT -> {
                    EditableTextField(
                        label = field.label,
                        value = fieldStates[field.name]!!.value,
                        onValueChange = { newValue ->
                            fieldStates[field.name]?.value = newValue
                        }
                    )
                }

                FieldType.DATE -> {
                    DatePickerField(
                        label = field.label,
                        selectedDateEpoch = fieldStates[field.name]!!.value.toLongOrNull() ?: 0L,
                        onDateSelected = { epochSeconds ->
                            fieldStates[field.name]?.value = epochSeconds.toString()
                        }
                    )
                }

                FieldType.CATEGORY -> {
                    CategorySelectorField(
                        label = field.label,
                        selectedCategory = fieldStates[field.name]!!.value,
                        onCategorySelected = { selectedCategory ->
                            fieldStates[field.name]?.value = selectedCategory
                        }
                    )
                }

                FieldType.CURRENCY -> {
                    CurrencySelectorField(
                        label = field.label,
                        selectedCurrency = fieldStates[field.name]!!.value,
                        onCurrencySelected = { selectedCurrency ->
                            fieldStates[field.name]?.value = selectedCurrency
                        }
                    )
                }
                else -> {}
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { onDismiss() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("取消")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    val editedFields = fieldStates.mapValues { it.value.value }
                    onConfirm(editedFields)
                }
            ) {
                Text("确认")
            }
        }
    }
}

@Composable
private fun ActionCompletedContent(
    action: Action?,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "操作已完成: ${action?.actionName}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        // 可根据需要展示最终数据
        Text(
            text = "数据已成功插入/更新。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActionCancelledContent(
    action: Action?
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "操作已取消: ${action?.actionName}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "您已取消此操作。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// 以下为辅助字段编辑组件的代码（未改动逻辑，保持与原始类似）

@Composable
private fun EditableTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = ContentAlpha.disabled),
            errorContainerColor = MaterialTheme.colorScheme.errorContainer
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    label: String,
    selectedDateEpoch: Long,
    onDateSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val date = remember(selectedDateEpoch) {
        if (selectedDateEpoch > 0) {
            val instant = Instant.fromEpochSeconds(selectedDateEpoch)
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            "${localDateTime.year}-${localDateTime.monthNumber.toString().padStart(2, '0')}-${localDateTime.dayOfMonth.toString().padStart(2, '0')}"
        } else {
            ""
        }
    }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (date.isNotBlank()) date else "请选择日期",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (date.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "选择日期",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (expanded) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = if (selectedDateEpoch > 0) selectedDateEpoch * 1000 else null
            )
            DatePickerDialog(
                onDismissRequest = { expanded = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedDate = datePickerState.selectedDateMillis
                            if (selectedDate != null) {
                                val instant = Instant.fromEpochMilliseconds(selectedDate)
                                val localDate =
                                    instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
                                val epochSeconds =
                                    localDate.atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds
                                onDateSelected(epochSeconds)
                            }
                            expanded = false
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { expanded = false }) {
                        Text("取消")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun CategorySelectorField(
    label: String,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedLevel1 by remember { mutableStateOf("") }
    var selectedLevel2 by remember { mutableStateOf("") }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory.isNotBlank()) {
            try {
                val category = Category.valueOf(selectedCategory)
                category.parentCategory?.let { parent ->
                    selectedLevel1 = parent.categoryName
                    selectedLevel2 = category.categoryName
                } ?: run {
                    selectedLevel1 = ""
                    selectedLevel2 = ""
                }
            } catch (e: IllegalArgumentException) {
                selectedLevel1 = ""
                selectedLevel2 = ""
            }
        }
    }

    val transactionType = TransactionType.EXPENSE

    val level1Categories = when (transactionType) {
        TransactionType.EXPENSE -> Category.getExpenseCategories()
        TransactionType.INCOME -> Category.getIncomeCategories()
    }

    val level2Categories = remember(selectedLevel1) {
        if (selectedLevel1.isNotBlank()) {
            val parent = Category.fromCategoryName(selectedLevel1)
            parent?.let { Category.getSubCategories(it) } ?: emptyList()
        } else {
            emptyList()
        }
    }

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selectedCategory.isNotBlank()) selectedCategory else "请选择分类",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedCategory.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "选择分类",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (expanded) {
            AlertDialog(
                onDismissRequest = { expanded = false },
                title = { Text(text = "选择分类") },
                text = {
                    Column {
                        DropdownMenuItemList(
                            items = level1Categories.map { it.categoryName },
                            onItemSelected = { categoryName ->
                                selectedLevel1 = categoryName
                                selectedLevel2 = ""
                            },
                            label = "一级分类",
                            selectedItem = selectedLevel1
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DropdownMenuItemList(
                            items = level2Categories.map { it.categoryName },
                            onItemSelected = { categoryName ->
                                selectedLevel2 = categoryName
                                onCategorySelected(categoryName)
                                expanded = false
                            },
                            label = "二级分类",
                            selectedItem = selectedLevel2,
                            enabled = level2Categories.isNotEmpty()
                        )
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }
    }
}

@Composable
private fun DropdownMenuItemList(
    items: List<String>,
    onItemSelected: (String) -> Unit,
    label: String,
    selectedItem: String,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (items.isEmpty()) {
            Text(
                text = "暂无选项",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            items.forEach { item ->
                Text(
                    text = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = enabled) { onItemSelected(item) }
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun CurrencySelectorField(
    label: String,
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currencies = listOf("USD", "EUR", "CNY", "JPY")

    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selectedCurrency.isNotBlank()) selectedCurrency else "请选择货币",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedCurrency.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "选择货币",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (expanded) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            onCurrencySelected(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
