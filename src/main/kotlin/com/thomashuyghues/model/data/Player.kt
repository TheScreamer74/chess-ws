package com.thomashuyghues.model.data

import io.ktor.websocket.*

data class Player(val id: String, val username: String, val websocketSession: DefaultWebSocketSession)
