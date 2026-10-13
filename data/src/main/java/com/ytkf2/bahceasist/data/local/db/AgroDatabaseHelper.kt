package com.ytkf2.bahceasist.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AgroDatabaseHelper @Inject constructor(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS field_notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                category TEXT NOT NULL,
                timestamp INTEGER NOT NULL,
                image_path TEXT,
                attached_temp REAL,
                attached_weather_condition TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS weather_cache (
                location_key TEXT PRIMARY KEY,
                payload_json TEXT NOT NULL,
                fetched_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS garden_tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                due_time TEXT NOT NULL,
                category TEXT NOT NULL,
                is_completed INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS field_notes")
        db.execSQL("DROP TABLE IF EXISTS weather_cache")
        db.execSQL("DROP TABLE IF EXISTS garden_tasks")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "bahce_asist.db"
        const val DATABASE_VERSION = 2
    }
}
