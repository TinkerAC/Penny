package app.penny.di

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.db.SqlDriver
import app.penny.database.PennyDatabase
import app.penny.platform.MultiplatformSettingsWrapper
import org.koin.dsl.module

actual fun platformModule() = module {
    single<SqlDriver> {
        val databasePath = "penny.db"
        val driver = JdbcSqliteDriver("jdbc:sqlite:$databasePath")
        // 检查数据库文件是否存在
        val dbFile = java.io.File(databasePath)
        if (!dbFile.exists()) {
            // 如果数据库文件不存在，创建数据库表结构
            PennyDatabase.Schema.create(driver)
        }
        driver
    }

    single { MultiplatformSettingsWrapper().createSettings() }
}
