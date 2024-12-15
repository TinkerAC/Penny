// shared/src/commonMain/kotlin/app/penny/feature/newLedger/ui/NewLedgerScreen.kt
package app.penny.feature.newLedger

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enum.Currency
import app.penny.core.domain.enum.LedgerCover
import app.penny.presentation.ui.components.CurrencySelectorModal
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import org.jetbrains.compose.resources.painterResource

class NewLedgerScreen(
    private val callBack: (() -> Unit)
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
                        callBack()
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
                        callBack()
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
            text = "设置我的账本",
            style = MaterialTheme.typography.titleMedium
        )

        LedgerNameInput(
            name = uiState.ledgerName,
            onNameChange = { name -> onIntent(NewLedgerIntent.SetLedgerName(name)) }
        )

        Text(
            text = "设置账本封面",
            style = MaterialTheme.typography.titleMedium
        )

        LedgerCoverSelector(
            selectedCover = uiState.ledgerCover,
            onCoverSelected = { cover -> onIntent(NewLedgerIntent.SelectCover(cover)) }
        )

        Text(
            text = "设置基础信息",
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
        label = { Text("账本名称") },
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
                text = "${currency.currencyName} (${currency.currencyCode})",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource( currency.regionFlag),
                contentDescription = currency.currencyCode,
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
        Text(text = "创建账本")
    }
}
