/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.nav_cupcake

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import app.penny.ui.screens.AnalyticsScreen
import app.penny.ui.screens.DashboardScreen
import app.penny.ui.screens.ProfileScreen
import app.penny.ui.screens.TransactionScreen
import app.penny.viewModels.DashboardViewModel

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

import penny.composeapp.generated.resources.Res
import penny.composeapp.generated.resources.analytics
import penny.composeapp.generated.resources.back_button
import penny.composeapp.generated.resources.dashboard
import penny.composeapp.generated.resources.profile
import penny.composeapp.generated.resources.transaction

/**
 * enum values that represent the screens in the app
 */
enum class PennyScreen(val title: StringResource) {
    Dashboard(title = Res.string.dashboard),
    Transaction(title = Res.string.transaction),
    Analytics(title = Res.string.analytics),
    Profile(title = Res.string.profile)

}

//软件的顶部栏，显示当前屏幕的标题，并在可以返回时显示返回按钮。
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PennyTopBar(
    currentScreen: PennyScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun PennyBottomBar(
    currentScreen: PennyScreen,
    onScreenSelected: (PennyScreen) -> Unit
) {
    BottomNavigation {
        PennyScreen.entries.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = when (screen) {
                            PennyScreen.Dashboard -> Icons.Filled.Home
                            PennyScreen.Transaction -> Icons.Filled.ShoppingCart
                            PennyScreen.Analytics -> Icons.Filled.Done // TODO: Replace with actual icon
                            PennyScreen.Profile -> Icons.Filled.Person
                        },
                        contentDescription = stringResource(screen.title)
                    )
                },
                label = { Text(stringResource(screen.title)) },
                selected = currentScreen == screen,
                onClick = { onScreenSelected(screen) }
            )
        }
    }
}

@Composable
fun PennyApp(
    viewModel: DashboardViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {


    val currentScreen = PennyScreen.valueOf(
        navController.currentBackStackEntryAsState().value?.destination?.route
            ?: PennyScreen.Dashboard.name
    )

    Scaffold(
        topBar = {
            PennyTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        bottomBar = {
            PennyBottomBar(
                currentScreen = currentScreen,
                onScreenSelected = { screen ->
                    navController.navigate(screen.name)
                }
            )
        }

    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = PennyScreen.Dashboard.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = PennyScreen.Dashboard.name) {
                DashboardScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),

                )
            }
            composable(route = PennyScreen.Transaction.name) {
                TransactionScreen(

                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = PennyScreen.Analytics.name) {
                AnalyticsScreen(
                    modifier = Modifier.fillMaxHeight()
                )
            }
            composable(route = PennyScreen.Profile.name) {
                ProfileScreen(
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}

