package com.ytkf2.bahceasist.feature.notes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.verticalScroll
import com.ytkf2.bahceasist.core.common.CalendarDay
import com.ytkf2.bahceasist.core.common.GardenCalendarHelper
import com.ytkf2.bahceasist.core.ui.components.BahcemHeader
import com.ytkf2.bahceasist.core.ui.components.LocationPickerDialog
import com.ytkf2.bahceasist.core.ui.components.TaskFormContent
import com.ytkf2.bahceasist.core.ui.components.formatTaskDueTime
import com.ytkf2.bahceasist.core.ui.theme.*
import com.ytkf2.bahceasist.domain.model.GardenTask
import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.model.NoteCategory
import java.time.LocalDate

fun NoteCategory.toDisplayLabel(): String = when (this) {
    NoteCategory.IRRIGATION -> "💧 Sulama"
    NoteCategory.SPRAYING -> "🧴 İlaçlama"
    NoteCategory.PLANTING -> "🌱 Ekim/Dikim"
    NoteCategory.HARVEST -> "🌾 Hasat"
    NoteCategory.BEEKEEPING -> "🐝 Arıcılık"
    NoteCategory.OTHER -> "📝 Bahçe Notu"
}

@Composable
fun AgendaScreen(
    uiState: AgendaUiState = AgendaUiState(),
    currentLocation: Location? = null,
    onCategorySelected: (NoteCategory?) -> Unit = {},
    onAddNote: (String, String, NoteCategory) -> Unit = { _, _, _ -> },
    onDeleteNote: (Long) -> Unit = {},
    onAddTask: (String, String, String) -> Unit = { _, _, _ -> },
    onToggleTask: (Long, Boolean) -> Unit = { _, _ -> },
    onDeleteTask: (Long) -> Unit = {},
    onSearchLocation: (String) -> Unit = {},
    onSelectLocation: (Location) -> Unit = {},
    locationSearchResults: List<Location> = emptyList(),
    isSearchingLocation: Boolean = false,
    onNewNoteClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var currentWeekReference by remember { mutableStateOf(LocalDate.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedCategory by remember { mutableStateOf("all") }
    var showLocationDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var addDialogTab by remember { mutableIntStateOf(0) } // 0: Görev Ekle, 1: Not Ekle

    val weekDays = remember(currentWeekReference) {
        GardenCalendarHelper.getWeekDays(currentWeekReference)
    }

    val currentMonthYearText = remember(selectedDate) {
        GardenCalendarHelper.formatMonthYear(selectedDate)
    }

    val filterChips = listOf(
        Pair("all", "Tümü"),
        Pair("sulama", "💧 Sulama"),
        Pair("gubre", "🌱 Gübreleme"),
        Pair("budama", "✂️ Budama"),
        Pair("hasat", "🧺 Hasat & Ekim")
    )

    val locDisplay = currentLocation?.let {
        "${it.region ?: it.country ?: ""}, ${it.name}"
    } ?: "Bursa, Nilüfer"

    if (showLocationDialog) {
        LocationPickerDialog(
            currentLocation = currentLocation,
            onDismissRequest = { showLocationDialog = false },
            onSearch = onSearchLocation,
            searchResults = locationSearchResults,
            isSearching = isSearchingLocation,
            onSelectLocation = { loc ->
                onSelectLocation(loc)
                showLocationDialog = false
            }
        )
    }

    if (showAddDialog) {
        var taskTitle by remember { mutableStateOf("") }
        var taskDate by remember(selectedDate) { mutableStateOf(selectedDate) }
        var selectedTimeSlot by remember { mutableStateOf("Sabah (09:00)") }
        var customTimeText by remember { mutableStateOf("") }
        var taskCategory by remember { mutableStateOf("Sulama") }

        var noteTitle by remember { mutableStateOf("") }
        var noteContent by remember { mutableStateOf("") }
        var noteCategory by remember { mutableStateOf(NoteCategory.IRRIGATION) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                TabRow(
                    selectedTabIndex = addDialogTab,
                    containerColor = Color.Transparent,
                    contentColor = ForestPrimary
                ) {
                    Tab(
                        selected = addDialogTab == 0,
                        onClick = { addDialogTab = 0 },
                        text = { Text("Görev Ekle", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = addDialogTab == 1,
                        onClick = { addDialogTab = 1 },
                        text = { Text("Not Ekle", fontWeight = FontWeight.Bold) }
                    )
                }
            },
            text = {
                if (addDialogTab == 0) {
                    // Görev Ekleme Formu
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
                } else {
                    // Not Ekleme Formu
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        OutlinedTextField(
                            value = noteTitle,
                            onValueChange = { noteTitle = it },
                            label = { Text("Not Başlığı (örn: Budama yapıldı)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = noteContent,
                            onValueChange = { noteContent = it },
                            label = { Text("Not Detayı") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "Kategori",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnGardenSurfaceVariant
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            NoteCategory.entries.forEach { cat ->
                                FilterChip(
                                    selected = noteCategory == cat,
                                    onClick = { noteCategory = cat },
                                    label = { Text(cat.toDisplayLabel(), fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (addDialogTab == 0) {
                            if (taskTitle.isNotBlank()) {
                                val formattedDueTime = formatTaskDueTime(taskDate, selectedTimeSlot, customTimeText)
                                onAddTask(taskTitle.trim(), formattedDueTime, taskCategory)
                                showAddDialog = false
                            }
                        } else {
                            if (noteTitle.isNotBlank()) {
                                onAddNote(noteTitle.trim(), noteContent.trim(), noteCategory)
                                showAddDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary)
                ) {
                    Text("Kaydet", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Vazgeç", color = OnGardenSurfaceVariant)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            BahcemHeader(
                subtitle = "Ajanda & Takvim",
                locationName = locDisplay,
                onLocationClick = { showLocationDialog = true }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButton = {
            Button(
                onClick = {
                    showAddDialog = true
                    onNewNoteClicked()
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                shape = CircleShape,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .height(48.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Yeni Görev / Not Ekle",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            // 1. MONTH HEADER & DAY HORIZON STRIP
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentMonthYearText,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestPrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    currentWeekReference = currentWeekReference.minusWeeks(1)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(GardenSurfaceContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Önceki Hafta",
                                    tint = ForestPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            TextButton(
                                onClick = {
                                    val today = LocalDate.now()
                                    currentWeekReference = today
                                    selectedDate = today
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Bugün",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestPrimary
                                )
                            }

                            IconButton(
                                onClick = {
                                    currentWeekReference = currentWeekReference.plusWeeks(1)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(GardenSurfaceContainer)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Sonraki Hafta",
                                    tint = ForestPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Days carousel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        weekDays.forEach { calDay ->
                            val isSelected = selectedDate == calDay.date
                            val isToday = calDay.isToday
                            val bgColor = if (isSelected) ForestPrimary else if (isToday) GardenSecondaryContainer else GardenSurfaceContainer
                            val textColor = if (isSelected) Color.White else OnGardenSurfaceVariant

                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(bgColor)
                                    .clickable { selectedDate = calDay.date }
                                    .padding(horizontal = 13.dp, vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = calDay.dayNameShort,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = textColor.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = "${calDay.dayOfMonth}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) GardenSecondaryContainer else if (isToday) ForestPrimary else Color.Transparent)
                                )
                            }
                        }
                    }
                }
            }

            // 2. CATEGORY FILTER CHIPS
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    filterChips.forEach { (catId, label) ->
                        val isSelected = selectedCategory == catId
                        val chipBg = if (isSelected) ForestPrimary else GardenSurfaceContainer
                        val chipText = if (isSelected) Color.White else OnGardenSurfaceVariant

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(chipBg)
                                .clickable { selectedCategory = catId }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = chipText
                            )
                        }
                    }
                }
            }

            // 3. SCHEDULED TASKS (Dinamik Görev Listesi)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    val filteredTasks = uiState.tasks.filter { task ->
                        val matchesDate = GardenCalendarHelper.isTaskForDate(
                            dueTime = task.dueTime,
                            createdAtMillis = task.createdAtMillis,
                            targetDate = selectedDate
                        )
                        val matchesCategory = selectedCategory == "all" || task.category.equals(selectedCategory, ignoreCase = true)
                        matchesDate && matchesCategory
                    }
                    val totalTasks = filteredTasks.size
                    val completedTasks = filteredTasks.count { it.isCompleted }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val taskHeading = if (selectedDate == LocalDate.now()) {
                                "Bugünün Bahçe Görevleri"
                            } else {
                                "${selectedDate.dayOfMonth} ${GardenCalendarHelper.getMonthName(selectedDate.monthValue)} Görevleri"
                            }
                            Text(
                                text = taskHeading,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnGardenSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = GardenSurfaceContainerHigh,
                                border = BorderStroke(1.dp, ForestPrimary.copy(alpha = 0.15f))
                            ) {
                                Text(
                                    text = if (totalTasks > 0) "$completedTasks/$totalTasks Tamamlandı" else "0 Görev",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForestPrimary,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        TextButton(
                            onClick = {
                                addDialogTab = 0
                                showAddDialog = true
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ForestPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Görev Ekle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestPrimary)
                        }
                    }

                    if (filteredTasks.isEmpty()) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLowest),
                            border = BorderStroke(1.dp, GardenSurfaceContainerHigh),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = GardenSecondary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Planlanmış Görev Yok",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnGardenSurface
                                )
                                Text(
                                    text = "Seçili gün için henüz kayıtlı bir görev bulunmuyor. Yeni bir görev ekleyerek bahçe takviminizi oluşturun.",
                                    fontSize = 12.sp,
                                    color = OnGardenSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Button(
                                    onClick = {
                                        addDialogTab = 0
                                        showAddDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Yeni Görev Ekle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        filteredTasks.forEach { task ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLowest),
                                border = BorderStroke(1.dp, GardenSurfaceContainerHigh),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Checkbox
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (task.isCompleted) ForestPrimary else GardenSurfaceContainer)
                                            .clickable { onToggleTask(task.id, !task.isCompleted) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (task.isCompleted) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = task.dueTime,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ForestPrimary
                                            )
                                            Text(
                                                text = task.category,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ForestPrimary,
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(GardenSecondaryFixed)
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }

                                        Text(
                                            text = task.title,
                                            fontSize = 14.sp,
                                            fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Bold,
                                            color = if (task.isCompleted) OnGardenSurfaceVariant else OnGardenSurface,
                                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                    }

                                    IconButton(
                                        onClick = { onDeleteTask(task.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Görevi Sil",
                                            tint = AlertError.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. GARDEN NOTEBOOK / OBSERVATIONS
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Bahçe Not Defteri",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnGardenSurface
                            )
                            Text(
                                text = "Kişisel gözlem ve saha kayıtları",
                                fontSize = 11.sp,
                                color = OnGardenSurfaceVariant
                            )
                        }

                        TextButton(
                            onClick = {
                                addDialogTab = 1
                                showAddDialog = true
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ForestPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Not Ekle", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForestPrimary)
                        }
                    }

                    // Note Cards
                    if (uiState.notes.isNotEmpty()) {
                        uiState.notes.forEach { note ->
                            val dateStr = GardenCalendarHelper.formatDateTime(note.timestampMillis)
                            val tempC = note.attachedTempC
                            val weatherStr = if (tempC != null) {
                                "${tempC.toInt()}°C • ${note.attachedCondition?.name ?: "Açık"}"
                            } else {
                                "Kaydedildi"
                            }
                            GardenNoteCard(
                                tag = note.category.toDisplayLabel(),
                                date = dateStr,
                                title = note.title,
                                content = note.content,
                                location = "Bahçem",
                                weatherCondition = weatherStr,
                                onDelete = { onDeleteNote(note.id) }
                            )
                        }
                    } else {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLowest),
                            border = BorderStroke(1.dp, GardenSurfaceContainerHigh),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = GardenSecondary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Text(
                                    text = "Kayıtlı Saha Notu Yok",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnGardenSurface
                                )
                                Text(
                                    text = "Bahçenizle ilgili sulama, budama veya gözlemlerinizi kaydetmek için yeni not ekleyin.",
                                    fontSize = 12.sp,
                                    color = OnGardenSurfaceVariant,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Button(
                                    onClick = {
                                        addDialogTab = 1
                                        showAddDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Saha Notu Ekle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
fun AgendaTaskCard(
    time: String,
    duration: String?,
    badgeText: String,
    badgeIcon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    badgeTextColor: Color,
    title: String,
    subtitle: String,
    isCritical: Boolean,
    extraFooter: String? = null
) {
    var isDone by remember { mutableStateOf(false) }

    val cardBg = if (isCritical) AlertErrorContainer.copy(alpha = 0.25f) else GardenSurfaceContainerLowest

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = BorderStroke(1.dp, if (isCritical) AlertError.copy(alpha = 0.2f) else GardenSurfaceContainerHigh),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isDone) ForestPrimary else GardenSurfaceContainer)
                    .clickable { isDone = !isDone },
                contentAlignment = Alignment.Center
            ) {
                if (isDone) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = time,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCritical) AlertError else ForestPrimary
                        )
                        duration?.let {
                            Text(text = "• $it", fontSize = 11.sp, color = OnGardenSurfaceVariant)
                        }
                    }

                    // Badge
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(badgeColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(imageVector = badgeIcon, contentDescription = null, tint = badgeTextColor, modifier = Modifier.size(12.dp))
                        Text(text = badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = badgeTextColor)
                    }
                }

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnGardenSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = OnGardenSurfaceVariant,
                    lineHeight = 16.sp
                )

                extraFooter?.let {
                    HorizontalDivider(color = AlertError.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AcUnit, contentDescription = null, tint = AlertError, modifier = Modifier.size(14.dp))
                        Text(text = it, fontSize = 11.sp, color = OnGardenSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun GardenNoteCard(
    tag: String,
    date: String,
    title: String = "",
    content: String,
    location: String,
    weatherCondition: String,
    onDelete: (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GardenSurfaceContainerLowest),
        border = BorderStroke(1.dp, GardenSurfaceContainerHigh),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestPrimary,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(GardenSecondaryFixed)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                    Text(text = date, fontSize = 11.sp, color = OnGardenSurfaceVariant)
                }

                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Notu Sil",
                            tint = AlertError,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (title.isNotBlank()) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnGardenSurface
                )
            }

            Text(
                text = content,
                fontSize = 13.sp,
                color = OnGardenSurface,
                lineHeight = 18.sp
            )

            HorizontalDivider(color = GardenSurfaceContainer)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Yard, contentDescription = null, tint = GardenSecondary, modifier = Modifier.size(14.dp))
                    Text(text = location, fontSize = 11.sp, color = OnGardenSurfaceVariant)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.WbSunny, contentDescription = null, tint = GardenSecondary, modifier = Modifier.size(14.dp))
                    Text(text = weatherCondition, fontSize = 11.sp, color = OnGardenSurfaceVariant)
                }
            }
        }
    }
}
