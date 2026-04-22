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
import kotlinx.datetime.LocalTime
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeederScreen(viewModel: FeederViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Моя кормушка",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = { showSheet = true }) {
                Icon(
                    imageVector = Icons.Default.AddAlarm,
                    contentDescription = "Добавить расписание",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
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

                    val progressRatio = state.currentFoodGrams / state.maxFoodCapacityGrams
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
        
        if (state.schedules.isNotEmpty()) {
            Text("Расписание", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            state.schedules.forEach { schedule ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${schedule.startTime} - ${schedule.endTime}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

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

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            AddScheduleSheetContent(
                onAdd = { start, end ->
                    viewModel.addSchedule(start, end)
                    showSheet = false
                },
                onCancel = { showSheet = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleSheetContent(
    onAdd: (LocalTime, LocalTime) -> Unit,
    onCancel: () -> Unit
) {
    val startTimeState = rememberTimePickerState(initialHour = 8, initialMinute = 0, is24Hour = true)
    val endTimeState = rememberTimePickerState(initialHour = 9, initialMinute = 0, is24Hour = true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Новое расписание",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Начало выдачи", style = MaterialTheme.typography.labelLarge)
        TimeInput(state = startTimeState)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Конец выдачи", style = MaterialTheme.typography.labelLarge)
        TimeInput(state = endTimeState)

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Отмена")
            }
            Button(
                onClick = {
                    onAdd(
                        LocalTime(startTimeState.hour, startTimeState.minute),
                        LocalTime(endTimeState.hour, endTimeState.minute)
                    )
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Добавить")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

