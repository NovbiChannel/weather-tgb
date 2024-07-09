package org.novbicreate.controller

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.User
import org.novbicreate.common.welcomeMessage
import org.novbicreate.controller.Commands.START
import org.novbicreate.controller.ControllerRoutes.MESSAGING_ROUTE

class StartController {
    @CommandHandler([START])
    suspend fun start(user: User, bot: TelegramBot) {
        message(welcomeMessage).send(user, bot)
        bot.inputListener[user] = MESSAGING_ROUTE
    }
}