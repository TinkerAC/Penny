package app.penny.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            UserInfoSection(viewModel = viewModel)
            Spacer(modifier = Modifier.height(8.dp))
            FunctionGrid()
            Spacer(modifier = Modifier.height(16.dp))
            MenuList()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Penny",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(start = 16.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                IconButton(onClick = { /* 打卡逻辑 */ }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "打卡",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        })
}

@Composable
fun UserInfoSection(
    viewModel: ProfileViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "用户头像",
            modifier = Modifier
                .size(64.dp)
                .padding(8.dp)
                .clickable(
                    onClick = {
                        viewModel.handleIntent(ProfileIntent.TryLogin)
                    }
                ),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = "未登录",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "已连续打卡 -",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "记账总天数 -",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = "记账总笔数 -",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun FunctionGrid() {
    val features = listOf(
        "Notification", "My Badge", "Penny's Box", "Settings"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        items(features) { feature ->
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Star, // Replace with actual icons
                    contentDescription = feature,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = feature,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MenuList() {
    val menuItems = listOf(
        "设置", "使用帮助", "意见反馈", "关于我们"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
    ) {
        menuItems.forEach { menuItem ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { /* 点击事件 */ },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Settings, // Replace with actual icons
                    contentDescription = menuItem,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = menuItem,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        }
    }
}