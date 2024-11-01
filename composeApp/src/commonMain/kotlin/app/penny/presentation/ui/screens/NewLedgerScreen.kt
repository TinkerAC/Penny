package app.penny.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.domain.enum.LedgerCover
import app.penny.presentation.ui.components.CurrencySelectorModal
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import app.penny.presentation.viewmodel.NewLedgerViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import org.jetbrains.compose.resources.painterResource

class NewLedgerScreen : Screen {


    @Composable
    override fun Content() {


        val viewModel: NewLedgerViewModel = koinScreenModel<NewLedgerViewModel>()
        val uiState = viewModel.uiState.collectAsState()
        val rootNavigator = LocalNavigator.current


        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = "新建账本",
                    onNavigateBack = { rootNavigator?.pop() }
                )
            },

            ) {paddingValues ->

            Column(
                modifier = Modifier.padding(paddingValues)
            ) {

                Text("设置我的账本")

                TextField(
                    value = "",
                    onValueChange = { viewModel.setName(it) },
                    label = { Text("账本名称") }
                )


                Text("设置账本封面")
                LazyRow {
                    items(LedgerCover.entries) { cover ->
                        Column(
                            modifier = Modifier.clickable { viewModel.setCover(cover) }
                        ) {

                            Text(if (cover == uiState.value.ledgerCover) "选中" else "" + cover.coverName)
                            Image(
                                painter = painterResource(cover.drawable),
                                contentDescription = cover.coverName
                            )
                        }
                    }
                }

                Text("设置基础信息")

                Row(
                    modifier = Modifier.clickable {
                        viewModel.setCurrencySelectorModalVisible(true)
                    }
                ) {

                    Text(uiState.value.currency.currencyName + " " + uiState.value.currency.currencyCode)

                    Image(
                        painter = painterResource(uiState.value.currency.regionFlag),
                        contentDescription = uiState.value.currency.currencyCode,
                        modifier = Modifier.size(24.dp)
                    )
                }


                Button(onClick = {
                    viewModel.createNewLedger(
                        uiState.value.ledgerName,
                        uiState.value.ledgerCurrencyCode,
                        cover = uiState.value.ledgerCover,
                        description = uiState.value.ledgerDescription
                    )

                    rootNavigator?.pop()
                }) {
                    Text("创建账本")
                }

            }
        }

        if (uiState.value.currencySelectorModalVisible) {
            CurrencySelectorModal(
                onDismiss = { viewModel.setCurrencySelectorModalVisible(false) },
                viewModel = viewModel
            )
        }


    }
}