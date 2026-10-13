package com.ytkf2.bahceasist.feature.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.ytkf2.bahceasist.core.common.GardenCalendarHelper
import com.ytkf2.bahceasist.core.common.Season
import com.ytkf2.bahceasist.core.ui.components.BahcemHeader
import com.ytkf2.bahceasist.core.ui.components.LocationPickerDialog
import com.ytkf2.bahceasist.core.ui.components.TaskFormContent
import com.ytkf2.bahceasist.core.ui.components.formatTaskDueTime
import com.ytkf2.bahceasist.core.ui.theme.*
import com.ytkf2.bahceasist.domain.model.AdviceType
import com.ytkf2.bahceasist.domain.model.GardenTask
import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.model.Severity
import com.ytkf2.bahceasist.domain.model.WeatherCondition
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.Instant

fun weatherConditionInfo(condition: WeatherCondition): Pair<String, ImageVector> = when (condition) {
    WeatherCondition.CLEAR -> "Açık / Güneşli" to Icons.Default.WbSunny
    WeatherCondition.PARTLY_CLOUDY -> "Parçalı Bulutlu" to Icons.Default.CloudQueue
    WeatherCondition.CLOUDY -> "Çok Bulutlu" to Icons.Default.Cloud
    WeatherCondition.FOG -> "Sisli" to Icons.Default.Dehaze
    WeatherCondition.DRIZZLE -> "Çiseleme" to Icons.Default.WaterDrop
    WeatherCondition.RAIN -> "Yağmurlu" to Icons.Default.Grain
    WeatherCondition.SNOW -> "Karlı" to Icons.Default.AcUnit
    WeatherCondition.THUNDERSTORM -> "Fırtına / Şimşekli" to Icons.Default.FlashOn
    WeatherCondition.UNKNOWN -> "Parçalı Bulutlu" to Icons.Default.CloudQueue
}

data class AlertDisplay(
    val title: String,
    val desc: String,
    val color: Color,
    val icon: ImageVector
)

