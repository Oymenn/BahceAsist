package com.ytkf2.bahceasist.core.common

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class GardenCalendarHelperTest {

    @Test
    fun verifySeasons() {
        // Ekim (October) should be Sonbahar (Autumn)
        val octoberDate = LocalDate.of(2026, 10, 13)
        assertEquals(Season.AUTUMN, GardenCalendarHelper.getSeason(octoberDate))
        assertEquals("Sonbahar", GardenCalendarHelper.getSeason(octoberDate).displayName)

        // Mart, Nisan, Mayıs -> İlkbahar
        assertEquals(Season.SPRING, GardenCalendarHelper.getSeason(LocalDate.of(2026, 3, 15)))
        assertEquals(Season.SPRING, GardenCalendarHelper.getSeason(LocalDate.of(2026, 5, 20)))

        // Temmuz -> Yaz
        assertEquals(Season.SUMMER, GardenCalendarHelper.getSeason(LocalDate.of(2026, 7, 10)))

        // Ocak -> Kış
        assertEquals(Season.WINTER, GardenCalendarHelper.getSeason(LocalDate.of(2026, 1, 15)))
    }

    @Test
    fun verifyDateFormatting() {
        val date = LocalDate.of(2026, 10, 13)
        val monthYear = GardenCalendarHelper.formatMonthYear(date)
        assertEquals("Ekim 2026", monthYear)

        val fullDate = GardenCalendarHelper.formatFullDate(date)
        assertEquals("13 Ekim 2026, Salı", fullDate)
    }

    @Test
    fun verifyWeekDaysGeneration() {
        // 13 Ekim 2026 is Tuesday
        val tuesday = LocalDate.of(2026, 10, 13)
        val week = GardenCalendarHelper.getWeekDays(tuesday)

        assertEquals(7, week.size)
        // Week starts with Monday (12 Oct)
        assertEquals("Pzt", week[0].dayNameShort)
        assertEquals(12, week[0].dayOfMonth)

        // Tuesday is 13 Oct
        assertEquals("Sal", week[1].dayNameShort)
        assertEquals(13, week[1].dayOfMonth)

        // Sunday is 18 Oct
        assertEquals("Paz", week[6].dayNameShort)
        assertEquals(18, week[6].dayOfMonth)
    }

    @Test
    fun verifyGreetings() {
        assertEquals("Hayırlı Sabahlar, Bahçıvan 🌱", GardenCalendarHelper.getGreeting(LocalTime.of(8, 30)))
        assertEquals("İyi Günler, Bahçıvan 🌱", GardenCalendarHelper.getGreeting(LocalTime.of(14, 0)))
        assertEquals("İyi Akşamlar, Bahçıvan 🌱", GardenCalendarHelper.getGreeting(LocalTime.of(19, 45)))
        assertEquals("Hayırlı Geceler, Bahçıvan 🌱", GardenCalendarHelper.getGreeting(LocalTime.of(23, 10)))
    }

    @Test
    fun verifyUpcomingDayNamesStartingFromTuesday() {
        val tuesday = LocalDate.of(2026, 10, 13) // Salı
        val names = GardenCalendarHelper.getUpcomingDayNames(5, tuesday)

        assertEquals(listOf("Bugün", "Çar", "Per", "Cum", "Cmt"), names)
        // "Sal" must never be the second element right after "Bugün"
        org.junit.Assert.assertNotEquals("Sal", names[1])
    }

    @Test
    fun verifyTaskDateFiltering() {
        val zone = java.time.ZoneId.systemDefault()
        val today = LocalDate.of(2026, 10, 13) // Salı
        val tomorrow = today.plusDays(1) // Çarşamba 14 Ekim
        val todayCreatedMillis = today.atTime(10, 0).atZone(zone).toInstant().toEpochMilli()

        // 1. Task created today for tomorrow ("Yarın")
        val tomorrowTaskDue = "Yarın • Sabah (09:00)"
        val isForToday = GardenCalendarHelper.isTaskForDate(tomorrowTaskDue, todayCreatedMillis, today, zone)
        val isForTomorrow = GardenCalendarHelper.isTaskForDate(tomorrowTaskDue, todayCreatedMillis, tomorrow, zone)

        org.junit.Assert.assertFalse("Yarın görevi bugün için gösterilmemeli", isForToday)
        org.junit.Assert.assertTrue("Yarın görevi yarın için gösterilmeli", isForTomorrow)

        // 2. Task created today for today ("Bugün")
        val todayTaskDue = "Bugün • Sabah (09:00)"
        org.junit.Assert.assertTrue(GardenCalendarHelper.isTaskForDate(todayTaskDue, todayCreatedMillis, today, zone))
        org.junit.Assert.assertFalse(GardenCalendarHelper.isTaskForDate(todayTaskDue, todayCreatedMillis, tomorrow, zone))

        // 3. Task with explicit date: "15 Ekim"
        val specificDateTaskDue = "15 Ekim • Sabah (09:00)"
        org.junit.Assert.assertFalse(GardenCalendarHelper.isTaskForDate(specificDateTaskDue, todayCreatedMillis, today, zone))
        org.junit.Assert.assertTrue(GardenCalendarHelper.isTaskForDate(specificDateTaskDue, todayCreatedMillis, LocalDate.of(2026, 10, 15), zone))
    }
}
