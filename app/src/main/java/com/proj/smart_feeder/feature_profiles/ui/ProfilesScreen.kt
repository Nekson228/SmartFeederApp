package com.proj.smart_feeder.feature_profiles.ui

import androidx.compose.foundation.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil3.compose.rememberAsyncImagePainter
import com.proj.smart_feeder.feature_profiles.domain.PetProfile
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(viewModel: ProfilesViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState {
        if (state.profiles.isEmpty()) 0 else state.profiles.size
    }

    var showStatsBottomSheet by remember { mutableStateOf(false) }
    var selectedProfileForStats by remember { mutableStateOf<PetProfile?>(null) }
    val sheetState = rememberModalBottomSheetState()

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(top = 16.dp)) {
            Text(
                text = "Питомцы",
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (state.profiles.isNotEmpty()) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(state.profiles.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(color)
                                .size(8.dp)
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val currentProfile = state.profiles[page]
                    PetProfileContent(
                        profile = currentProfile,
                        onSave = { name, breed, age, weight, photoUri ->
                            viewModel.updateProfile(currentProfile.id, name, breed, age, weight, photoUri)
                        },
                        onStatsClick = {
                            selectedProfileForStats = currentProfile
                            showStatsBottomSheet = true
                        }
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Профили не найдены")
                }
            }
        }
    }

    if (showStatsBottomSheet && selectedProfileForStats != null) {
        ModalBottomSheet(
            onDismissRequest = { showStatsBottomSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            PetStatisticsDetail(profile = selectedProfileForStats!!)
        }
    }
}

@Composable
fun PetStatisticsDetail(profile: PetProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(16.dp)
    ) {
        Text(
            text = "Статистика: ${profile.name}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    profile.feedingStats.forEach { h ->
                        Box(
                            Modifier.width(35.dp).fillMaxHeight(h).background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("История кормлений", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        val dummyHistory = listOf(
            "Сегодня, 08:30 — 60г",
            "Вчера, 20:15 — 55г",
            "Вчера, 12:00 — 60г",
            "Вчера, 08:00 — 50г",
            "20 окт, 19:40 — 70г",
            "20 окт, 11:30 — 60г",
            "19 окт, 20:00 — 55г"
        )

        Column(Modifier.verticalScroll(rememberScrollState())) {
            dummyHistory.forEach { entry ->
                ListItem(
                    headlineContent = { Text(entry) },
                    leadingContent = { Icon(Icons.Default.History, null, tint = MaterialTheme.colorScheme.primary) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun PetProfileContent(
    profile: PetProfile,
    onSave: (String, String, String, String, String?) -> Unit,
    onStatsClick: () -> Unit
) {
    var name by remember(profile.id) { mutableStateOf(profile.name) }
    var breed by remember(profile.id) { mutableStateOf(profile.breed) }
    var age by remember(profile.id) { mutableStateOf(profile.age) }
    var weight by remember(profile.id) { mutableStateOf(profile.weight) }
    var photoUri by remember(profile.id) { mutableStateOf(profile.photoUri) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        photoUri = uri?.toString()
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        .clip(CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Pets,
                            null,
                            modifier = Modifier.size(50.dp),
                            tint = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(4.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Порода") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Возраст") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Вес") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Статистика питания", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clickable { onStatsClick() },
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth().height(100.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    profile.feedingStats.forEach { h ->
                        Box(
                            Modifier.width(25.dp).fillMaxHeight(h).background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(4.dp)
                            )
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Объем за последние 7 дней (среднее: 320г/день)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Недавние фото с кормушки", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

        LazyRow(contentPadding = PaddingValues(vertical = 8.dp)) {
            items(5) { index ->
                Card(Modifier.size(140.dp).padding(end = 12.dp), shape = RoundedCornerShape(16.dp)) {
                    Box(
                        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Image,
                            null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(0.3f),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            "Photo #$index",
                            modifier = Modifier.align(Alignment.BottomCenter).padding(4.dp),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Button(
            onClick = { onSave(name, breed, age, weight, photoUri) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Сохранить изменения")
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}