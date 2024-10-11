package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {
    private const val secret = "mySecret"
    private const val issuer = "ktor.io"
    private const val audience = "ktor_audience"
    const val realm = "Access to 'protected' route"

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun generateToken(username: String): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withClaim("username", username)
        .withExpiresAt(Date(System.currentTimeMillis() + 600000)) // Token valid for 10 mins
        .sign(algorithm)
}