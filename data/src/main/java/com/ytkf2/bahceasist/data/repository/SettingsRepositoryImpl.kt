package com.ytkf2.bahceasist.data.repository

import com.ytkf2.bahceasist.data.local.pref.AppPreferences
import com.ytkf2.bahceasist.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val appPreferences: AppPreferences
) : SettingsRepository {

    override val alertsEnabled: Flow<Boolean>
        get() = appPreferences.alertsEnabled

    override suspend fun setAlertsEnabled(enabled: Boolean) {
        appPreferences.saveAlertsEnabled(enabled)
    }
}
