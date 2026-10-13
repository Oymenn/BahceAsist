package com.ytkf2.bahceasist.domain.usecase.location

import com.ytkf2.bahceasist.core.common.result.AppResult
import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.repository.LocationRepository
import javax.inject.Inject

class SearchLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(query: String): AppResult<List<Location>> =
        repository.searchLocations(query)
}