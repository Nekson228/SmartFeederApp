package com.proj.smart_feeder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.proj.smart_feeder.feature_feeder.ui.FeederScreen
import com.proj.smart_feeder.feature_profiles.ui.ProfilesScreen
import com.proj.smart_feeder.feature_settings.ui.SettingsScreen
import com.proj.smart_feeder.feature_settings.ui.SettingsViewModel
import com.proj.smart_feeder.ui.theme.*
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = koinViewModel()
            val settingsState by settingsViewModel.uiState.collectAsState()

            SmartBowlTheme(darkTheme = settingsState.isDarkMode) {
                MainContainer()
            }
        }
    }
}

sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String) {
    data object Feeder : Screen("home", Icons.Default.Home, "Главная")
    data object Profiles : Screen("profiles", Icons.Default.Pets, "Питомцы")
    data object Settings : Screen("settings", Icons.Default.Settings, "Настройки")
}

@Composable
fun MainContainer() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feeder.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Feeder.route) { FeederScreen() }
            composable(Screen.Profiles.route) { ProfilesScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(Screen.Feeder, Screen.Profiles, Screen.Settings)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


