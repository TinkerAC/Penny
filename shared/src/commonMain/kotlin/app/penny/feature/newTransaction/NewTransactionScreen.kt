// file: composeApp/src/commonMain/kotlin/app/penny/presentation/ui/screens/newTransaction/NewTransactionScreen.kt
package app.penny.feature.newTransaction
import LedgerDropdownSelector
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
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
import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.LedgerModel
import app.penny.presentation.ui.components.CategorySelector
import app.penny.presentation.ui.components.numPad.NumPad
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.ScreenTransition
import co.touchlab.kermit.Logger
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalUuidApi::class, ExperimentalVoyagerApi::class)
class NewTransactionScreen : Screen, ScreenTransition {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val newTransactionViewModel = koinScreenModel<NewTransactionViewModel>()

        val rootNavigator = LocalNavigator.currentOrThrow

        val uiState by newTransactionViewModel.uiState.collectAsState()

        val tabs = TransactionType.entries.map { it.name }
        Logger.d("tabs: $tabs")

        val eventFlow = newTransactionViewModel.eventFlow

        // 监听事件流,处理Ui事件
        LaunchedEffect(Unit) {
            eventFlow.collect { event ->
                when (event) {
                    is NewTransactionUiEvent.NavigateBack -> {
                        rootNavigator.pop()
                    }

                    is NewTransactionUiEvent.ShowSnackBar -> {
                        uiState.snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }

                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = uiState.snackbarHostState) },
            topBar = {
                TopAppBar(title = {
                    Row {
                        Column(
                            modifier = Modifier.weight(2f)
                        ) {
                            Button(
                                onClick = {
                                    rootNavigator.pop()
                                }
                            ) {
                                Text("Back")
                            }
                        }
                        Column(
                            modifier = Modifier.weight(6f)
                        ) {
                            TabRow(selectedTabIndex = uiState.selectedTab.tabIndex) {
                                tabs.forEachIndexed { index, title ->
                                    Tab(
                                        text = { Text(title) },
                                        selected = uiState.selectedTab.tabIndex == index,
                                        onClick = {
                                            newTransactionViewModel.handleIntent(
                                                NewTransactionIntent.SelectTab(
                                                    NewTransactionTab.entries[index]
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        //Ledger Selector

                        Column(
                            modifier = Modifier.weight(2f)
                        ) {
                            LedgerDropdownSelector(
                                currentLedger = uiState.selectedLedger ?: LedgerModel(),
                                allLedgers = uiState.ledgers,
                                onLedgerSelected = { ledger ->
                                    newTransactionViewModel.handleIntent(
                                        NewTransactionIntent.SelectLedger(ledger)
                                    )
                                })
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
                        doneButtonState = uiState.doneButtonState,
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
                when (uiState.selectedTab) {
                    NewTransactionTab.EXPENSE -> {
                        val parentCategories = Category.getSubCategories(Category.EXPENSE)
                        CategorySelector(
                            modifier = Modifier,
                            parentCategories = parentCategories,
                            getSubCategories = { parent -> Category.getSubCategories(parent) },
                            getCategoryName = { category -> category.categoryName },
//        getCategoryIcon = { category -> category.categoryIcon },

                            viewModel = newTransactionViewModel
                        )
                    }

                    NewTransactionTab.INCOME -> {
                        val parentCategories = Category.getSubCategories(Category.INCOME)
                        CategorySelector(
                            modifier = Modifier,
                            parentCategories = parentCategories,
                            getSubCategories = { parent -> Category.getSubCategories(parent) },
                            getCategoryName = { category -> category.categoryName },
//        getCategoryIcon = { category -> category.categoryIcon },
                            viewModel = newTransactionViewModel
                        )
                    }
                }
            }
        }
    }

}

