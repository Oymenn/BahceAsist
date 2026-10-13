package com.ytkf2.bahceasist.data.di

import android.content.Context
import com.ytkf2.bahceasist.data.local.dao.FieldNoteDao
import com.ytkf2.bahceasist.data.local.dao.GardenTaskDao
import com.ytkf2.bahceasist.data.local.dao.WeatherCacheDao
import com.ytkf2.bahceasist.data.local.db.AgroDatabaseHelper
import com.ytkf2.bahceasist.data.local.pref.AppPreferences
import com.ytkf2.bahceasist.data.remote.api.OpenMeteoForecastApi
import com.ytkf2.bahceasist.data.remote.api.OpenMeteoGeocodingApi
import com.ytkf2.bahceasist.data.repository.FieldNoteRepositoryImpl
import com.ytkf2.bahceasist.data.repository.LocationRepositoryImpl
import com.ytkf2.bahceasist.data.repository.SettingsRepositoryImpl
import com.ytkf2.bahceasist.data.repository.TaskRepositoryImpl
import com.ytkf2.bahceasist.data.repository.WeatherRepositoryImpl
import com.ytkf2.bahceasist.domain.repository.FieldNoteRepository
import com.ytkf2.bahceasist.domain.repository.LocationRepository
import com.ytkf2.bahceasist.domain.repository.SettingsRepository
import com.ytkf2.bahceasist.domain.repository.TaskRepository
import com.ytkf2.bahceasist.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataProvidesModule {

    @Provides
    @Singleton
    fun provideAgroDatabaseHelper(@ApplicationContext context: Context): AgroDatabaseHelper {
        return AgroDatabaseHelper(context)
    }

    @Provides
    @Singleton
    fun provideAppPreferences(@ApplicationContext context: Context): AppPreferences {
        return AppPreferences(context)
    }

    @Provides
    @Singleton
    fun provideFieldNoteDao(dbHelper: AgroDatabaseHelper): FieldNoteDao {
        return FieldNoteDao(dbHelper)
    }

    @Provides
    @Singleton
    fun provideWeatherCacheDao(dbHelper: AgroDatabaseHelper): WeatherCacheDao {
        return WeatherCacheDao(dbHelper)
    }

    @Provides
    @Singleton
    fun provideGardenTaskDao(dbHelper: AgroDatabaseHelper): GardenTaskDao {
        return GardenTaskDao(dbHelper)
    }

    @Provides
    @Singleton
    fun provideOpenMeteoForecastApi(): OpenMeteoForecastApi {
        return OpenMeteoForecastApi()
    }

    @Provides
    @Singleton
    fun provideOpenMeteoGeocodingApi(): OpenMeteoGeocodingApi {
        return OpenMeteoGeocodingApi()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindsModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(impl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindFieldNoteRepository(impl: FieldNoteRepositoryImpl): FieldNoteRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository
}
