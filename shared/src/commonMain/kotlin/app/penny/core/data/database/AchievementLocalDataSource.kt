package app.penny.core.data.database


import app.penny.database.AchievementEntity
import app.penny.database.AchievementQueries


class AchievementLocalDataSource(
    private val achievementQueries: AchievementQueries

) {

    fun insertAchievement(achievement: AchievementEntity) {
        achievementQueries.insertAchievement(
            name = achievement.name,
            description = achievement.description,
            goal = achievement.goal,
            icon_uri = achievement.icon_uri,
        )
    }

    fun getAchievementById(achievementId: Long): AchievementEntity {
        return achievementQueries.getAchievementById(achievementId).executeAsOne()
    }


    fun getAllAchievements(): List<AchievementEntity> {
        return achievementQueries.getAllAchievements().executeAsList()
    }


    fun updateAchievement(achievement: AchievementEntity) {
        achievementQueries.updateAchievement(
            name = achievement.name,
            description = achievement.description,
            icon_uri = achievement.icon_uri,
            id = achievement.id,
            goal = achievement.goal
        )
    }


    fun deleteAchievement(achievementId: Long) {
        achievementQueries.deleteAchievement(achievementId)
    }


}