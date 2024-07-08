package org.novbicreate.domain

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.novbicreate.domain.ApiRoutes.POST_ERROR
import org.novbicreate.domain.models.ErrorData
import org.novbicreate.domain.models.WeatherData
import org.novbicreate.utils.Resource
import java.net.ConnectException
import java.util.concurrent.TimeoutException

class ApiRepositoryImpl(private val client: HttpClient): ApiRepository {
    companion object {
        private const val LANGUAGE = "language"
        private const val CITY = "city"
    }
    override suspend fun getWeather(city: String): Resource<WeatherData> {
        return try {
            Resource.Success(data = client.get(ApiRoutes.GET_WEATHER) {
                parameter(LANGUAGE, "russian")
                parameter(CITY, city)
            }.body())
        } catch (e: Exception) {
            e.printStackTrace()
            sendErrorToMetric(e)
            val message = when (e) {
                is ConnectException -> "Связь с сервером потеряна, попробуйте поже"
                is TimeoutException -> "Время ожидания ответа от сервера истекло. Пожалуйста, попробуйте еще раз."
                is IllegalArgumentException -> "Я не знаю такого города. Пожалуйста, проверьте название города и попробуйте еще раз."
                else -> "Извините, я не понял. Пожалуйста, отправьте название города, где вы хотите узнать погоду."
            }
            Resource.Error(message)
        }
    }
    private suspend fun sendErrorToMetric(e: Exception) {
        client.post(POST_ERROR) {
            contentType(ContentType.Application.Json)
            setBody(ErrorData(
                type = "client",
                source = "weather_tg_bot",
                time = System.currentTimeMillis(),
                description = e.localizedMessage
            ))
        }
    }
}