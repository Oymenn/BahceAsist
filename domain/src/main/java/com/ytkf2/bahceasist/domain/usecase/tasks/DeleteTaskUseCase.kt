package com.ytkf2.bahceasist.domain.usecase.tasks

import com.ytkf2.bahceasist.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteTask(id)
}
