package app.penny.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.penny.database.PennyDatabase
import org.koin.dsl.module


actual fun platformModule() = module {
    single<SqlDriver> {
        NativeSqliteDriver(
            schema = PennyDatabase.Schema,
            name = "penny.db"
        )
    }

}

