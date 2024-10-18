package com.thomashuyghues.game

import com.thomashuyghues.model.data.Player

class GameManager {
    private val activeGames: MutableMap<String, Game> = mutableMapOf() // Map to hold active games by game ID or player ID

    // Start a new game and store it
    suspend fun startNewGame(player1: Player, player2: Player): Game {
        val game = Game(player1, player2)
        val gameId = generateGameId(player1, player2)
        activeGames[gameId] = game
        game.startGame() // Start the chess game
        println("Game started with ID: $gameId")
        return game
    }

    // Generate a unique game ID based on players' IDs
    private fun generateGameId(player1: Player, player2: Player): String {
        return "${player1.id}-${player2.id}"
    }

    // Retrieve a game by player ID or game ID
    fun getGameByPlayer(playerId: String): Game? {
        return activeGames.values.find { it.player1.id == playerId || it.player2.id == playerId }
    }

    // Optionally, you can add logic to remove games once they are finished
    fun removeGame(gameId: String) {
        activeGames.remove(gameId)
    }
}