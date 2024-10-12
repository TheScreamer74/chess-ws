package com.thomashuyghues.route

import com.sun.security.auth.UserPrincipal
import com.thomashuyghues.plugins.verifyJWT
import io.ktor.server.auth.*
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
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Missing token"))
            return@webSocket
        }

        val username = verifyJWT(token) ?: run {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid token"))
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
