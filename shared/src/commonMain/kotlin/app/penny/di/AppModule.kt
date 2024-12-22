package app.penny.di

import app.cash.sqldelight.db.SqlDriver
import app.penny.core.data.database.ChatMessageLocalDataSource
import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.core.data.database.dataSourceImpl.LedgerLocalDataSourceImpl
import app.penny.core.data.database.TransactionLocalDataSource
import app.penny.core.data.database.UserLocalDataSource
import app.penny.core.data.database.dataSourceImpl.ChatMessageLocalDataSourceImpl
import app.penny.core.data.database.dataSourceImpl.TransactionLocalDataSourceImpl
import app.penny.core.data.database.dataSourceImpl.UserLocalDataSourceImpl
import app.penny.core.data.kvstore.TokenManager
import app.penny.core.data.kvstore.TokenProvider
import app.penny.core.data.kvstore.UserDataManager
import app.penny.core.data.kvstore.UserPreferenceManager
import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.ChatRepository
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.StatisticRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserPreferenceRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.data.repository.impl.AuthRepositoryImpl
import app.penny.core.data.repository.impl.ChatRepositoryImpl
import app.penny.core.data.repository.impl.LedgerRepositoryImpl
import app.penny.core.data.repository.impl.StatisticRepositoryImpl
import app.penny.core.data.repository.impl.TransactionRepositoryImpl
import app.penny.core.data.repository.impl.UserDataRepositoryImpl
import app.penny.core.data.repository.impl.UserPreferenceRepositoryImpl
import app.penny.core.data.repository.impl.UserRepositoryImpl
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.usecase.CountUnsyncedDataUseCase
import app.penny.core.domain.usecase.DeleteLedgerUseCase
import app.penny.core.domain.usecase.DownloadUnsyncedLedgerUseCase
import app.penny.core.domain.usecase.GetAllLedgerUseCase
import app.penny.core.domain.usecase.GetSummaryUseCase
import app.penny.core.domain.usecase.InitLocalUserUseCase
import app.penny.core.domain.usecase.InsertRandomTransactionUseCase
import app.penny.core.domain.usecase.LoginUseCase
import app.penny.core.domain.usecase.RegisterUseCase
import app.penny.core.domain.usecase.SearchTransactionsUseCase
import app.penny.core.domain.usecase.SyncDataUseCase
import app.penny.core.domain.usecase.UploadUnsyncedLedgerUseCase
import app.penny.core.network.ApiClient
import app.penny.core.network.clients.AiApiClient
import app.penny.core.network.clients.AuthApiClient
import app.penny.core.network.clients.SyncApiClient
import app.penny.core.network.clients.UserApiClient
import app.penny.database.PennyDatabase
import app.penny.feature.aiChat.AIChatViewModel
import app.penny.feature.analytics.AnalyticViewModel
import app.penny.feature.dashBoard.DashboardViewModel
import app.penny.feature.debugBoard.DebugViewModel
import app.penny.feature.ledgerDetail.LedgerDetailViewModel
import app.penny.feature.myLedger.MyLedgerViewModel
import app.penny.feature.newLedger.NewLedgerViewModel
import app.penny.feature.newTransaction.NewTransactionViewModel
import app.penny.feature.profile.ProfileViewModel
import app.penny.feature.setting.SettingViewModel
import app.penny.feature.transactions.TransactionViewModel
import app.penny.presentation.viewmodel.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun commonModule() = module {

    // 提供 PennyDatabase
    single {
        PennyDatabase(get<SqlDriver>())
    }

    // 提供 Queries
    single {
        get<PennyDatabase>().transactionsQueries
    }

    single { get<PennyDatabase>().ledgerQueries }


    single { get<PennyDatabase>().userQueries }
    single { get<PennyDatabase>().chatMessageQueries }

    // 提供 DataSource
    single<TransactionLocalDataSource> { TransactionLocalDataSourceImpl(get()) }

    single<LedgerLocalDataSource> { LedgerLocalDataSourceImpl(get()) }

    single<UserLocalDataSource> { UserLocalDataSourceImpl(get()) }

    single<ChatMessageLocalDataSource> { ChatMessageLocalDataSourceImpl(get()) }


    //SettingManager
    single { UserDataManager(get()) }


    //module-apiClient
    single { AuthApiClient(get()) }
    single { UserApiClient(get(), get()) }
    single { SyncApiClient(get(), get()) }
    single { AiApiClient(get(), get()) }




    single<TokenProvider> { TokenManager(get(), get()) }
    single { TokenManager(get(), get()) }

    single { UserPreferenceManager(get()) }
    // 提供 Repository
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get(), get()) }

    single<LedgerRepository> { LedgerRepositoryImpl(get(), get()) }

    single<UserDataRepository> { UserDataRepositoryImpl(get(), get(), get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    single<UserRepository> { UserRepositoryImpl(get(), get()) }

    single<ChatRepository> { ChatRepositoryImpl(get(), get(), get()) }

    single<UserPreferenceRepository> { UserPreferenceRepositoryImpl(get()) }


    single<StatisticRepository> { StatisticRepositoryImpl(get()) }


    //注入ApiClient


    // Koin 模块定义
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true // 忽略未知字段
                        isLenient = true // 宽松模式，支持非标准 JSON
                    }
                )
            }
        }
    }


    single { ApiClient(get(), get(), get(), get()) }

    // 提供 UseCase
    factory { GetAllLedgerUseCase(get()) }
    factory { InsertRandomTransactionUseCase(get(), get()) }
    factory { InitLocalUserUseCase(get(), get()) }
    factory { SearchTransactionsUseCase(get()) }
    factory { LoginUseCase(get(), get(), get()) }
    factory { UploadUnsyncedLedgerUseCase(get(), get()) }
    factory { RegisterUseCase(get()) }
    factory { DownloadUnsyncedLedgerUseCase(get(), get()) }
    factory { SyncDataUseCase(get(), get(), get(), get()) }
    factory {
        CountUnsyncedDataUseCase(
            get(), get(), get(), get()
        )
    }

    factory { DeleteLedgerUseCase(
        get(),get()
    ) }

    factory { GetSummaryUseCase(get()) }

    factory { AIChatViewModel(get(), get(), get(), get(), get()) }


    // 注入 ViewModel

    factory { SettingViewModel(get()) }
    factory { (ledger: LedgerModel) -> LedgerDetailViewModel(ledger,get(),get()) }


    factory { NewTransactionViewModel(get(), get(), get()) }


    factory {
        DebugViewModel(
            insertRandomTransactionUseCase = get(),
            uploadUnsyncedLedgerUseCase = get(),
            userDataRepository = get(),
            downloadUnsyncedLedgerUseCase = get(),
            get(),
            get(),
            get()
        )
    }


    factory { AnalyticViewModel(get(), get(), get()) }
    factory { TransactionViewModel(get(), get()) }
    factory { MainViewModel(get(), get()) }
    factory { MyLedgerViewModel(get(), get()) }
    factory { NewLedgerViewModel(get(), get()) }
    factory { ProfileViewModel(get(), get(), get(), get(), get()) }

    factory { MyLedgerViewModel(get(), get()) }

    factory { NewLedgerViewModel(get(), get()) }

    factory { AIChatViewModel(get(), get(), get(), get(), get()) }
    factory {
        DashboardViewModel(
            get(), get(), get()
        )
    }


}

expect fun platformModule(): Module
