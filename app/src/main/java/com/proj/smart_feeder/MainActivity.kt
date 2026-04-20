package com.proj.smart_feeder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.proj.smart_feeder.feature_feeder.ui.FeederScreen
import com.proj.smart_feeder.feature_profiles.ui.ProfilesScreen
import com.proj.smart_feeder.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Состояние темы (true - Mocha/Dark, false - Latte/Light)
            var isDarkMode by remember { mutableStateOf(true) }

            SmartBowlTheme(darkTheme = isDarkMode) {
                MainContainer(isDarkMode = isDarkMode, onThemeChange = { isDarkMode = it })
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
fun MainContainer(isDarkMode: Boolean, onThemeChange: (Boolean) -> Unit) {
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
            composable(Screen.Settings.route) { SettingsScreen(isDarkMode, onThemeChange) }
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


// --- ЭКРАН 3: НАСТРОЙКИ ---
@Composable
fun SettingsScreen(isDarkMode: Boolean, onThemeChange: (Boolean) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Настройки", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(24.dp))
        Text("Внешний вид", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        SettingsItem("Темная тема") {
            Switch(checked = isDarkMode, onCheckedChange = { onThemeChange(it) })
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Типы уведомлений", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)

        NotificationGroup("Критические", listOf("Корм закончился", "Засор в дозаторе", "Миска отключена"))
        NotificationGroup("Здоровье", listOf("Скорость поедания снизилась"))
        NotificationGroup("Информационные", listOf("Отчет о кормлении"))

        Spacer(modifier = Modifier.height(16.dp))
        Text("Система", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        SettingsItem("Очистить кэш приложения") {
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("124 МБ")
            }
        }
    }
}

@Composable
fun NotificationGroup(title: String, items: List<String>) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
        items.forEach { item ->
            var checked by remember { mutableStateOf(true) }
            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Checkbox(checked = checked, onCheckedChange = { checked = it })
            }
        }
    }
}

@Composable
fun SettingsItem(title: String, trailing: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(title, color = MaterialTheme.colorScheme.onSurface)
        trailing()
    }
}
