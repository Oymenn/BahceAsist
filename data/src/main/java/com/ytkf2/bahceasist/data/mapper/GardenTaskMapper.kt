package com.ytkf2.bahceasist.data.mapper

import com.ytkf2.bahceasist.data.local.entity.GardenTaskEntity
import com.ytkf2.bahceasist.domain.model.GardenTask

fun GardenTaskEntity.toDomain(): GardenTask {
    return GardenTask(
        id = id,
        title = title,
        dueTime = dueTime,
        category = category,
        isCompleted = isCompleted == 1,
        createdAtMillis = createdAt
    )
}

fun GardenTask.toEntity(): GardenTaskEntity {
    return GardenTaskEntity(
        id = id,
        title = title,
        dueTime = dueTime,
        category = category,
        isCompleted = if (isCompleted) 1 else 0,
        createdAt = createdAtMillis
    )
}
