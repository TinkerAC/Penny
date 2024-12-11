// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingStep0.kt
package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.onboarding_cloud_sync

class OnboardingStep0 : OnboardingStepScreen {
    override var onPrevious: (() -> Unit)? = null
    override var onNext: (() -> Unit)? = null
    override var onFinish: (() -> Unit)? = null

    @Composable
    override fun Content() {
        OnboardingPage(
            illustration = painterResource(Res.drawable.onboarding_cloud_sync),
            title = "Your Data, Everywhere",
            content = "Seamlessly sync your financial records across all devices. Access your data anytime, anywhere.",
            currentPage = 0,
            totalPages = 5, // 更新为5个步骤
            onNext = { onNext?.invoke() },
            onPrevious = onPrevious
        )
    }
}