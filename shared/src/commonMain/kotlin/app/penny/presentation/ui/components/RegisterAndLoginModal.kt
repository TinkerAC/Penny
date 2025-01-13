package app.penny.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.penny.core.domain.exception.LoginException
import app.penny.core.domain.exception.RegisterException
import app.penny.core.domain.usecase.LoginUseCase
import app.penny.core.domain.usecase.RegisterUseCase
import app.penny.di.getKoinInstance
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch

@Composable
fun RegisterAndLoginModal(
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    var isLoginMode by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val loginUseCase: LoginUseCase = getKoinInstance()
    val registerUseCase: RegisterUseCase = getKoinInstance()
    var errorMessage by remember { mutableStateOf("") }

    // Modal content
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() },
        color = Color.Transparent
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .animateContentSize(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable(enabled = false) {},
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Toggle between Login and Register
                    LoginRegisterToggle(
                        isLoginMode = isLoginMode,
                        onToggle = { isLoginMode = !isLoginMode }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Circular Avatar
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

                    // Email TextField
                    var email by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(
                                stringResource(SharedRes.strings.email)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password TextField
                    var password by remember { mutableStateOf("") }
                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(
                                stringResource(SharedRes.strings.password)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image =
                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        }
                    )

                    // Confirm Password TextField in Register mode

                    var confirmPassword by remember { mutableStateOf("") }
                    var confirmPasswordVisible by remember { mutableStateOf(false) }
                    if (!isLoginMode) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text(stringResource(SharedRes.strings.confirm_password)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image =
                                    if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = {
                                    confirmPasswordVisible = !confirmPasswordVisible
                                }) {
                                    Icon(imageVector = image, contentDescription = null)
                                }
                            }
                        )
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // render error message if any
                    if (errorMessage.isNotEmpty()) {

                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))


                    }

                    // Login/Register Button
                    Button(
                        onClick = {
                            scope.launch {
                                if (isLoginMode) {
                                    // Login logic
                                    try {
                                        loginUseCase(email, password)
                                        onLoginSuccess()
                                    } catch (e: LoginException) {
                                        errorMessage = when (e) {
                                            is LoginException.InvalidCredentialsException -> "Invalid email or password"
                                            is LoginException.NetworkException -> "Network connection failed"
                                            is LoginException.ServerException -> "Server encountered an error"
                                            is LoginException.UnknownException -> "Unknown error occurred"
                                        }
                                    }
                                } else {
                                    // Register logic
                                    try {
                                        registerUseCase(email, password, confirmPassword, null)
                                        errorMessage = "Register success, please login"
                                        // toggle to login mode after successful registration
                                        isLoginMode = true
                                    } catch (e: RegisterException) {
                                        errorMessage = when (e) {
                                            is RegisterException.EmailAlreadyRegisteredException -> "This email has been registered, please login"
                                            is RegisterException.NetworkException -> "Network error, please try again later"
                                            is RegisterException.ServerException -> "Server error, please try again later"
                                            is RegisterException.UnknownException -> "Unknown error occurred"
                                            is RegisterException.PasswordNotMatchException -> "Password not match"
                                        }
                                    }


                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (isLoginMode)
                                stringResource(SharedRes.strings.login)
                            else
                                stringResource(SharedRes.strings.register)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Close Button
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


