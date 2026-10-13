package com.ytkf2.bahceasist.data.local.dao

import android.content.ContentValues
import android.database.Cursor
import com.ytkf2.bahceasist.data.local.db.AgroDatabaseHelper
import com.ytkf2.bahceasist.data.local.entity.GardenTaskEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GardenTaskDao @Inject constructor(
    private val dbHelper: AgroDatabaseHelper
) {

    fun insert(entity: GardenTaskEntity): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("title", entity.title)
            put("due_time", entity.dueTime)
            put("category", entity.category)
            put("is_completed", entity.isCompleted)
            put("created_at", entity.createdAt)
        }
        return db.insert("garden_tasks", null, values)
    }

    fun updateCompletion(id: Long, isCompleted: Boolean): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("is_completed", if (isCompleted) 1 else 0)
        }
        return db.update("garden_tasks", values, "id = ?", arrayOf(id.toString()))
    }

    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete("garden_tasks", "id = ?", arrayOf(id.toString()))
    }

    fun getAll(): List<GardenTaskEntity> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            "garden_tasks",
            null,
            null,
            null,
            null,
            null,
            "is_completed ASC, created_at DESC"
        )
        return cursor.use {
            val list = mutableListOf<GardenTaskEntity>()
            while (it.moveToNext()) {
                list.add(it.toEntity())
            }
            list
        }
    }

    private fun Cursor.toEntity(): GardenTaskEntity {
        return GardenTaskEntity(
            id = getLong(getColumnIndexOrThrow("id")),
            title = getString(getColumnIndexOrThrow("title")),
            dueTime = getString(getColumnIndexOrThrow("due_time")),
            category = getString(getColumnIndexOrThrow("category")),
            isCompleted = getInt(getColumnIndexOrThrow("is_completed")),
            createdAt = getLong(getColumnIndexOrThrow("created_at"))
        )
    }
}
