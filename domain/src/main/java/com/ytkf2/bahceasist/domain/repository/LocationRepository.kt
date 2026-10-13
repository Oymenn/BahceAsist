package com.ytkf2.bahceasist.domain.repository

import com.ytkf2.bahceasist.core.common.result.AppResult
import com.ytkf2.bahceasist.domain.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val selectedLocation: Flow<Location?>
    suspend fun searchLocations(query: String): AppResult<List<Location>>
    suspend fun selectLocation(location: Location)
}