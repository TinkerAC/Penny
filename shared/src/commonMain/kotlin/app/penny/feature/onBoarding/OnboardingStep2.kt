
// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingStep2.kt
package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.onboarding_chat_bot

class OnboardingStep2 : OnboardingStepScreen {
    override var onPrevious: (() -> Unit)? = null
    override var onNext: (() -> Unit)? = null
    override var onFinish: (() -> Unit)? = null

    @Composable
    override fun Content() {
        OnboardingPage(
            illustration = painterResource(Res.drawable.onboarding_chat_bot),
            title = "Talk. Manage. Simplify.",
            content = "Experience the power of AI with natural language commands to manage your finances.",
            currentPage = 2,
            totalPages = 5,
            onNext = { onNext?.invoke() },
            onPrevious = { onPrevious?.invoke() }
        )
    }
}

