package com.ytkf2.bahceasist.data.remote.dto

data class GeocodingResponseDto(
    val results: List<GeocodingResultDto> = emptyList()
)

data class GeocodingResultDto(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    val admin1: String? = null
)
