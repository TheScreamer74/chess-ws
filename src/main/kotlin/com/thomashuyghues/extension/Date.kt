package com.thomashuyghues.extension

import io.github.cdimascio.dotenv.dotenv
import java.time.Instant
import java.util.Date

val dotenv = dotenv()

fun Date.getRefreshTokenExpiration() : Date {
    return Date(System.currentTimeMillis() + com.thomashuyghues.plugins.dotenv["JWT_REFRESH_EXP"].toInt())
}

fun Date.getAccessTokenExpiration() : Date {
    return Date(System.currentTimeMillis() + com.thomashuyghues.plugins.dotenv["JWT_ACCESS_EXP"].toInt())
}