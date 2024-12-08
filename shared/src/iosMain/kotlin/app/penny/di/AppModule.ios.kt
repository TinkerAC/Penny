// file: shared/src/iosMain/kotlin/app/penny/di/AppModule.ios.kt
package app.penny.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.penny.database.PennyDatabase
import app.penny.platform.MultiplatformSettingsWrapper
import org.koin.dsl.module

actual fun platformModule() = module {
    single<SqlDriver> {
        val driver = NativeSqliteDriver(
            schema = PennyDatabase.Schema,
            name = "penny.db"
        )

        // 启用日志记录
        // 使用 sqlite3_trace 输出 SQL 查询
        driver.execute(null, "PRAGMA sqlite_trace = ON;", 0)

        driver
    }

    single { MultiplatformSettingsWrapper().createSettings() }
}
