package com.thomashuyghues.game

import ChessGame
import com.thomashuyghues.model.data.Player
import io.ktor.websocket.*

class Game(val player1: Player, val player2: Player) {
    lateinit var chessGame: ChessGame
    suspend fun startGame() {
        // Logic to initialize a new chess game
        println("Starting game between ${player1.username} and ${player2.username}")

        // Inform both players that the game has started
        player1.websocketSession.send("Game started! You are playing against ${player2.username}.")
        player2.websocketSession.send("Game started! You are playing against ${player1.username}.")

        chessGame = ChessGame()
    }
    suspend fun handleMove(player: Player, move: String) {
        // deserialize move here, or make move a Data Class with 4 int
        // change the way to manage player turn
         chessGame.makeMove()
    }
}

class Queue (private val gameManager: GameManager){
    private val queue: MutableList<Player> = mutableListOf()

    // Add player to the queue
    suspend fun addPlayerToQueue(player: Player) {
        queue.add(player)
        println("Player ${player.username} added to the queue")

        // If two players are in the queue, start a new game
        if (queue.size >= 2) {
            val player1 = queue.removeAt(0)
            val player2 = queue.removeAt(0)
            val game = gameManager.startNewGame(player1, player2) // Start the game
        }
    }
}