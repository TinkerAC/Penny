// file: src/commonMain/kotlin/app/penny/feature/ledgerDetail/LedgerDetailScreen.kt
package app.penny.feature.ledgerDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.LedgerModel
import app.penny.feature.ledgerDetail.component.BalanceSummarySection
import app.penny.feature.ledgerDetail.component.BasicInfoSection
import app.penny.feature.ledgerDetail.component.LedgerCard
import app.penny.platform.getRawStringResource
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalUuidApi::class)
class LedgerDetailScreen(
    private val ledger: LedgerModel
) : Screen {

    override val key: ScreenKey = "ledger_detail_${ledger.uuid}"

    @Composable
    override fun Content() {
        val snackbarHostState = remember { SnackbarHostState() }

        val viewModel = koinScreenModel<LedgerDetailViewModel> {
            parametersOf(ledger)
        }
        val uiState = viewModel.uiState.collectAsState()

        val rootNavigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is LedgerDetailUiEvent.ShowSnackBar ->
                        snackbarHostState.showSnackbar(
                            message = getRawStringResource(event.message),
                            duration = SnackbarDuration.Short
                        )
                }
            }
        }

        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.padding(16.dp)
                )
            },
            topBar = {
                SingleNavigateBackTopBar(
                    title = stringResource(SharedRes.strings.ledger_details),
                    onNavigateBack = { rootNavigator.pop() }
                )
            },
            content = { innerPadding ->
                LedgerDetailContent(
                    uiState = uiState.value,
                    onNameChange = { newName ->
                        viewModel.handleIntent(
                            LedgerDetailIntent.ChangeName(newName)
                        )
                    },
                    modifier = Modifier.padding(innerPadding)
                )

            },

            bottomBar = {
                Row(
                    modifier =
                    Modifier.fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // 添加按钮间的间距

                ) {
                    Button(
                        onClick = {
                            viewModel.handleIntent(LedgerDetailIntent.SaveLedger)
                            rootNavigator.pop()
                        },
                        modifier = Modifier.weight(1f) // 使用 weight 让按钮平分宽度
                    ) {
                        Text(stringResource(SharedRes.strings.save))
                    }
                    Button(
                        onClick = {
                            viewModel.handleIntent(LedgerDetailIntent.DeleteLedger)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(
                                alpha = 0.8f
                            )
                        ),
                        modifier = Modifier.weight(1f) // 使用 weight 让按钮平分宽度
                    ) {
                        Text(
                            stringResource(SharedRes.strings.delete),
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }


        )
    }
}

@Composable
fun LedgerDetailContent(
    uiState: LedgerDetailUiState,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LedgerCard(
            ledger = uiState.ledger,
            name = uiState.ledger.name,
            entryCount = uiState.entryCount,
            onNameChange = onNameChange
        )

        BalanceSummarySection(
            currencySymbol = uiState.ledger.currency.currencySymbol,
            totalIncome = uiState.totalIncome,
            totalExpense = uiState.totalExpense,
            balance = uiState.balance
        )


        BasicInfoSection(
            currency = uiState.ledger.currency
        )

        Spacer(modifier = Modifier.weight(1f))


    }
}