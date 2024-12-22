package app.penny.feature.newTransaction.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.penny.feature.newTransaction.NewTransactionIntent
import app.penny.feature.newTransaction.NewTransactionTab
import app.penny.feature.newTransaction.NewTransactionUiState
import app.penny.feature.newTransaction.NewTransactionViewModel
import app.penny.presentation.ui.components.SliderToggleButton
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun NewTransactionTopBar(
    onBackClicked: () -> Unit,
    uiState: NewTransactionUiState,
    viewModel: NewTransactionViewModel
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(SharedRes.strings.back)
                )
            }
        },
        title = {
            SliderToggleButton(
                options = NewTransactionTab.entries.map { it.name },
                selectedIndex = uiState.selectedTab.tabIndex,
                onToggle = { index ->
                    viewModel.handleIntent(
                        NewTransactionIntent.SelectTab(
                            NewTransactionTab.entries[index]
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        actions = {
            IconButton(
                onClick = { viewModel.handleIntent(NewTransactionIntent.ShowLedgerSelectDialog) }
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = stringResource(SharedRes.strings.select_ledger)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    )
}
