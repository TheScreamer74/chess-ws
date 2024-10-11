package com.example.database.users

import com.example.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

// Create a user
fun createUser(username: String, email: String, passwordHash: String): User? {
    var userId: Int? = null
    transaction {
        val insertStatement = Users.insert {
            it[Users.username] = username
            it[Users.email] = email
            it[Users.passwordHash] = passwordHash
        }

        userId = insertStatement.resultedValues?.firstOrNull()?.get(Users.id)
    }

    return userId?.let { getUserById(it) }
}

// Fetch a user by id
fun getUserById(userId: Int): User? {
    return transaction {
        Users.select { Users.id eq userId }
            .map {
                User(
                    id = it[Users.id],
                    username = it[Users.username],
                    email = it[Users.email],
                    passwordHash = it[Users.passwordHash]
                )
            }
            .singleOrNull()
    }
}

// Fetch a user by username
fun getUserByUsername(username: String): User? {
    return transaction {
        Users.select { Users.username eq username }
            .map {
                User(
                    id = it[Users.id],
                    username = it[Users.username],
                    email = it[Users.email],
                    passwordHash = it[Users.passwordHash]
                )
            }
            .singleOrNull()
    }
}