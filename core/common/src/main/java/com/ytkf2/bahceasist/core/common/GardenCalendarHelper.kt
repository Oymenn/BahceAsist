package com.ytkf2.bahceasist.core.common

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale

enum class Season(val displayName: String, val description: String) {
    SPRING("İlkbahar", "Uyanış ve dikim mevsimi"),
    SUMMER("Yaz", "Gelişim ve sulama mevsimi"),
    AUTUMN("Sonbahar", "Hasat ve kışa hazırlık mevsimi"),
    WINTER("Kış", "Dinlenme ve koruma mevsimi")
}

data class CalendarDay(
    val date: LocalDate,
    val dayNameShort: String,
    val dayNameFull: String,
    val dayOfMonth: Int,
    val isToday: Boolean
)

object GardenCalendarHelper {

    val TURKISH_MONTH_NAMES = listOf(
        "Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
        "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"
    )

    fun getMonthName(monthNumber: Int): String {
        return if (monthNumber in 1..12) TURKISH_MONTH_NAMES[monthNumber - 1] else ""
    }

    /**
     * Verilen tarihe göre Türkiye / Kuzey Yarımküre mevsimini belirler.
     * Mart, Nisan, Mayıs -> İlkbahar
     * Haziran, Temmuz, Ağustos -> Yaz
     * Eylül, Ekim, Kasım -> Sonbahar
     * Aralık, Ocak, Şubat -> Kış
     */
    fun getSeason(date: LocalDate = LocalDate.now()): Season {
        return when (date.monthValue) {
            3, 4, 5 -> Season.SPRING
            6, 7, 8 -> Season.SUMMER
            9, 10, 11 -> Season.AUTUMN
            12, 1, 2 -> Season.WINTER
            else -> Season.AUTUMN
        }
    }

    /**
     * Günün saatine göre Türkçe selamlama mesajı üretir.
     */
    fun getGreeting(time: LocalTime = LocalTime.now()): String {
        return when (time.hour) {
            in 5..11 -> "Hayırlı Sabahlar, Bahçıvan 🌱"
            in 12..17 -> "İyi Günler, Bahçıvan 🌱"
            in 18..21 -> "İyi Akşamlar, Bahçıvan 🌱"
            else -> "Hayırlı Geceler, Bahçıvan 🌱"
        }
    }

    /**
     * Tam Türkçe tarih formatı: "13 Ekim 2026, Salı"
     */
    fun formatFullDate(date: LocalDate = LocalDate.now()): String {
        val monthName = getMonthName(date.monthValue)
        val dayName = getDayNameFull(date.dayOfWeek)
        return "${date.dayOfMonth} $monthName ${date.year}, $dayName"
    }

    /**
     * Başlık için Ay ve Yıl: "Ekim 2026"
     */
    fun formatMonthYear(date: LocalDate = LocalDate.now()): String {
        val monthName = getMonthName(date.monthValue)
        return "$monthName ${date.year}"
    }

    fun getDayNameShort(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Pzt"
            DayOfWeek.TUESDAY -> "Sal"
            DayOfWeek.WEDNESDAY -> "Çar"
            DayOfWeek.THURSDAY -> "Per"
            DayOfWeek.FRIDAY -> "Cum"
            DayOfWeek.SATURDAY -> "Cmt"
            DayOfWeek.SUNDAY -> "Paz"
        }
    }

    fun getDayNameFull(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Pazartesi"
            DayOfWeek.TUESDAY -> "Salı"
            DayOfWeek.WEDNESDAY -> "Çarşamba"
            DayOfWeek.THURSDAY -> "Perşembe"
            DayOfWeek.FRIDAY -> "Cuma"
            DayOfWeek.SATURDAY -> "Cumartesi"
            DayOfWeek.SUNDAY -> "Pazar"
        }
    }

    /**
     * Referans tarihin bulunduğu haftanın (Pazartesi - Pazar) 7 gününü üretir.
     */
    fun getWeekDays(referenceDate: LocalDate = LocalDate.now()): List<CalendarDay> {
        val today = LocalDate.now()
        val monday = referenceDate.minusDays((referenceDate.dayOfWeek.value - 1).toLong())
        return (0..6).map { offset ->
            val date = monday.plusDays(offset.toLong())
            CalendarDay(
                date = date,
                dayNameShort = getDayNameShort(date.dayOfWeek),
                dayNameFull = getDayNameFull(date.dayOfWeek),
                dayOfMonth = date.dayOfMonth,
                isToday = date == today
            )
        }
    }

    /**
     * Gelecek N günün kısa isimlerini döndürür (Bugün, Çar, Per, ...)
     */
    fun getUpcomingDayNames(count: Int = 5, startDate: LocalDate = LocalDate.now()): List<String> {
        return (0 until count).map { offset ->
            if (offset == 0) "Bugün"
            else getDayNameShort(startDate.plusDays(offset.toLong()).dayOfWeek)
        }
    }

    /**
     * Epoch milisaniyeyi "13 Ekim 2026, 13:14" formatına çevirir
     */
    fun formatDateTime(timestampMillis: Long): String {
        val instant = java.time.Instant.ofEpochMilli(timestampMillis)
        val zone = java.time.ZoneId.systemDefault()
        val zdt = instant.atZone(zone)
        val monthName = getMonthName(zdt.monthValue)
        val timeStr = String.format(Locale.US, "%02d:%02d", zdt.hour, zdt.minute)
        return "${zdt.dayOfMonth} $monthName ${zdt.year}, $timeStr"
    }

    fun formatDateOnly(timestampMillis: Long): String {
        val instant = java.time.Instant.ofEpochMilli(timestampMillis)
        val zone = java.time.ZoneId.systemDefault()
        val zdt = instant.atZone(zone)
        val monthName = getMonthName(zdt.monthValue)
        return "${zdt.dayOfMonth} $monthName ${zdt.year}"
    }

    /**
     * Bir görevin belirtilen tarihe (varsayılan: Bugün) ait olup olmadığını belirler.
     * dueTime formatına göre:
     * - "Bugün" ile başlıyorsa ve oluşturulma tarihi hedef tarihle aynı gün ise -> true
     * - "Yarın" ile başlıyorsa ve oluşturulma tarihinin ertesi günü hedef tarih ise -> true
     * - "14 Ekim" gibi doğrudan gün-ay içeriyorsa -> hedef gün ve ayla eşleşiyorsa true
     * - Başka bir ay adı içeriyorsa -> eşleşmediği için false
     * - Sadece saat belirtilmişse -> oluşturulma tarihi hedef tarih ise true
     */
    fun isTaskForDate(
        dueTime: String,
        createdAtMillis: Long,
        targetDate: LocalDate = LocalDate.now(),
        zone: ZoneId = ZoneId.systemDefault()
    ): Boolean {
        val createdDate = Instant.ofEpochMilli(createdAtMillis).atZone(zone).toLocalDate()
        val trimmed = dueTime.trim()

        if (trimmed.startsWith("Bugün")) {
            return createdDate == targetDate
        }

        if (trimmed.startsWith("Yarın")) {
            return createdDate.plusDays(1) == targetDate
        }

        val targetPattern = "${targetDate.dayOfMonth} ${getMonthName(targetDate.monthValue)}"
        if (trimmed.contains(targetPattern)) {
            return true
        }

        val hasAnyMonth = TURKISH_MONTH_NAMES.any { month -> trimmed.contains(month) }
        if (hasAnyMonth) {
            return false
        }

        return createdDate == targetDate
    }
}
