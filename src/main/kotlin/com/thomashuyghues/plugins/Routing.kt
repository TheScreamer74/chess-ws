package com.thomashuyghues.plugins

import com.thomashuyghues.route.loginRoute
import com.thomashuyghues.route.registerRoute
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException

fun Application.configureRouting() {
    routing {
        // Public route
        registerRoute()
        loginRoute()
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
        // Protected route
        authenticate("auth-jwt") {
            get("/protected-route") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                call.respondText("Hello, $username! This is a protected route.")
            }
        }
    }
}
