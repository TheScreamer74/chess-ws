package com.thomashuyghues.handler

import com.thomashuyghues.game.GameManager
import com.thomashuyghues.model.data.Message
import com.thomashuyghues.model.data.MessageType
import com.thomashuyghues.model.data.Move
import com.thomashuyghues.model.data.Player
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun handleWebSocket(player: Player, incoming: ReceiveChannel<Frame>, gameManager: GameManager) {
    for (frame in incoming) {
        if (frame is Frame.Text) {
            val jsonMessage = frame.readText()
            val game = gameManager.getGameByPlayer(player.id)

            try {
                // Deserialize the incoming message
                val message = Json.decodeFromString<Message>(jsonMessage)

                when (message.type) {
                    MessageType.MESSAGE -> {
                        // Handle chat message
                        val chatContent = message.content ?: continue

                        // Broadcast chat message to both players in the game
                        val formattedMessage = "${player.username}: $chatContent"

                        // Send to both players in the game
                        game?.players?.forEach { otherPlayer ->
                            otherPlayer.websocketSession.send(Frame.Text(formattedMessage))
                        }
                    }

                    MessageType.MOVE -> {
                        // Deserialize the move from the content field
                        val move = try {
                            Json.decodeFromString<Move>(message.content ?: "")
                        } catch (e: Exception) {
                            val message = Message(MessageType.ERROR, "Invalid move format")
                            player.websocketSession.send(Json.encodeToString(message))
                            continue
                        }

                        // Handle chess move
                        if (game != null && game.getCurrentPlayer() == player.color.toString()) {
                            val opponent = game.players.filterNot { it == player }
                            var systemMessage: Message
                            if (game.handleMove(move)) {
                                game.printBoard()
                                systemMessage = Message(MessageType.SYSTEM, Json.encodeToString(game.getBoardRepresentation()))
                                game.players.forEach { player ->
                                    player.websocketSession.send(Json.encodeToString(systemMessage))
                                }
                                systemMessage = Message(MessageType.SYSTEM, "It's your turn")
                                opponent[0].websocketSession.send(Json.encodeToString(systemMessage))
                            } else {
                                systemMessage = Message(MessageType.ERROR, "Wrong move")
                                player.websocketSession.send(Json.encodeToString(systemMessage))
                            }
                        } else {
                            // If it's not the player's turn, ignore the move
                            val systemMessage = Message(MessageType.ERROR, "It's not your turn")
                            player.websocketSession.send(Json.encodeToString(systemMessage))
                        }
                    }

                    else -> {
                        // Invalid message type, can be logged or ignored
                        val systemMessage = Message(MessageType.ERROR, "Invalid message type")
                        player.websocketSession.send(Json.encodeToString(systemMessage))
                    }
                }

            } catch (e: SerializationException) {
                // Handle invalid JSON or deserialization error
                val systemMessage = Message(MessageType.ERROR, "Invalid message format")
                player.websocketSession.send(Json.encodeToString(systemMessage))
            }
        }
    }
}