package org.novbicreate.controller

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.MessageUpdate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.novbicreate.common.additionalMessage
import org.novbicreate.controller.ControllerRoutes.MESSAGING_ROUTE
import org.novbicreate.domain.ApiRepository

class MessagingController: KoinComponent {
    private val _repository: ApiRepository by inject()

    @InputHandler([MESSAGING_ROUTE])
    suspend fun messaging(update: MessageUpdate?, user: User, bot: TelegramBot) {
        val city = update?.text?: "Москва"
        val message = _repository.handleWeatherMessage(city)
        message(message).send(user, bot)
        message(additionalMessage).send(user, bot)
        bot.inputListener[user] = MESSAGING_ROUTE
    }
}