package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.onboarding_cloud_sync
@Composable
fun OnboardingScreen0(
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,

) {
    OnboardingPage(
        illustration = painterResource(
            Res.drawable.onboarding_cloud_sync
        ),
        title = "Your Data, Everywhere",
        content = "Seamlessly sync your financial records across all devices. Access your data anytime, anywhere, with peace of mind in the cloud.",
        currentPage = currentPage,
        totalPages = totalPages,
        onNext = onNext
    )
}
