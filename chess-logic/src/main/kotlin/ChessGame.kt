import kotlin.math.abs

class ChessGame {
    val board = Board()
    var currentPlayer = "White"
    private var lastMove: Pair<Int, Int>? = null  // To track the last move made for en passant

    fun makeMove(fromX: Int, fromY: Int, toX: Int, toY: Int): Map<Boolean, Board> {
        val piece = board.getPiece(fromX, fromY)

        // Ensure it's the current player's turn
        if (piece == null || piece.color != currentPlayer) {
            println("It's $currentPlayer's turn.")
            return mapOf(false to board)
        }

        // Get valid moves for the piece
        val possibleMoves = piece.possibleMoves(fromX, fromY, board.board)

        // if player is currently in check filter possibles moves
        if (isCheck(currentPlayer, board)) {
            val validMoves = possibleMoves.filter { moves ->
                val simulatedBoard = board.clone()
                simulatedBoard.movePiece(fromX, fromY, moves.first, moves.second)
                !isCheck(currentPlayer, simulatedBoard)
            }

            if (!validMoves.contains(Pair(toX, toY))) {
                println("Invalid move. You are in check, and this move doesn't remove the check.")
                return mapOf(false to board)
            }
        } else {
            if (!possibleMoves.contains(Pair(toX, toY))) {
                println("Invalid move.")
                return mapOf(false to board)
            }
        }

        // Handle capturing of an opponent's piece
        val targetPiece = board.getPiece(toX, toY)
        if (targetPiece != null && targetPiece.color != currentPlayer) {
            println("${targetPiece::class.simpleName} of ${targetPiece.color} captured.")
        }

        // Handle en passant capture
        if (piece is Pawn && fromY != toY && board.getPiece(toX, toY) == null) {
            if (lastMove != null && abs(fromX - lastMove!!.first) == 0) {
                // Check for valid en passant capture
                board.movePiece(fromX, fromY, toX, toY, true)
                println("En passant capture!")
            }
        } else {
            // Move the piece Normally
            board.movePiece(fromX, fromY, toX, toY)
        }

        // Check if the opponent's king is in check after the move
        val opponentColor = if (currentPlayer == "White") "Black" else "White"
        if (isCheck(opponentColor, board)) {
            // Find the opponent's king position and highlight it
            val kingPosition = findKingPosition(opponentColor)
            if (kingPosition != null) {
                board.board[kingPosition.first][kingPosition.second].color = "red"
                (board.board[kingPosition.first][kingPosition.second].piece as King).isInCheck = true
            }
            if (isCheckmate(opponentColor)) {
                println("Checkmate! $currentPlayer wins!")
                return mapOf(true to board)
            } else {
                println("$opponentColor is in check!")
            }
        } else {
            // Find the current player's king position and remove the chess state
            val kingPosition = findKingPosition(currentPlayer)
            if (kingPosition != null) {
                board.board[kingPosition.first][kingPosition.second].color = "Gray"
                (board.board[kingPosition.first][kingPosition.second].piece as King).isInCheck = false
            }
        }

        // Check if the move results in pawn promotion
       /* if (piece is Pawn && (toX == 0 || toX == 7)) {
            promotePawn(toX, toY)
        }*/

        when(piece) {
            // Handle en passant eligibility (for two-square pawn advance)
            is Pawn -> {
                if (abs(fromX - toX) == 2) {
                    piece.canBeCapturedEnPassant = true  // Enable en passant capture for this pawn
                    lastMove = Pair(toX, toY)  // Track the current move for en passant
                }
            }
            // Handle Castling eligibility
            is King -> {
                val square = board.getPiece(toX, toY)
                if (square is Rook && square.hasAlreadyMoved && square.color == currentPlayer)
                    lastMove = Pair(toX, toY) // Track the current move for castling
            }
            else -> {
                lastMove = null  // Reset last move if it's not a two-square pawn move
            }
        }


        // Switch turn to the other player
        switchPlayer()
        return mapOf(true to board)
    }

    // Function to handle pawn promotion
    /*private fun promotePawn(row: Int, col: Int) {
        val pawn = board.getPiece(row, col) as Pawn

        // Call UI to get promotion choice (this should be handled by ChessApp UI in a real app)
       // val selectedPromotion = ChessApp.getPromotionChoice()  // TODO ADD A NOTIFICATION FOR OBSERVER

        // Replace the pawn with the selected promotion piece
        board.board[row][col].piece = when (selectedPromotion) {
            "Queen" -> Queen(pawn.color)
            "Rook" -> Rook(pawn.color)
            "Bishop" -> Bishop(pawn.color)
            "Knight" -> Knight(pawn.color)
            else -> Queen(pawn.color)  // Default to Queen if no valid selection
        }
    }*/

    private fun switchPlayer() {
        currentPlayer = if (currentPlayer == "White") "Black" else "White"
    }

    // Function to check if the opponent's king is in check
    fun isCheck(opponentColor: String, board: Board): Boolean {
        val kingPosition = findKingPosition(opponentColor)
        if (kingPosition != null) {
            // Check if any of the current player's pieces can attack the opponent's king
            val currentPlayerColor = if (opponentColor == "White") "Black" else "White"
            for (i in 0..7) {
                for (j in 0..7) {
                    val piece = board.getPiece(i, j)
                    if (piece != null && piece.color == currentPlayerColor) {
                        val possibleMoves = piece.possibleMoves(i, j, board.board)
                        if (possibleMoves.contains(kingPosition)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    // Function to check if the opponent is in checkmate
    fun isCheckmate(opponentColor: String): Boolean {
        // Check if any move can get the opponent's king out of check
        for (i in 0..7) {
            for (j in 0..7) {
                val piece = board.getPiece(i, j)
                if (piece != null && piece.color == opponentColor) {
                    val possibleMoves = piece.possibleMoves(i, j, board.board)
                    for (move in possibleMoves) {
                        // Simulate the move and check if it removes the check
                        val tempBoard = board.clone()
                        tempBoard.movePiece(i, j, move.first, move.second)
                        if (!isCheckAfterMove(tempBoard, opponentColor)) {
                            return false  // If there's a move that gets out of check, it's not checkmate
                        }
                    }
                }
            }
        }
        return true  // No moves can get out of check, it's checkmate
    }

    // Helper function to find the king's position on the board
    private fun findKingPosition(color: String): Pair<Int, Int>? {
        for (i in 0..7) {
            for (j in 0..7) {
                val piece = board.getPiece(i, j)
                if (piece is King && piece.color == color) {
                    return Pair(i, j)
                }
            }
        }
        return null  // King not found (shouldn't happen in a normal game)
    }


    // Function to check if the opponent's king is in check after a move
    fun isCheckAfterMove(tempBoard: Board, opponentColor: String): Boolean {
        val kingPosition = findKingPosition(opponentColor)
        if (kingPosition != null) {
            val currentPlayerColor = if (opponentColor == "White") "Black" else "White"
            for (i in 0..7) {
                for (j in 0..7) {
                    val piece = tempBoard.getPiece(i, j)
                    if (piece != null && piece.color == currentPlayerColor) {
                        val possibleMoves = piece.possibleMoves(i, j, tempBoard.board)
                        if (possibleMoves.contains(kingPosition)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    fun isSquareEmpty(x: Int, y: Int) : Boolean {
        return board.getPiece(x, y) == null
    }
}