package com.example.route

import com.example.database.users.getUserByUsername
import com.example.model.endpoint.LoginRequest
import com.example.plugins.generateJWT
import com.example.utils.validatePassword
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