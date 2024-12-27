package app.penny.feature.aiChat.components.editSheet

import CurrencySelectorField
import EditableTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.servershared.dto.LedgerDto

@Composable
fun LedgerEditSheet(
    // 传入初始的 LedgerDto
    ledgerDto: LedgerDto,
    // 当用户点击“保存”或需要将编辑后的结果返回时，通过该回调函数向上层导出最新的 LedgerDto
    onSubmit: (LedgerDto) -> Unit
) {
    // 通过 remember 将要编辑的字段保存在可组合函数状态中
    val ledgerName = remember { mutableStateOf(ledgerDto.name) }
    val currency = remember { mutableStateOf(ledgerDto.currencyCode) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 记账名称编辑
        EditableTextField(
            label = "记账名称",
            value = ledgerName.value,
            onValueChange = { newValue ->
                ledgerName.value = newValue
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 货币选择
        CurrencySelectorField(
            label = "选择货币",
            selectedCurrency = currency.value,
            onCurrencySelected = { selected ->
                currency.value = selected
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 保存/提交 按钮，将最新数据回传给上层
        Button(onClick = {
            // 复制一份新的 LedgerDto 并回调给上层
            val updatedLedgerDto = ledgerDto.copy(
                name = ledgerName.value,
                currencyCode = currency.value
            )
            onSubmit(updatedLedgerDto)
        }) {
            Text("保存")
        }
    }
}