// shared/src/commonMain/kotlin/app/penny/feature/newLedger/ui/NewLedgerScreen.kt
package app.penny.feature.newLedger

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enumerate.Currency
import app.penny.core.domain.enumerate.LedgerCover
import app.penny.presentation.ui.components.CurrencySelectorModal
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

class NewLedgerScreen(
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: NewLedgerViewModel = koinScreenModel()
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.current

        // 处理事件流
        LaunchedEffect(Unit) {
            viewModel.eventFlow.collect { event ->
                when (event) {
                    is NewLedgerUiEvent.ShowSnackBar -> {
                        uiState.snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }

                    is NewLedgerUiEvent.OnFinishInsert -> {
                        navigator?.pop()
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = "新建账本",
                    onNavigateBack = {
                        navigator?.pop()
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = uiState.snackbarHostState) },
            content = { paddingValues ->
                NewLedgerContent(
                    uiState = uiState,
                    onIntent = { intent -> viewModel.handleIntent(intent) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        )

        if (uiState.currencySelectorModalVisible) {
            CurrencySelectorModal(
                onDismiss = { viewModel.handleIntent(NewLedgerIntent.CloseCurrencySelectorModal) },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun NewLedgerContent(
    uiState: NewLedgerUiState,
    onIntent: (NewLedgerIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(SharedRes.strings.ledger_name),
            style = MaterialTheme.typography.titleMedium
        )

        LedgerNameInput(
            name = uiState.ledgerName,
            onNameChange = { name -> onIntent(NewLedgerIntent.SetLedgerName(name)) }
        )

        Text(
            text = stringResource(SharedRes.strings.ledger_cover),
            style = MaterialTheme.typography.titleMedium
        )

        LedgerCoverSelector(
            selectedCover = uiState.ledgerCover,
            onCoverSelected = { cover -> onIntent(NewLedgerIntent.SelectCover(cover)) }
        )

        Text(
            text = stringResource(SharedRes.strings.basic_info),
            style = MaterialTheme.typography.titleMedium
        )

        CurrencySelector(
            currency = uiState.currency,
            onClick = { onIntent(NewLedgerIntent.OpenCurrencySelectorModal) }
        )

        CreateLedgerButton(
            isLoading = uiState.isLoading,
            onClick = { onIntent(NewLedgerIntent.ConfirmCreateLedger) }
        )
    }
}

@Composable
fun LedgerNameInput(
    name: String,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text(text = stringResource(SharedRes.strings.ledger_name)) },
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun LedgerCoverSelector(
    selectedCover: LedgerCover,
    onCoverSelected: (LedgerCover) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(LedgerCover.entries.toTypedArray()) { cover ->
            LedgerCoverItem(
                cover = cover,
                isSelected = cover == selectedCover,
                onClick = { onCoverSelected(cover) }
            )
        }
    }
}

@Composable
fun LedgerCoverItem(
    cover: LedgerCover,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            modifier = Modifier.size(128.dp)
        ) {
            Image(
                painter = painterResource(cover.drawable),
                contentDescription = cover.coverName,
                modifier = Modifier
                    .size(48.dp)
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = cover.coverName,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun CurrencySelector(
    currency: Currency,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${currency.displayName} (${currency.code})",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(currency.regionFlag),
                contentDescription = currency.code,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CreateLedgerButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = stringResource(SharedRes.strings.create_ledger))
    }
}
