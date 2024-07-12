package org.novbicreate.domain

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.novbicreate.common.MetadataCache
import org.novbicreate.domain.ApiRoutes.GET_METADATA
import org.novbicreate.domain.ApiRoutes.GET_WEATHER
import org.novbicreate.domain.ApiRoutes.POST_ERROR
import org.novbicreate.domain.ApiRoutes.POST_EVENT
import org.novbicreate.domain.models.ErrorData
import org.novbicreate.domain.models.EventData
import org.novbicreate.domain.models.Metadata
import org.novbicreate.domain.models.WeatherData
import java.net.ConnectException
import java.util.concurrent.TimeoutException

class ApiRepositoryImpl(private val client: HttpClient): ApiRepository {
    private var _metadata: Metadata? = null
    override suspend fun handleWeatherMessage(city: String, language: String?): String {
        return try {
            val weather = client.get(GET_WEATHER) {
                parameter("language", language)
                parameter("city", city)
            }.body<WeatherData>()
            sendEventToMetric("Запрошена погода для города $city")
            handleWeatherMessage(weather)
        } catch (e: Exception) {
            sendErrorToMetric(e)
            handleErrorMessage(e)
        }
    }

    override suspend fun getMetaData(language: String?): Metadata {
        val metadata = client.get(GET_METADATA) {
            parameter("language", language)
        }.body<Metadata>()
        _metadata = metadata
        return metadata
    }

    private fun handleWeatherMessage(weather: WeatherData): String {
        val conditions = weather.conditions.replaceFirstChar { it.uppercase() }
        val conditionEmoji = weather.conditionsEmoji
        val temperature = "${weather.temperature}°C"
        val humidity = "${_metadata?.humidity} ${weather.humidity}% \uD83D\uDCA7"
        val windSpeed = if (weather.windSpeed!= 0) "${_metadata?.wind} \uD83D\uDCA8 ${weather.windSpeed} ${_metadata?.ms}" else ""
        return "${_metadata?.weatherTitle} ${weather.city}:" +
                "\n" +
                "\n" +
                "$conditions $conditionEmoji $temperature" +
                "\n$humidity" +
                "\n$windSpeed"
    }

    private fun handleErrorMessage(e: Exception): String {
        e.printStackTrace()
        return when (e) {
            is ConnectException -> _metadata?.connectionErrorMessage?: e.localizedMessage
            is TimeoutException -> _metadata?.timeoutErrorMessage?: e.localizedMessage
            is IllegalArgumentException -> _metadata?.illegalArgumentErrorMessage?: e.localizedMessage
            else -> _metadata?.unknownError?: e.localizedMessage
        }
    }

    private suspend fun sendErrorToMetric(e: Exception) {
        try {
            client.post(POST_ERROR) {
                contentType(ContentType.Application.Json)
                setBody(ErrorData(
                    type = "client",
                    source = "weather_tg_bot",
                    time = System.currentTimeMillis(),
                    description = e.localizedMessage
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    private suspend fun sendEventToMetric(details: String) {
        try {
            client.post(POST_EVENT) {
                contentType(ContentType.Application.Json)
                setBody(
                    EventData(
                        type = "client",
                        source = "weather_tg_bot",
                        time = System.currentTimeMillis(),
                        details = details
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }
}