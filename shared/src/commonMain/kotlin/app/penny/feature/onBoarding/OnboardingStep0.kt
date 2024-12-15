// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingStep0.kt
package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.painterResource


class OnboardingStep0 : OnboardingStepScreen {
    override var onPrevious: (() -> Unit)? = null
    override var onNext: (() -> Unit)? = null
    override var onFinish: (() -> Unit)? = null

    @Composable
    override fun Content() {
        OnboardingPage(
            illustration = painterResource(SharedRes.images.onboarding_cloud_sync),
            title = "Your Data, Everywhere",
            content = "Seamlessly sync your financial records across all devices. Access your data anytime, anywhere.",
            currentPage = 0,
            totalPages = 5, // 更新为5个步骤
            onNext = { onNext?.invoke() },
            onPrevious = onPrevious
        )
    }
}