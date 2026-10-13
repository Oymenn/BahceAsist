package com.ytkf2.bahceasist.domain.model

// Notun kategorisi
enum class NoteCategory {
    IRRIGATION,   // 💧 Sulama
    SPRAYING,     // 🧴 İlaçlama
    PLANTING,     // 🌱 Ekim / Dikim
    HARVEST,      // 🌾 Hasat
    BEEKEEPING,   // 🐝 Arıcılık
    OTHER         // 📝 Diğer
}

// Tarla Notu
data class FieldNote(
    val id: Long = 0,                         // Veritabanı kimliği (0 = yeni not)
    val title: String,                        // Not başlığı
    val content: String,                      // Not detayı
    val category: NoteCategory,               // Kategori
    val timestampMillis: Long,                // Notun yazıldığı zaman
    val imagePath: String? = null,            // Varsa çekilen fotoğrafın dosya yolu
    val attachedTempC: Double? = null,        // Not anındaki sıcaklık (°C)
    val attachedCondition: WeatherCondition? = null // Not anındaki hava durumu
)