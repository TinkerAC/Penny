package app.penny.feature.myLedger.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.core.domain.model.LedgerModel
import app.penny.feature.ledgerDetail.LedgerDetailScreen
import app.penny.shared.SharedRes
import cafe.adriel.voyager.navigator.Navigator
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Composable
fun LedgerCard(
    ledgerModel: LedgerModel,
    rootNavigator: Navigator,
    isDefault: Boolean = false,
    onSetDefault: (LedgerModel) -> Unit = {},
    onGetLedgerCount: (Uuid) -> Long
) {
    // 用于控制下拉菜单的展开/收起
    val (menuExpanded, setMenuExpanded) = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(false)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDefault) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .padding(16.dp)
            .border(
                2.dp,
                if (isDefault) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.5f
                ),
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 左侧图片
            Image(
                painter = painterResource(ledgerModel.cover.drawable),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .aspectRatio(1f)
            )

            // 右侧内容
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 顶部标题行
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = ledgerModel.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    // 菜单触发器
                    Box {
                        Column {
                            IconButton(
                                onClick = { setMenuExpanded(true) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (isDefault) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Default",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        // 下拉菜单
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { setMenuExpanded(false) }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(SharedRes.strings.edit)
                                    )
                                },
                                onClick = {
                                    setMenuExpanded(false)
                                    rootNavigator.push(LedgerDetailScreen(ledgerModel))
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Icon"
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        stringResource(SharedRes.strings.set_to_default)
                                    )
                                },
                                onClick = {
                                    setMenuExpanded(false)
                                    onSetDefault(ledgerModel)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Set Default Icon"
                                    )
                                }
                            )
                        }
                    }
                }

                Text(
                    text = "Entries: ${onGetLedgerCount(ledgerModel.uuid)}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
