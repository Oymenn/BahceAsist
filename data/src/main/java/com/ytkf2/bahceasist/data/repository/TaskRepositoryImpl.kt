package com.ytkf2.bahceasist.data.repository

import com.ytkf2.bahceasist.data.local.dao.GardenTaskDao
import com.ytkf2.bahceasist.data.mapper.toDomain
import com.ytkf2.bahceasist.data.mapper.toEntity
import com.ytkf2.bahceasist.domain.model.GardenTask
import com.ytkf2.bahceasist.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: GardenTaskDao
) : TaskRepository {

    private val _tasksUpdateTrigger = MutableStateFlow(System.currentTimeMillis())

    override fun observeTasks(): Flow<List<GardenTask>> {
        return _tasksUpdateTrigger.map {
            withContext(Dispatchers.IO) {
                taskDao.getAll().map { it.toDomain() }
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun saveTask(task: GardenTask): Long = withContext(Dispatchers.IO) {
        val id = taskDao.insert(task.toEntity())
        _tasksUpdateTrigger.value = System.currentTimeMillis()
        id
    }

    override suspend fun toggleTask(id: Long, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        taskDao.updateCompletion(id, isCompleted)
        _tasksUpdateTrigger.value = System.currentTimeMillis()
    }

    override suspend fun deleteTask(id: Long) = withContext(Dispatchers.IO) {
        taskDao.delete(id)
        _tasksUpdateTrigger.value = System.currentTimeMillis()
    }
}
