package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.locations.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.module() {
    install(Locations) // Enable locations for REST-style endpoints


    configureSerialization()
    configureSockets()
    configureSecurity()
    configureRouting()
}
