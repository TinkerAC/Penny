package app.penny.presentation.ui.screens

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class MyLedgerScreen : Screen {


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {


        val rootNavigator = LocalNavigator.currentOrThrow

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
                            rootNavigator.push(NewLedgerScreen())
                        }
                    ) {
                        Text("新建账本")
                    }
                }
            }
        )
        {

        }


    }
}