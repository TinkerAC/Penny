package app.penny.presentation.ui.screens.myLedger

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import app.penny.presentation.ui.components.LedgerCard
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import app.penny.presentation.ui.screens.newLedger.NewLedgerScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class MyLedgerScreen : Screen {


    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow

        val viewModel = koinScreenModel<MyLedgerViewModel>()

        val uiState = viewModel.uiState.collectAsState()



        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = "我的账本",
                    onNavigateBack = {
                        rootNavigator.pop()
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                ) {
                    Button(
                        onClick = {
                            rootNavigator.push(NewLedgerScreen(
                                updateMyLedgerCallBack = {
                                    viewModel.handleIntent(MyLedgerIntent.RefreshLedgers)
                                }
                            ))
                        }
                    ) {
                        Text("新建账本")
                    }
                }
            }
        )
        { paddingValues ->
            LazyColumn (
                modifier = Modifier.padding(paddingValues)
            ){
                items(uiState.value.ledgers) { ledger ->
                    LedgerCard(
                        ledgerModel = ledger
                    )
                }
            }

        }


    }
}