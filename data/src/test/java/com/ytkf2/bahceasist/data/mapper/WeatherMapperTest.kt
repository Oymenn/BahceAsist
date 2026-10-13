package com.ytkf2.bahceasist.data.mapper

import com.ytkf2.bahceasist.data.remote.dto.CurrentDto
import com.ytkf2.bahceasist.data.remote.dto.DailyDto
import com.ytkf2.bahceasist.data.remote.dto.HourlyDto
import com.ytkf2.bahceasist.data.remote.dto.WeatherResponseDto
import com.ytkf2.bahceasist.domain.model.Location
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class WeatherMapperTest {

    @Test
    fun `toDomain filters out past days and starts strictly with today`() {
        val zone = ZoneId.systemDefault()
        // Simulate reference date: 2026-10-13 (Salı) at noon
        val todayDate = LocalDate.of(2026, 10, 13)
        val todayNoonMillis = todayDate.atTime(12, 0).atZone(zone).toInstant().toEpochMilli()

        // Create daily timestamps: yesterday, today, tomorrow, day after tomorrow
        val yesterdaySec = todayDate.minusDays(1).atStartOfDay(zone).toEpochSecond()
        val todaySec = todayDate.atStartOfDay(zone).toEpochSecond()
        val tomorrowSec = todayDate.plusDays(1).atStartOfDay(zone).toEpochSecond()
        val dayAfterTomorrowSec = todayDate.plusDays(2).atStartOfDay(zone).toEpochSecond()

        val dto = WeatherResponseDto(
            latitude = 40.2,
            longitude = 28.9,
            current = CurrentDto(
                time = todayNoonMillis / 1000L,
                temperature = 20.0,
                windSpeed = 10.0,
                weatherCode = 1
            ),
            hourly = HourlyDto(
                time = listOf(todayNoonMillis / 1000L),
                temperature = listOf(20.0),
                precipitation = listOf(0.0),
                windSpeed = listOf(10.0)
            ),
            daily = DailyDto(
                time = listOf(yesterdaySec, todaySec, tomorrowSec, dayAfterTomorrowSec),
                weatherCode = listOf(0, 0, 1, 2),
                maxTemp = listOf(19.0, 20.0, 21.0, 22.0),
                minTemp = listOf(10.0, 11.0, 12.0, 13.0),
                precipitationSum = listOf(0.0, 0.0, 0.0, 0.0),
                maxWind = listOf(15.0, 16.0, 17.0, 18.0)
            )
        )

        val location = Location("Bursa", "Nilüfer", "Türkiye", 40.2, 28.9)
        val domain = dto.toDomain(location, todayNoonMillis)

        // Must filter out yesterday (yesterdaySec)
        assertEquals(3, domain.daily.size)

        // First item must be today (2026-10-13)
        val firstDailyDate = Instant.ofEpochMilli(domain.daily[0].dateMillis).atZone(zone).toLocalDate()
        assertEquals(todayDate, firstDailyDate)
        assertEquals(20.0, domain.daily[0].maxTempC, 0.01)

        // Second item must be tomorrow (2026-10-14)
        val secondDailyDate = Instant.ofEpochMilli(domain.daily[1].dateMillis).atZone(zone).toLocalDate()
        assertEquals(todayDate.plusDays(1), secondDailyDate)
        assertEquals(21.0, domain.daily[1].maxTempC, 0.01)

        // Yesterday's maxTemp (19.0) must not be present
        assertTrue(domain.daily.none { it.maxTempC == 19.0 })
    }
}
