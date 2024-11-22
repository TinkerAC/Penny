package app.penny.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enum.Currency
import app.penny.feature.newLedger.NewLedgerViewModel
import org.jetbrains.compose.resources.painterResource


import app.penny.feature.newLedger.NewLedgerIntent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectorModal(
    onDismiss: () -> Unit,
    viewModel: NewLedgerViewModel
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxSize()
    ) {


        LazyColumn {
            items(Currency.entries.toTypedArray()) { currency ->
                Row(
                    modifier = Modifier.fillMaxSize().clickable(
                        onClick = {
                            viewModel.handleIntent(NewLedgerIntent.SelectCurrency(currency))
                            onDismiss()
                        })
                ) {
                    Image(
                        painter = painterResource(resource = currency.regionFlag),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp).clip(shape = CircleShape).border(
                            2.dp,
                            Color.Cyan, shape =
                            CircleShape
                        ) // 可选：为圆形添加边框
                    )

                    Text(
                        text = currency.name,
                        modifier = Modifier.padding(16.dp)
                    )

                    Text(
                        text = currency.currencyCode,
                        modifier = Modifier.padding(16.dp)
                    )

                }
            }
        }


    }
}