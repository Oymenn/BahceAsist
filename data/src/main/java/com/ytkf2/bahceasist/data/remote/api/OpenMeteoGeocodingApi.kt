package com.ytkf2.bahceasist.data.remote.api

import com.ytkf2.bahceasist.core.common.Constants
import com.ytkf2.bahceasist.data.remote.dto.GeocodingResponseDto
import com.ytkf2.bahceasist.data.remote.dto.GeocodingResultDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenMeteoGeocodingApi @Inject constructor() {

    suspend fun search(query: String): GeocodingResponseDto = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val urlString = "${Constants.GEOCODING_BASE_URL}v1/search?name=$encodedQuery&count=10&country_code=TR&language=tr&format=json"

        val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Accept", "application/json")
        }

        try {
            if (connection.responseCode !in 200..299) {
                return@withContext GeocodingResponseDto(emptyList())
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val responseString = reader.use { it.readText() }
            val root = JSONObject(responseString)

            if (!root.has("results")) {
                return@withContext GeocodingResponseDto(emptyList())
            }

            val resultsArray = root.getJSONArray("results")
            val list = mutableListOf<GeocodingResultDto>()
            for (i in 0 until resultsArray.length()) {
                val item = resultsArray.getJSONObject(i)
                list.add(
                    GeocodingResultDto(
                        name = item.optString("name", ""),
                        latitude = item.optDouble("latitude", 0.0),
                        longitude = item.optDouble("longitude", 0.0),
                        country = item.optString("country").takeIf { it.isNotEmpty() },
                        admin1 = item.optString("admin1").takeIf { it.isNotEmpty() }
                    )
                )
            }
            GeocodingResponseDto(list)
        } finally {
            connection.disconnect()
        }
    }
}
