package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.util.*

fun Application.configureRouting() {
    routing {
        // Public route
        post("/login") {
            val credentials = call.receive<Map<String, String>>()
            val username = credentials["username"]
            val password = credentials["password"]

            if (username == "user" && password == "password") {
                // Generate JWT token
                val token = JWT.create()
                    .withAudience("jwt-audience")
                    .withIssuer("ktor.io")
                    .withClaim("username", username)
                    .withExpiresAt(Date(System.currentTimeMillis() + 1000L * 60 * 10)) // Expires in 10 minute
                    .sign(Algorithm.HMAC256("secre t"))

                call.respond(mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
            }
        }
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
