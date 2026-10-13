package com.ytkf2.bahceasist.data.remote.api

import com.ytkf2.bahceasist.core.common.Constants
import com.ytkf2.bahceasist.data.remote.dto.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenMeteoForecastApi @Inject constructor() {

    suspend fun fetchForecastJson(
        latitude: Double,
        longitude: Double
    ): String = withContext(Dispatchers.IO) {
        val urlString = "${Constants.FORECAST_BASE_URL}v1/forecast?" +
                "latitude=$latitude&longitude=$longitude" +
                "&current=temperature_2m,wind_speed_10m,weather_code" +
                "&hourly=temperature_2m,precipitation,wind_speed_10m" +
                "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max" +
                "&past_days=7&forecast_days=7&timezone=auto&timeformat=unixtime&wind_speed_unit=kmh"

        val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Accept", "application/json")
        }

        try {
            if (connection.responseCode !in 200..299) {
                throw Exception("HTTP error code: ${connection.responseCode}")
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            reader.use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    fun parseForecastJson(jsonString: String): WeatherResponseDto {
        return parseForecastJson(JSONObject(jsonString))
    }

    suspend fun getForecast(
        latitude: Double,
        longitude: Double
    ): WeatherResponseDto {
        val json = fetchForecastJson(latitude, longitude)
        return parseForecastJson(json)
    }

    private fun parseForecastJson(root: JSONObject): WeatherResponseDto {
        val lat = root.optDouble("latitude", 0.0)
        val lon = root.optDouble("longitude", 0.0)

        // Current
        val currentObj = root.getJSONObject("current")
        val current = CurrentDto(
            time = currentObj.optLong("time"),
            temperature = currentObj.optDouble("temperature_2m"),
            windSpeed = currentObj.optDouble("wind_speed_10m"),
            weatherCode = currentObj.optInt("weather_code")
        )

        // Hourly
        val hourlyObj = root.getJSONObject("hourly")
        val hourlyTimes = hourlyObj.getJSONArray("time").let { arr ->
            List(arr.length()) { i -> arr.getLong(i) }
        }
        val hourlyTemps = hourlyObj.getJSONArray("temperature_2m").let { arr ->
            List(arr.length()) { i -> if (arr.isNull(i)) null else arr.getDouble(i) }
        }
        val hourlyPrecip = hourlyObj.getJSONArray("precipitation").let { arr ->
            List(arr.length()) { i -> if (arr.isNull(i)) null else arr.getDouble(i) }
        }
        val hourlyWind = hourlyObj.getJSONArray("wind_speed_10m").let { arr ->
            List(arr.length()) { i -> if (arr.isNull(i)) null else arr.getDouble(i) }
        }
        val hourly = HourlyDto(
            time = hourlyTimes,
            temperature = hourlyTemps,
            precipitation = hourlyPrecip,
            windSpeed = hourlyWind
        )

        // Daily
        val dailyObj = root.getJSONObject("daily")
        val dailyTimes = dailyObj.getJSONArray("time").let { arr ->
            List(arr.length()) { i -> arr.getLong(i) }
        }
        val dailyCodes = dailyObj.getJSONArray("weather_code").let { arr ->
            List(arr.length()) { i -> if (arr.isNull(i)) null else arr.getInt(i) }
        }
        val dailyMaxTemps = dailyObj.getJSONArray("temperature_2m_max").let { arr ->
            List(arr.length()) { i -> if (arr.isNull(i)) null else arr.getDouble(i) }
        }
        val dailyMinTemps = dailyObj.getJSONArray("temperature_2m_min").let { arr ->
            List(arr.length()) { i -> if (arr.isNull(i)) null else arr.getDouble(i) }
        }
        val dailyPrecipSums = dailyObj.getJSONArray("precipitation_sum").let { arr ->
            List(arr.length()) { i -> if (arr.isNull(i)) null else arr.getDouble(i) }
        }
        val dailyMaxWinds = dailyObj.getJSONArray("wind_speed_10m_max").let { arr ->
            List(arr.length()) { i -> if (arr.isNull(i)) null else arr.getDouble(i) }
        }
        val daily = DailyDto(
            time = dailyTimes,
            weatherCode = dailyCodes,
            maxTemp = dailyMaxTemps,
            minTemp = dailyMinTemps,
            precipitationSum = dailyPrecipSums,
            maxWind = dailyMaxWinds
        )

        return WeatherResponseDto(lat, lon, current, hourly, daily)
    }
}
