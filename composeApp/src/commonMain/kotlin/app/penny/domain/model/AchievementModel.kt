package app.penny.domain.model

data class AchievementModel(
    val id: Long = 0,
    val name: String = "",
    val description: String? = null,
    val iconUri: String = "", //TODO: Change to static  default icon
    val goal: Long = 0

)