package app.penny.presentation.uiState

import app.penny.core.domain.model.LedgerModel
import app.penny.presentation.ui.screens.BottomNavItem

data class MainUiState(
    val ledgers: List<LedgerModel> = emptyList(),
    val firstTime: Boolean = false,
    val selectedNavigationItem: BottomNavItem = BottomNavItem.Dashboard,
    val navigationItems: List<BottomNavItem> = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Transactions,
        BottomNavItem.Analytics,
        BottomNavItem.Profile
    )
)