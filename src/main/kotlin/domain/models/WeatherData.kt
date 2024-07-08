package org.novbicreate.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val city: String,
    val conditions: String,
    val temperature: Int,
    val humidity: Int,
    val windSpeed: Int? = 0,
)
