package app.penny.feature.ledgerDetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.enumerate.Currency
import app.penny.presentation.ui.components.CurrencyListItem
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun BasicInfoSection(
    currency: Currency
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 标题部分
        Text(
            text = stringResource(SharedRes.strings.basic_info),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp)) // 增大间距，使布局更宽松

        // 显示货币部分，并为其添加下划线
        Text(
            text = stringResource(SharedRes.strings.currency),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        // 在这里添加下划线
        Spacer(modifier = Modifier.height(4.dp)) // 调整下划线与文本之间的间距
        Box(
            modifier = Modifier

                .height(2.dp) // 下划线的高度
                .background(MaterialTheme.colorScheme.primary) // 下划线颜色
        )

        CurrencyListItem(
            currency = currency,
            onClick = {}
        )

    }
}