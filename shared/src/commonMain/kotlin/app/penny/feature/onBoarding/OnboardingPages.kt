// file: shared/src/commonMain/kotlin/app/penny/feature/onBoarding/OnboardingPages.kt
package app.penny.feature.onBoarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.penny.core.domain.usecase.InitLocalUserUseCase
import app.penny.di.getKoinInstance
import app.penny.presentation.ui.components.PennyLogo
import app.penny.presentation.ui.components.RegisterAndLoginModal
import app.penny.presentation.ui.theme.spacing
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Composable
fun OnboardingPage(
    illustration: Painter?,
    title: String,
    content: String,
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onPrevious: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = MaterialTheme.spacing.large)
            ) {
                // Logo
                PennyLogo()
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                // Illustration (可选)
                illustration?.let {
                    Image(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                }

                // Title
                Text(
                    text = title,
                    style = typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                // Content
                Text(
                    text = content,
                    style = typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
                    textAlign = TextAlign.Center
                )
            }

            // Footer Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.large)
            ) {
                // Page Indicator
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(totalPages) { index ->
                        val color = if (index == currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        }
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(color = color, shape = CircleShape)
                        )
                        if (index != totalPages - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                // Navigation Buttons (上一步/下一步)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { onPrevious?.invoke() },
                        enabled = onPrevious != null,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = "上一步",
                            style = typography.titleMedium
                        )
                    }

                    Button(
                        onClick = onNext,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = if (currentPage == totalPages - 1) "完成" else "下一步",
                            style = typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun OnboardingLoginPage(
    illustration: Painter?,
    title: String,
    content: String,
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit
) {
    var showLoginModal by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val initLocalUserUseCase = getKoinInstance<InitLocalUserUseCase>()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = MaterialTheme.spacing.large)
            ) {
                // Logo
                PennyLogo()
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

                // Illustration (可选)
                illustration?.let {
                    Image(
                        painter = it,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
                }

                // Title
                Text(
                    text = title,
                    style = typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                // Content
                Text(
                    text = content,
                    style = typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium),
                    textAlign = TextAlign.Center
                )
            }

            // Footer Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = MaterialTheme.spacing.large)
            ) {
                // Page Indicator
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(totalPages) { index ->
                        val color = if (index == currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        }
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(color = color, shape = CircleShape)
                        )
                        if (index != totalPages - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                // 登录 / 注册 按钮
                Button(
                    onClick = {

                        showLoginModal = true
                              },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = MaterialTheme.spacing.medium)
                ) {
                    Text(
                        text = "登录 / 注册",
                        style = typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                // 跳过按钮
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            initLocalUserUseCase()
                        }
                        onNext()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = MaterialTheme.spacing.medium)
                ) {
                    Text(
                        text = "跳过",
                        style = typography.titleMedium
                    )
                }
            }
        }

        // Login / Register Modal
        if (showLoginModal) {
            RegisterAndLoginModal(
                onDismiss = { showLoginModal = false },
                onLoginSuccess = {
                    onNext()
                }
            )
        }
    }
}
