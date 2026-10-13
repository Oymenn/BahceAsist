package com.ytkf2.bahceasist.domain.usecase.location

import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSelectedLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(): Flow<Location?> = repository.selectedLocation
}