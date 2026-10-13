package com.ytkf2.bahceasist.data.local.entity

data class GardenTaskEntity(
    val id: Long = 0,
    val title: String,
    val dueTime: String,
    val category: String,
    val isCompleted: Int,
    val createdAt: Long
)
