package org.novbicreate.domain

import org.novbicreate.domain.models.WeatherData
import org.novbicreate.utils.Resource

interface ApiRepository {
    suspend fun getWeather(city: String): Resource<WeatherData>
    suspend fun sendEvent(details: String)
}