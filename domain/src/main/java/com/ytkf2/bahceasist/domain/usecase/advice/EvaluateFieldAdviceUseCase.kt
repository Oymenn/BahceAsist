package com.ytkf2.bahceasist.domain.usecase.advice

import com.ytkf2.bahceasist.domain.model.Advice
import com.ytkf2.bahceasist.domain.model.AdviceThresholds
import com.ytkf2.bahceasist.domain.model.AdviceType
import com.ytkf2.bahceasist.domain.model.Severity
import com.ytkf2.bahceasist.domain.model.WeatherSnapshot
import javax.inject.Inject

class EvaluateFieldAdviceUseCase @Inject constructor() {

    operator fun invoke(
        weather: WeatherSnapshot,
        nowMillis: Long,
        thresholds: AdviceThresholds = AdviceThresholds()
    ): List<Advice> {
        val next24h = weather.hourly.filter { it.timeMillis in nowMillis..(nowMillis + 24 * HOUR) }
        val next6h = next24h.filter { it.timeMillis <= nowMillis + 6 * HOUR }
        val next48hRain = weather.hourly
            .filter { it.timeMillis in nowMillis..(nowMillis + 48 * HOUR) }
            .sumOf { it.precipitationMm }

        val windSoon = (next6h.map { it.windSpeedKmh } + weather.current.windSpeedKmh).maxOrNull() ?: weather.current.windSpeedKmh
        val windDay = (next24h.map { it.windSpeedKmh } + weather.current.windSpeedKmh).maxOrNull() ?: weather.current.windSpeedKmh
        val minTemp = (next24h.map { it.temperatureC } + weather.current.temperatureC).minOrNull() ?: weather.current.temperatureC
        val maxTemp = (next24h.map { it.temperatureC } + weather.current.temperatureC).maxOrNull() ?: weather.current.temperatureC

        val advices = buildList {
            // 1. Don uyarısı (≤ 2°C) - KRİTİK
            if (minTemp <= thresholds.frostTempC) {
                add(Advice(AdviceType.FROST_WARNING, Severity.CRITICAL, minTemp))
            }
            // 2. Fırtına riski (≥ 50 km/s) - KRİTİK
            if (windDay >= thresholds.stormWindKmh) {
                add(Advice(AdviceType.STORM_WARNING, Severity.CRITICAL, windDay))
            }
            // 3. İlaçlama yapma (> 18 km/s rüzgar) - UYARI
            if (windSoon > thresholds.maxSprayWindKmh) {
                add(Advice(AdviceType.NO_SPRAYING_WIND, Severity.WARNING, windSoon))
            }
            // 4. Aşırı sıcaklık (≥ 35°C) - UYARI
            if (maxTemp >= thresholds.heatTempC) {
                add(Advice(AdviceType.HEAT_WARNING, Severity.WARNING, maxTemp))
            }
            // 5. Sulamayı ertele (Son 24 saatte ≥ 8 mm yağış) - BİLGİ
            if (weather.precipitationLast24hMm >= thresholds.skipIrrigationRainMm) {
                add(Advice(AdviceType.POSTPONE_IRRIGATION, Severity.INFO, weather.precipitationLast24hMm))
            }
            // 6. Kuraklık uyarısı (Son 7 gün + gelecek 48 saatte < 2 mm yağış) - BİLGİ
            if (weather.precipitationLast7dMm + next48hRain < thresholds.droughtRainMm) {
                add(Advice(AdviceType.DROUGHT_WARNING, Severity.INFO))
            }
        }

        // Önem seviyesine göre sırala (CRITICAL en üstte görünsün), hiç uyarı yoksa ALL_CLEAR ver
        return advices.sortedByDescending { it.severity }
            .ifEmpty { listOf(Advice(AdviceType.ALL_CLEAR, Severity.INFO)) }
    }

    private companion object {
        const val HOUR = 3_600_000L
    }
}