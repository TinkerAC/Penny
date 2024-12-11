
// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingStep1.kt
package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.onboarding_data_trend

class OnboardingStep1 : OnboardingStepScreen {
    override var onPrevious: (() -> Unit)? = null
    override var onNext: (() -> Unit)? = null
    override var onFinish: (() -> Unit)? = null

    @Composable
    override fun Content() {
        OnboardingPage(
            illustration = painterResource(Res.drawable.onboarding_data_trend),
            title = "Trends That Matter",
            content = "Discover hidden patterns in your spending habits with beautiful charts and insights.",
            currentPage = 1,
            totalPages = 5,
            onNext = { onNext?.invoke() },
            onPrevious = { onPrevious?.invoke() }
        )
    }
}
