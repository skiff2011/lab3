package com.github.skiff2011.lab3

import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main() {
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    try {
        botsApi.registerBot(AndroidSearchBot())
        println("Bot is running...")
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}