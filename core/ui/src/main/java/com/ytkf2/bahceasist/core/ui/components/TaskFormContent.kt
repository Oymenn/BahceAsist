package com.ytkf2.bahceasist.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ytkf2.bahceasist.core.common.GardenCalendarHelper
import com.ytkf2.bahceasist.core.ui.theme.*
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun formatTaskDueTime(
    taskDate: LocalDate,
    timeSlot: String,
    customTime: String
): String {
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)

    val datePrefix = when (taskDate) {
        today -> "Bugün"
        tomorrow -> "Yarın"
        else -> "${taskDate.dayOfMonth} ${GardenCalendarHelper.getMonthName(taskDate.monthValue)}"
    }

    val timeSuffix = when {
        timeSlot == "Özel Saat" && customTime.isNotBlank() -> customTime.trim()
        timeSlot != "Tüm Gün" -> timeSlot
        else -> ""
    }

    return if (timeSuffix.isNotBlank()) "$datePrefix • $timeSuffix" else datePrefix
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormContent(
    taskTitle: String,
    onTaskTitleChange: (String) -> Unit,
    taskDate: LocalDate,
    onTaskDateChange: (LocalDate) -> Unit,
    selectedTimeSlot: String,
    onTimeSlotChange: (String) -> Unit,
    customTimeText: String,
    onCustomTimeChange: (String) -> Unit,
    taskCategory: String,
    onCategoryChange: (String) -> Unit,
    categoryOptions: List<String> = listOf("Sulama", "Gübreleme", "Sera", "Budama", "Hasat", "Genel"),
    modifier: Modifier = Modifier
) {
    var showDatePickerModal by remember { mutableStateOf(false) }

    if (showDatePickerModal) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = taskDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        )

        DatePickerDialog(
            onDismissRequest = { showDatePickerModal = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val picked = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            onTaskDateChange(picked)
                        }
                        showDatePickerModal = false
                    }
                ) {
                    Text("Tamam", color = ForestPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerModal = false }) {
                    Text("Vazgeç", color = OnGardenSurfaceVariant)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        // 1. Görev Adı
        OutlinedTextField(
            value = taskTitle,
            onValueChange = onTaskTitleChange,
            label = { Text("Görev Adı (örn: Fide sulaması, Budama)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 2. Tarih Seçimi
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Tarih Seçimi",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnGardenSurfaceVariant
            )

            // Hızlı Tarih Butonları
            val today = remember { LocalDate.now() }
            val tomorrow = remember { today.plusDays(1) }
            val inTwoDays = remember { today.plusDays(2) }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                // Bugün
                FilterChip(
                    selected = taskDate == today,
                    onClick = { onTaskDateChange(today) },
                    label = { Text("Bugün", fontSize = 11.sp) },
                    leadingIcon = if (taskDate == today) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                )

                // Yarın
                FilterChip(
                    selected = taskDate == tomorrow,
                    onClick = { onTaskDateChange(tomorrow) },
                    label = { Text("Yarın", fontSize = 11.sp) },
                    leadingIcon = if (taskDate == tomorrow) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    } else null
                )

                // 2 Gün Sonra
                FilterChip(
                    selected = taskDate == inTwoDays,
                    onClick = { onTaskDateChange(inTwoDays) },
                    label = {
                        Text(
                            "${inTwoDays.dayOfMonth} ${GardenCalendarHelper.getMonthName(inTwoDays.monthValue)}",
                            fontSize = 11.sp
                        )
                    }
                )

                // Hafta Sonu
                val daysUntilSat = (DayOfWeek.SATURDAY.value - today.dayOfWeek.value + 7) % 7
                val saturday = today.plusDays(if (daysUntilSat == 0) 7 else daysUntilSat.toLong())
                FilterChip(
                    selected = taskDate == saturday,
                    onClick = { onTaskDateChange(saturday) },
                    label = { Text("Hafta Sonu", fontSize = 11.sp) }
                )

                // Takvimden Seç
                FilterChip(
                    selected = taskDate != today && taskDate != tomorrow && taskDate != inTwoDays && taskDate != saturday,
                    onClick = { showDatePickerModal = true },
                    label = { Text("📅 Takvimden Seç", fontSize = 11.sp) }
                )
            }

            // Seçili Tarih Kartı
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = GardenSurfaceContainerLow,
                border = BorderStroke(1.dp, ForestPrimary.copy(alpha = 0.25f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerModal = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = ForestPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "${taskDate.dayOfMonth} ${GardenCalendarHelper.getMonthName(taskDate.monthValue)} ${taskDate.year}, ${GardenCalendarHelper.getDayNameFull(taskDate.dayOfWeek)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestPrimary
                        )
                    }
                    Text(
                        text = "Değiştir",
                        fontSize = 11.sp,
                        color = GardenSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 3. Saat / Vakit Seçimi
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Zaman / Vakit",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnGardenSurfaceVariant
            )

            val timeOptions = listOf("Sabah (09:00)", "Öğle (13:00)", "Akşam (18:00)", "Tüm Gün", "Özel Saat")
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                timeOptions.forEach { opt ->
                    FilterChip(
                        selected = selectedTimeSlot == opt,
                        onClick = {
                            onTimeSlotChange(opt)
                            if (opt != "Özel Saat") onCustomTimeChange("")
                        },
                        label = { Text(opt, fontSize = 11.sp) }
                    )
                }
            }

            if (selectedTimeSlot == "Özel Saat") {
                OutlinedTextField(
                    value = customTimeText,
                    onValueChange = onCustomTimeChange,
                    label = { Text("Saat veya Vakit girin (örn: 16:30, İkindi)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 4. Kategori
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Kategori",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnGardenSurfaceVariant
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                categoryOptions.forEach { cat ->
                    FilterChip(
                        selected = taskCategory == cat,
                        onClick = { onCategoryChange(cat) },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }
        }
    }
}
