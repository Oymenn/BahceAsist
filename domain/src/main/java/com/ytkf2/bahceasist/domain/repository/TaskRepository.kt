package com.ytkf2.bahceasist.domain.repository

import com.ytkf2.bahceasist.domain.model.GardenTask
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<GardenTask>>
    suspend fun saveTask(task: GardenTask): Long
    suspend fun toggleTask(id: Long, isCompleted: Boolean)
    suspend fun deleteTask(id: Long)
}
