package app.penny.core.data.repository

import app.penny.core.domain.model.UserAchievementModel

interface UserAchievementRepository {
    suspend fun insertUserAchievement(userAchievement: UserAchievementModel)
    suspend fun getUserAchievementById(userAchievementId: Long): UserAchievementModel
    suspend fun getAllUserAchievements(): List<UserAchievementModel>
    suspend fun updateUserAchievement(userAchievementModel: UserAchievementModel)
    suspend fun deleteUserAchievement(userAchievementId: Long)
}