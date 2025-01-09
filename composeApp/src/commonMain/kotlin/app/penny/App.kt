import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.penny.core.data.enumerate.userRepository
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserPreferenceRepository
import app.penny.di.getKoinInstance
import app.penny.feature.onBoarding.OnboardingNavigatorScreen
import app.penny.presentation.ui.LocaleManager
import app.penny.presentation.ui.MainScreen
import app.penny.presentation.ui.ThemeManager
import app.penny.presentation.ui.ThemeState
import app.penny.presentation.ui.theme.AppTheme
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import co.touchlab.kermit.Logger
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun App() {
    val userPreferenceRepository = getKoinInstance<UserPreferenceRepository>()
    val language = userPreferenceRepository.getLanguage()
    // 设置语言
    LocaleManager.setLocaleTo(language)

    var themeState by remember {
        mutableStateOf(
            ThemeState(
                appTheme = userPreferenceRepository.getAppTheme(),
                displayMode = userPreferenceRepository.getDisplayMode(),
                constraints = userPreferenceRepository.getConstraints()
            )
        )
    }

    // 用于判断是否需要展示OnBoarding，或者进入主界面
    var startOnBoardingPage by remember { mutableStateOf<Int?>(null) }
    // 判断是否第一次启动的标志
    var isFirstTime by remember { mutableStateOf<Boolean?>(null) }

    val userDataRepository = getKoinInstance<UserDataRepository>()
    val ledgerRepository = getKoinInstance<LedgerRepository>()

    // 使用 LaunchedEffect 初始化数据和监听主题变化
    LaunchedEffect(Unit) {
        // 1. 获取第一次启动
        isFirstTime = userDataRepository.getIsFirstTime()

        // 2. 声明数据损坏标志
        var isUserDataBroken = false
        var isLedgerDataBroken = false

        // 3. 检测用户数据
        try {
            val user = userRepository.findByUuid(userDataRepository.getUser().uuid)!!
            // 如果能正确取得 userModel，则用户数据正常
        } catch (e: Exception) {
            isUserDataBroken = true
            //wipe other broken data
            userDataRepository.clearUserData()
            Logger.d("User data is broken")
        }

        // 4. 如果用户数据正常，再检测账本数据
        if (!isUserDataBroken) {
            try {
                val defaultLedger = userDataRepository.getDefaultLedger()
                val userLedgers = ledgerRepository.findByUserUuid(userDataRepository.getUser().uuid)
                // 若查询为空 或 defaultLedger 不在列表中，则说明账本数据有问题
                if (userLedgers.isEmpty() || userLedgers.none { it.uuid == defaultLedger.uuid }) {
                    isLedgerDataBroken = true
                }

                // 也可以单独测试这个 defaultLedger
                ledgerRepository.findByUuid(defaultLedger.uuid)
            } catch (e: Exception) {
                isLedgerDataBroken = true
                //wipe broken ledger data
                userDataRepository.removeDefaultLedger()
                userDataRepository.removeLastSyncedAt()
                Logger.d("Ledger data is broken")
            }
        }

        // 5. 根据逻辑，确定 startOnBoardingPage
        startOnBoardingPage = when {
            // (A) 如果是第一次启动
            isFirstTime == true -> 0
            // (B) 如果用户数据损坏
            isUserDataBroken -> 3
            // (C) 如果用户数据正常，但账本数据损坏
            isLedgerDataBroken -> 4
            // (D) 否则一切正常，不进入OnBoarding
            else -> -1
        }

        // 6. 监听主题变化事件
        ThemeManager.themeChanges.collect { newThemeState ->
            themeState = newThemeState
        }
    }

    // 加载完成与否的判断
    // isFirstTime 可能是 null，表示尚未加载完成；startOnBoardingPage 也可能是null
    if (isFirstTime == null || startOnBoardingPage == null) {
        // 还在加载中
        CircularProgressIndicator()

    } else {

        Navigator(
            screen = if (startOnBoardingPage != -1) {
                // 如果不是-1，说明需要进入OnBoarding
                OnboardingNavigatorScreen(
                    startPage = startOnBoardingPage!!
                )
            } else {
                // 一切正常，直接跳转到主界面
                MainScreen()
            }
        ) { navigator ->
            AppTheme(
                appTheme = themeState.appTheme,
                appDisplayMode = themeState.displayMode,
                appThemeContrast = themeState.constraints
            ) {
                SlideTransition( navigator = navigator)
            }
        }
    }
}