package com.thomashuyghues.database.refresh_token

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

fun storeRefreshToken(userId: Int, token: String, expiresAt: Instant) {
    transaction {
        RefreshTokens.insert {
            it[RefreshTokens.userId] = userId
            it[RefreshTokens.token] = token
            it[RefreshTokens.expiresAt] = expiresAt
        }
    }
}

fun rotateRefreshToken(userId: Int, newToken: String, expiresAt: Instant, oldToken : String) {
    transaction {
        RefreshTokens.deleteWhere {
            token eq oldToken
        }
    }
    storeRefreshToken(userId, newToken, expiresAt)
}

fun isTokenExists(token: String) : Boolean {
    return transaction {
        // Query the RefreshTokens table to check if the token exists
        RefreshTokens.select { RefreshTokens.token eq token }
            .singleOrNull() != null  // Returns true if the token exists, false otherwise
    }
}