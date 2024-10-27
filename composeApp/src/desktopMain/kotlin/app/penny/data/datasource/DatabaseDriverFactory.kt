package app.penny.data.datasource

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.penny.database.PennyDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = "app.db"
        val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")

        // 检查数据库文件是否已存在
        val dbFile = File(databasePath)
        if (!dbFile.exists()) {
            // 如果数据库文件不存在，创建数据库表结构
            PennyDatabase.Schema.create(driver)
        }

        return driver
    }
}