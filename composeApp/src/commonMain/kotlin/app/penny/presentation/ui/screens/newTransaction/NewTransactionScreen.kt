package app.penny.presentation.ui.screens.newTransaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.domain.enum.TransactionType
import app.penny.presentation.ui.components.numPad.NumPadScreen
import app.penny.presentation.ui.components.numPad.NumPadViewModel
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.ScreenTransition

@OptIn(ExperimentalVoyagerApi::class)
class NewTransactionScreen : Screen, ScreenTransition {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val newTransactionViewModel = koinScreenModel<NewTransactionViewModel>()

        val numPadViewModel = koinScreenModel<NumPadViewModel>()

        val rootNavigator = LocalNavigator.currentOrThrow

        val selectedTabIndex = remember { mutableStateOf(0) }


        val tabs = TransactionType.entries.map { it.name }

        Scaffold(
            topBar = {
                TopAppBar(title = {
                    Row(
                    ) {
                        Column(
                            modifier = Modifier.weight(2f)
                        ) {

                            Text("New Transaction")//TODO: Navigate back button

                        }
                        Column(
                            modifier = Modifier.weight(8f)
                        ) {
                            TabRow(selectedTabIndex = selectedTabIndex.value) {
                                tabs.forEachIndexed { index, title ->
                                    Tab(text = { Text(title) },
                                        selected = selectedTabIndex.value == index,
                                        onClick = { selectedTabIndex.value = index }
                                    )
                                }
                            }
                        }
                    }
                })
            },

            bottomBar = {
                //TODO: The NumPad component

                BottomAppBar(
                    modifier = Modifier.height(350.dp)
                    //why wrapContentSize didn't work?
                ) {
                    NumPadScreen().Content()
                }

            }

        )
        { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                when (selectedTabIndex.value) {
                    0 -> {
                        NewExpenseTransactionTabContent()
                    }

                    1 -> {
                        NewIncomeTransactionTabContent()
                    }
                }
            }
        }
    }

}




