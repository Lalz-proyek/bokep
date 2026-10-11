package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ThemeMode
import com.example.ui.screens.AppDetailDialog
import com.example.ui.screens.DeviceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PermissionsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.PinkCatTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val userPreferences by mainViewModel.userPreferences.collectAsState()

            val isDark = when (userPreferences.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            PinkCatTheme(darkTheme = isDark) {
                PinkCatApp(viewModel = mainViewModel)
            }
        }
    }
}

sealed class NavTab(
    val index: Int,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Home : NavTab(0, "Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home_tab")
    object Device : NavTab(1, "Device", Icons.Filled.PhoneAndroid, Icons.Outlined.PhoneAndroid, "nav_device_tab")
    object Permissions : NavTab(2, "Permissions", Icons.Filled.Security, Icons.Outlined.Security, "nav_permissions_tab")
    object Settings : NavTab(3, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings_tab")

    companion object {
        val allTabs = listOf(Home, Device, Permissions, Settings)
    }
}

@Composable
fun PinkCatApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val selectedApp by viewModel.selectedApp.collectAsState()

    // Handle back button gracefully
    BackHandler(enabled = selectedApp != null || currentTab != 0) {
        if (selectedApp != null) {
            viewModel.selectApp(null)
        } else if (currentTab != 0) {
            viewModel.selectTab(0)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavTab.allTabs.forEach { tab ->
                    val isSelected = currentTab == tab.index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab.index) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            label = "ScreenTransition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tabIndex ->
            when (tabIndex) {
                0 -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToDevice = { viewModel.selectTab(1) },
                    onNavigateToPermissions = { viewModel.selectTab(2) }
                )
                1 -> DeviceScreen(viewModel = viewModel)
                2 -> PermissionsScreen(viewModel = viewModel)
                3 -> SettingsScreen(viewModel = viewModel)
                else -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToDevice = { viewModel.selectTab(1) },
                    onNavigateToPermissions = { viewModel.selectTab(2) }
                )
            }
        }
    }

    // App Detail Dialog if an app is selected
    if (selectedApp != null) {
        AppDetailDialog(
            app = selectedApp!!,
            viewModel = viewModel,
            onDismiss = { viewModel.selectApp(null) }
        )
    }
}
