package app.penny.presentation.ui.screens.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.penny.domain.model.TransactionModel
import cafe.adriel.voyager.core.screen.Screen

class TransactionScreen : Screen {
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {


            }

        ) { }
    }
}


@Composable
fun TransactionRow(
    transaction: TransactionModel,
    modifier: Modifier = Modifier
) {
    Row {

        Icon(
            Icons.Default.Menu,
            contentDescription = "Category Icon"
        )

        Column {
            Text(text = transaction.category.categoryName) //
            Text(text = transaction.transactionDate.toString())
        }

        Text(text = transaction.amount.toString())

    }





}
