// file: composeApp/src/commonMain/kotlin/app/penny/presentation/ui/components/numPad/NumPad.kt
package app.penny.presentation.ui.components.numPad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun NumPad(
    amountText: String,
    remarkText: String,
    doneButtonState: DoneButtonState,
    onRemarkChanged: (String) -> Unit,
    onNumPadButtonClicked: (NumPadButton) -> Unit,
    onDoneButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
            .padding(16.dp)
    ) {
        // 金额显示和备注输入
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = remarkText,
                onValueChange = onRemarkChanged,
                label = { Text("备注") },
                placeholder = { Text("点击写备注") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CNY $amountText",
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = {
                // TODO: 插入照片逻辑，暂不实现
            }) {
                Icon(Icons.Default.Notifications, contentDescription = "添加附件")
            }
        }

        // 数字和功能按钮
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            // 第一行
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                NumPadButton(
                    numPadButton = NumPadButton.Number("1"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Number("2"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Number("3"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Backspace,
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
            }
            // 第二行
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                NumPadButton(
                    numPadButton = NumPadButton.Number("4"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(2f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Number("5"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(2f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Number("6"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(2f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Operator("+"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Operator("-"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
            }
            // 第三行
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                NumPadButton(
                    numPadButton = NumPadButton.Number("7"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(2f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Number("8"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(2f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Number("9"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(2f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Operator("×"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Operator("÷"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
            }
            // 第四行
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                NumPadButton(
                    numPadButton = NumPadButton.Function.AddAnotherTransaction,
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Number("0"),
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Decimal,
                    onClick = onNumPadButtonClicked,
                    modifier = Modifier.weight(1f)
                )
                NumPadButton(
                    numPadButton = NumPadButton.Function.Done,
                    onClick = { onDoneButtonClicked() },
                    text = doneButtonState.displayText,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun NumPadButton(
    numPadButton: NumPadButton,
    onClick: (NumPadButton) -> Unit,
    modifier: Modifier = Modifier,
    text: String = numPadButton.text
) {
    Button(
        onClick = {
            onClick(numPadButton)
        },
        modifier = modifier
    ) {
        Text(
            text,
            modifier = Modifier.align(Alignment.CenterVertically),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
