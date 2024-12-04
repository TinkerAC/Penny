package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.onboarding_data_trend

@Composable
fun OnboardingScreen1(
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit
) {
    OnboardingPage(
        illustration = painterResource(
            Res.drawable.onboarding_data_trend),
        title = "Trends That Matter",
        content = "Discover hidden patterns in your spending habits. Beautiful charts and actionable insights help you take control of your finances.",
        currentPage = currentPage,
        totalPages = totalPages,
        onNext = onNext
    )
}

