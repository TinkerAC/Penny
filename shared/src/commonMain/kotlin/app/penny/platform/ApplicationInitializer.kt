package app.penny.platform

import app.penny.core.data.kvstore.TokenManager
import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.di.getKoinInstance
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.dsl.KoinAppDeclaration

expect class ApplicationInitializer(
    application: Any? = null
) {
    // init logic with expect-actual
    fun initKoin(appDeclaration: KoinAppDeclaration = {}): ApplicationInitializer

    fun initNotifierManager(): ApplicationInitializer


}

// initSession: 通用逻辑，返回 ApplicationInitializer 本身
fun ApplicationInitializer.initSession(
): ApplicationInitializer {
    val userDataRepository = getKoinInstance<UserDataRepository>()
    val tokenManager = getKoinInstance<TokenManager>()
    val authRepository = getKoinInstance<AuthRepository>()

    CoroutineScope(Dispatchers.Default).launch {
        val isFirstTime = userDataRepository.getIsFirstTime()
        val hasUserLoggedInBefore = authRepository.hasLoggedIn()

        if (isFirstTime || !hasUserLoggedInBefore) {
            Logger.i("First time launch Or User hasn't logged in once")
            return@launch
        }

        Logger.i { "Initializing session..." }
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


// initialize: 链式调用 initKoin 和 initSession
fun ApplicationInitializer.initialize(): ApplicationInitializer {
    return this.initNotifierManager().initKoin().initSession()
}


fun ApplicationInitializer.printDeviceInfo(): ApplicationInitializer {
    println(
        "Platform: ${getPlatform()}"
    )
    return this
}