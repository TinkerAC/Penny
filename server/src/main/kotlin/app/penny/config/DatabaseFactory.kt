package app.penny.config

import app.penny.models.Users
import com.typesafe.config.ConfigFactory
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {

        val originalConfig = ConfigFactory.load()

        val config = originalConfig.getConfig("database")

        val driverClassName = config.getString("driver")

        //local mysql
        val jdbcURL = config.getString("url")
        val user = config.getString("user")
        val password = config.getString("password")

        Database.connect(jdbcURL, driver = driverClassName, user = user, password = password)

        transaction {
            SchemaUtils.create(Users)
        }
    }
}