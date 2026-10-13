package com.ytkf2.bahceasist.domain.usecase.weather

import com.ytkf2.bahceasist.core.common.result.AppResult
import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.repository.WeatherRepository
import javax.inject.Inject

// İnternetten yeni havayı çeker
class RefreshWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(location: Location): AppResult<Unit> =
        repository.refresh(location)
}