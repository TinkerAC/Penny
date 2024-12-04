package app.penny.feature.onBoarding

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import app.penny.presentation.ui.MainScreen
import cafe.adriel.voyager.navigator.currentOrThrow

class OnboardingScreen : Screen {
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        var currentStep by remember { mutableStateOf(0) }
        when (currentStep) {
            0 -> OnboardingScreen1(
                currentPage = 0,
                totalPages = 4,
                onNext = { currentStep++ })

            1 -> OnboardingScreen1(
                currentPage = 1,
                totalPages = 4,
                onNext = { currentStep++ })

            2 -> OnboardingScreen2(
                currentPage = 2,
                totalPages = 4,
                onNext = { currentStep++ })

            3 -> OnboardingScreen3Login(
                currentPage = 3,
                totalPages = 4,
                onNext = {
                    rootNavigator.push(MainScreen())
                })

            else -> rootNavigator.replaceAll(
                MainScreen()

            )

        }
    }
}
