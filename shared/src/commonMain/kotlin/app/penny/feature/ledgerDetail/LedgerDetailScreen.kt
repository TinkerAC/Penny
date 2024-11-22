package app.penny.feature.ledgerDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.core.domain.model.LedgerModel
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.painterResource

class LedgerDetailScreen(
    private val ledgerModel: LedgerModel
) : Screen {

    override val key: ScreenKey = "ledger_detail_${ledgerModel.id}"

    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<LedgerDetailViewModel>()
        val uiState = viewModel.uiState.collectAsState()

        val rootNavigator = LocalNavigator.currentOrThrow


        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = "账本详情",
                    onNavigateBack = {
                        rootNavigator.pop()
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {

                Card {
                    Row(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Image(
                            painter = painterResource(ledgerModel.cover.drawable),
                            contentDescription = null,
                            modifier = Modifier.width(50.dp).weight(3f)
                        )

                        Column(
                            modifier = Modifier.weight(7f)
                        ) {
                            Text(
                                text = "账本名称",
                                fontSize = 20.sp,
                                modifier = Modifier
                            )
                            Row(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                TextField(
                                    value = uiState.value.ledger.name,
                                    onValueChange = { /*TODO*/ },
                                    label = {
                                    },
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .weight(9f)
                                )


//                                IconButton(
//                                    onClick = {
//                                    modifier = Modifier.weight(1f)
//                                ) {
//                                    Icon(
//                                        Icons.Filled.Edit,
//                                        contentDescription = null
//                                    )
//                                }


                            }

                        }


                    }
                }



                Text("Basic Info")


                Text("Balance: ${ledgerModel.balance}")
                Text("Count: ${ledgerModel.count}")

                Text("Currency: ${ledgerModel.currency}")




                Button(
                    onClick = {
                        rootNavigator.pop()
                    }
                ) {
                    Text("保存")
                }
                Button(
                    onClick = {

                        viewModel.handleIntent(LedgerDetailIntent.DeleteLedger)

                        rootNavigator.pop()
                    }
                ) {
                    Text("删除Ledger")
                }


            }


        }


    }


}