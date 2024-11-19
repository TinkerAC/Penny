package app.penny.data.repository

import app.penny.domain.model.AchievementModel

interface AchievementRepository {
    suspend fun insertAchievement(achievement: AchievementModel)
    suspend fun getAchievementById(achievementId: Long): AchievementModel
    suspend fun getAllAchievements(): List<AchievementModel>
    suspend fun updateAchievement(achievementModel: AchievementModel)
    suspend fun deleteAchievement(achievementId: Long)


}