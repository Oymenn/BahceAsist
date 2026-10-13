package com.ytkf2.bahceasist.domain.usecase.tasks

import com.ytkf2.bahceasist.domain.model.GardenTask
import com.ytkf2.bahceasist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<GardenTask>> = repository.observeTasks()
}
