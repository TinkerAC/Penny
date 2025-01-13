package app.penny.feature.currencyConvater

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enumerate.Currency
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

class CurrencyConverterScreen : Screen {
    @Composable
    override fun Content() {
        val localNavigator = LocalNavigator.currentOrThrow

        // 创建 ViewModel，并订阅 UIState
        val viewModel = koinScreenModel<CurrencyConverterViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        // 页面布局
        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = stringResource(SharedRes.strings.currency_converter),
                    onNavigateBack = { localNavigator.pop() },
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    // 第一个卡片（FromCurrency）
                    ConverterCard(
                        title = SharedRes.strings.from_currency,
                        currency = uiState.fromCurrency,
                        amount = uiState.amount,
                        isAmountEditable = true,
                        onAmountChange = { newAmount ->
                            // 当用户在第一个卡片输入金额时
                            viewModel.onAmountChanged(newAmount)
                        },
                        currencyList = viewModel.availableCurrencies,
                        onCurrencySelected = { newCurrency ->
                            viewModel.onFromCurrencyChanged(newCurrency)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 交换货币图标（圆形）
                    Box(
                        modifier = Modifier
                            .size(48.dp) // 确保 Box 是正方形，这样 CircleShape 裁剪出圆形
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                CircleShape
                            )
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                viewModel.onSwapCurrencies()
                            },
                            modifier = Modifier.size(40.dp) // 比外层略小
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Swap",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 第二个卡片（ToCurrency）
                    ConverterCard(
                        title = SharedRes.strings.to_currency,
                        currency = uiState.toCurrency,
                        amount = uiState.result,
                        // 让第二个卡片也能编辑
                        isAmountEditable = true,
                        onAmountChange = { newValue ->
                            // 当用户在第二个卡片输入金额时
                            viewModel.onResultChanged(newValue)
                        },
                        currencyList = viewModel.availableCurrencies,
                        onCurrencySelected = { newCurrency ->
                            viewModel.onToCurrencyChanged(newCurrency)
                        }
                    )
                }

                // 今日汇率展示
                Column {
                    Text(
                        text = stringResource(SharedRes.strings.today_exchange_rate),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "1 ${uiState.fromCurrency.code} = ${uiState.currentRate} ${uiState.toCurrency.code}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 *CurrencyConverterCard , the left side is a circular flag + currency drop-down box, and the right side is the amount input/display
 *
 *@param title card title
 * @param currency current currency
 * @param amount amount
 * @param isAmountEditable whether the amount can be edited
 * @param onAmountChange callback when the amount changes
 * @param currencyList supported currency list
 * @param onCurrencySelected callback when the currency is selected
 *
 */
@Composable
private fun ConverterCard(
    title: StringResource,
    currency: Currency,
    amount: String,
    isAmountEditable: Boolean,
    onAmountChange: (String) -> Unit,
    currencyList: List<Currency>,
    onCurrencySelected: (Currency) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column {
            // 标题
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // 货币选择行
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：国旗 + 下拉框
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 圆形国旗
                    Image(
                        painter = painterResource(currency.regionFlag),
                        contentDescription = null,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 货币下拉框
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Text(
                            text = currency.code + " " + if (expanded) "▲" else "▼",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { expanded = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            currencyList.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(text = c.code) },
                                    onClick = {
                                        expanded = false
                                        onCurrencySelected(c)
                                    }
                                )
                            }
                        }
                    }
                }

                // 右侧：金额输入/显示
                if (isAmountEditable) {
                    TextField(
                        value = amount,
                        onValueChange = { newValue ->
                            onAmountChange(newValue)
                        },
                        modifier = Modifier.width(180.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                } else {
                    // 只展示，不可编辑
                    Text(
                        text = amount,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
