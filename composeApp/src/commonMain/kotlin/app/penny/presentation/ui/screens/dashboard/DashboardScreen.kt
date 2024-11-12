// DashboardScreen.kt
package app.penny.presentation.ui.screens.dashboard

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.penny.presentation.ui.screens.myLedger.MyLedgerScreen
import app.penny.presentation.ui.screens.newTransaction.NewTransactionScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class DashboardScreen : Screen {

    @Composable
    override fun Content() {
        val dashboardViewModel = koinScreenModel<DashboardViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        val rootNavigator = navigator.parent

        Button(
            onClick = {
                rootNavigator?.push(NewTransactionScreen())
            }
        ) {
            Text("Add Transaction")
        }

        Button(
            onClick = {
                rootNavigator?.push(MyLedgerScreen())
            }
        ) {
            Text("My Ledger")
        }


        Button(
            onClick = {
                dashboardViewModel.handleIntent(DashboardIntent.insertRandomTransaction)
            }
        ) {
            Text("Insert Random Transaction")
        }


    }
}