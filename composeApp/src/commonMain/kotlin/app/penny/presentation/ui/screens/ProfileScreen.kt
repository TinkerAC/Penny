// shared/src/commonMain/kotlin/app/penny/presentation/ui/screens/ProfileScreen.kt

package app.penny.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.penny.presentation.ui.screens.transactions.TransactionListScreen
import cafe.adriel.voyager.core.screen.Screen


class ProfileScreen : Screen {

    @Composable
    override fun Content() {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),

            ) {
//            TransactionListScreen().Content()

        }
    }
}