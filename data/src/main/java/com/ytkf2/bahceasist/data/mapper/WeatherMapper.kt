package com.ytkf2.bahceasist.data.mapper

import com.ytkf2.bahceasist.data.remote.dto.WeatherResponseDto
import com.ytkf2.bahceasist.domain.model.*

fun WeatherResponseDto.toDomain(location: Location, fetchedAtMillis: Long): WeatherSnapshot {
    val nowMs = current.time * 1000L
    val hourlyList = hourly.time.indices.mapNotNull { i ->
        val temp = hourly.temperature.getOrNull(i) ?: return@mapNotNull null
        HourlyForecast(
            timeMillis = hourly.time[i] * 1000L,
            temperatureC = temp,
            precipitationMm = hourly.precipitation.getOrNull(i) ?: 0.0,
            windSpeedKmh = hourly.windSpeed.getOrNull(i) ?: 0.0
        )
    }

    val dayMs = 24 * 3_600_000L

    val zone = java.time.ZoneId.systemDefault()
    val today = if (fetchedAtMillis > 0) {
        java.time.Instant.ofEpochMilli(fetchedAtMillis).atZone(zone).toLocalDate()
    } else {
        java.time.LocalDate.now(zone)
    }

    val allDaily = daily.time.indices.map { i ->
        DailyForecast(
            dateMillis = daily.time[i] * 1000L,
            minTempC = daily.minTemp.getOrNull(i) ?: 0.0,
            maxTempC = daily.maxTemp.getOrNull(i) ?: 0.0,
            precipitationSumMm = daily.precipitationSum.getOrNull(i) ?: 0.0,
            maxWindKmh = daily.maxWind.getOrNull(i) ?: 0.0,
            condition = wmoToCondition(daily.weatherCode.getOrNull(i) ?: -1)
        )
    }

    val upcomingDaily = allDaily.filter { item ->
        val itemDate = java.time.Instant.ofEpochMilli(item.dateMillis).atZone(zone).toLocalDate()
        !itemDate.isBefore(today)
    }

    return WeatherSnapshot(
        location = location,
        fetchedAtMillis = fetchedAtMillis,
        current = CurrentWeather(
            temperatureC = current.temperature,
            windSpeedKmh = current.windSpeed,
            condition = wmoToCondition(current.weatherCode)
        ),
        hourly = hourlyList,
        daily = if (upcomingDaily.isNotEmpty()) upcomingDaily else allDaily,
        precipitationLast24hMm = hourlyList
            .filter { it.timeMillis in (nowMs - dayMs)..nowMs }
            .sumOf { it.precipitationMm },
        precipitationLast7dMm = hourlyList
            .filter { it.timeMillis in (nowMs - 7 * dayMs)..nowMs }
            .sumOf { it.precipitationMm }
    )
}
