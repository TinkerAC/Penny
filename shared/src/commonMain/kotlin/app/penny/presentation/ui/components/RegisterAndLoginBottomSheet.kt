// file: shared/src/commonMain/kotlin/app/penny/presentation/ui/components/RegisterAndLoginBottomSheet.kt
package app.penny.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import app.penny.feature.profile.ProfileIntent
import app.penny.feature.profile.ProfileUiState
import app.penny.feature.profile.ProfileViewModel

@Composable
fun RegisterAndLoginBottomSheet(
    uiState: State<ProfileUiState>,
    viewModel: ProfileViewModel
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 切换登录/注册模式的Toggle按钮
        ToggleButton(
            isLoginMode = uiState.value.modalInLoginMode,
            onToggle = {
                viewModel.handleIntent(ProfileIntent.ToggleModalMode)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 头像区域
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.value.email ?: "",
            onValueChange = { email ->
                viewModel.handleIntent(ProfileIntent.InputEmail(email))
            },
            label = { Text("邮箱") },
            modifier =
            Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        viewModel.handleIntent(
                            ProfileIntent.UnfocusEmail(
                                uiState.value.email ?: ""
                            )
                        )
                    }
                },
            singleLine = true,

            )

        Spacer(modifier = Modifier.height(16.dp))

        // 密码输入
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image =
                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val description = if (passwordVisible) "隐藏密码" else "显示密码"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )
        if (!uiState.value.modalInLoginMode) {
            Spacer(modifier = Modifier.height(16.dp))

            // 注册模式下的确认密码输入
            var confirmPassword by remember { mutableStateOf("") }
            var confirmPasswordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("确认密码") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    val description = if (confirmPasswordVisible) "隐藏密码" else "显示密码"

                    IconButton(onClick = {
                        confirmPasswordVisible = !confirmPasswordVisible
                    }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 登录或注册按钮
        Button(
            onClick = {
                if (uiState.value.modalInLoginMode) {
                    viewModel.handleIntent(ProfileIntent.Login(uiState.value.email ?: "", password))
                } else {
                    viewModel.handleIntent(ProfileIntent.Register(uiState.value.email ?: "", password))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState.value.modalInLoginMode) "登录" else "注册")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 关闭按钮
        TextButton(onClick = {
            viewModel.handleIntent(ProfileIntent.DismissLoginModal)
        }) {
            Text("关闭")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

