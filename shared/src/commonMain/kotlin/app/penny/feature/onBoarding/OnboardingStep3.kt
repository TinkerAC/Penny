// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingStep3Login.kt
package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.painterResource


class OnboardingStep3Login : OnboardingStepScreen {
    override var onPrevious: (() -> Unit)? = null
    override var onNext: (() -> Unit)? = null
    override var onFinish: (() -> Unit)? = null

    @Composable
    override fun Content() {
        OnboardingLoginPage(
            illustration = painterResource(SharedRes.images.onboarding_login),
            title = "Welcome to Penny",
            content = "Your personal finance assistant. Let's get started!",
            currentPage = 3,
            totalPages = 5,
            onNext = {
                onNext?.invoke()
            },
            onFinish={
                onFinish?.invoke()
            }

        )
    }
}
