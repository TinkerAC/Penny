package app.penny.feature.myLedger

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import app.penny.feature.myLedger.component.LedgerCard
import app.penny.feature.newLedger.NewLedgerScreen
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource
import kotlin.uuid.ExperimentalUuidApi

class MyLedgerScreen : Screen {

    @OptIn(ExperimentalUuidApi::class)
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<MyLedgerViewModel>()
        val uiState = viewModel.uiState.collectAsState()


        LaunchedEffect(Unit) {
            viewModel.refreshData()
        }

        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = stringResource(SharedRes.strings.my_ledger),
                    onNavigateBack = {
                        rootNavigator.pop()
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        rootNavigator.push(NewLedgerScreen())
                    }) {
                    Icon(
                        imageVector = Icons.Filled.PostAdd,
                        contentDescription = stringResource(SharedRes.strings.show_numpad)
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        )
        { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues)
            ) {
                items(uiState.value.ledgers) { ledger ->
                    LedgerCard(
                        ledgerModel = ledger,
                        rootNavigator = rootNavigator,
                        isDefault = uiState.value.defaultLedger?.uuid == ledger.uuid,
                        onSetDefault = {
                            viewModel.handleIntent(
                                MyLedgerIntent.SetDefaultLedger(it)
                            )
                        }
                    )
                }
            }

        }


    }
}