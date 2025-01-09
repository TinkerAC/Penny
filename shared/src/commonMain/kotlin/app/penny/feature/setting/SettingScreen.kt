package app.penny.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.penny.feature.setting.component.ExpendSetting
import app.penny.feature.setting.component.ExposedDropDownSetting
import app.penny.feature.setting.component.SettingSection
import app.penny.feature.setting.component.SwitchSetting
import app.penny.feature.setting.component.ThemeColorOptionContent
import app.penny.presentation.ui.components.SingleNavigateBackTopBar
import app.penny.presentation.ui.theme.AppTheme
import app.penny.shared.SharedRes
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.stringResource


class SettingScreen : Screen {
    @Composable
    override fun Content() {
        val rootNavigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<SettingViewModel>()
        val uiState by viewModel.uiState.collectAsState()



        LaunchedEffect(Unit) {
            viewModel.refreshData()
        }


        Scaffold(
            topBar = {
                SingleNavigateBackTopBar(
                    title = stringResource(SharedRes.strings.settings),
                    onNavigateBack = { rootNavigator.pop() },
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyColumn(
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface),
                    contentPadding = PaddingValues(24.dp),
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .background(MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            // Preference setting section
                            SettingSection(
                                title = stringResource(SharedRes.strings.preference),
                                description = stringResource(SharedRes.strings.preference_setting_description),
                                settingItems = listOf {

                                    ExposedDropDownSetting(
                                        settingName = stringResource(SharedRes.strings.language),
                                        items = uiState.languages,
                                        selectedItem = uiState.language,
                                        onItemSelected = {
                                            viewModel.handleIntent(
                                                SettingIntent.SetLanguage(it)
                                            )
                                        },
                                        displayMapper = { it.displayName }
                                    )

                                    ExpendSetting(
                                        settingName = stringResource(SharedRes.strings.theme_color),
                                        currentValue = uiState.appTheme,
                                        options = uiState.appThemes!!,
                                        onValueChange = {
                                            viewModel.handleIntent(
                                                SettingIntent.SetTheme(it)
                                            )
                                        },
                                        optionContent = { themeColor, isSelected, modifier ->
                                            ThemeColorOptionContent(
                                                themeColor,
                                                isSelected,
                                                modifier
                                            )
                                        },
                                    )
                                    ExposedDropDownSetting(
                                        settingName = stringResource(SharedRes.strings.display_mode),
                                        items = uiState.displayModes,
                                        selectedItem = uiState.displayMode,
                                        onItemSelected = {
                                            viewModel.handleIntent(
                                                SettingIntent.SetDisplayMode(it)
                                            )
                                        },
                                        displayMapper = { stringResource(it.displayName) }
                                    )
                                    ExposedDropDownSetting(
                                        settingName = stringResource(SharedRes.strings.contrast),
                                        items = uiState.constraints,
                                        selectedItem = uiState.constraint,
                                        onItemSelected = {
                                            viewModel.handleIntent(
                                                SettingIntent.SetConstraints(it)
                                            )
                                        },
                                        displayMapper = { stringResource(it.displayName) }
                                    )
                                }
                            )




                            Spacer(modifier = Modifier.height(8.dp))

                            // 安全设置部分：锁定开关
                            SettingSection(
                                title = stringResource(SharedRes.strings.security),
                                description = stringResource(SharedRes.strings.security_setting_description),
                                settingItems = listOf {
                                    SwitchSetting(
                                        title = "Lock App",
                                        checked = true,
                                        onCheckedChange = {}
                                    )
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // 关于信息部分
                            AboutSection()
                        }
                    }
                }
            }


            if (uiState.showColorPicker) {
                ColorPickerDialog(
                    onDismissRequest = { viewModel.handleIntent(SettingIntent.HideColorPicker) },
                    onConfirmed = { color ->
                        viewModel.handleIntent(
                            SettingIntent.SetDynamicTheme(
                                AppTheme.DynamicAppTheme(
                                    color
                                )
                            )
                        )
                        viewModel.handleIntent(SettingIntent.HideColorPicker)
                    },
                    onCancel = { viewModel.handleIntent(SettingIntent.HideColorPicker) }
                )
            }
        }
    }
}




/** 信息展示设置项 **/
@Composable
fun InfoSetting(
    settingName: String,
    info: String
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = settingName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = info,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}


/** 关于部分 **/
@Composable
fun AboutSection() {
    SettingSection(
        title = "About",
        description = "Author information",
        settingItems = listOf {
            AuthorInfoSurface()
        }
    )
}

/** 作者信息展示区 **/
@Composable
fun AuthorInfoSurface() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            InfoSetting(
                settingName = stringResource(SharedRes.strings.author),
                info = "Tinker"
            )
            InfoSetting(
                settingName = stringResource(SharedRes.strings.email),
                info = "220110790202@zufe.edu.cn"
            )

            InfoSetting(
                settingName = stringResource(SharedRes.strings.version),
                info = "1.0.0"
            )
            InfoSetting(
                settingName = stringResource(SharedRes.strings.github),
                info = "https://github.com/TinkerAC/Penny"
            )

        }
    }
}