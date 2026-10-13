package com.ytkf2.bahceasist.domain.usecase.tasks

import com.ytkf2.bahceasist.domain.model.GardenTask
import com.ytkf2.bahceasist.domain.repository.TaskRepository
import javax.inject.Inject

class SaveTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: GardenTask): Long = repository.saveTask(task)
}
