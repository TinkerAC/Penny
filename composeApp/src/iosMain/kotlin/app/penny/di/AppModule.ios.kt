package app.penny.di

import app.penny.data.datasource.DatabaseDriverFactory
import org.koin.dsl.module


actual fun platformModule() = module {
    single { DatabaseDriverFactory() }
}

