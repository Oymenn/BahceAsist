package com.ytkf2.bahceasist.domain.usecase.weather

import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.model.WeatherSnapshot
import com.ytkf2.bahceasist.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Canlı hava durumu akışını dinler
class ObserveWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) {
    operator fun invoke(location: Location): Flow<WeatherSnapshot?> =
        repository.observeWeather(location)
}