package com.github.skiff2011.lab3

import java.io.FileInputStream
import java.util.*

fun loadToken(): String {
    val properties = Properties()
    FileInputStream(CONFIG_FILE).use { properties.load(it) }
    return properties.getProperty(TOKEN_KEY) ?: error("API_KEY not found")
}

private const val TOKEN_KEY = "BOT_TOKEN"
private const val CONFIG_FILE = "config.properties"