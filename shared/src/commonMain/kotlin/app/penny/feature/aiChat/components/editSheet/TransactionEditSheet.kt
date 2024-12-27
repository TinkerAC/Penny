package app.penny.feature.aiChat.components.editSheet

import CategorySelectorField
import CurrencySelectorField
import DatePickerField
import EditableTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enum.Category
import app.penny.servershared.dto.TransactionDto


@Composable
fun TransactionEditSheet(
    // 传入初始的交易数据
    transactionDto: TransactionDto,
    // 编辑完成后，通过 onSubmit 将更新后的 TransactionDto 回调给上层
    onSubmit: (TransactionDto) -> Unit
) {
    // 将待编辑字段存储到 Compose 状态中
    val transactionType = remember { mutableStateOf(transactionDto.transactionType) }
    val transactionDate = remember { mutableStateOf(transactionDto.transactionDate) }
    val categoryName = remember { mutableStateOf(transactionDto.categoryName) }
    val currencyCode = remember { mutableStateOf(transactionDto.currencyCode) }
    val amount = remember { mutableStateOf(transactionDto.amount) }
    val remark = remember { mutableStateOf(transactionDto.remark ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 1. 交易类型
        EditableTextField(
            label = "交易类型",
            value = transactionType.value,
            onValueChange = { newValue ->
                transactionType.value = newValue
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. 交易日期
        DatePickerField(
            label = "交易日期",
            selectedDateEpoch = transactionDate.value,
            onDateSelected = { selectedEpoch ->
                transactionDate.value = selectedEpoch
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. 分类
        // 这里需要将字符串转换为 Category；若不确定字符串是否合法，可做异常处理
        val selectedCategory = try {
            Category.valueOf(categoryName.value)
        } catch (e: Exception) {
            null
        }
        CategorySelectorField(
            label = "分类",
            selectedCategory = selectedCategory,
            onCategorySelected = { cat ->
                // 将选中的分类名保存回来
                categoryName.value = cat.name
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. 货币
        CurrencySelectorField(
            label = "货币",
            selectedCurrency = currencyCode.value,
            onCurrencySelected = { selected ->
                currencyCode.value = selected
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 5. 金额
        EditableTextField(
            label = "金额",
            value = amount.value,
            onValueChange = { newValue ->
                amount.value = newValue
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 6. 备注
        EditableTextField(
            label = "备注",
            value = remark.value,
            onValueChange = { newValue ->
                remark.value = newValue
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 保存按钮
        Button(
            onClick = {
                // 复制最新修改的数据到 TransactionDto，再通过 onSubmit 回传给上层
                val updatedTransaction = transactionDto.copy(
                    transactionType = transactionType.value,
                    transactionDate = transactionDate.value,
                    categoryName = categoryName.value,
                    currencyCode = currencyCode.value,
                    amount = amount.value,
                    remark = remark.value
                )
                onSubmit(updatedTransaction)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存")
        }
    }
}