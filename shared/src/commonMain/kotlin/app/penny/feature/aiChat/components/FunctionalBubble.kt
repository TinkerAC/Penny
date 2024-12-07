// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/components/FunctionalBubble.kt
package app.penny.feature.aiChat.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.Action

/**
 * 通用的功能气泡。
 * 根据传入的dto, 动态生成可编辑字段（通过dto.editableFields()），
 * 用户修改后点击确认，将编辑后的字段通过onConfirm返回。
 *
 * @param show 是否显示此气泡
 * @param action 要执行的Action
 * @param dto 待编辑的dto，可为空（如果为空表示无法解析dto，需要用户手动输入全部字段）
 * @param onConfirm 用户点击确认时回调 (editedFields: Map<String, String?>)
 * @param onDismiss 用户点击取消时回调
 */
@Composable
fun FunctionalBubble(
    show: Boolean,
    action: Action?,
    dto: BaseEntityDto?,
    onConfirm: (Map<String, String?>) -> Unit,
    onDismiss: () -> Unit
) {
    if (!show || action == null) return

    // 可编辑字段列表
    val fields = dto?.editableFields() ?: emptyList()

    // 使用remember存储编辑后的值
    val fieldStates = remember(fields) {
        fields.associate { (k, v) -> k to mutableStateOf(v ?: "") }.toMutableMap()
    }

    // 如果dto是null，可能需要用户完全新建(这里暂时示例只处理已有字段的情况)
    // 未来扩展可根据actionName定义默认字段

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
                Text(text = "准备执行操作: ${action.actionName}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                fields.forEach { (fieldName, _) ->
                    val state = fieldStates[fieldName]
                    if (state != null) {
                        OutlinedTextField(
                            value = state.value,
                            onValueChange = { state.value = it },
                            label = { Text(fieldName) },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = { onDismiss() }, modifier = Modifier.weight(1f)) {
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