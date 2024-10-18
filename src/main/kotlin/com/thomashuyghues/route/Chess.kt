package com.thomashuyghues.route

import com.thomashuyghues.game.GameManager
import com.thomashuyghues.game.Queue
import com.thomashuyghues.model.data.Player
import com.thomashuyghues.plugins.verifyJWT
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*

val gameManager = GameManager()
val chessQueue = Queue(gameManager)

fun Route.chessRoute() {
    webSocket("/play") {
        val token = call.request.headers["Authorization"] ?: run {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "{\"message\":\"token must be provided\"}"))
            return@webSocket
        }

        val username = verifyJWT(token) ?: run {
            close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Token is invalid or expired"))
            return@webSocket
        }

        val player = Player(id = token, username = username.name, websocketSession = this)

        // Add the player to the queue
        chessQueue.addPlayerToQueue(player)

        // Handle WebSocket messages (chess moves, etc.)
        for (frame in incoming) {
            if (frame is Frame.Text) {
                val message = frame.readText()
                val game = gameManager.getGameByPlayer(player.id)
                game?.handleMove(player, message)
            }
        }
    }
}