package org.novbicreate.controller

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.novbicreate.common.MetadataCache
import org.novbicreate.controller.Commands.START
import org.novbicreate.controller.ControllerRoutes.MESSAGING_ROUTE

class StartController: KoinComponent {
    private val metadataCache: MetadataCache by inject()

    @CommandHandler([START])
    suspend fun start(user: User, bot: TelegramBot) {
        metadataCache.initialize(user)
        val metadata = metadataCache.getMetadata()
        message(metadata.welcomeMessage).send(user, bot)
        bot.inputListener[user] = MESSAGING_ROUTE
    }
}