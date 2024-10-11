package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.util.*

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
    }
}

// Generate JWT token and return it
fun generateJWT(username: String) : String {
    return JWT.create()
        .withAudience("jwt-audience")
        .withIssuer("ktor.io")
        .withClaim("username", username)
        .withExpiresAt(Date(System.currentTimeMillis() + 1000L * 60 * 10)) // Expires in 10 minute
        .sign(Algorithm.HMAC256(dotenv["SECRET_JWT"]))
}
