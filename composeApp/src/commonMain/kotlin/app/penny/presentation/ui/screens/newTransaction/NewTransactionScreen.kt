// file: composeApp/src/commonMain/kotlin/app/penny/presentation/ui/screens/newTransaction/NewTransactionScreen.kt
package app.penny.presentation.ui.screens.newTransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.domain.enum.TransactionType
import app.penny.presentation.ui.components.numPad.NumPad
import app.penny.presentation.ui.components.numPad.NumPadButton
import app.penny.presentation.ui.components.numPad.NumPadViewModel
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.ScreenTransition

@OptIn(ExperimentalVoyagerApi::class)
class NewTransactionScreen : Screen, ScreenTransition {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val newTransactionViewModel = koinScreenModel<NewTransactionViewModel>()

        val rootNavigator = LocalNavigator.currentOrThrow

        val uiState by newTransactionViewModel.uiState.collectAsState()

        val tabs = TransactionType.entries.map { it.name }

        // 监听交易完成事件
        if (uiState.transactionCompleted) {
            LaunchedEffect(Unit) {
                rootNavigator.pop()
                newTransactionViewModel.resetTransactionCompleted()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Row {
                        Column(
                            modifier = Modifier.weight(2f)
                        ) {
                            Text("New Transaction")
                            // TODO: 返回按钮
                        }
                        Column(
                            modifier = Modifier.weight(8f)
                        ) {
                            TabRow(selectedTabIndex = uiState.selectedTab.tabIndex) {
                                tabs.forEachIndexed { index, title ->
                                    Tab(
                                        text = { Text(title) },
                                        selected = uiState.selectedTab.tabIndex == index,
                                        onClick = {
                                            newTransactionViewModel.handleIntent(
                                                NewTransactionIntent.SelectTab(NewTransactionTab.entries[index])
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                })
            },

            bottomBar = {
                BottomAppBar(
                    modifier = Modifier.height(300.dp)
                ) {
                    NumPad(
                        amountText = uiState.amountText,
                        remarkText = uiState.remarkText,
                        doneButtonText = uiState.doneButtonText,
                        onRemarkChanged = { newRemark ->
                            newTransactionViewModel.onRemarkChanged(newRemark)
                        },
                        onNumPadButtonClicked = { numPadButton ->
                            newTransactionViewModel.onNumPadButtonClicked(numPadButton)
                        },
                        onDoneButtonClicked = {
                            newTransactionViewModel.onDoneButtonClicked()
                        }
                    )
                }
            }

        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                when (uiState.selectedTab.tabIndex) {
                    0 -> {
                        NewExpenseTransactionTabContent(
                            onCategorySelected = { category ->
                                newTransactionViewModel.handleIntent(
                                    NewTransactionIntent.SelectExpenseCategory(category)
                                )
                            }
                        )
                    }

                    1 -> {
                        NewIncomeTransactionTabContent(
                            onCategorySelected = { category ->
                                newTransactionViewModel.handleIntent(
                                    NewTransactionIntent.SelectIncomeCategory(category)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

}
