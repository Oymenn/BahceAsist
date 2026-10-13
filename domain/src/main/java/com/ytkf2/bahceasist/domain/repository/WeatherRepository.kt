package com.ytkf2.bahceasist.domain.repository


import com.ytkf2.bahceasist.core.common.result.AppResult
import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.model.WeatherSnapshot
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    // 1. Veritabanındaki hava durumu verisini canlı yayınlar (Flow = veri musluğu)
    fun observeWeather(location: Location): Flow<WeatherSnapshot?>

    // 2. İnternetten (Open-Meteo'dan) güncel hava durumunu çeker ve veritabanına kaydeder
    suspend fun refresh(location: Location): AppResult<Unit>
}