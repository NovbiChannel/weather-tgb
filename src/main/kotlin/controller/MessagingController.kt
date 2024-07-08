package org.novbicreate.controller

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.MessageUpdate
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.novbicreate.controller.ControllerRoutes.MESSAGING_ROUTE
import org.novbicreate.domain.ApiRepositoryImpl
import org.novbicreate.domain.models.WeatherData
import org.novbicreate.domain.sendEvent
import org.novbicreate.utils.Resource

class MessagingController {
    private val _client = HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }
    private val _repository = ApiRepositoryImpl(_client)

    @InputHandler([MESSAGING_ROUTE])
    suspend fun messaging(update: MessageUpdate?, user: User, bot: TelegramBot) {
        val city = update?.text?: "Москва"
        val weatherRespond = _repository.getWeather(city)
        handleWeatherResponse(weatherRespond, user, bot)
    }

    private suspend fun handleWeatherResponse(weatherRespond: Resource<WeatherData>, user: User, bot: TelegramBot) {
        when (weatherRespond) {
            is Resource.Success -> {
                val weather = weatherRespond.data!!
                val message = buildWeatherMessage(weather)
                message(message).send(user, bot)
                sendAdditionalMessage(user, bot)
                sendEvent("Запрошена погода для города ${weather.city}", _client)
            }
            is Resource.Error -> sendErrorMessage(user, bot, weatherRespond.message)
        }
        bot.inputListener[user] = MESSAGING_ROUTE
    }

    private fun buildWeatherMessage(weather: WeatherData): String {
        val conditions = weather.conditions.replaceFirstChar { it.uppercase() }
        val temperature = "${weather.temperature}°C"
        val humidity = "Влажность ${weather.humidity}%"
        val windSpeed = if (weather.windSpeed!= 0) ", ветер ${weather.windSpeed} м/с" else ""
        return "Погода в городе ${weather.city}: $conditions $temperature. $humidity$windSpeed"
    }

    private suspend fun sendErrorMessage(user: User, bot: TelegramBot, message: String?) {
        message(message?: "Что-то пошло не так...").send(user, bot)
    }

    private suspend fun sendAdditionalMessage(user: User, bot: TelegramBot) {
        message("Если вы хотите узнать погоду в другом городе, просто отправьте его название.").send(user, bot)
    }
}