package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.locations.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@OptIn(KtorExperimentalLocationsAPI::class)
fun main() {
    embeddedServer(Netty, port = 8080) {
        install(Locations) // Enable locations for REST-style endpoints
        routing {
            get("/api") {
                call.respondText("Welcome to Chess API")
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
