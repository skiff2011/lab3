package com.github.skiff2011.lab3

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.prometheus.client.CollectorRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val prometheusMeterRegistry = PrometheusMeterRegistry(
    PrometheusConfig.DEFAULT,
    CollectorRegistry.defaultRegistry,
    Clock.SYSTEM,
)

fun configureMetrics(scope: CoroutineScope) {
    println("Init metrics")
    prometheusMeterRegistry.apply {
        ClassLoaderMetrics().bindTo(this)
        JvmMemoryMetrics().bindTo(this)
        JvmGcMetrics().bindTo(this)
        ProcessorMetrics().bindTo(this)
        JvmThreadMetrics().bindTo(this)
    }
    println("Metrics initialised")
    scope.launch {
        embeddedServer(Netty, port = 9091) {
            routing {
                get("/metrics") {
                    call.respondText(prometheusMeterRegistry.scrape(), contentType = io.ktor.http.ContentType.Text.Plain)
                }
            }
        }.start(wait = true)
    }
}