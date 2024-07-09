package org.novbicreate.domain

interface ApiRepository {
    suspend fun handleWeatherMessage(city: String): String
}