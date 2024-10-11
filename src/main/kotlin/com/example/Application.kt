package com.example

import com.example.plugins.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.locations.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
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
        install(Authentication) {
            jwt("auth-jwt") {
                verifier(JwtConfig.verifier)
                validate { credential ->
                    if (credential.payload.getClaim("username").asString().isNotEmpty()) JWTPrincipal(credential.payload) else null
                }
            }
        }
        routing {
            // Public route
            get("/login") {
                val username = call.parameters["username"] ?: ""
                val password = call.parameters["password"] ?: ""

                // You would verify the username and password with your database
                if (username == "user" && password == "password") { // TODO : setup database
                    val token = JwtConfig.generateToken(username)
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
                    val username = principal?.payload?.getClaim("username")?.asString()
                    call.respondText("Hello, $username! You are authenticated.")
                }
            }
        }
    }.start(wait = true)
}

fun Application.module() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = JwtConfig.realm
            verifier(JwtConfig.verifier)
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or missing")
            }
        }
    }
    install(ContentNegotiation) {
        json()
    }
    configureSerialization()
    configureSockets()
    configureSecurity()
    configureRouting()
}
