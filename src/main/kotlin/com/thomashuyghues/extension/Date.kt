package com.thomashuyghues.extension

import java.util.Date

fun getRefreshTokenExpiration(): Date {
    return Date(System.currentTimeMillis() + com.thomashuyghues.plugins.dotenv["JWT_REFRESH_EXP"].toInt())
}

fun getAccessTokenExpiration(): Date {
    return Date(System.currentTimeMillis() + com.thomashuyghues.plugins.dotenv["JWT_ACCESS_EXP"].toInt())
}