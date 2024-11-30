package app.penny

import app.penny.core.data.kvstore.TokenManager
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.model.UserModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.dsl.KoinAppDeclaration

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect class
ApplicationInitializer(
    application: Any? = null
) : KoinComponent {
    // init logic with expect-actual
    fun initKoin(appDeclaration: KoinAppDeclaration = {}): ApplicationInitializer


}


// initSession: 通用逻辑，返回 ApplicationInitializer 本身
fun ApplicationInitializer.initSession(
): ApplicationInitializer {
    val tokenManager: TokenManager by inject()
    Logger.i { "Initializing session..." }
    CoroutineScope(Dispatchers.Default).launch {
        try {
            val accessToken = tokenManager.getAccessToken()
            Logger.i { "Access token loaded successfully: $accessToken" }
        } catch (e: IllegalStateException) {
            Logger.w(e) { "Failed to get access token" }
            // 根据需求处理异常
        }
    }
    return this // 返回自身

}

@OptIn(ExperimentalUuidApi::class)
fun ApplicationInitializer.initUser(): ApplicationInitializer {
    //如果数据库中没有用户信息,创建默认用户

    val userRepository: UserRepository by inject()
    val userDataRepository: UserDataRepository by inject()

    CoroutineScope(Dispatchers.Default).launch {
        val count = userRepository.count()
        if (count == 0L) {
            Logger.i { "Creating default user..." }//TODO: 在UI中引导用户创建账户
            // 创建默认用户
            val user =
                UserModel(
                    uuid = Uuid.random(),
                    username = "PennyPal",
                    email = ""
                )
            userRepository.insert(user)
            userDataRepository.setUserUuid(user.uuid.toString())
        }

    }
    return this


}


@OptIn(ExperimentalUuidApi::class)
fun ApplicationInitializer.initLedger() {
    val userDataRepository: UserDataRepository by inject()
    val ledgerRepository: LedgerRepository by inject()
    CoroutineScope(Dispatchers.Default).launch {
        // 初始化账本

        val userUuid = userDataRepository.getUserUuid()

//        if (ledgerRepository)


    }


}


// initialize: 链式调用 initKoin 和 initSession
fun ApplicationInitializer.initialize(): ApplicationInitializer {
    return this.initKoin().initSession().initUser()
}