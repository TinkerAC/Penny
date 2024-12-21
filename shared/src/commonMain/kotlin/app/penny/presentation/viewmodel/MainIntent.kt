package app.penny.presentation.viewmodel

import app.penny.presentation.ui.screens.BottomNavItem

sealed class MainIntent {
    class SelectBottomNavigationItem(val bottomNavItem: BottomNavItem) : MainIntent()
}