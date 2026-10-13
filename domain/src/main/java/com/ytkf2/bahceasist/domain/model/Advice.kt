package com.ytkf2.bahceasist.domain.model

// Uyarı türleri
enum class AdviceType {
    NO_SPRAYING_WIND,    // Rüzgar çok: İlaçlama yapma!
    FROST_WARNING,       // Sıcaklık çok düşük: Don riski!
    POSTPONE_IRRIGATION, // Çok yağmur yağdı: Sulamayı ertele
    STORM_WARNING,       // Fırtına riski!
    HEAT_WARNING,        // Aşırı sıcaklık riski
    DROUGHT_WARNING,     // Uzun süredir yağış yok: Kuraklık / Sulama hatırlatması
    ALL_CLEAR            // Her şey yolunda, tarla için uygun hava
}

// Önem seviyesi (Ekrandaki kartın rengini belirleyecek: Yeşil, Sarı, Kırmızı)
enum class Severity {
    INFO,
    WARNING,
    CRITICAL
}

// Üretilen tavsiye nesnesi
data class Advice(
    val type: AdviceType,
    val severity: Severity,
    val value: Double? = null // Ekranda göstermek için tetikleyen değer (örn: "19.5 km/s" veya "1.2 °C")
)

// Kural motorunun eşik sınırları
data class AdviceThresholds(
    val maxSprayWindKmh: Double = 18.0,
    val frostTempC: Double = 2.0,
    val skipIrrigationRainMm: Double = 8.0,
    val stormWindKmh: Double = 50.0,
    val heatTempC: Double = 35.0,
    val droughtRainMm: Double = 2.0
)