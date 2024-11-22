package app.penny.core.domain.model

data class UserAchievementModel(
    val id: Long = 0,
    val achievementId: Long = 0,
    val progress: Long = 0,
    val completedAt: Long = 0,
)