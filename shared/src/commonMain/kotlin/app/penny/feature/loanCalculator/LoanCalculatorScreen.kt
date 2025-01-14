// file: /Users/tinker/StudioProjects/Penny/shared/src/commonMain/kotlin/app/penny/feature/loanCalculator/LoanCalculatorScreen.kt
package app.penny.feature.loanCalculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource

class LoanCalculatorScreen : Screen {
    @Composable
    override fun Content() {
        val localNavigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<LoanCalculatorViewModel>()
        val uiState = viewModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = stringResource(SharedRes.strings.tool_loan_calculator),
                    onNavigateBack = {
                        localNavigator.pop()
                    }
                )
            }
        ) { paddingValues ->
            // 使用 LazyColumn 实现更灵活的布局和滑动
            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ============= 输入区域的 Card =============
                item {
                    Card(
                        modifier = Modifier.fillParentMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        // 设置整体内容的 padding
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.value.loanAmount,
                                onValueChange = { viewModel.updateLoanAmount(it) },
                                label = {
                                    Text(stringResource(SharedRes.strings.loan_amount))
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillParentMaxWidth() // 不需要额外 padding
                            )

                            Spacer(modifier = Modifier.height(16.dp)) // 添加组件间距

                            OutlinedTextField(
                                value = uiState.value.interestRate,
                                onValueChange = { viewModel.updateInterestRate(it) },
                                label = {
                                    Text(stringResource(SharedRes.strings.annual_interest_rate) + "(%)")
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier
                                    .fillParentMaxWidth() // 不需要额外 padding
                            )

                            Spacer(modifier = Modifier.height(16.dp)) // 添加组件间距

                            // 贷款年限
                            RowWithLabelAndDropdown(
                                label = stringResource(SharedRes.strings.loan_term_years),
                                options = listOf(5, 10, 15, 20, 30),
                                selectedOption = uiState.value.loanYears,
                                onOptionSelected = { viewModel.updateLoanYears(it) }
                            )

                            Spacer(modifier = Modifier.height(16.dp)) // 添加组件间距

                            // 还款方式
                            RowWithLabelAndDropdown(
                                label = stringResource(SharedRes.strings.repayment_method),
                                options = RepaymentMethod.entries.toList(),
                                selectedOption = uiState.value.repaymentMethod,
                                onOptionSelected = { viewModel.updateRepaymentMethod(it) }
                            )
                        }
                    }
                }


                // ============= 计算按钮 =============
                item {
                    Button(
                        onClick = { viewModel.calculateLoan() },
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(stringResource(SharedRes.strings.calculate))
                    }
                }

                // ============= 结果区域的 Card =============
                item {
                    Card(
                        modifier = Modifier.fillParentMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                        ),
                        elevation = CardDefaults.cardElevation(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            if (uiState.value.errorMessage != null) {
                                Text(
                                    text = stringResource(SharedRes.strings.error) +
                                            " ${uiState.value.errorMessage}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                Text(
                                    text =
                                    "${stringResource(SharedRes.strings.monthly_payment)}：${uiState.value.monthlyPayment} ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                Text(
                                    text = "${stringResource(SharedRes.strings.total_interest)}：${uiState.value.totalInterest} ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                Text(
                                    text = "${stringResource(SharedRes.strings.total_payment)}：${uiState.value.totalAmount} ",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(vertical = 8.dp)
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
private fun <T> RowWithLabelAndDropdown(
    label: String,
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    androidx.compose.foundation.layout.Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(label)
        Spacer(modifier = Modifier.width(16.dp))

        DropdownMenuButton(
            options = options,
            selectedOption = selectedOption,
            onOptionSelected = onOptionSelected
        )
    }
}

/**
 * 下拉选择按钮，点击后弹出下拉菜单。
 */
@Composable
fun <T> DropdownMenuButton(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = !expanded }) {
            Text(
                selectedOption.toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    text = { Text(option.toString()) }
                )
            }
        }
    }
}
