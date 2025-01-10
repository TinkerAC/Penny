// file: src/commonMain/kotlin/app/penny/feature/analytics/component/topbar/AnalyticsTopBar.kt
package app.penny.feature.analytics.component.topbar

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.valueObject.YearMonth
import app.penny.feature.analytics.AnalyticTab
import app.penny.feature.analytics.AnalyticUiState
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.LocalDate

@Composable
fun AnalyticsTopBar(
    uiState: AnalyticUiState,
    onTabSelected: (AnalyticTab) -> Unit,
    onYearSelected: (Int) -> Unit,
    onYearMonthSelected: (YearMonth) -> Unit,
    onStartDateSelected: (LocalDate) -> Unit,
    onEndDateSelected: (LocalDate) -> Unit,
    onLedgerSelectionClick: () -> Unit
) {
    // 状态：是否展开
    var isExpanded by remember { mutableStateOf(true) }

    // 当选中的 Tab 改变时，自动展开内容
    LaunchedEffect(uiState.selectedTab) {
        isExpanded = true
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TopTabRow(
            selectedTab = uiState.selectedTab,
            onTabSelected = onTabSelected,
            onLedgerSelectionClick = onLedgerSelectionClick,
            isExpandable = uiState.selectedTab.hasContent(),
            isExpanded = isExpanded,
            onExpandToggle = { isExpanded = !isExpanded }
        )

        // 使用 AnimatedVisibility 控制内容的显示与隐藏
        AnimatedVisibility(
            visible = uiState.selectedTab.hasContent() && isExpanded,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(
                animationSpec = tween(durationMillis = 300)
            ),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .animateContentSize(animationSpec = tween(durationMillis = 300)),
                shape = MaterialTheme.shapes.medium,
                colors =CardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
                    disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = ContentAlpha.disabled)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ){
                    when (uiState.selectedTab) {
                        AnalyticTab.Monthly -> {
                            MonthlyTabContent(
                                selectedYearMonth = uiState.selectedYearMonth,
                                onYearMonthSelected = onYearMonthSelected,
                            )
                        }

                        AnalyticTab.Yearly -> {
                            YearlyTabContent(
                                selectedYear = uiState.selectedYear,
                                onYearSelected = onYearSelected
                            )
                        }

                        AnalyticTab.Custom -> {
                            CustomTabContent(
                                startDate = uiState.startDate,
                                endDate = uiState.endDate,
                                onStartDateSelected = onStartDateSelected,
                                onEndDateSelected = onEndDateSelected
                            )
                        }

                        else -> {
                            // 对于Recent选项，无需额外操作
                        }}

                }
            }
        }
    }
}

// 扩展函数，用于判断 Tab 是否具有内容
private fun AnalyticTab.hasContent(): Boolean {
    return when (this) {
        AnalyticTab.Monthly, AnalyticTab.Yearly, AnalyticTab.Custom -> true
        else -> false
    }
}

@Composable
private fun TopTabRow(
    selectedTab: AnalyticTab,
    onTabSelected: (AnalyticTab) -> Unit,
    onLedgerSelectionClick: () -> Unit,
    isExpandable: Boolean,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit
) {
    val tabs = AnalyticTab.entries

    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 可滚动的 TabRow
            ScrollableTabRow(
                selectedTabIndex = tabs.indexOf(selectedTab),
                edgePadding = 0.dp,
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        text = {
                            Text(
                                text = stringResource(tab.displayNameStringResource),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            // 如果当前 Tab 具有内容，则显示展开/收起按钮
            if (isExpandable) {
                IconButton(
                    onClick = onExpandToggle,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = if (isExpanded) {
                            "collapse"
                        } else {
                            "expand"
                        },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Ledger 选择按钮
            IconButton(
                onClick = onLedgerSelectionClick,
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = stringResource(SharedRes.strings.switch_ledger),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}