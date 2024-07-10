package org.novbicreate.domain

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.novbicreate.common.*
import org.novbicreate.domain.ApiRoutes.GET_WEATHER
import org.novbicreate.domain.ApiRoutes.POST_ERROR
import org.novbicreate.domain.ApiRoutes.POST_EVENT
import org.novbicreate.domain.models.ErrorData
import org.novbicreate.domain.models.EventData
import org.novbicreate.domain.models.WeatherData
import java.net.ConnectException
import java.util.concurrent.TimeoutException

class ApiRepositoryImpl(private val client: HttpClient): ApiRepository {

    override suspend fun handleWeatherMessage(city: String): String {
        return try {
            val weather = client.get(GET_WEATHER) {
                parameter("language", "russian")
                parameter("city", city)
            }.body<WeatherData>()
            sendEventToMetric("Запрошена погода для города $city")
            handleWeatherMessage(weather)
        } catch (e: Exception) {
            sendErrorToMetric(e)
            handleErrorMessage(e)
        }
    }

    private fun handleWeatherMessage(weather: WeatherData): String {
        val conditions = weather.conditions.replaceFirstChar { it.uppercase() }
        val conditionEmoji = weather.conditionsEmoji
        val temperature = "${weather.temperature}°C"
        val humidity = "Влажность ${weather.humidity}% \uD83D\uDCA7"
        val windSpeed = if (weather.windSpeed!= 0) "Ветер \uD83D\uDCA8 ${weather.windSpeed} м/с" else ""
        return "Погода в городе ${weather.city}:" +
                "\n" +
                "\n" +
                "$conditions $conditionEmoji $temperature" +
                "\n$humidity" +
                "\n$windSpeed"
    }

    private fun handleErrorMessage(e: Exception): String {
        e.printStackTrace()
        return when (e) {
            is ConnectException -> connectionErrorMessage
            is TimeoutException -> timeoutErrorMessage
            is IllegalArgumentException -> illegalArgumentErrorMessage
            else -> unknownError
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