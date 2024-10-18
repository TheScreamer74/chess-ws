package com.thomashuyghues.game

import ChessGame
import King
import Queen
import Bishop
import Knight
import Pawn
import Rook
import com.thomashuyghues.model.data.Move
import com.thomashuyghues.model.data.Player
import com.thomashuyghues.model.data.PlayerColor
import io.ktor.websocket.*

class Game(val player1: Player, val player2: Player) {
    private lateinit var chessGame: ChessGame
    val players = listOf(player1, player2)
    suspend fun startGame() {
        // Logic to initialize a new chess game
        println("Starting game between ${player1.username} and ${player2.username}")

        // Inform both players that the game has started
        player1.websocketSession.send("Game started! You are playing against ${player2.username} as ${player2.color}.")
        player2.websocketSession.send("Game started! You are playing against ${player1.username} as ${player1.color}.")

        chessGame = ChessGame()
    }
    fun handleMove(move: Move) : Boolean {
        // deserialize move here, or make move a Data Class with 4 int
        // change the way to manage player turn
         return chessGame.makeMove(move.fromX, move.fromY, move.toX, move.toY)
    }

    fun getCurrentPlayer() : String {
        return chessGame.currentPlayer
    }

    fun printBoard() {
        for (i in 0..7) {
            for (j in 0..7) {
                print("${chessGame.board.board[i][j].piece}  ")
            }
            println()
        }
    }

    fun getBoardRepresentation() : Array<Array<String>> {
        return Array(8) { row ->
            Array(8) { col ->
                val piece = chessGame.board.board[row][col].piece
                if (piece == null) {
                    ""  // No piece
                } else {
                    // Generate the representation "<PieceType letter><Color letter>"
                    val pieceLetter = when (piece) {
                        is King -> "K"
                        is Queen -> "Q"
                        is Rook -> "R"
                        is Bishop -> "B"
                        is Knight -> "N"
                        is Pawn -> "P"
                        else -> ""
                    }

                    val colorLetter = when (piece.color) {
                        "White" -> "W"
                        "Black" -> "B"
                        else -> ""
                    }

                    // Combine to form "<PieceType letter><Color letter>"
                    "$pieceLetter$colorLetter"
                }
            }
        }

    }
}

class Queue (private val gameManager: GameManager) {
    private val queue: MutableList<Player> = mutableListOf()

    // Add player to the queue
    suspend fun addPlayerToQueue(player: Player) {
        queue.add(player)
        println("Player ${player.username} added to the queue")

        // If two players are in the queue, start a new game
        if (queue.size >= 2) {
            val player1 = queue.removeAt(0)
            val player2 = queue.removeAt(0)
            // Randomly assign colors
            val players = listOf(player1, player2).shuffled()
            players[0].color = PlayerColor.WHITE
            players[1].color = PlayerColor.BLACK
            gameManager.startNewGame(player1, player2) // Start the game
        }
    }
}