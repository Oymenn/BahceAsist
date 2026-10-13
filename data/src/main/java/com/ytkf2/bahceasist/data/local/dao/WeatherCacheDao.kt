package com.ytkf2.bahceasist.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ytkf2.bahceasist.data.local.db.AgroDatabaseHelper
import com.ytkf2.bahceasist.data.local.entity.WeatherCacheEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherCacheDao @Inject constructor(
    private val dbHelper: AgroDatabaseHelper
) {

    fun save(cache: WeatherCacheEntity) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("location_key", cache.locationKey)
            put("payload_json", cache.payloadJson)
            put("fetched_at", cache.fetchedAt)
        }
        db.insertWithOnConflict("weather_cache", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun get(locationKey: String): WeatherCacheEntity? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "weather_cache",
            null,
            "location_key = ?",
            arrayOf(locationKey),
            null,
            null,
            null
        )
        return cursor.use {
            if (it.moveToFirst()) it.toEntity() else null
        }
    }

    fun delete(locationKey: String): Int {
        val db = dbHelper.writableDatabase
        return db.delete("weather_cache", "location_key = ?", arrayOf(locationKey))
    }

    private fun Cursor.toEntity(): WeatherCacheEntity {
        return WeatherCacheEntity(
            locationKey = getString(getColumnIndexOrThrow("location_key")),
            payloadJson = getString(getColumnIndexOrThrow("payload_json")),
            fetchedAt = getLong(getColumnIndexOrThrow("fetched_at"))
        )
    }
}
