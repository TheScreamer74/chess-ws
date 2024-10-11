package com.example.database.users

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 26).uniqueIndex()
    val email = varchar("email", 30).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)

    override val primaryKey = PrimaryKey(id)
}