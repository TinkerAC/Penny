package app.penny.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.penny.database.PennyDatabase
import app.penny.platform.MultiplatformSettingsWrapper
import me.sujanpoudel.utils.paths.appDataDirectory
import org.koin.dsl.module
import java.nio.file.Paths

actual fun platformModule() = module {
    single<SqlDriver> {
        // 获取数据库目录路径
        val appDir = appDataDirectory(appId = "penny", createDir = true)
        println("App directory: $appDir") // 调试信息

        // 构造数据库文件路径
        val databasePath = Paths.get(appDir.toString(), "penny.db").toString()
        println("Database path: $databasePath") // 调试信息

        val dbFile = Paths.get(databasePath).toFile()

        // 创建 SQLite 驱动
        val driver = try {
            JdbcSqliteDriver("jdbc:sqlite:$databasePath")
        } catch (e: Exception) {
            throw IllegalStateException("Failed to create JDBC SQLite driver for $databasePath", e)
        }

        // 初始化数据库（如果文件不存在）
        if (!dbFile.exists()) {
            try {
                PennyDatabase.Schema.create(driver)
                println("Database schema created at $databasePath")
            } catch (e: Exception) {
                throw IllegalStateException("Failed to create database schema at $databasePath", e)
            }
        }

        driver
    }

    single { MultiplatformSettingsWrapper().createSettings() }
}
