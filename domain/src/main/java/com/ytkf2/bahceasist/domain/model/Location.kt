package com.ytkf2.bahceasist.domain.model

data class Location(
    val name: String,        // Şehir/İlçe adı (örn: "Ankara", "Polatlı")
    val region: String?,     // Bölge/İl (varsa)
    val country: String?,    // Ülke ("Türkiye")
    val latitude: Double,    // Enlem
    val longitude: Double    // Boylam
) {
    /**
     * Önbellek (Cache) anahtarı:
     * Çok yakın koordinatlar aynı veriyi kullansın diye 2 ondalığa yuvarlıyoruz (örn: "39.93,32.86")
     */
    val key: String get() = "%.2f,%.2f".format(java.util.Locale.US, latitude, longitude)
}