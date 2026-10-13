package com.ytkf2.bahceasist.data.repository

import com.ytkf2.bahceasist.core.common.result.AppError
import com.ytkf2.bahceasist.core.common.result.AppResult
import com.ytkf2.bahceasist.data.local.dao.WeatherCacheDao
import com.ytkf2.bahceasist.data.local.entity.WeatherCacheEntity
import com.ytkf2.bahceasist.data.mapper.toDomain
import com.ytkf2.bahceasist.data.remote.api.OpenMeteoForecastApi
import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.model.WeatherSnapshot
import com.ytkf2.bahceasist.domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val forecastApi: OpenMeteoForecastApi,
    private val weatherCacheDao: WeatherCacheDao
) : WeatherRepository {

    private val _cacheUpdateTrigger = MutableStateFlow(System.currentTimeMillis())

    override fun observeWeather(location: Location): Flow<WeatherSnapshot?> {
        return _cacheUpdateTrigger.map {
            withContext(Dispatchers.IO) {
                val cached = weatherCacheDao.get(location.key) ?: return@withContext null
                try {
                    val dto = forecastApi.parseForecastJson(cached.payloadJson)
                    dto.toDomain(location, cached.fetchedAt)
                } catch (_: Exception) {
                    null
                }
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun refresh(location: Location): AppResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val jsonString = forecastApi.fetchForecastJson(location.latitude, location.longitude)
            val now = System.currentTimeMillis()
            weatherCacheDao.save(
                WeatherCacheEntity(
                    locationKey = location.key,
                    payloadJson = jsonString,
                    fetchedAt = now
                )
            )
            _cacheUpdateTrigger.value = now
            AppResult.Success(Unit)
        } catch (e: IOException) {
            AppResult.Failure(AppError.NoInternet)
        } catch (e: Exception) {
            AppResult.Failure(AppError.Unknown(e))
        }
    }
}
