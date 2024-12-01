package com.github.skiff2011.lab3

import org.fluentd.logger.FluentLogger
import org.jsoup.Jsoup
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class AndroidSearchBot : TelegramLongPollingBot(loadToken()) {

    val logger = FluentLogger.getLogger("myapp", "localhost", 8080)

    override fun getBotUsername(): String = BOT_NAME

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val message = update.message.text
            val chatId = update.message.chatId

            val userId = update.message.from.id

            val logs = mapOf(
                "UserId" to userId,
                "query" to message,
            )

            logger.log("UserData", logs)

            val reply = when (message) {
                START_MESSAGE -> printGreeting(chatId)
                else -> performSearch(chatId, message)
            }

            try {
                execute(reply)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun printGreeting(chatId: Long): SendMessage {
        val replyMessage = SendMessage()
        replyMessage.chatId = chatId.toString()
        replyMessage.text = "Hello! What is your question about Android?"
        return replyMessage
    }

    private fun performSearch(chatId: Long, query: String): SendMessage {
        val links = searchAndroidDocumentation(query)

        val replyMessage = SendMessage()
        replyMessage.chatId = chatId.toString()

        replyMessage.text = if (links.isEmpty()) {
            "Nothing found"
        } else {
            links.joinToString(separator = "\n")
        }

        return replyMessage
    }

    private fun searchAndroidDocumentation(query: String): List<String> {
        val searchUrl = "https://www.google.com/search?q=site:developer.android.com+$query"
        val results = mutableListOf<String>()

        try {
            // Fetch the search result page
            val doc = Jsoup.connect(searchUrl).userAgent("Mozilla/5.0").get()

            // Select links in the search results
            val elements = doc.select("a[href]")

            val regex = Regex("https://developer\\.android\\.com/.*")

            for (element in elements) {
                val href = element.attr("href")

                // Filter valid links pointing to developer.android.com
                if (href.startsWith("/url?q=") && href.contains("developer.android.com")) {
                    val cleanLink = href.substringAfter("/url?q=").substringBefore("&")
                    if (regex.matches(cleanLink)) {
                        if (results.size < 5) {
                            results.add(cleanLink)
                        } else {
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return results
    }
}

private const val BOT_NAME = "Lab3AndroidSearchBot"
private const val START_MESSAGE = "/start"