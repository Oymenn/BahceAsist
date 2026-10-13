package com.ytkf2.bahceasist.domain.usecase.location

import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.repository.LocationRepository
import javax.inject.Inject

class SelectLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(location: Location) =
        repository.selectLocation(location)
}