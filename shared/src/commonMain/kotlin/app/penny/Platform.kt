package app.penny

import app.penny.core.data.kvstore.TokenManager
import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.di.getKoinInstance
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.KoinAppDeclaration

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
    val userDataRepository: UserDataRepository by inject()
    val tokenManager: TokenManager by inject()
    val authRepository = getKoinInstance<AuthRepository>()

    CoroutineScope(Dispatchers.Default).launch {
        val isFirstTime = userDataRepository.getIsFirstTime()
        val isUserLoggedIn = authRepository.isLoggedIn()

        if (isFirstTime || !isUserLoggedIn) {
            Logger.i("First time launch Or User not logged in")
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
    return this.initKoin().initSession()
}