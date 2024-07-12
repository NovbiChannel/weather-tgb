package org.novbicreate.controller

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.InputHandler
import eu.vendeli.tgbot.api.message.message
import eu.vendeli.tgbot.types.User
import eu.vendeli.tgbot.types.internal.MessageUpdate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.novbicreate.common.MetadataCache
import org.novbicreate.controller.ControllerRoutes.MESSAGING_ROUTE
import org.novbicreate.domain.ApiRepository

class MessagingController: KoinComponent {
    private val _repository: ApiRepository by inject()
    private val metadataCache: MetadataCache by inject()

    @InputHandler([MESSAGING_ROUTE])
    suspend fun messaging(update: MessageUpdate?, user: User, bot: TelegramBot) {
        val metadata = metadataCache.getMetadata()
        val city = update?.text?: "Moscow"
        val message = _repository.handleWeatherMessage(city, user.languageCode)
        message(message).send(user, bot)
        message(metadata.additionalMessage).send(user, bot)
        bot.inputListener[user] = MESSAGING_ROUTE
    }
}