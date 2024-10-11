package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.locations.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlin.time.Duration.Companion.minutes

@OptIn(KtorExperimentalLocationsAPI::class)
fun main() {
    embeddedServer(Netty, port = 8080) {
        install(Locations) // Enable locations for REST-style endpoints
        install(WebSockets) { // Enable locations for WS
            pingPeriod = 1.minutes
        }
        routing {
            get("/api") {
                call.respondText("Welcome to Chess API")
            }
            webSocket("/ws") {
                try {
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                println("Received: $text")
                                send(Frame.Text("Message received: $text"))
                            }
                            else -> println("Other frame: $frame")
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    println("WebSocket closed.")
                } finally {
                    println("WebSocket closed.")
                }
            }
        }
    }.start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureSockets()
    configureSecurity()
    configureRouting()
}
