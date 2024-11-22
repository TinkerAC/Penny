package app.penny.core.data.repository

import app.penny.core.domain.model.AchievementModel

interface AchievementRepository {
    suspend fun insertAchievement(achievement: AchievementModel)
    suspend fun getAchievementById(achievementId: Long): AchievementModel
    suspend fun getAllAchievements(): List<AchievementModel>
    suspend fun updateAchievement(achievementModel: AchievementModel)
    suspend fun deleteAchievement(achievementId: Long)


}