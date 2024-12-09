// file: shared/src/androidMain/kotlin/app/penny/di/AppModule.android.kt
package app.penny.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.penny.database.PennyDatabase
import app.penny.platform.MultiplatformSettingsWrapper
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun platformModule() = module {
    single<SqlDriver> {
        val driver = AndroidSqliteDriver(
            schema = PennyDatabase.Schema,
            context = androidContext(),
            name = "penny.db"
        )

        driver
    }

    single { MultiplatformSettingsWrapper(context = androidContext()).createSettings() }

    // 其他配置
    single {
        HttpClient()
    }
}
