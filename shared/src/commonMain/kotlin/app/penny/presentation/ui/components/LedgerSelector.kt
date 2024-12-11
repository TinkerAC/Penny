import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.LedgerModel
import androidx.compose.ui.text.style.TextOverflow
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.ledger_cover_default
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun LedgerDropdownSelector(
    allLedgers: List<LedgerModel>,
    currentLedger: LedgerModel,
    onLedgerSelected: (LedgerModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) } // 控制下拉菜单的显示状态

    Box(modifier = modifier) {
        // 显示当前选中的 Ledger
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = currentLedger.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // DropdownMenu 显示所有 Ledger
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            allLedgers.forEach { ledger ->
                LedgerDropdownItem(
                    ledger = ledger,
                    isSelected = ledger.uuid == currentLedger.uuid,
                    onClick = {
                        onLedgerSelected(ledger)
                        expanded = false // 选中后关闭菜单
                    }
                )
            }
        }
    }
}

@Composable
fun LedgerDropdownItem(
    ledger: LedgerModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(backgroundColor)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 圆形图标显示 Cover
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                painter = painterResource(Res.drawable.ledger_cover_default),
                contentDescription = "${ledger.cover.name} Cover",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Ledger 名称和货币代码
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = ledger.name,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = ledger.currency.currencyCode,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // 可选的选中标记
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}