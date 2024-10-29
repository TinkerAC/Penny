package app.penny.domain.model

data class AchievementModel(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val iconUri: String = "",
    val goal: Long = 0

)