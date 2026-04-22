package com.proj.smart_feeder.feature_feeder.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel


@Composable
fun FeederScreen(viewModel: FeederViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text(
            text = "Моя кормушка",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(160.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                Column {
                    Text("Запас корма", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)

                    Text(
                        text = "${state.currentFoodGrams}г / ${state.maxFoodCapacityGrams / 1000}кг",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val progressRatio = state.currentFoodGrams.toFloat() / state.maxFoodCapacityGrams.toFloat()
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = MaterialTheme.colorScheme.tertiary,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                }

                val wifiTint = if (state.lastSeenTimestamp != null) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                Column(
                    modifier = Modifier.align(Alignment.TopEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "WiFi Status",
                        tint = wifiTint
                    )
                    val timeText = state.lastSeenTimestamp?.let {
                        val millis = it.toEpochMilliseconds()
                        android.text.format.DateUtils.getRelativeTimeSpanString(millis).toString()
                    } ?: ""
                    Text(
                        text = timeText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Последние кормления", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        if (state.recentFeedings.isEmpty()) {
            Text("История пуста", modifier = Modifier.padding(top = 8.dp))
        } else {
            state.recentFeedings.forEach { feeding ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(feeding, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

