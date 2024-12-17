// file: shared/src/commonMain/kotlin/app/penny/feature/dashBoard/DashboardIntent.kt
package app.penny.feature.dashBoard

sealed class DashboardIntent {
    data class Scroll(val delta: Float) : DashboardIntent()
    object Release : DashboardIntent()
}