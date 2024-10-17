package com.thomashuyghues.route

import com.thomashuyghues.plugins.verifyJWT
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.util.concurrent.CopyOnWriteArrayList

// A list to keep track of all connected users
val connectedUsers = CopyOnWriteArrayList<WebSocketSession>()

fun Routing.chatWebSocket() {

    webSocket("/chat") {
        val token = call.request.headers["Authorization"] ?: run {
            // Send an error message to the client if the token is missing
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, """{"error":"Unauthorized","message":"Authorization token must be provided."}"""))
            // NOTE : Set up a custom logger
            println("WebSocket connection closed: Token not provided.")
            return@webSocket
        }

        val username = verifyJWT(token) ?: run {
            // Send an error message to the client if the token is invalid or expired
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, """{"error":"Invalid Token","message":"The provided token is invalid or expired. Please refresh your token."}"""))
            // NOTE : Set up a custom logger
            println("WebSocket connection closed: Invalid or expired token.")
            return@webSocket
        }

        // Add the connected user's session to the list
        connectedUsers.add(this)
        try {
            // Receive messages from this user
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val receivedText = frame.readText()
                    // Broadcast the received message to all connected users
                    for (user in connectedUsers) {
                        if (user != this) {
                            user.send("${username.name} said: $receivedText")
                        }
                    }
                }
            }
        } catch (e: ClosedReceiveChannelException) {
            println("User disconnected: ${closeReason.await()}")
        } finally {
            // Remove user session when disconnected
            connectedUsers.remove(this)
        }
    }
}
