// DashboardScreen.kt
package app.penny.feature.dashboard

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.penny.feature.myLedger.MyLedgerScreen
import app.penny.feature.newTransaction.NewTransactionScreen
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
                dashboardViewModel.handleIntent(DashboardIntent.InsertRandomTransaction)
            }
        ) {
            Text("Insert Random Transaction")
        }

        Button(
            onClick = {
                dashboardViewModel.handleIntent(DashboardIntent.UploadUpdatedLedgers)
            }
        ) {
            Text("Upload Ledgers")
        }

        Button(
            onClick = {
                dashboardViewModel.handleIntent(DashboardIntent.ClearUserData)
            }
        ) {
            Text("Clear User Data(Token, User Name, User Email)")
        }

        Text("Last Synced At: ${dashboardViewModel.uiState.value.lastSyncedAt ?: "Never"}")


    }
}
