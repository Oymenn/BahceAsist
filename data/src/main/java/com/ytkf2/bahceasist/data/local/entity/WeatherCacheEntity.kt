package com.ytkf2.bahceasist.data.local.entity

data class WeatherCacheEntity(
    val locationKey: String,
    val payloadJson: String,
    val fetchedAt: Long
)
