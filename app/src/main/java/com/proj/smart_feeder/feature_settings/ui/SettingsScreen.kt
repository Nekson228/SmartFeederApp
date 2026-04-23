package com.proj.smart_feeder.feature_settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.Icons
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("Внешний вид", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        SettingsItem("Темная тема") {
            Switch(
                checked = uiState.isDarkMode,
                onCheckedChange = { viewModel.toggleDarkMode(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Типы уведомлений", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)

        val categories = uiState.notificationSettings.groupBy { it.category }
        categories.forEach { (category, items) ->
            NotificationGroup(category, items) { id, enabled ->
                viewModel.toggleNotification(id, enabled)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Система", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        SettingsItem("Очистить кэш приложения") {
            Button(
                onClick = { viewModel.clearCache() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("${uiState.cacheSizeMb} МБ")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Устройство", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        
        var isEditingBowlId by remember { mutableStateOf(false) }
        var bowlIdInput by remember { mutableStateOf("") }

        LaunchedEffect(isEditingBowlId) {
            if (isEditingBowlId) {
                bowlIdInput = uiState.bowlId ?: ""
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ID кормушки", color = MaterialTheme.colorScheme.onSurface)
                if (isEditingBowlId) {
                    IconButton(onClick = {
                        viewModel.saveBowlId(bowlIdInput)
                        isEditingBowlId = false
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Сохранить", tint = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    IconButton(onClick = { isEditingBowlId = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать", modifier = Modifier.size(20.dp))
                    }
                }
            }
            
            if (isEditingBowlId) {
                OutlinedTextField(
                    value = bowlIdInput,
                    onValueChange = { bowlIdInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            } else {
                Text(
                    text = uiState.bowlId ?: "Не задан",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationGroup(
    title: String,
    items: List<com.proj.smart_feeder.feature_settings.domain.NotificationSetting>,
    onToggle: (String, Boolean) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
        items.forEach { item ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.title, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Checkbox(
                    checked = item.isEnabled,
                    onCheckedChange = { onToggle(item.id, it) }
                )
            }
        }
    }
}

@Composable
fun SettingsItem(title: String, trailing: @Composable () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Text(title, color = MaterialTheme.colorScheme.onSurface)
        trailing()
    }
}

