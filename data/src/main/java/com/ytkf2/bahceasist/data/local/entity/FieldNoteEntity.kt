package com.ytkf2.bahceasist.data.local.entity

data class FieldNoteEntity(
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: String,
    val timestamp: Long,
    val imagePath: String? = null,
    val attachedTemp: Double? = null,
    val attachedWeatherCondition: String? = null
)
