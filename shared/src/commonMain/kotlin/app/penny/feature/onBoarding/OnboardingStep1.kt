// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingStep1.kt
package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.painterResource


class OnboardingStep1 : OnboardingStepScreen {
    override var onPrevious: (() -> Unit)? = null
    override var onNext: (() -> Unit)? = null
    override var onFinish: (() -> Unit)? = null

    @Composable
    override fun Content() {
        OnboardingPage(
            illustration = painterResource(SharedRes.images.onboarding_data_trend),
            title = "Trends That Matter",
            content = "Discover hidden patterns in your spending habits with beautiful charts and insights.",
            currentPage = 1,
            totalPages = 5,
            onNext = { onNext?.invoke() },
            onPrevious = { onPrevious?.invoke() }
        )
    }
}
