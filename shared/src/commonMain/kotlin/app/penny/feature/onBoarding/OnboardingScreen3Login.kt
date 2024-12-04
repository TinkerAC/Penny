package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.onboarding_login

@Composable
fun OnboardingScreen3Login(
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit
) {
    OnboardingLoginPage(
        illustration = painterResource(
            Res.drawable.onboarding_login
        ),
        title = "Welcome to Penny",
        content = "Your personal finance assistant that helps you manage your money better. Let's get started!",
        currentPage = currentPage,
        totalPages = totalPages,
        onNext = { onNext() }
    )


}