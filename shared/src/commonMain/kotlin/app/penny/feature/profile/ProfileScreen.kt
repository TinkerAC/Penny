package app.penny.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.penny.feature.myLedger.MyLedgerScreen
import app.penny.feature.notification.NotificationScreen
import app.penny.feature.setting.SettingScreen
import app.penny.feature.toolBox.ToolBoxScreen
import app.penny.presentation.ui.components.RegisterAndLoginBottomSheet
import app.penny.presentation.ui.components.UserAvatar
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource

class ProfileScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<ProfileViewModel>()
        val uiState = viewModel.uiState.collectAsState()

        val bottomSheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )



        LaunchedEffect(Unit) {
            viewModel.refreshData()
        }



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
                text = stringResource(SharedRes.strings.profile),
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

        UserAvatar(
            imageUrl = uiState.userAvatarUrl,
            onClick = {
                viewModel.handleIntent(ProfileIntent.TryLogin)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = when {
                uiState.isLoggedIn -> uiState.username ?: uiState.email
                ?: stringResource(SharedRes.strings.default_username)

                else -> stringResource(SharedRes.strings.tap_to_login)
            },
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            StatisticItem(
                number = uiState.ledgerCount,
                label = stringResource(SharedRes.strings.ledgers)
            )
            StatisticItem(
                number = uiState.totalTransactionDateSpan,
                label = stringResource(SharedRes.strings.days)
            )
            StatisticItem(
                number = uiState.totalTransactionCount,
                label = stringResource(SharedRes.strings.transactions)
            )
        }
    }
}

@Composable
fun StatisticItem(number: Long, label: String) {
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
    val rootNavigator = LocalNavigator.currentOrThrow
    val features = listOf(
        FeatureItem(
            stringResource(SharedRes.strings.notification),
            Icons.Default.Notifications,
            NotificationScreen()
        ),
        FeatureItem(
            stringResource(SharedRes.strings.my_ledger),
            Icons.Filled.AccountBalanceWallet,
            MyLedgerScreen()
        ),
        FeatureItem(
            stringResource(SharedRes.strings.pennys_box),
            Icons.Default.HomeRepairService,
            ToolBoxScreen()
        ),
        FeatureItem(
            stringResource(SharedRes.strings.settings),
            Icons.Default.Settings, SettingScreen()
        )
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
                        feature.screen?.let {
                            rootNavigator.push(it)
                        }
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
    val icon: ImageVector,
    val screen: Screen? = null
)

@Composable
fun MenuList() {
    val localUriHandler = LocalUriHandler.current
    val menuItems = listOf(
        MenuItem(
            stringResource(SharedRes.strings.help),
            Icons.AutoMirrored.Filled.Help,
            link = "https://www.github.com/TinkerAC/Penny"
        ),
        MenuItem(
            stringResource(SharedRes.strings.feedback),
            Icons.Default.Feedback,
            link = "https://www.github.com/TinkerAC/Penny/issues"
        ),
        MenuItem(
            stringResource(SharedRes.strings.about),
            Icons.Default.Info,
            link = "https://www.github.com/TinkerAC/Penny"
        )
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

                        localUriHandler.openUri(menuItem.link)

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
    val icon: ImageVector,
    val link: String = ""
)