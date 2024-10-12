package com.thomashuyghues.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.thomashuyghues.extension.getAccessTokenExpiration
import com.thomashuyghues.extension.getRefreshTokenExpiration
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

val dotenv = dotenv()

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "ktor-sample"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(dotenv["SECRET_JWT"]))
                    .withAudience("jwt-audience")
                    .withIssuer("ktor.io")
                    .build())
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Invalid token")
            }
        }
        // JWT for refresh tokens
        jwt("auth-refresh-jwt") {
            realm = "ktor.io"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(dotenv["SECRET_JWT"]))
                    .withAudience("jwt-audience")
                    .withIssuer("ktor.io")
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Refresh token is invalid or expired")
            }
        }
    }
}

fun generateTokens(username: String): Pair<String, String> {
    val access = JWT.create()
        .withAudience("jwt-audience")
        .withIssuer("ktor.io")
        .withClaim("username", username)
        .withExpiresAt(getAccessTokenExpiration())
        .sign(Algorithm.HMAC256(dotenv["SECRET_JWT"]))

    val refresh = JWT.create()
        .withAudience("jwt-audience")
        .withIssuer("ktor.io")
        .withClaim("username", username)
        .withExpiresAt(getRefreshTokenExpiration())
        .sign(Algorithm.HMAC256(dotenv["SECRET_JWT"]))

    return Pair(access, refresh)
}

fun verifyJWT(token: String): UserIdPrincipal? {
    val jwtVerifier = JWT
        .require(Algorithm.HMAC256(dotenv["SECRET_JWT"]))
        .withIssuer("ktor.io")
        .build()

    val decodedJWT = try {
        jwtVerifier.verify(token)
    } catch (e: Exception) {
        null
    }

    return decodedJWT?.let {
        val username = it.getClaim("username").asString()
        if (username != null) {
            UserIdPrincipal(username)
        } else {
            null
        }
    }
}
