package app.penny.feature.loanCalculator

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource


class LoanCalculatorScreen(
) : Screen {
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
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                OutlinedTextField(
                    value = uiState.value.loanAmount,
                    onValueChange = { viewModel.updateLoanAmount(it) },
                    label = {
                        Text(
                            stringResource(SharedRes.strings.loan_amount)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = uiState.value.interestRate,
                    onValueChange = { viewModel.updateInterestRate(it) },
                    label = { Text(stringResource(SharedRes.strings.annual_interest_rate)) },

                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
//                    Text("贷款期限（年）：")
                    Text(stringResource(SharedRes.strings.loan_term_years))
                    Spacer(modifier = Modifier.width(16.dp))
                    DropdownMenuButton(options = listOf(5, 10, 15, 20, 30),
                        selectedOption = uiState.value.loanYears,
                        onOptionSelected = { viewModel.updateLoanYears(it) })
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Text(stringResource(SharedRes.strings.repayment_method))
                    Spacer(modifier = Modifier.width(16.dp))
                    DropdownMenuButton(options = RepaymentMethod.entries,
                        selectedOption = uiState.value.repaymentMethod,
                        onOptionSelected = { viewModel.updateRepaymentMethod(it) })
                }

                Button(
                    onClick = { viewModel.calculateLoan() },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                ) {
                    Text(stringResource(SharedRes.strings.calculate))
                }

                if (uiState.value.errorMessage != null) {
                    Text(
                        text = stringResource(SharedRes.strings.error) + "${uiState.value.errorMessage}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    Text(
                        text = "每月还款额：${uiState.value.monthlyPayment} 元",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text =
                        "总支付利息：${uiState.value.totalInterest} 元",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text =
                        "总支付金额：${uiState.value.totalAmount} 元",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun <T> DropdownMenuButton(
    options: List<T>, selectedOption: T, onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = !expanded }) {
            Text(selectedOption.toString())
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
