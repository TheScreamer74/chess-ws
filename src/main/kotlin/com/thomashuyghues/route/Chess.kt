package com.thomashuyghues.route

import com.thomashuyghues.game.GameManager
import com.thomashuyghues.game.Queue
import com.thomashuyghues.handler.handleWebSocket
import com.thomashuyghues.model.data.Message
import com.thomashuyghues.model.data.MessageType
import com.thomashuyghues.model.data.Player
import com.thomashuyghues.model.data.PlayerColor
import com.thomashuyghues.plugins.verifyJWT
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val gameManager = GameManager()
val chessQueue = Queue(gameManager)

fun Route.chessRoute() {
    webSocket("/play") {
        val token = call.request.headers["Authorization"] ?: run {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, Json.encodeToString(Message(MessageType.ERROR, "You must provide a token"))))
            return@webSocket
        }

        val username = verifyJWT(token) ?: run {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, Json.encodeToString(Message(MessageType.ERROR, "Your token is invalid or expired"))))
            return@webSocket
        }

        val player = Player(id = token, username = username.name, websocketSession = this, PlayerColor.NOT_YET)

        // Add the player to the queue
        chessQueue.addPlayerToQueue(player)

        // Handle WebSocket messages (chess moves, etc.)
        handleWebSocket(player, incoming, gameManager)
    }
}