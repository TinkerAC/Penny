package app.penny.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.db.SqlDriver
import app.penny.database.PennyDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun platformModule() = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = PennyDatabase.Schema,
            context = androidContext(),
            name = "penny.db"
        )
    }
}
