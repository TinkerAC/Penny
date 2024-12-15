package app.penny.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.core.domain.model.LedgerModel
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun LedgerCard(
    ledgerModel: LedgerModel
) {


    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.padding(8.dp)
    ) {

        Row {

            Column(
                modifier = Modifier.weight(4f)
            ) {
                Image(
                    painter = painterResource(ledgerModel.cover.drawable),
                    contentDescription = null,
                    modifier = Modifier.width(50.dp)
                )
            }


            Column(
                modifier = Modifier.weight(6f)


            ) {
                Row {
                    Text(
                        text = ledgerModel.name,
                        fontSize = 20.sp,
                        modifier = Modifier.weight(8f)
                    )

                    IconButton(
                        onClick = {//TODO: edit ledger|detail
                        },

                        modifier = Modifier.weight(2f)

                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = null
                        )
                    }
                }

                Text(
                    text = ledgerModel.balance.toString()
                )

                Text(
                    text = ledgerModel.count.toString(
                    )
                )


            }


        }

    }


}
