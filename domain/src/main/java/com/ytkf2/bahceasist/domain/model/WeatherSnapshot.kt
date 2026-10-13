package com.ytkf2.bahceasist.domain.model

// Anlık hava durumu
data class CurrentWeather(
    val temperatureC: Double,
    val windSpeedKmh: Double,
    val condition: WeatherCondition
)

// Saatlik tahmin
data class HourlyForecast(
    val timeMillis: Long,
    val temperatureC: Double,
    val precipitationMm: Double,
    val windSpeedKmh: Double
)

// Günlük tahmin
data class DailyForecast(
    val dateMillis: Long,
    val minTempC: Double,
    val maxTempC: Double,
    val precipitationSumMm: Double,
    val maxWindKmh: Double,
    val condition: WeatherCondition
)

// Tarlanın tüm hava durumu özeti
data class WeatherSnapshot(
    val location: Location,
    val fetchedAtMillis: Long,
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val precipitationLast24hMm: Double,
    val precipitationLast7dMm: Double
)