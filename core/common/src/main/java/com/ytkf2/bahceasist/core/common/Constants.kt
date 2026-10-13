package com.ytkf2.bahceasist.core.common

object Constants {
    // Open-Meteo Hava Durumu API adresi
    const val FORECAST_BASE_URL = "https://api.open-meteo.com/"

    // Open-Meteo Şehir Arama (Geocoding) API adresi
    const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"

    // Verinin "eski/bayat" sayılma süresi (3 saat)
    const val WEATHER_STALE_AFTER_MS = 3 * 60 * 60 * 1000L
}