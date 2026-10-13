package com.ytkf2.bahceasist.domain.usecase.tasks

import com.ytkf2.bahceasist.domain.repository.TaskRepository
import javax.inject.Inject

class ToggleTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Long, isCompleted: Boolean) = repository.toggleTask(id, isCompleted)
}
