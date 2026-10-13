package com.ytkf2.bahceasist.data.remote.dto

data class WeatherResponseDto(
    val latitude: Double,
    val longitude: Double,
    val current: CurrentDto,
    val hourly: HourlyDto,
    val daily: DailyDto
)

data class CurrentDto(
    val time: Long,
    val temperature: Double,
    val windSpeed: Double,
    val weatherCode: Int
)

data class HourlyDto(
    val time: List<Long>,
    val temperature: List<Double?>,
    val precipitation: List<Double?>,
    val windSpeed: List<Double?>
)

data class DailyDto(
    val time: List<Long>,
    val weatherCode: List<Int?>,
    val maxTemp: List<Double?>,
    val minTemp: List<Double?>,
    val precipitationSum: List<Double?>,
    val maxWind: List<Double?>
)
