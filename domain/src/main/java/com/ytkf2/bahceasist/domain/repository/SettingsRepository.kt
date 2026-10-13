package com.ytkf2.bahceasist.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val alertsEnabled: Flow<Boolean>
    suspend fun setAlertsEnabled(enabled: Boolean)
}