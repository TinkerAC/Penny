package app.penny.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.penny.presentation.ui.components.RegisterAndLoginBottomSheet
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

class ProfileScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<ProfileViewModel>()
        val uiState = viewModel.uiState.collectAsState()

        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        if (uiState.value.loggingModalVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                    viewModel.handleIntent(ProfileIntent.DismissLoginModal)
                },
                sheetState = bottomSheetState,
            ) {
                RegisterAndLoginBottomSheet(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
        }

        Scaffold(
            topBar = { TopBar() },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    UserInfoSection(
                        viewModel = viewModel,
                        uiState = uiState.value
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    FunctionGrid() // 不再调用Intent，标记未实现导航逻辑
                    Spacer(modifier = Modifier.height(16.dp))
                    MenuList() // 同上，不调用Intent，标记未实现导航逻辑
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "个人中心",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun UserInfoSection(
    viewModel: ProfileViewModel,
    uiState: ProfileUiState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "用户头像",
            modifier = Modifier
                .size(80.dp)
                .clip(MaterialTheme.shapes.large)
                .clickable(
                    onClick = {
                        viewModel.handleIntent(ProfileIntent.TryLogin)
                    }
                ),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when {
                uiState.isLoggedIn -> uiState.username ?: uiState.email ?: "PennyPal"
                else -> "点击登录"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatisticItem(number = uiState.continuousCheckInDays, label = "连续打卡")
            StatisticItem(number = uiState.totalTransactionDays, label = "记账天数")
            StatisticItem(number = uiState.totalTransactionCount, label = "记账笔数")
        }
    }
}

@Composable
fun StatisticItem(number: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun FunctionGrid() {
    // 原本这里有导航 Intent，现在移除
    // TODO: navigation not implemented
    val features = listOf(
        FeatureItem("通知", Icons.Default.Notifications),
        FeatureItem("我的徽章", Icons.Default.Badge),
        FeatureItem("Penny's Box", Icons.Default.Storage),
        FeatureItem("设置", Icons.Default.Settings)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(features) { feature ->
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        // TODO: navigation not implemented
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = feature.icon,
                    contentDescription = feature.name,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = feature.name,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

data class FeatureItem(
    val name: String,
    val icon: ImageVector
)

@Composable
fun MenuList() {
    // TODO: navigation not implemented
    val menuItems = listOf(
        MenuItem("使用帮助", Icons.Default.Help),
        MenuItem("意见反馈", Icons.Default.Feedback),
        MenuItem("关于我们", Icons.Default.Info)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        menuItems.forEach { menuItem ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable {
                        // TODO: navigation not implemented
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = menuItem.icon,
                    contentDescription = menuItem.name,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
        }
    }
}

data class MenuItem(
    val name: String,
    val icon: ImageVector
)