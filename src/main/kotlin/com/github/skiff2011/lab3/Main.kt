package com.github.skiff2011.lab3

import kotlinx.coroutines.*
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

fun main() {
    configureMetrics(scope)
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
    try {
        botsApi.registerBot(AndroidSearchBot())
        println("Bot is running...")
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
    Runtime.getRuntime().addShutdownHook(Thread {
        println("Shutting down...")
        scope.cancel() // Cancel the scope and all its coroutines
    })
}