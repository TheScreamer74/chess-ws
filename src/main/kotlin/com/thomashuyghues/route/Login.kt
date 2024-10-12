package com.thomashuyghues.route

import com.thomashuyghues.database.refresh_token.storeRefreshToken
import com.thomashuyghues.database.users.getUserByUsername
import com.thomashuyghues.extension.getRefreshTokenExpiration
import com.thomashuyghues.model.endpoint.LoginRequest
import com.thomashuyghues.plugins.generateTokens
import com.thomashuyghues.utils.validatePassword
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.loginRoute() {
    post("/login") {
        val parameters = call.receive<LoginRequest>()
        val user = getUserByUsername(parameters.username)

        if (user != null && validatePassword(parameters.password, user.passwordHash)) {
            val (access, refresh) = generateTokens(user.username)
            storeRefreshToken(user.id, refresh, Date().getRefreshTokenExpiration().toInstant())
            call.respond(
                mapOf(
                    "access" to access,
                    "refresh" to refresh
                )
            )
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }
}