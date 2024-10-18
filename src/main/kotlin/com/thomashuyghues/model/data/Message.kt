package com.thomashuyghues.model.data

import kotlinx.serialization.Serializable

@Serializable
data class Message(val type: MessageType, val content: String? = null)

@Serializable
enum class MessageType {
    MOVE,
    MESSAGE,
    SYSTEM,
    ERROR
}