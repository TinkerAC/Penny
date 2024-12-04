package app.penny.feature.onBoarding

import androidx.compose.runtime.Composable

import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.onboarding_chat_bot

@Composable
fun OnboardingScreen2(
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit
) {
    OnboardingPage(
        illustration = painterResource(
            Res.drawable.onboarding_chat_bot
        ),
        title = "Talk. Manage. Simplify.",
        content = "Experience the power of AI with natural language commands. From tracking expenses to setting budgets, managing your finances has never been this intuitive.",
        currentPage = currentPage,
        totalPages = totalPages,
        onNext = onNext
    )
}
