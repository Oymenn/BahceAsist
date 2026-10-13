package com.ytkf2.bahceasist.data.local.pref

import android.content.Context
import android.content.SharedPreferences
import com.ytkf2.bahceasist.domain.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _selectedLocation = MutableStateFlow<Location?>(loadSelectedLocation())
    val selectedLocation: StateFlow<Location?> = _selectedLocation.asStateFlow()

    private val _alertsEnabled = MutableStateFlow(loadAlertsEnabled())
    val alertsEnabled: StateFlow<Boolean> = _alertsEnabled.asStateFlow()

    private fun loadSelectedLocation(): Location {
        val name = prefs.getString(KEY_LOC_NAME, null)
        val lat = prefs.getString(KEY_LOC_LAT, null)?.toDoubleOrNull()
        val lon = prefs.getString(KEY_LOC_LON, null)?.toDoubleOrNull()

        return if (name != null && lat != null && lon != null) {
            Location(
                name = name,
                region = prefs.getString(KEY_LOC_REGION, null),
                country = prefs.getString(KEY_LOC_COUNTRY, null),
                latitude = lat,
                longitude = lon
            )
        } else {
            // Varsayılan lokasyon: Bursa / Nilüfer
            DEFAULT_LOCATION
        }
    }

    fun saveSelectedLocation(location: Location) {
        prefs.edit()
            .putString(KEY_LOC_NAME, location.name)
            .putString(KEY_LOC_REGION, location.region)
            .putString(KEY_LOC_COUNTRY, location.country)
            .putString(KEY_LOC_LAT, location.latitude.toString())
            .putString(KEY_LOC_LON, location.longitude.toString())
            .apply()
        _selectedLocation.value = location
    }

    private fun loadAlertsEnabled(): Boolean {
        return prefs.getBoolean(KEY_ALERTS_ENABLED, true)
    }

    fun saveAlertsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ALERTS_ENABLED, enabled).apply()
        _alertsEnabled.value = enabled
    }

    companion object {
        private const val PREFS_NAME = "bahce_asist_prefs"
        private const val KEY_LOC_NAME = "loc_name"
        private const val KEY_LOC_REGION = "loc_region"
        private const val KEY_LOC_COUNTRY = "loc_country"
        private const val KEY_LOC_LAT = "loc_lat"
        private const val KEY_LOC_LON = "loc_lon"
        private const val KEY_ALERTS_ENABLED = "alerts_enabled"

        val DEFAULT_LOCATION = Location(
            name = "Nilüfer",
            region = "Bursa",
            country = "Türkiye",
            latitude = 40.21,
            longitude = 28.95
        )
    }
}
