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


// --- ЭКРАН 2: ПРОФИЛИ ---
@Composable
fun ProfilesScreen() {
    val pagerState = rememberPagerState(initialPage = 0) { 1 } // Кол-во питомцев
    Column(modifier = Modifier.fillMaxSize().padding(top = 16.dp)) {
        Text(
            text = "Питомцы",
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            PetProfileContent()
        }
    }
}

@Composable
fun PetProfileContent() {
    var name by remember { mutableStateOf("Барсик") }
    var age by remember { mutableStateOf("3 года") }
    var weight by remember { mutableStateOf("5.4 кг") }
    var breed by remember { mutableStateOf("Британская") }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(100.dp).background(MaterialTheme.colorScheme.secondary, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Pets, null, modifier = Modifier.size(50.dp), tint = MaterialTheme.colorScheme.onSecondary)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Имя") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Порода") }, modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Возраст") }, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Вес") }, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Статистика питания", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth().height(100.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
                    val stats = listOf(0.4f, 0.8f, 0.6f, 1f, 0.7f, 0.9f, 0.5f)
                    stats.forEach { h ->
                        Box(Modifier.width(25.dp).fillMaxHeight(h).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)))
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Объем за последние 7 дней (среднее: 320г/день)", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Недавние фото с кормушки", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        LazyRow(contentPadding = PaddingValues(vertical = 8.dp)) {
            items(5) { index ->
                Card(Modifier.size(140.dp).padding(end = 12.dp), shape = RoundedCornerShape(16.dp)) {
                    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Image, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.3f), modifier = Modifier.size(40.dp))
                        Text("Photo #$index", modifier = Modifier.align(Alignment.BottomCenter).padding(4.dp), fontSize = 10.sp)
                    }
                }
            }
        }

        Button(onClick = { /* Сохранение */ }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            Text("Сохранить изменения")
        }
        Spacer(modifier = Modifier.height(100.dp)) // Запас под BottomBar
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
