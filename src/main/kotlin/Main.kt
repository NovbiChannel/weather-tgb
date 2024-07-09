package org.novbicreate

import eu.vendeli.tgbot.TelegramBot
import org.koin.core.context.startKoin
import org.novbicreate.di.appModule

suspend fun main() {
    val bot = TelegramBot(System.getenv("BOT_TOKEN"))
    startKoin {
        modules(appModule)
    }
    bot.handleUpdates()
}