package com.thomashuyghues.model.data

import io.ktor.websocket.*

data class Player(val id: String, val username: String, val websocketSession: DefaultWebSocketSession, var color: PlayerColor)

enum class PlayerColor {
    WHITE {
        override fun toString(): String {
            return "White"
        }
    },
    BLACK {
        override fun toString(): String {
            return "Black"
        }
    },
    NOT_YET {
        override fun toString(): String {
            return "Not yet"
        }
    };

    abstract override fun toString(): String
}