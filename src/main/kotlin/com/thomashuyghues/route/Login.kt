package com.thomashuyghues.route

import com.thomashuyghues.database.users.getUserByUsername
import com.thomashuyghues.model.endpoint.LoginRequest
import com.thomashuyghues.plugins.generateJWT
import com.thomashuyghues.utils.validatePassword
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.loginRoute() {
    post("/login") {
        val parameters = call.receive<LoginRequest>()
        val user = getUserByUsername(parameters.username)

        if (user != null && validatePassword(parameters.password, user.passwordHash)) {
            val token = generateJWT(user.username)
            call.respond(mapOf("token" to token))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }
}