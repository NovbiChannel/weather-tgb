package org.novbicreate.domain.repository

import org.novbicreate.domain.models.Metadata

interface ApiRepository {
    suspend fun handleWeatherMessage(city: String, language: String?): String
    suspend fun getMetaData(language: String?): Metadata
}