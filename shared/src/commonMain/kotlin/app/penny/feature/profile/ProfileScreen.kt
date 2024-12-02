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
                    viewModel = viewModel,
                    onDismiss = {
                        viewModel.handleIntent(ProfileIntent.DismissLoginModal)
                    },
                    uiState = uiState.value
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
                    FunctionGrid(viewModel)
                    Spacer(modifier = Modifier.height(16.dp))
                    MenuList(viewModel)
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
        // 用户头像
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
        // 用户名或登录提示
        Text(
            text = when {
                uiState.isLoggedIn -> uiState.username ?: uiState.email ?: "PennyPal"
                else -> "点击登录"
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        // 统计信息
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
fun FunctionGrid(viewModel: ProfileViewModel) {
    val features = listOf(
        FeatureItem("通知", Icons.Default.Notifications, ProfileIntent.NavigateToNotifications),
        FeatureItem("我的徽章", Icons.Default.Badge, ProfileIntent.NavigateToBadges),
        FeatureItem("Penny's Box", Icons.Default.Storage, ProfileIntent.NavigateToPennyBox),
        FeatureItem("设置", Icons.Default.Settings, ProfileIntent.NavigateToSettings)
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
                    .clickable { viewModel.handleIntent(feature.intent) },
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
    val icon: ImageVector,
    val intent: ProfileIntent
)

@Composable
fun MenuList(viewModel: ProfileViewModel) {
    val menuItems = listOf(
        MenuItem("使用帮助", Icons.Default.Help, ProfileIntent.NavigateToHelp),
        MenuItem("意见反馈", Icons.Default.Feedback, ProfileIntent.NavigateToFeedback),
        MenuItem("关于我们", Icons.Default.Info, ProfileIntent.NavigateToAboutUs)
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
                    .clickable { viewModel.handleIntent(menuItem.intent) },
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
    val icon: ImageVector,
    val intent: ProfileIntent
)