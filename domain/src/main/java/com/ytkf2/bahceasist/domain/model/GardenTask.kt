package com.ytkf2.bahceasist.domain.model

data class GardenTask(
    val id: Long = 0,
    val title: String,
    val dueTime: String,
    val category: String = "Genel",
    val isCompleted: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis()
)
