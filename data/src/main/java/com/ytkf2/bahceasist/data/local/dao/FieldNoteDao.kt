package com.ytkf2.bahceasist.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.ytkf2.bahceasist.data.local.db.AgroDatabaseHelper
import com.ytkf2.bahceasist.data.local.entity.FieldNoteEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FieldNoteDao @Inject constructor(
    private val dbHelper: AgroDatabaseHelper
) {

    fun insert(entity: FieldNoteEntity): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", entity.title)
            put("content", entity.content)
            put("category", entity.category)
            put("timestamp", entity.timestamp)
            put("image_path", entity.imagePath)
            put("attached_temp", entity.attachedTemp)
            put("attached_weather_condition", entity.attachedWeatherCondition)
        }
        return db.insert("field_notes", null, values)
    }

    fun update(entity: FieldNoteEntity): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", entity.title)
            put("content", entity.content)
            put("category", entity.category)
            put("timestamp", entity.timestamp)
            put("image_path", entity.imagePath)
            put("attached_temp", entity.attachedTemp)
            put("attached_weather_condition", entity.attachedWeatherCondition)
        }
        return db.update("field_notes", values, "id = ?", arrayOf(entity.id.toString()))
    }

    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete("field_notes", "id = ?", arrayOf(id.toString()))
    }

    fun getById(id: Long): FieldNoteEntity? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "field_notes",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )
        return cursor.use {
            if (it.moveToFirst()) it.toEntity() else null
        }
    }

    fun getAll(category: String? = null): List<FieldNoteEntity> {
        val db = dbHelper.readableDatabase
        val selection = if (category != null) "category = ?" else null
        val selectionArgs = if (category != null) arrayOf(category) else null

        val cursor = db.query(
            "field_notes",
            null,
            selection,
            selectionArgs,
            null,
            null,
            "timestamp DESC"
        )
        return cursor.use {
            val list = mutableListOf<FieldNoteEntity>()
            while (it.moveToNext()) {
                list.add(it.toEntity())
            }
            list
        }
    }

    private fun Cursor.toEntity(): FieldNoteEntity {
        return FieldNoteEntity(
            id = getLong(getColumnIndexOrThrow("id")),
            title = getString(getColumnIndexOrThrow("title")),
            content = getString(getColumnIndexOrThrow("content")),
            category = getString(getColumnIndexOrThrow("category")),
            timestamp = getLong(getColumnIndexOrThrow("timestamp")),
            imagePath = getStringOrNull("image_path"),
            attachedTemp = getDoubleOrNull("attached_temp"),
            attachedWeatherCondition = getStringOrNull("attached_weather_condition")
        )
    }

    private fun Cursor.getStringOrNull(columnName: String): String? {
        val index = getColumnIndexOrThrow(columnName)
        return if (isNull(index)) null else getString(index)
    }

    private fun Cursor.getDoubleOrNull(columnName: String): Double? {
        val index = getColumnIndexOrThrow(columnName)
        return if (isNull(index)) null else getDouble(index)
    }
}
