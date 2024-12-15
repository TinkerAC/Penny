// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingNavigatorScreen.kt
package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import app.penny.presentation.ui.MainScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow

/**
 * OnboardingNavigatorScreen负责多步Onboarding流程的导航。
 * 使用Voyager的Navigator包裹各个Onboarding步骤Screen。
 */
class OnboardingNavigatorScreen : Screen {
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        // 使用子Navigator来管理Onboarding步骤
        val onboardingScreens = listOf(
            OnboardingStep0(),
            OnboardingStep1(),
            OnboardingStep2(),
            OnboardingStep3Login(),
            OnboardingStep4InitLedger()
        )

        // 初始化子Navigator
        CurrentOnboardingFlow(onboardingScreens = onboardingScreens, onFinish = {
            // 当用户完成Onboarding后，导航至主界面
            rootNavigator.replaceAll(MainScreen())
        })
    }
}

@Composable
fun CurrentOnboardingFlow(
    onboardingScreens: List<Screen>,
    onFinish: () -> Unit
) {
    val rootNavigator = LocalNavigator.currentOrThrow

    // 使用一个本地的Navigator来承载Onboarding多个步骤
    Navigator(onboardingScreens.first()) { navigator ->
        val currentIndex = onboardingScreens.indexOf(navigator.lastItem)
        val isLast = currentIndex == onboardingScreens.lastIndex

        // 包装当前Screen，并为其提供「上一步」和「下一步」的回调
        val currentScreen = navigator.lastItem
        (currentScreen as? OnboardingStepScreen)?.SetNavigationActions(
            onPrevious = {
                if (currentIndex > 0) {
                    navigator.pop()
                }
            },
            onNext = {
                if (isLast) {
                    // 完成Onboarding流程
                    onFinish()
                } else {
                    // 导航至下一个步骤
                    navigator.push(onboardingScreens[currentIndex + 1])
                }
            },
            onFinish = onFinish
        )

        // 显示当前的Onboarding步骤Screen内容
        currentScreen.Content()
    }
}


// 定义一个接口，使Onboarding步骤可以接收「上一步」「下一步」回调
interface OnboardingStepScreen : Screen {
    var onPrevious: (() -> Unit)?
    var onNext: (() -> Unit)?
    var onFinish: (() -> Unit)?

    fun SetNavigationActions(
        onPrevious: () -> Unit,
        onNext: () -> Unit,
        onFinish: () -> Unit
    ) {
        this.onPrevious = onPrevious
        this.onNext = onNext
        this.onFinish = onFinish
    }
}