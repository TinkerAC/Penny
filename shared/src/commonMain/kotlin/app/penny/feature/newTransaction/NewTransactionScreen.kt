// file: shared/src/commonMain/kotlin/app/penny/feature/newTransaction/NewTransactionScreen.kt
// file: composeApp/src/commonMain/kotlin/app/penny/presentation/ui/screens/newTransaction/NewTransactionScreen.kt
package app.penny.feature.newTransaction

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import app.penny.core.domain.enumerate.Category
import app.penny.core.domain.enumerate.TransactionType
import app.penny.feature.newTransaction.component.CategorySelector
import app.penny.feature.newTransaction.component.NewTransactionTopBar
import app.penny.feature.newTransaction.component.NumPad
import app.penny.presentation.ui.components.LedgerSelectDialog
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.ScreenTransition
import co.touchlab.kermit.Logger
import dev.icerock.moko.resources.compose.stringResource
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class, ExperimentalVoyagerApi::class)
class NewTransactionScreen : Screen, ScreenTransition {

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {

        val keyboardController = LocalSoftwareKeyboardController.current//for hiding keyboard on IOS

        val viewModel = koinScreenModel<NewTransactionViewModel>()
        val navigator = LocalNavigator.currentOrThrow
        val uiState by viewModel.uiState.collectAsState()

        val tabs = TransactionType.entries.map { it.name }
        Logger.d { "Tabs: $tabs" }


        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is NewTransactionUiEvent.NavigateBack -> navigator.pop()
                    is NewTransactionUiEvent.ShowSnackBar -> {
                        uiState.snackBarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }

        Scaffold(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        keyboardController?.hide()
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = uiState.snackBarHostState) },
            topBar = {
                NewTransactionTopBar(
                    onBackClicked = { navigator.pop() },
                    uiState = uiState,
                    viewModel = viewModel
                )
            },

            floatingActionButton = {
                FloatingActionButton(onClick = {
                    viewModel.handleIntent(NewTransactionIntent.ShowNumPad)
                }) {
                    Icon(
                        imageVector = Icons.Default.Keyboard,
                        contentDescription = stringResource(SharedRes.strings.show_numpad)
                    )
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize(),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,

                    ) {
                    when (uiState.selectedTab) {
                        NewTransactionTab.EXPENSE -> {
                            val parentCategories = Category.getSubCategories(Category.EXPENSE)
                            CategorySelector(
                                modifier = Modifier.fillMaxWidth(),
                                primaryCategories = parentCategories,
                                viewModel = viewModel
                            )
                        }

                        NewTransactionTab.INCOME -> {
                            val parentCategories = Category.getSubCategories(Category.INCOME)
                            CategorySelector(
                                modifier = Modifier.fillMaxWidth(),
                                primaryCategories = parentCategories,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        )
        if (uiState.ledgerSelectDialogVisible) {
            LedgerSelectDialog(
                onLedgerSelected = { ledger ->
                    viewModel.handleIntent(NewTransactionIntent.SelectLedger(ledger))
                },
                onDismissRequest = {
                    viewModel.handleIntent(NewTransactionIntent.HideLedgerSelectDialog)
                },
                allLedgers = uiState.ledgers,
                currentLedger = uiState.selectedLedger!!,
            )
        }


        // 使用 ModalBottomSheet 来展示 Numpad
        if (uiState.showNumPad) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.handleIntent(NewTransactionIntent.HideNumPad)
                },
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                ),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NumPad(
                    amountText = uiState.amountText,
                    remarkText = uiState.remarkText,
                    doneButtonState = uiState.doneButtonState,
                    onRemarkChanged = viewModel::onRemarkChanged,
                    onNumPadButtonClicked = viewModel::onNumPadButtonClicked,
                    onDoneButtonClicked = viewModel::onDoneButtonClicked,
                    onCloseClicked = {
                        viewModel.handleIntent(NewTransactionIntent.HideNumPad)
                    },
                    currentLedger = uiState.selectedLedger!!,
                )
            }
        }
    }
}