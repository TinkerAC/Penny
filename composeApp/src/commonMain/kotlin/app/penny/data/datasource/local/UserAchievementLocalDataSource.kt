package app.penny.data.datasource.local

import app.penny.database.UserAchievementEntity
import app.penny.database.UserAchievementQueries

class UserAchievementLocalDataSource(
    private val userAchievementQueries: UserAchievementQueries

) {


    fun insertUserAchievement(achievementEntity: UserAchievementEntity) {
        userAchievementQueries.insertUserAchievement(
            achievement_id = achievementEntity.achievement_id,
            completed_at = achievementEntity.completed_at,
            progress = achievementEntity.progress,
        )
    }


    fun getUSerAchievementById(achievementId: Long): UserAchievementEntity {
        return userAchievementQueries.getUserAchievementById(achievementId).executeAsOne()
    }

    fun getAllUserAchievements(): List<UserAchievementEntity> {
        return userAchievementQueries.getAllUserAchievements().executeAsList()
    }


    fun deleteUserAchievement(achievementId: Long) {
        userAchievementQueries.deleteUserAchievement(achievementId)
    }


    fun updateUserAchievement(achievementEntity: UserAchievementEntity) {
        userAchievementQueries.updateUserAchievement(
            id = achievementEntity.id,
            achievement_id = achievementEntity.achievement_id,
            completed_at = achievementEntity.completed_at,
            progress = achievementEntity.progress,
        )
    }


}