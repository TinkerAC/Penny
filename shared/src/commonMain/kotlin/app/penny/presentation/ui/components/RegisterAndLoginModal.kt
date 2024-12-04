package app.penny.presentation.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun RegisterAndLoginModal(
    onDismiss: () -> Unit, onLoginSuccess: () -> Unit
) {
    var isLoginMode by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // 模态窗口内容
    Surface(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() }, color = Color.Transparent
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.9f).animateContentSize(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).clickable(enabled = false) {}, // 禁止点击关闭
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 圆角滑块切换登录或注册
                    ToggleButton(isLoginMode = isLoginMode,
                        onToggle = { isLoginMode = !isLoginMode })

                    Spacer(modifier = Modifier.height(16.dp))

                    // Circular Avatar
                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape)
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
                        label = { Text("邮箱") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password TextField
                    var password by remember { mutableStateOf("") }
                    var passwordVisible by remember { mutableStateOf(false) }
                    OutlinedTextField(value = password,
                        onValueChange = { password = it },
                        label = { Text("密码") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff

                            val description = if (passwordVisible) "隐藏密码" else "显示密码"

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = description)
                            }
                        })

                    if (!isLoginMode) {
                        Spacer(modifier = Modifier.height(16.dp))

                        // Confirm Password TextField (注册模式下)
                        var confirmPassword by remember { mutableStateOf("") }
                        var confirmPasswordVisible by remember { mutableStateOf(false) }
                        OutlinedTextField(value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("确认密码") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (confirmPasswordVisible) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff

                                val description =
                                    if (confirmPasswordVisible) "隐藏密码" else "显示密码"

                                IconButton(onClick = {
                                    confirmPasswordVisible = !confirmPasswordVisible
                                }) {
                                    Icon(imageVector = image, contentDescription = description)
                                }
                            })
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 登录或注册按钮
                    Button(
                        onClick = {
                            // 处理登录或注册逻辑
                            if (isLoginMode) {
                                // 登录逻辑
                                scope.launch {
                                    // 模拟登录成功
                                    onLoginSuccess()
                                    onDismiss()
                                }
                            } else {
                                // 注册逻辑
                                scope.launch {
                                    // 模拟注册成功
                                    onLoginSuccess()
                                    onDismiss()
                                }
                            }
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isLoginMode) "登录" else "注册")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 关闭按钮
                    TextButton(onClick = onDismiss) {
                        Text("关闭")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ToggleButton(
    isLoginMode: Boolean, onToggle: () -> Unit
) {
    val toggleState = if (isLoginMode) 0f else 1f
    val positionFraction by animateFloatAsState(
        targetValue = toggleState, animationSpec = tween(durationMillis = 300)
    )

    Box(modifier = Modifier
        .width(160.dp)
        .height(40.dp)
        .background(
            color = MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape
        ).clickable { onToggle() }) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 滑块宽度为容器宽度的一半
            val sliderWidth = remember { mutableStateOf(0f) }
            val totalWidth = remember { mutableStateOf(0f) }

            Box(modifier = Modifier.fillMaxSize().onGloballyPositioned { coordinates ->
                totalWidth.value = coordinates.size.width.toFloat()
                sliderWidth.value = totalWidth.value / 2f
            }) {
                // 滑块
                Box(
                    modifier = Modifier.width(with(LocalDensity.current) { (sliderWidth.value).toDp() })
                        .fillMaxHeight().offset {
                            IntOffset(
                                x = (positionFraction * sliderWidth.value).toInt(), y = 0
                            )
                        }.background(
                            color = MaterialTheme.colorScheme.primary, shape = CircleShape
                        ), contentAlignment = Alignment.Center
                ) {

                }

                // 标签
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "登录",
                            color = if (isLoginMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "注册",
                            color = if (!isLoginMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}


