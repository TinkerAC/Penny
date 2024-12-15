package app.penny.core.data.database

import app.penny.database.UserEntity

interface UserLocalDataSource {

    fun insert(userEntity: UserEntity)

    fun upsertByUuid(userEntity: UserEntity)

    fun findByUuid(uuid: String): UserEntity?

    fun count(): Long


    fun findAll(): List<UserEntity>

    fun deleteAll()
    fun findByEmail(email: String): UserEntity?


    fun findByEmailIsNull(): List<UserEntity>
}