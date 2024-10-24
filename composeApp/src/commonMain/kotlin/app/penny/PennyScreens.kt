package app.penny

sealed class PennyScreens(val route: String) {
    data object Dashboard : PennyScreens("dashboard")
    data object Transactions : PennyScreens("transactions")
    data object Profile : PennyScreens("profile")
    data object Analytics : PennyScreens("analytics")
}