package com.github.skiff2011.lab3

import org.fluentd.logger.FluentLogger
import org.jsoup.Jsoup
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.time.measureTimedValue

class AndroidSearchBot : TelegramLongPollingBot(loadToken()) {
    override fun getBotUsername(): String = BOT_NAME

    override fun onUpdateReceived(update: Update) {
        val message = update.message.text
        val chatId = update.message.chatId
        val userId = update.message.from.id

        val logs = mutableMapOf<String, Any>(
            USER_ID_KEY to userId,
            QUERY_KEY to message,
        )


        if (update.hasMessage() && update.message.hasText()) {
            val reply = when (message) {
                START_MESSAGE -> printGreeting(chatId)
                else -> performSearch(chatId, message, logs)
            }

            try {
                execute(reply)
            } catch (e: Exception) {
                e.printStackTrace()
                logs[SEARCH_RESULT_SUCCESS_KEY] = false
            }
        }
        logger.log(USER_REQUEST_DATA, logs.toMap())
    }

    private fun printGreeting(chatId: Long): SendMessage {
        val replyMessage = SendMessage()
        replyMessage.chatId = chatId.toString()
        replyMessage.text = "Hello! What is your question about Android?"
        return replyMessage
    }

    private fun performSearch(chatId: Long, query: String, logParams: MutableMap<String, Any>): SendMessage {
        val (links, duration) = measureTimedValue {
            searchAndroidDocumentation(query)
        }

        logParams[SEARCH_RESULT_COUNT_KEY] = links.size
        logParams[SEARCH_RESULT_SUCCESS_KEY] = true
        logParams[SEARCH_TIME_KEY] = duration.inWholeMilliseconds

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

    companion object {
        val logger: FluentLogger = FluentLogger.getLogger(TAG, HOST, PORT)
    }
}

private const val TAG = "myapp"
private const val HOST = "localhost"
private const val PORT = 8080
private const val BOT_NAME = "Lab3AndroidSearchBot"
private const val START_MESSAGE = "/start"
private const val USER_ID_KEY = "user_id"
private const val QUERY_KEY = "query"
private const val SEARCH_RESULT_COUNT_KEY = "search_result_count"
private const val SEARCH_RESULT_SUCCESS_KEY = "search_success"
private const val SEARCH_TIME_KEY = "search_time_millis"
private const val USER_REQUEST_DATA = "user_request_data"