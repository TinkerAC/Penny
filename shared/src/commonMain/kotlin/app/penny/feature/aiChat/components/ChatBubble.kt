package app.penny.feature.aiChat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.CalendarMonth
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.model.UserModel
import app.penny.servershared.EditableField
import app.penny.servershared.FieldType
import app.penny.servershared.enumerate.DtoAssociated
import app.penny.servershared.enumerate.UserIntent
import app.penny.servershared.enumerate.UserIntentStatus
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

@Composable
fun MessageBubble(
    message: ChatMessage,
    onActionConfirm: (SystemMessage, Map<String, String?>) -> Unit,
    onActionDismiss: (SystemMessage) -> Unit
) {
    val isUser = message.sender !== UserModel.System
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Image(
                painter = painterResource(SharedRes.images.avatar_penny),
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (isUser) {
            UserMessageBubble(message = message)
        } else {
            SystemMessageBubble(
                message = message as SystemMessage,
                onActionConfirm = onActionConfirm,
                onActionDismiss = onActionDismiss
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(SharedRes.images.avatar_boy),
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
            )
        }
    }
}

@Composable
fun UserMessageBubble(message: ChatMessage) {
    Card(
        shape = MaterialTheme.shapes.medium.copy(
            bottomStart = CornerSize(16.dp),
            bottomEnd = CornerSize(16.dp),
            topStart = CornerSize(16.dp),
            topEnd = CornerSize(0.dp)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.widthIn(max = 240.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = message.content ?: "null",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Composable
fun SystemMessageBubble(
    message: SystemMessage,
    onActionConfirm: (SystemMessage, Map<String, String?>) -> Unit,
    onActionDismiss: (SystemMessage) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium.copy(
            bottomStart = CornerSize(16.dp),
            bottomEnd = CornerSize(16.dp),
            topStart = CornerSize(0.dp),
            topEnd = CornerSize(16.dp)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.widthIn(max = 240.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = message.content ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when (message.userIntent?.status) {
                UserIntentStatus.Pending -> {
                    IntentPendingContent(message = message, onConfirm = { editedFields ->
                        onActionConfirm(message, editedFields)
                    }, onDismiss = {
                        onActionDismiss(message)
                    })
                }

                UserIntentStatus.Completed -> {
                    ActionCompletedContent(
                        userIntent = message.userIntent,
                    )
                }

                UserIntentStatus.Cancelled -> {
                    ActionCancelledContent(
                        userIntent = message.userIntent
                    )
                }

                null -> TODO()
            }
        }
    }
}

@Composable
private fun IntentPendingContent(
    message: SystemMessage, onConfirm: (Map<String, String?>) -> Unit, onDismiss: () -> Unit
) {

    if (message.userIntent !is DtoAssociated) {
        return androidx.compose.material3.Text(
            text = stringResource(SharedRes.strings.unknown_status),
            modifier = Modifier.padding(8.dp)
        )
    }

    val fields: List<EditableField> = message.userIntent.dto?.getEditableFields() ?: emptyList()
    val fieldStates = remember(fields) {
        fields.associate { it.name to mutableStateOf(it.value ?: "") }.toMutableMap()
    }

    androidx.compose.foundation.layout.Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
        androidx.compose.material3.Text(
            text = stringResource(SharedRes.strings.pending_action) + ": ${message.userIntent?.intentName}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))

        fields.forEach { field ->
            when (field.type) {
                FieldType.TEXT -> {
                    EditableTextField(label = field.label,
                        value = fieldStates[field.name]?.value ?: "",
                        onValueChange = { newValue ->
                            fieldStates[field.name]?.value = newValue
                        })
                }

                FieldType.DATE -> {
                    DatePickerField(label = field.label,
                        selectedDateEpoch = fieldStates[field.name]?.value?.toLongOrNull() ?: 0L,
                        onDateSelected = { epochSeconds ->
                            fieldStates[field.name]?.value = epochSeconds.toString()
                        })
                }

                FieldType.CATEGORY -> {
                    val selectedCategory = fieldStates[field.name]?.value
                    val currentCategory = remember(selectedCategory) {
                        try {
                            Category.valueOf(selectedCategory ?: "")
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    }
                    CategorySelectorField(label = field.label,
                        selectedCategory = currentCategory,
                        onCategorySelected = { selectedCategoryEnum ->
                            fieldStates[field.name]?.value = selectedCategoryEnum.name
                        })
                }

                FieldType.CURRENCY -> {
                    CurrencySelectorField(label = field.label,
                        selectedCurrency = fieldStates[field.name]?.value ?: "",
                        onCurrencySelected = { selectedCurrency ->
                            fieldStates[field.name]?.value = selectedCurrency
                        })
                }

                else -> {}
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { onDismiss() }, colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(stringResource(SharedRes.strings.cancel))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                val editedFields = fieldStates.mapValues { it.value.value }
                onConfirm(editedFields)
            }) {
                Text(stringResource(SharedRes.strings.confirm))
            }
        }
    }
}


@Composable
private fun ActionCompletedContent(
    userIntent: UserIntent?,
) {
    androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp)) {
        androidx.compose.material3.Text(
            text = stringResource(SharedRes.strings.action_completed) + ": ${userIntent?.intentName}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Text(
            text = stringResource(SharedRes.strings.action_completed_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun ActionCancelledContent(
    userIntent: UserIntent?
) {
    androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp)) {
        androidx.compose.material3.Text(
            text = stringResource(SharedRes.strings.action_cancelled) + ": ${userIntent?.intentName}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        androidx.compose.material3.Text(
            text = stringResource(SharedRes.strings.you_have_cancelled_the_action),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EditableTextField(
    label: String, value: String, onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
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
    label: String, selectedDateEpoch: Long, onDateSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val date = remember(selectedDateEpoch) {
        if (selectedDateEpoch > 0) {
            val instant = Instant.fromEpochSeconds(selectedDateEpoch)
            val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            "${localDateTime.year}-${
                localDateTime.monthNumber.toString().padStart(2, '0')
            }-${localDateTime.dayOfMonth.toString().padStart(2, '0')}"
        } else {
            ""
        }
    }

    androidx.compose.foundation.layout.Column {
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = true }.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (date.isNotBlank()) date else stringResource(SharedRes.strings.please_select_date),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (date.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = stringResource(SharedRes.strings.select_date),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (expanded) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = if (selectedDateEpoch > 0) selectedDateEpoch * 1000 else null
            )
            DatePickerDialog(onDismissRequest = { expanded = false }, confirmButton = {
                TextButton(onClick = {
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
                }) {
                    Text(stringResource(SharedRes.strings.confirm))
                }
            }, dismissButton = {
                TextButton(onClick = { expanded = false }) {
                    Text(stringResource(SharedRes.strings.cancel))
                }
            }) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun CategorySelectorField(
    label: String, selectedCategory: Category?, onCategorySelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLevel1 = remember { mutableStateOf<Category?>(null) }
    val selectedLevel2 = remember { mutableStateOf<Category?>(null) }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory != null) {
            selectedCategory.parentCategory?.let { parent ->
                selectedLevel1.value = parent
                selectedLevel2.value = selectedCategory
            } ?: run {
                selectedLevel1.value = null
                selectedLevel2.value = null
            }
        } else {
            selectedLevel1.value = null
            selectedLevel2.value = null
        }
    }

    val transactionType = TransactionType.EXPENSE

    val level1Categories = when (transactionType) {
        TransactionType.EXPENSE -> Category.getExpenseCategories()
        TransactionType.INCOME -> Category.getIncomeCategories()
    }

    val level2Categories = remember(selectedLevel1.value) {
        if (selectedLevel1.value != null) {
            val parent = selectedLevel1.value
            Category.getSubCategories(parent!!)
        } else {
            emptyList()
        }
    }

    androidx.compose.foundation.layout.Column {
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = true }.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = selectedCategory?.let { stringResource(it.categoryName) }
                    ?: stringResource(SharedRes.strings.please_select_category),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedCategory != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(SharedRes.strings.select_category),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (expanded) {
            AlertDialog(onDismissRequest = { expanded = false },
                title = { Text(text = stringResource(SharedRes.strings.select_category)) },
                text = {
                    androidx.compose.foundation.layout.Column {
                        DropdownMenuItemList(
                            items = level1Categories,
                            onItemSelected = { category ->
                                selectedLevel1.value = category
                                selectedLevel2.value = null
                            },
                            label = stringResource(SharedRes.strings.primary_category),
                            selectedItem = selectedLevel1.value,
                            enabled = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        DropdownMenuItemList(
                            items = level2Categories,
                            onItemSelected = { category ->
                                selectedLevel2.value = category
                                onCategorySelected(category)
                                expanded = false
                            },
                            label = stringResource(SharedRes.strings.secondary_category),
                            selectedItem = selectedLevel2.value,
                            enabled = level2Categories.isNotEmpty()
                        )
                    }
                },
                confirmButton = {},
                dismissButton = {})
        }
    }
}

@Composable
private fun CurrencySelectorField(
    label: String, selectedCurrency: String, onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currencies = listOf("USD", "EUR", "CNY", "JPY")

    androidx.compose.foundation.layout.Column {
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = true }.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (selectedCurrency.isNotBlank()) selectedCurrency else stringResource(
                        SharedRes.strings.please_select_currency
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedCurrency.isNotBlank()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(SharedRes.strings.select_currency),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (expanded) {
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                currencies.forEach { currency ->
                    DropdownMenuItem(text = { Text(currency) }, onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    })
                }
            }
        }
    }
}


@Composable
private fun DropdownMenuItemList(
    items: List<Category>,
    onItemSelected: (Category) -> Unit,
    label: String,
    selectedItem: Category?,
    enabled: Boolean = true
) {
    androidx.compose.foundation.layout.Column {
        androidx.compose.material3.Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (items.isEmpty()) {
            androidx.compose.material3.Text(
                text = stringResource(SharedRes.strings.no_options),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            items.forEach { item ->
                DropdownMenuItem(text = { Text(stringResource(item.categoryName)) }, onClick = {
                    onItemSelected(item)
                }, enabled = enabled
                )
            }
        }
    }
}
