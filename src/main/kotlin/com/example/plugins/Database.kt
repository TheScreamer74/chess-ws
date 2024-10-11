package com.example.plugins

import com.example.database.users.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.module() {
    // Initialize database connection
    val dbConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/ktor_users"
        driverClassName = "org.postgresql.Driver"
        username = "xxxx"
        password = "xxxx"
        maximumPoolSize = 10
    }

    val dataSource = HikariDataSource(dbConfig)
    Database.connect(dataSource)

    // Create the Users table if it doesn't exist
    transaction {
        SchemaUtils.create(Users)
    }

    routing {
        // Define your routes here
    }
}