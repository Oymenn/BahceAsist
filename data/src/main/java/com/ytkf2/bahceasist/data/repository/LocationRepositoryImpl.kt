package com.ytkf2.bahceasist.data.repository

import com.ytkf2.bahceasist.core.common.TurkeyLocationRegistry
import com.ytkf2.bahceasist.core.common.result.AppResult
import com.ytkf2.bahceasist.data.local.pref.AppPreferences
import com.ytkf2.bahceasist.data.remote.api.OpenMeteoGeocodingApi
import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val appPreferences: AppPreferences,
    private val geocodingApi: OpenMeteoGeocodingApi
) : LocationRepository {

    override val selectedLocation: Flow<Location?>
        get() = appPreferences.selectedLocation

    override suspend fun selectLocation(location: Location) {
        appPreferences.saveSelectedLocation(location)
    }

    override suspend fun searchLocations(query: String): AppResult<List<Location>> {
        if (query.isBlank()) {
            return AppResult.Success(emptyList())
        }

        // 1. Önce Türkiye 81 il ve ilçe veritabanından anında ara (100% offline ve anlık)
        val localMatches = TurkeyLocationRegistry.search(query).map { item ->
            Location(
                name = item.district,
                region = item.province,
                country = "Türkiye",
                latitude = item.latitude,
                longitude = item.longitude
            )
        }

        if (localMatches.isNotEmpty()) {
            return AppResult.Success(localMatches)
        }

        // 2. Yerelde bulunamazsa Open-Meteo Türkiye sınırları içinde ara
        return try {
            val response = geocodingApi.search(query.trim())
            val locations = response.results.map { dto ->
                Location(
                    name = dto.name,
                    region = dto.admin1,
                    country = "Türkiye",
                    latitude = dto.latitude,
                    longitude = dto.longitude
                )
            }
            AppResult.Success(locations)
        } catch (e: Exception) {
            AppResult.Success(emptyList())
        }
    }
}
