package com.thomashuyghues.route

import com.thomashuyghues.database.refresh_token.isTokenExists
import com.thomashuyghues.database.refresh_token.rotateRefreshToken
import com.thomashuyghues.database.users.getUserByUsername
import com.thomashuyghues.extension.getRefreshTokenExpiration
import com.thomashuyghues.plugins.generateTokens
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.refreshToken() {
    authenticate("auth-refresh-jwt") {
        post("/refresh-token") {
            val principal = call.principal<JWTPrincipal>()
            val username = principal!!.payload.getClaim("username").asString()
            val oldRefreshToken =
                call.request.headers["Authorization"]?.removePrefix("Bearer ")!! // past authentication the token is obligatory there

            if (!isTokenExists(oldRefreshToken)) {
                call.respond(HttpStatusCode.Unauthorized, "Token is expired you might re-log").run {
                    return@post
                }
            }


            // Generate new tokens
            val (newAccessToken, newRefreshToken) = generateTokens(username)

            val user = getUserByUsername(username)

            user ?: call.respond(
                HttpStatusCode.InternalServerError,
                "Unexpected error, user $username associated with token not found in database"
            ).run {
                return@post
            }

            // Rotate the refresh tokens
            rotateRefreshToken(
                user.id,
                newRefreshToken,
                getRefreshTokenExpiration().toInstant(),
                oldRefreshToken
            ) // TODO : Find a way to remove non-assertion

            call.respond(mapOf("accessToken" to newAccessToken, "refreshToken" to newRefreshToken))
        }
    }
}