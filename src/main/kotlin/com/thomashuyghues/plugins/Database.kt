package com.thomashuyghues.plugins

import com.thomashuyghues.database.users.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val dotenv = dotenv()
    // Initialize database connection
    val dbConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/chess"
        driverClassName = "org.postgresql.Driver"
        username = dotenv["DB_USERNAME"]
        password = dotenv["DB_PASSWORD"]
        maximumPoolSize = 10
    }

    val dataSource = HikariDataSource(dbConfig)
    Database.connect(dataSource)

    // Create the Users table if it doesn't exist
    transaction {
        SchemaUtils.create(Users)
    }
}