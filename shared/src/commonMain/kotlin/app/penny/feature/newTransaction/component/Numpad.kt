// file: shared/src/commonMain/kotlin/app/penny/feature/newTransaction/component/Numpad.kt
package app.penny.feature.newTransaction.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource


@Composable
fun NumPad(
    amountText: String,
    remarkText: String,
    doneButtonState: DoneButtonState,
    onRemarkChanged: (String) -> Unit,
    onNumPadButtonClicked: (NumPadButton) -> Unit,
    onDoneButtonClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 金额显示和备注输入
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = remarkText,
                onValueChange = onRemarkChanged,
                label = {
                    Text(
                        stringResource(SharedRes.strings.remarks),
                    )
                },
                placeholder = {
                    Text(
                        stringResource(SharedRes.strings.tap_to_add_remarks)
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "CNY $amountText",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
            IconButton(
                onClick = {
                    // TODO: 插入照片逻辑，暂不实现
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "添加附件",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onCloseClicked) {
                Icon(
                    imageVector = Icons.Default.KeyboardHide,
                    contentDescription = stringResource(SharedRes.strings.hide_numpad),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )

        // 数字和功能按钮
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                listOf(
                    NumPadButton.Number("1"),
                    NumPadButton.Number("2"),
                    NumPadButton.Number("3"),
                    NumPadButton.Backspace
                ),
                listOf(
                    NumPadButton.Number("4"),
                    NumPadButton.Number("5"),
                    NumPadButton.Number("6"),
                    NumPadButton.Operator("+"),
                    NumPadButton.Operator("-")
                ),
                listOf(
                    NumPadButton.Number("7"),
                    NumPadButton.Number("8"),
                    NumPadButton.Number("9"),
                    NumPadButton.Operator("×"),
                    NumPadButton.Operator("÷")
                ),
                listOf(
                    NumPadButton.Function.AddAnotherTransaction,
                    NumPadButton.Number("0"),
                    NumPadButton.Decimal,
                    NumPadButton.Function.Done
                )
            ).forEach { rowButtons ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    rowButtons.forEach { button ->
                        when (button) {
                            is NumPadButton.Function.Done -> {
                                ActionButton(
                                    text = doneButtonState.displayText,
                                    onClick = onDoneButtonClicked,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                )
                            }

                            is NumPadButton.Backspace -> {
                                IconButton(
                                    onClick = { onNumPadButtonClicked(button) },
                                    modifier = Modifier
                                        .weight(2f)
                                        .height(56.dp)
                                        .aspectRatio(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "删除",
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            else -> {
                                NumPadButtonComponent(
                                    numPadButton = button,
                                    onClick = onNumPadButtonClicked,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(56.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NumPadButtonComponent(
    numPadButton: NumPadButton,
    onClick: (NumPadButton) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onClick(numPadButton) },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    ) {
        Text(
            text = numPadButton.text,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
    }
}