@Composable
fun DashboardScreen(
    uiState: DashboardUiState = DashboardUiState(),
    onRefresh: () -> Unit = {},
    onNavigateToAgenda: () -> Unit = {},
    onSearchLocation: (String) -> Unit = {},
    onSelectLocation: (Location) -> Unit = {},
    onToggleTask: (Long, Boolean) -> Unit = { _, _ -> },
    onDeleteTask: (Long) -> Unit = {},
    onAddTask: (String, String, String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var frostAlertDismissed by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }

    val weather = uiState.weather
    val current = weather?.current
    val tempDisplay = current?.let { "${it.temperatureC.toInt()}°" } ?: "14°"
    val locDisplay = uiState.selectedLocation?.let {
        "${it.region ?: it.country ?: ""}, ${it.name}"
    } ?: "Bursa, Nilüfer"
    val condition = current?.condition ?: WeatherCondition.PARTLY_CLOUDY
    val (conditionText, conditionIcon) = weatherConditionInfo(condition)
    val today = remember { LocalDate.now() }
    val zone = remember { ZoneId.systemDefault() }
    val todayDaily = weather?.daily?.firstOrNull { item ->
        val itemDate = Instant.ofEpochMilli(item.dateMillis).atZone(zone).toLocalDate()
        itemDate == today
    } ?: weather?.daily?.firstOrNull()
    val highLowText = todayDaily?.let {
        "En Yüksek: ${it.maxTempC.toInt()}° / En Düşük: ${it.minTempC.toInt()}°"
    } ?: "Hissedilen: 16°C • En Yüksek: 18° / En Düşük: 6°"

    if (showLocationDialog) {
        LocationPickerDialog(
            currentLocation = uiState.selectedLocation,
            onDismissRequest = { showLocationDialog = false },
            onSearch = onSearchLocation,
            searchResults = uiState.locationSearchResults,
            isSearching = uiState.isSearchingLocation,
            onSelectLocation = { loc ->
                onSelectLocation(loc)
                showLocationDialog = false
            }
        )
    }

    if (showAddTaskDialog) {
        var taskTitle by remember { mutableStateOf("") }
        var taskDate by remember { mutableStateOf(LocalDate.now()) }
        var selectedTimeSlot by remember { mutableStateOf("Sabah (09:00)") }
        var customTimeText by remember { mutableStateOf("") }
        var taskCategory by remember { mutableStateOf("Sulama") }

        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = {
                Text(
                    text = "Yeni Bahçe Görevi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ForestPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    TaskFormContent(
                        taskTitle = taskTitle,
                        onTaskTitleChange = { taskTitle = it },
                        taskDate = taskDate,
                        onTaskDateChange = { taskDate = it },
                        selectedTimeSlot = selectedTimeSlot,
                        onTimeSlotChange = { selectedTimeSlot = it },
                        customTimeText = customTimeText,
                        onCustomTimeChange = { customTimeText = it },
                        taskCategory = taskCategory,
                        onCategoryChange = { taskCategory = it }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (taskTitle.isNotBlank()) {
                            val formattedDueTime = formatTaskDueTime(taskDate, selectedTimeSlot, customTimeText)
                            onAddTask(taskTitle.trim(), formattedDueTime, taskCategory)
                            showAddTaskDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary)
                ) {
                    Text("Ekle", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Vazgeç", color = OnGardenSurfaceVariant)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            BahcemHeader(
                subtitle = "Hava & Bahçe",
                locationName = locDisplay,
                onLocationClick = { showLocationDialog = true }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // Loading Bar
            if (uiState.isLoading) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape),
                        color = ForestPrimary
                    )
                }
            }

            // 1. GREETING, DATE & SEASON (Dinamik Gün, Ay, Yıl ve Mevsim)
            item {
                val currentSeason = remember { GardenCalendarHelper.getSeason() }
                val currentGreeting = remember { GardenCalendarHelper.getGreeting() }
                val todayFormatted = remember { GardenCalendarHelper.formatFullDate() }

                val seasonIcon = when (currentSeason) {
                    Season.SPRING -> Icons.Default.Eco
                    Season.SUMMER -> Icons.Default.WbSunny
                    Season.AUTUMN -> Icons.Default.Park
                    Season.WINTER -> Icons.Default.AcUnit
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Text(
                            text = currentGreeting,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GardenSecondary
                        )
                        Text(
                            text = todayFormatted,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnGardenSurface
                        )
                    }

                    // Season Pill
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GardenSecondaryContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = seasonIcon,
                            contentDescription = "Mevsim",
                            tint = ForestPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = currentSeason.displayName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestPrimary
                        )
                    }
                }
            }

            // 2. MAIN WEATHER CARD
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLowest),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    border = BorderStroke(1.dp, GardenSurfaceContainerHigh),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Temp & Condition Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = tempDisplay,
                                    fontSize = 54.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ForestPrimary,
                                    letterSpacing = (-1).sp
                                )

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = GardenSecondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = locDisplay,
                                            fontSize = 12.sp,
                                            color = OnGardenSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = conditionText,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnGardenSurface
                                    )
                                    Text(
                                        text = highLowText,
                                        fontSize = 11.sp,
                                        color = OnGardenSurfaceVariant
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(GardenSurfaceContainerLow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = conditionIcon,
                                    contentDescription = "Hava",
                                    tint = GardenSecondary,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        // 4-Grid Stats
                        val windVal = current?.let { "${it.windSpeedKmh.toInt()} km/s" } ?: "8 km/s"
                        val rain24Val = weather?.precipitationLast24hMm?.let { "%.1f mm".format(java.util.Locale.US, it) } ?: "%62"
                        val rain7dVal = weather?.precipitationLast7dMm?.let { "%.1f mm".format(java.util.Locale.US, it) } ?: "%48"
                        val uvOrFeel = current?.let { if (it.temperatureC >= 25) "Sıcak" else if (it.temperatureC < 10) "Serin" else "Ilıman" } ?: "Orta"

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            WeatherStatItem(
                                title = "24s Yağış",
                                value = rain24Val,
                                subtitle = "Son 24 Saat",
                                icon = Icons.Default.WaterDrop,
                                iconColor = WeatherTertiary,
                                modifier = Modifier.weight(1f)
                            )
                            WeatherStatItem(
                                title = "Rüzgar",
                                value = windVal,
                                subtitle = "Güncel Hız",
                                icon = Icons.Default.Air,
                                iconColor = GardenOutline,
                                modifier = Modifier.weight(1f)
                            )
                            WeatherStatItem(
                                title = "7g Yağış",
                                value = rain7dVal,
                                subtitle = "Haftalık Toplam",
                                icon = Icons.Default.Opacity,
                                iconColor = ForestPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            WeatherStatItem(
                                title = "Hissedilen",
                                value = uvOrFeel,
                                subtitle = "Dengeli",
                                icon = Icons.Default.WbSunny,
                                iconColor = GardenSecondary,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Soil moisture progress
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Toprak Nemi & Kök Derinliği",
                                    fontSize = 11.sp,
                                    color = OnGardenSurfaceVariant
                                )
                                Text(
                                    text = "Kök Bölgesi Optimum Nem (%68)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestPrimary
                                )
                            }
                            LinearProgressIndicator(
                                progress = { 0.68f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = ForestPrimaryContainer,
                                trackColor = GardenSurfaceContainerHighest
                            )
                        }
                    }
                }
            }

            // 3. METEOROLOGICAL ALERT CARD (Kural Motorundan Gelen Canlı Tavsiye)
            val topAdvice = uiState.advices.firstOrNull { it.type != AdviceType.ALL_CLEAR } ?: uiState.advices.firstOrNull()
            if (topAdvice != null && !frostAlertDismissed) {
                val alertDisplay = when (topAdvice.type) {
                    AdviceType.FROST_WARNING -> AlertDisplay(
                        title = "Don Riski (${topAdvice.value?.toInt() ?: 0}°C)!",
                        desc = "Kritik soğuk bekleniyor. Açık alandaki hassas bitkileri ve fideleri don örtüsüyle koruma altına alın.",
                        color = AlertError,
                        icon = Icons.Default.AcUnit
                    )
                    AdviceType.STORM_WARNING -> AlertDisplay(
                        title = "Kuvvetli Fırtına Uyarısı (${topAdvice.value?.toInt() ?: 0} km/s)!",
                        desc = "Şiddetli rüzgar uyarısı. Sera örtülerini ve yeni dikilen fidanların hereklerini kontrol edin.",
                        color = AlertError,
                        icon = Icons.Default.Air
                    )
                    AdviceType.NO_SPRAYING_WIND -> AlertDisplay(
                        title = "Rüzgarlı Hava: İlaçlama Yapmayın (${topAdvice.value?.toInt() ?: 0} km/s)!",
                        desc = "Rüzgar hızı yüksek olduğu için ilaçlama zaiyatı ve yaprak yanıklığı riski var. Rüzgarın dinmesini bekleyin.",
                        color = Color(0xFFE65100),
                        icon = Icons.Default.Warning
                    )
                    AdviceType.HEAT_WARNING -> AlertDisplay(
                        title = "Aşırı Sıcaklık Uyarısı (${topAdvice.value?.toInt() ?: 0}°C)!",
                        desc = "Aşırı sıcaklar su stresine yol açabilir. Sulamayı gün ortasında değil, serin saatlerde uygulayın.",
                        color = Color(0xFFD84315),
                        icon = Icons.Default.WbSunny
                    )
                    AdviceType.POSTPONE_IRRIGATION -> AlertDisplay(
                        title = "Sulamayı Erteleyin (${topAdvice.value?.toInt() ?: 0} mm yağış)!",
                        desc = "Son saatlerde toprağa yeterli miktarda doğal yağış düştü. Kök boğulmasını önlemek için sulamayı erteleyin.",
                        color = GardenSecondary,
                        icon = Icons.Default.WaterDrop
                    )
                    AdviceType.DROUGHT_WARNING -> AlertDisplay(
                        title = "Yağış Azlığı & Sulama Hatırlatması!",
                        desc = "Son 7 günde ve yakın tahminlerde yağış görünmüyor. Kök bölgesi nemini denetleyip sulama planlayın.",
                        color = WeatherTertiary,
                        icon = Icons.Default.WaterDrop
                    )
                    AdviceType.ALL_CLEAR -> AlertDisplay(
                        title = "Tarla ve Bahçe Koşulları Müsait 🌱",
                        desc = "Hava durumu bahçe işleri, fide bakımı ve budama için elverişli görünüyor.",
                        color = GardenSecondary,
                        icon = Icons.Default.Eco
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = WeatherTertiaryFixed),
                        border = BorderStroke(1.dp, WeatherTertiaryContainer.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(WeatherTertiaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = alertDisplay.icon,
                                            contentDescription = null,
                                            tint = WeatherTertiaryFixed,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(alertDisplay.color)
                                            )
                                            Text(
                                                text = "TARLA VE BAHÇE TAVSİYESİ",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = alertDisplay.color
                                            )
                                        }
                                        Text(
                                            text = alertDisplay.title,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = OnGardenSurface
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { frostAlertDismissed = true },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Kapat",
                                        tint = OnGardenSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(
                                text = alertDisplay.desc,
                                fontSize = 12.sp,
                                color = OnGardenSurfaceVariant,
                                lineHeight = 17.sp
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = { onNavigateToAgenda() },
                                    colors = ButtonDefaults.buttonColors(containerColor = WeatherTertiary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Ajandaya Git", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onRefresh() },
                                    colors = ButtonDefaults.outlinedButtonColors(containerColor = GardenSurfaceContainerLowest),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = WeatherTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Yenile", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestPrimary)
                                }
                            }
                        }
                    }
                }
            }

            // 4. WEEKLY 5-DAY WEATHER & AGENDA
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLowest),
                    border = BorderStroke(1.dp, GardenSurfaceContainerHigh),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val todayTasks = uiState.tasks.filter { task ->
                        GardenCalendarHelper.isTaskForDate(
                            dueTime = task.dueTime,
                            createdAtMillis = task.createdAtMillis,
                            targetDate = today,
                            zone = zone
                        )
                    }
                    val totalCount = todayTasks.size
                    val completedCount = todayTasks.count { it.isCompleted }
                    val remainingCount = totalCount - completedCount
                    val statusPill = if (totalCount > 0) "$completedCount/$totalCount Tamamlandı" else "0 Görev"

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .padding(end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = ForestPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Haftalık Bahçe Ajandası",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnGardenSurface,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GardenSurfaceContainerHigh,
                                border = BorderStroke(1.dp, ForestPrimary.copy(alpha = 0.15f))
                            ) {
                                Text(
                                    text = statusPill,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestPrimary,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // 5-Day Strip
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val upcomingDaily = weather?.daily
                                ?.filter { dayItem ->
                                    val itemDate = Instant.ofEpochMilli(dayItem.dateMillis)
                                        .atZone(zone)
                                        .toLocalDate()
                                    !itemDate.isBefore(today)
                                }
                                ?.distinctBy { dayItem ->
                                    Instant.ofEpochMilli(dayItem.dateMillis)
                                        .atZone(zone)
                                        .toLocalDate()
                                }
                                ?.take(5)

                            if (!upcomingDaily.isNullOrEmpty()) {
                                upcomingDaily.forEachIndexed { index, dayItem ->
                                    val itemDate = Instant.ofEpochMilli(dayItem.dateMillis)
                                        .atZone(zone)
                                        .toLocalDate()
                                    val isToday = itemDate == today || (index == 0 && itemDate <= today)
                                    val dayName = if (isToday) "Bugün" else GardenCalendarHelper.getDayNameShort(itemDate.dayOfWeek)
                                    val (_, dayIcon) = weatherConditionInfo(dayItem.condition)
                                    val isWarning = dayItem.minTempC <= 2.0 || dayItem.maxWindKmh >= 50.0
                                    DayForecastBadge(
                                        day = dayName,
                                        temp = "${dayItem.maxTempC.toInt()}°",
                                        icon = dayIcon,
                                        isToday = isToday,
                                        isWarning = isWarning,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            } else {
                                val fallbackDays = remember(today) { GardenCalendarHelper.getUpcomingDayNames(5, today) }
                                val fallbackTemps = listOf("14°", "15°", "13°", "12°", "14°")
                                val fallbackIcons = listOf(
                                    Icons.Default.WbSunny,
                                    Icons.Default.WbSunny,
                                    Icons.Default.CloudQueue,
                                    Icons.Default.WaterDrop,
                                    Icons.Default.WbSunny
                                )
                                fallbackDays.forEachIndexed { idx, dayName ->
                                    DayForecastBadge(
                                        day = dayName,
                                        temp = fallbackTemps.getOrElse(idx) { "14°" },
                                        icon = fallbackIcons.getOrElse(idx) { Icons.Default.WbSunny },
                                        isToday = idx == 0,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = GardenSurfaceContainer)

                        // Tasks Checklist Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "BUGÜNÜN BAHÇE GÖREVLERİ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnGardenSurfaceVariant
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = if (totalCount > 0) "$remainingCount Kaldı" else "Görev Yok",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GardenSecondary
                                )
                                TextButton(
                                    onClick = { showAddTaskDialog = true },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = ForestPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text("Ekle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestPrimary)
                                }
                            }
                        }

                        if (todayTasks.isEmpty()) {
                            // Görev yoksa temiz boş durum (kullanıcı görev silince ekranda takılı sahte görev kalmaz)
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLow),
                                border = BorderStroke(1.dp, GardenSurfaceContainerHigh),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircleOutline,
                                        contentDescription = null,
                                        tint = GardenSecondary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Text(
                                        text = "Planlanmış Görev Yok",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = OnGardenSurface
                                    )
                                    Text(
                                        text = "Bugün için kayıtlı bir görev bulunmuyor. Yeni görev ekleyerek bahçe takviminizi oluşturun.",
                                        fontSize = 11.sp,
                                        color = OnGardenSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Button(
                                        onClick = { showAddTaskDialog = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Görev Ekle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            todayTasks.forEach { task ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(GardenSurfaceContainerLow)
                                        .clickable { onToggleTask(task.id, !task.isCompleted) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (task.isCompleted) ForestPrimary else GardenSurfaceContainerHighest),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (task.isCompleted) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }

                                        Column {
                                            Text(
                                                text = task.title,
                                                fontSize = 13.sp,
                                                fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                                                color = if (task.isCompleted) OnGardenSurfaceVariant else OnGardenSurface,
                                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = task.category,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ForestPrimary
                                                )
                                                Text(
                                                    text = "• ${task.dueTime}",
                                                    fontSize = 10.sp,
                                                    color = OnGardenSurfaceVariant
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteTask(task.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Görevi Sil",
                                            tint = AlertError.copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherStatItem(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GardenSurfaceContainerLow)
            .padding(vertical = 10.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(13.dp))
            Text(text = title, fontSize = 10.sp, color = OnGardenSurfaceVariant)
        }
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = OnGardenSurface)
        Text(text = subtitle, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = GardenSecondary)
    }
}

@Composable
fun DayForecastBadge(
    day: String,
    temp: String,
    icon: ImageVector,
    isToday: Boolean,
    isWarning: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isToday -> ForestPrimary
        isWarning -> WeatherTertiaryFixed
        else -> GardenSurfaceContainerLow
    }
    val textColor = when {
        isToday -> Color.White
        isWarning -> AlertError
        else -> OnGardenSurface
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = day, fontSize = 10.sp, color = textColor.copy(alpha = 0.8f))
        Text(text = temp, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
        Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(15.dp))
    }
}
