// NumPad.kt

package app.penny.presentation.ui.components.numPad

import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

class NumPadScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<NumPadViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            // 金额显示和备注输入
            Row(
                verticalAlignment = Alignment.CenterVertically,

                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = uiState.remarkText,
                    onValueChange = { viewModel.onRemarkChanged(it) },
                    label = { Text("备注") },
                    placeholder = { Text("点击写备注") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "CNY ${uiState.amountText}",
                    style = MaterialTheme.typography.headlineMedium
                )
                IconButton(onClick = {
                    // TODO: 插入照片逻辑，暂不实现
                }) {
                    Icon(Icons.Default.Notifications, contentDescription = "添加附件")
                }
            }

//            Spacer(modifier = Modifier.height(16.dp))

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
                    NumPadButton(viewModel, NumPadButton.Number("1"), modifier = Modifier.weight(1f))
                    NumPadButton(viewModel, NumPadButton.Number("2"), modifier = Modifier.weight(1f))
                    NumPadButton(viewModel, NumPadButton.Number("3"), modifier = Modifier.weight(1f))
                    NumPadButton(viewModel, NumPadButton.Backspace, modifier = Modifier.weight(1f)
                    )
                }
                // 第二行
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NumPadButton(viewModel, NumPadButton.Number("4"), modifier = Modifier.weight(2f))
                    NumPadButton(viewModel, NumPadButton.Number("5"), modifier = Modifier.weight(2f))
                    NumPadButton(viewModel, NumPadButton.Number("6"), modifier = Modifier.weight(2f))
                    NumPadButton(viewModel, NumPadButton.Operator("+"), modifier = Modifier.weight(1f))
                    NumPadButton(viewModel, NumPadButton.Operator("-"), modifier = Modifier.weight(1f)
                    )
                }
                // 第三行
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NumPadButton(viewModel, NumPadButton.Number("7"), modifier = Modifier.weight(2f))
                    NumPadButton(viewModel, NumPadButton.Number("8"), modifier = Modifier.weight(2f))
                    NumPadButton(viewModel, NumPadButton.Number("9"), modifier = Modifier.weight(2f))
                    NumPadButton(viewModel, NumPadButton.Operator("×"), modifier = Modifier.weight(1f))
                    NumPadButton(viewModel, NumPadButton.Operator("÷"), modifier = Modifier.weight(1f))
                }
                // 第四行
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NumPadButton(
                        viewModel,
                        NumPadButton.Function.AddAnotherTransaction,
                        modifier = Modifier.weight(1f)
                    )
                    NumPadButton(viewModel, NumPadButton.Number("0"), modifier = Modifier.weight(1f))
                    NumPadButton(viewModel, NumPadButton.Decimal, modifier = Modifier.weight(1f))
                    NumPadButton(
                        viewModel,
                        NumPadButton.Function.Done,
                        text = uiState.doneButtonText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun NumPadButton(
    viewModel: NumPadViewModel,
    numPadButton: NumPadButton,
    modifier: Modifier = Modifier,
    text: String = numPadButton.text
) {
    Button(
        onClick = {
            if (numPadButton is NumPadButton.Function.Done) {
                viewModel.onDoneButtonClicked()
            } else {
                viewModel.onNumPadButtonClicked(numPadButton)
            }
        },
        modifier = modifier
    ) {
        Text(text)
    }
}
