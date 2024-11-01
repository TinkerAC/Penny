package app.penny.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.penny.domain.model.LedgerModel
import org.jetbrains.compose.resources.imageResource
import penny.composeapp.generated.resources.Res
import penny.composeapp.generated.resources.svg_pie_chart

@Composable
fun LedgerCard(
    ledgerModel: LedgerModel
) {

    Card {

        Image(
            imageResource(Res.drawable.svg_pie_chart),
            contentDescription = null
        )
        Column {
            Text(text = ledgerModel.name)
            Text(text = ledgerModel.balance.toString())
        }
        Row {
            //用URI 加载图片
            Image(
                imageResource(Res.drawable.svg_pie_chart),
                contentDescription = null
            )
            Column {
                Text(text = ledgerModel.name)
                Text(text = ledgerModel.balance.toString())
            }
        }

    }


}
