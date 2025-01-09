package app.penny.feature.aiChat.components.messageBubble.system

import CategorySelectorField
import CurrencySelectorField
import DatePickerField
import EditableTextField
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enumerate.Category
import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.usecase.buildDto
import app.penny.servershared.EditableField
import app.penny.servershared.FieldType
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.enumerate.DtoAssociated
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource



@Composable
fun IntentPendingContent(
    message: SystemMessage, onConfirm: (BaseEntityDto) -> Unit, onDismiss: () -> Unit
) {
    // 检查 userIntent 是否实现了 DtoAssociated
    val dtoAssociated = message.userIntent as? DtoAssociated

    if (dtoAssociated == null) {
        Text(
            text = stringResource(SharedRes.strings.unknown_status),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )
        return
    }

    // 获取可编辑字段
    val fields: List<EditableField> = dtoAssociated.dto?.getEditableFields() ?: emptyList()

    // 管理编辑字段的状态
    val fieldStates = remember { mutableStateMapOf<String, String?>() }
    // 初始化字段状态
    LaunchedEffect(fields) {
        fields.forEach { field ->
            fieldStates[field.name] = field.value
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // 根据 DTO 类型渲染相应的编辑 Sheet
        when (val dto = dtoAssociated.dto) {
            is TransactionDto -> {
                TransactionEditSheetContent(
                    dtoFields = fields, fieldStates = fieldStates
                )
            }

            is LedgerDto -> {
                LedgerEditSheetContent(
                    dtoFields = fields, fieldStates = fieldStates
                )
            }

            else -> {
                DefaultEditSheetContent(
                    dtoFields = fields, fieldStates = fieldStates
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 渲染确认和取消按钮
        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onDismiss, colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = stringResource(SharedRes.strings.cancel))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                // 收集编辑字段的值
                val editedFields = fieldStates.toMap()

                val updatedDto = buildDto(
                    user = message.user,
                    userIntent = message.userIntent,
                    originalDto = dtoAssociated.dto,
                    editedFields = editedFields
                )

                onConfirm(updatedDto!!)
            }) {
                Text(text = stringResource(SharedRes.strings.confirm))
            }
        }
    }
}


@Composable
fun TransactionEditSheetContent(
    dtoFields: List<EditableField>, fieldStates: MutableMap<String, String?>
) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
        dtoFields.forEach { field ->
            when (field.type) {
                FieldType.TEXT -> {
                    EditableTextField(label = field.label,
                        value = fieldStates[field.name] ?: "",
                        onValueChange = { newValue ->
                            fieldStates[field.name] = newValue
                        })
                }

                FieldType.DATE -> {
                    DatePickerField(label = field.label,
                        selectedDateEpoch = fieldStates[field.name]?.toLongOrNull() ?: 0L,
                        onDateSelected = { epochSeconds ->
                            fieldStates[field.name] = epochSeconds.toString()
                        })
                }

                FieldType.CATEGORY -> {
                    val selectedCategory = fieldStates[field.name]
                    val currentCategory = try {
                        Category.valueOf(selectedCategory ?: "")
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                    CategorySelectorField(label = field.label,
                        selectedCategory = currentCategory,
                        onCategorySelected = { selectedCategoryEnum ->
                            fieldStates[field.name] = selectedCategoryEnum.name
                        })
                }

                FieldType.CURRENCY -> {
                    CurrencySelectorField(label = field.label,
                        selectedCurrency = fieldStates[field.name] ?: "",
                        onCurrencySelected = { selectedCurrency ->
                            fieldStates[field.name] = selectedCurrency
                        })
                }

                // 其他类型可继续扩展
                else -> {}
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
fun LedgerEditSheetContent(
    dtoFields: List<EditableField>, fieldStates: MutableMap<String, String?>
) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
        dtoFields.forEach { field ->
            when (field.type) {
                FieldType.TEXT -> {
                    EditableTextField(label = field.label,
                        value = fieldStates[field.name] ?: "",
                        onValueChange = { newValue ->
                            fieldStates[field.name] = newValue
                        })
                }

                FieldType.CURRENCY -> {
                    CurrencySelectorField(label = field.label,
                        selectedCurrency = fieldStates[field.name] ?: "",
                        onCurrencySelected = { selectedCurrency ->
                            fieldStates[field.name] = selectedCurrency
                        })
                }

                // 若 Ledger 也需要 date、category 等字段，可一并在此扩展
                else -> {}
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Composable
fun DefaultEditSheetContent(
    dtoFields: List<EditableField>, fieldStates: MutableMap<String, String?>
) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth()
    ) {
        dtoFields.forEach { field ->
            when (field.type) {
                FieldType.TEXT -> {
                    EditableTextField(label = field.label,
                        value = fieldStates[field.name] ?: "",
                        onValueChange = { newValue ->
                            fieldStates[field.name] = newValue
                        })
                }

                FieldType.DATE -> {
                    DatePickerField(label = field.label,
                        selectedDateEpoch = fieldStates[field.name]?.toLongOrNull() ?: 0,
                        onDateSelected = { newEpoch ->
                            fieldStates[field.name] = newEpoch.toString()
                        })
                }

                FieldType.CATEGORY -> {
                    val selectedCategory = fieldStates[field.name]
                    val currentCategory = try {
                        Category.valueOf(selectedCategory ?: "")
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                    CategorySelectorField(label = field.label,
                        selectedCategory = currentCategory,
                        onCategorySelected = { newCategory ->
                            fieldStates[field.name] = newCategory.name
                        })
                }

                FieldType.CURRENCY -> {
                    CurrencySelectorField(label = field.label,
                        selectedCurrency = fieldStates[field.name] ?: "",
                        onCurrencySelected = { newCurrency ->
                            fieldStates[field.name] = newCurrency
                        })
                }

                else -> {}
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

