package com.thomashuyghues.plugins

import com.thomashuyghues.route.chatWebSocket
import com.thomashuyghues.route.loginRoute
import com.thomashuyghues.route.refreshToken
import com.thomashuyghues.route.registerRoute
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Public route
        registerRoute()
        loginRoute()

        refreshToken()
        // Protected route
        authenticate("auth-jwt") {
            get("/protected-route") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                call.respondText("Hello, $username! This is a protected route.")
            }
            chatWebSocket()
        }
    }
}
