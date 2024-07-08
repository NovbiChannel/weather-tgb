package org.novbicreate

import eu.vendeli.tgbot.TelegramBot

suspend fun main() {
    val bot = TelegramBot(System.getenv("BOT_TOKEN"))
    bot.handleUpdates()
}