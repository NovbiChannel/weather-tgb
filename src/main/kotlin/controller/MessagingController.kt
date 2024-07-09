package org.novbicreate.controller

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.MessageUpdate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.novbicreate.controller.ControllerRoutes.MESSAGING_ROUTE
import org.novbicreate.domain.ApiRepository
import org.novbicreate.domain.models.WeatherData
import org.novbicreate.utils.Resource

class MessagingController: KoinComponent {
    private val _repository: ApiRepository by inject()

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
                _repository.sendEvent("Запрошена погода для города ${weather.city}")
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