// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingStep2.kt
package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.painterResource


class OnboardingStep2 : OnboardingStepScreen {
    override var onPrevious: (() -> Unit)? = null
    override var onNext: (() -> Unit)? = null
    override var onFinish: (() -> Unit)? = null

    @Composable
    override fun Content() {
        OnboardingPage(
            illustration = painterResource(SharedRes.images.onboarding_chat_bot),
            title = "Talk. Manage. Simplify.",
            content = "Experience the power of AI with natural language commands to manage your finances.",
            currentPage = 2,
            totalPages = 5,
            onNext = { onNext?.invoke() },
            onPrevious = { onPrevious?.invoke() }
        )
    }
}

