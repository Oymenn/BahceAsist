package com.ytkf2.bahceasist.domain.usecase.advice

import com.ytkf2.bahceasist.domain.model.AdviceType
import com.ytkf2.bahceasist.domain.model.CurrentWeather
import com.ytkf2.bahceasist.domain.model.HourlyForecast
import com.ytkf2.bahceasist.domain.model.Location
import com.ytkf2.bahceasist.domain.model.Severity
import com.ytkf2.bahceasist.domain.model.WeatherCondition
import com.ytkf2.bahceasist.domain.model.WeatherSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EvaluateFieldAdviceUseCaseTest {

    private val useCase = EvaluateFieldAdviceUseCase()
    private val now = 1_700_000_000_000L
    private val hour = 3_600_000L

    // Test için sahte bir hava durumu verisi üreten yardımcı fonksiyon
    private fun fakeSnapshot(
        wind: Double = 5.0,
        temp: Double = 15.0,
        rain24h: Double = 0.0,
        rain7d: Double = 20.0,
    ) = WeatherSnapshot(
        location = Location("Ankara", null, null, 39.9, 32.8),
        fetchedAtMillis = now,
        current = CurrentWeather(temp, wind, WeatherCondition.CLEAR),
        hourly = List(24) { HourlyForecast(now + it * hour, temp, 0.0, wind) },
        daily = emptyList(),
        precipitationLast24hMm = rain24h,
        precipitationLast7dMm = rain7d,
    )

    private fun List<com.ytkf2.bahceasist.domain.model.Advice>.types() = map { it.type }

    @Test
    fun `rüzgar tam 18 ise ilaçlama uyarısı yok`() {
        val result = useCase(fakeSnapshot(wind = 18.0), now)
        assertFalse(AdviceType.NO_SPRAYING_WIND in result.types())
    }

    @Test
    fun `rüzgar 18_1 ise ilaçlama uyarısı var`() {
        val result = useCase(fakeSnapshot(wind = 18.1), now)
        assertTrue(AdviceType.NO_SPRAYING_WIND in result.types())
    }

    @Test
    fun `sıcaklık tam 2 derece ise don uyarısı var`() {
        val result = useCase(fakeSnapshot(temp = 2.0), now)
        val frostAdvice = result.first { it.type == AdviceType.FROST_WARNING }
        assertEquals(Severity.CRITICAL, frostAdvice.severity)
    }

    @Test
    fun `sıcaklık 2_1 ise don uyarısı yok`() {
        val result = useCase(fakeSnapshot(temp = 2.1), now)
        assertFalse(AdviceType.FROST_WARNING in result.types())
    }

    @Test
    fun `24 saatte 8 mm yağış varsa sulama ertelenir`() {
        val result = useCase(fakeSnapshot(rain24h = 8.0), now)
        assertTrue(AdviceType.POSTPONE_IRRIGATION in result.types())
    }

    @Test
    fun `hiçbir kural tetiklenmezse ALL_CLEAR döner`() {
        val result = useCase(fakeSnapshot(), now)
        assertEquals(listOf(AdviceType.ALL_CLEAR), result.types())
    }
}