
// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingStep3Login.kt
package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.onboarding_login

class OnboardingStep3Login : OnboardingStepScreen {
    override var onPrevious: (() -> Unit)? = null
    override var onNext: (() -> Unit)? = null
    override var onFinish: (() -> Unit)? = null

    @Composable
    override fun Content() {
        OnboardingLoginPage(
            illustration = painterResource(Res.drawable.onboarding_login),
            title = "Welcome to Penny",
            content = "Your personal finance assistant. Let's get started!",
            currentPage = 3,
            totalPages = 5,
            onNext = {

                onNext?.invoke()
            }
        )
    }
}
