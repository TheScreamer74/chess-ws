package com.thomashuyghues.database.refresh_token

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object RefreshTokens : Table("refresh_tokens") {
    val id = integer("id").autoIncrement()
    val token = varchar("token",255).uniqueIndex()
    val userId = integer("user_id")
    val expiresAt = timestamp("expires_at")
    val issuedAt = timestamp("issued_at")

    override val primaryKey = PrimaryKey(id)
}