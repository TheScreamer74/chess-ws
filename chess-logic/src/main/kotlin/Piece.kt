abstract class Piece(val color: String) {
    abstract fun possibleMoves(x: Int, y: Int, board: Array<Array<Square>>): List<Pair<Int, Int>>
    abstract fun copy(): Piece

    // Utility function to check if a position is within bounds of the chessboard
    fun isWithinBounds(x: Int, y: Int): Boolean {
        return x in 0..7 && y in 0..7
    }
}

class King(color: String) : Piece(color) {
    var isInCheck = false
    var hasAlreadyMoved = false
    override fun possibleMoves(x: Int, y: Int, board: Array<Array<Square>>): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()

        // All possible relative moves for the king
        val directions = listOf(
            Pair(-1, -1), Pair(-1, 0), Pair(-1, 1),
            Pair(0, -1), Pair(0, 1),
            Pair(1, -1), Pair(1, 0), Pair(1, 1)
        )

        for (direction in directions) {
            val newX = x + direction.first
            val newY = y + direction.second

            // Check if the move is within bounds
            if (isWithinBounds(newX, newY)) {
                val targetSquare = board[newX][newY]
                val targetPiece = targetSquare.piece

                // Add the move if the target square is empty or occupied by an opponent
                if (targetPiece == null || targetPiece.color != this.color) {
                    moves.add(Pair(newX, newY))
                }
            }
        }
        addCastlingMoves(x, y, board, moves)
        return moves
    }

    override fun copy(): Piece {
        return King(this.color)
    }

    // Add en passant move if applicable
    private fun addCastlingMoves(x: Int, y: Int, board: Array<Array<Square>>, moves: MutableList<Pair<Int, Int>>) {
        val direction = if (color == "White") 1 else -1  // Adjust direction for en passant capture //TODO Change function
        val opponentPawnRow = if (color == "White") 4 else 3  // Row where en passant can be triggered

        // En passant is only possible when the pawn is on the correct row
        if (x == opponentPawnRow) {
            // Check left and right for an en passant capture
            if (y > 0 && board[x][y - 1].piece is Pawn && board[x][y - 1].piece?.color != color) {
                val pawn = board[x][y - 1].piece as Pawn
                if (pawn.canBeCapturedEnPassant) {
                    moves.add(Pair(x + direction, y - 1))
                }
            }
            if (y < 7 && board[x][y + 1].piece is Pawn && board[x][y + 1].piece?.color != color) {
                val pawn = board[x][y + 1].piece as Pawn
                if (pawn.canBeCapturedEnPassant) {
                    moves.add(Pair(x + direction, y + 1))
                }
            }
        }
    }
}

class Pawn(color: String) : Piece(color) {
    var canBeCapturedEnPassant: Boolean = false

    override fun possibleMoves(x: Int, y: Int, board: Array<Array<Square>>): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()
        val direction = if (color == "White") 1 else -1  // White moves up, Black moves down

        // One square forward
        if (isWithinBounds(x + direction, y) && board[x + direction][y].piece == null) {
            moves.add(Pair(x + direction, y))
        }

        // Two squares forward (only if the pawn is in its initial position)
        val initialRow = if (color == "White") 1 else 6
        if (x == initialRow && board[x + direction][y].piece == null && board[x + 2 * direction][y].piece == null) {
            moves.add(Pair(x + 2 * direction, y))
        }

        // Capturing diagonally
        if (isWithinBounds(x + direction, y - 1) && board[x + direction][y - 1].piece != null &&
            board[x + direction][y - 1].piece?.color != this.color) {
            moves.add(Pair(x + direction, y - 1))
        }
        if (isWithinBounds(x + direction, y + 1) && board[x + direction][y + 1].piece != null &&
            board[x + direction][y + 1]

                .piece?.color != this.color) {
            moves.add(Pair(x + direction, y + 1))
        }
        addEnPassantMoves(x, y, board, moves)
        return moves
    }

    override fun copy(): Piece {
        return Pawn(this.color)
    }

    // Add en passant move if applicable
    private fun addEnPassantMoves(x: Int, y: Int, board: Array<Array<Square>>, moves: MutableList<Pair<Int, Int>>) {
        val direction = if (color == "White") 1 else -1  // Adjust direction for en passant capture
        val opponentPawnRow = if (color == "White") 4 else 3  // Row where en passant can be triggered

        // En passant is only possible when the pawn is on the correct row
        if (x == opponentPawnRow) {
            // Check left and right for an en passant capture
            if (y > 0 && board[x][y - 1].piece is Pawn && board[x][y - 1].piece?.color != color) {
                val pawn = board[x][y - 1].piece as Pawn
                if (pawn.canBeCapturedEnPassant) {
                    moves.add(Pair(x + direction, y - 1))
                }
            }
            if (y < 7 && board[x][y + 1].piece is Pawn && board[x][y + 1].piece?.color != color) {
                val pawn = board[x][y + 1].piece as Pawn
                if (pawn.canBeCapturedEnPassant) {
                    moves.add(Pair(x + direction, y + 1))
                }
            }
        }
    }
}

class Queen(color: String) : Piece(color) {

    override fun possibleMoves(x: Int, y: Int, board: Array<Array<Square>>): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()

        // Add all possible moves in the horizontal, vertical, and diagonal directions
        addLinearMoves(x, y, board, moves) // Horizontal and vertical moves
        addDiagonalMoves(x, y, board, moves) // Diagonal moves

        return moves
    }

    override fun copy(): Piece {
        return Queen(this.color)
    }

    // Helper function to add all horizontal and vertical moves
    private fun addLinearMoves(x: Int, y: Int, board: Array<Array<Square>>, moves: MutableList<Pair<Int, Int>>) {
        // Move left
        for (i in (y - 1) downTo 0) {
            if (addMoveOrStop(x, i, board, moves)) break
        }
        // Move right
        for (i in (y + 1)..7) {
            if (addMoveOrStop(x, i, board, moves)) break
        }
        // Move up
        for (i in (x - 1) downTo 0) {
            if (addMoveOrStop(i, y, board, moves)) break
        }
        // Move down
        for (i in (x + 1)..7) {
            if (addMoveOrStop(i, y, board, moves)) break
        }
    }

    // Helper function to add all diagonal moves
    private fun addDiagonalMoves(x: Int, y: Int, board: Array<Array<Square>>, moves: MutableList<Pair<Int, Int>>) {
        // Top-left diagonal
        var i = x - 1
        var j = y - 1
        while (i >= 0 && j >= 0) {
            if (addMoveOrStop(i, j, board, moves)) break
            i--
            j--
        }

        // Top-right diagonal
        i = x - 1
        j = y + 1
        while (i >= 0 && j <= 7) {
            if (addMoveOrStop(i, j, board, moves)) break
            i--
            j++
        }

        // Bottom-left diagonal
        i = x + 1
        j = y - 1
        while (i <= 7 && j >= 0) {
            if (addMoveOrStop(i, j, board, moves)) break
            i++
            j--
        }

        // Bottom-right diagonal
        i = x + 1
        j = y + 1
        while (i <= 7 && j <= 7) {
            if (addMoveOrStop(i, j, board, moves)) break
            i++
            j++
        }
    }

    // Helper function to add a move or stop if the path is blocked
    private fun addMoveOrStop(
        x: Int,
        y: Int,
        board: Array<Array<Square>>,
        moves: MutableList<Pair<Int, Int>>
    ): Boolean {
        val square = board[x][y]
        val piece = square.piece
        if (piece == null) {
            moves.add(Pair(x, y))
            return false // Continue moving
        } else if (piece.color != this.color) {
            moves.add(Pair(x, y)) // Capture the opponent piece
            return true // Stop as the path is blocked
        }
        return true // Stop if there is a piece of the same color
    }
}

class Bishop(color: String) : Piece(color) {

    override fun possibleMoves(x: Int, y: Int, board: Array<Array<Square>>): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()

        // Add diagonal moves
        addDiagonalMoves(x, y, board, moves)

        return moves
    }

    override fun copy(): Piece {
        return Bishop(this.color)
    }

    // Helper function to add all diagonal moves
    private fun addDiagonalMoves(x: Int, y: Int, board: Array<Array<Square>>, moves: MutableList<Pair<Int, Int>>) {
        // Top-left diagonal
        var i = x - 1
        var j = y - 1
        while (i >= 0 && j >= 0) {
            if (addMoveOrStop(i, j, board, moves)) break
            i--
            j--
        }

        // Top-right diagonal
        i = x - 1
        j = y + 1
        while (i >= 0 && j <= 7) {
            if (addMoveOrStop(i, j, board, moves)) break
            i--
            j++
        }

        // Bottom-left diagonal
        i = x + 1
        j = y - 1
        while (i <= 7 && j >= 0) {
            if (addMoveOrStop(i, j, board, moves)) break
            i++
            j--
        }

        // Bottom-right diagonal
        i = x + 1
        j = y + 1
        while (i <= 7 && j <= 7) {
            if (addMoveOrStop(i, j, board, moves)) break
            i++
            j++
        }
    }

    // Helper function to add a move or stop if the path is blocked
    private fun addMoveOrStop(
        x: Int,
        y: Int,
        board: Array<Array<Square>>,
        moves: MutableList<Pair<Int, Int>>
    ): Boolean {
        val square = board[x][y]
        val piece = square.piece
        if (piece == null) {
            moves.add(Pair(x, y))
            return false // Continue moving
        } else if (piece.color != this.color) {
            moves.add(Pair(x, y)) // Capture the opponent piece
            return true // Stop as the path is blocked
        }
        return true // Stop if there is a piece of the same color
    }
}

class Knight(color: String) : Piece(color) {

    override fun possibleMoves(x: Int, y: Int, board: Array<Array<Square>>): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()

        // Define all possible knight moves (L-shape)
        val potentialMoves = listOf(
            Pair(x + 2, y + 1), Pair(x + 2, y - 1),
            Pair(x - 2, y + 1), Pair(x - 2, y - 1),
            Pair(x + 1, y + 2), Pair(x + 1, y - 2),
            Pair(x - 1, y + 2), Pair(x - 1, y - 2)
        )

        // Filter valid moves
        for (move in potentialMoves) {
            val (newX, newY) = move
            if (isWithinBounds(newX, newY) && (board[newX][newY].piece == null || board[newX][newY].piece?.color != this.color)) {
                moves.add(move)
            }
        }

        return moves
    }

    override fun copy(): Piece {
        return Knight(this.color)
    }
}


class Rook(color: String) : Piece(color) {
    var hasAlreadyMoved = false

    override fun possibleMoves(x: Int, y: Int, board: Array<Array<Square>>): List<Pair<Int, Int>> {
        val moves = mutableListOf<Pair<Int, Int>>()

        // Add horizontal and vertical moves
        addLinearMoves(x, y, board, moves)

        return moves
    }

    override fun copy(): Piece {
        return Rook(this.color)
    }

    // Helper function to add all horizontal and vertical moves
    private fun addLinearMoves(x: Int, y: Int, board: Array<Array<Square>>, moves: MutableList<Pair<Int, Int>>) {
        // Move left
        for (i in (y - 1) downTo 0) {
            if (addMoveOrStop(x, i, board, moves)) break
        }
        // Move right
        for (i in (y + 1)..7) {
            if (addMoveOrStop(x, i, board, moves)) break
        }
        // Move up
        for (i in (x - 1) downTo 0) {
            if (addMoveOrStop(i, y, board, moves)) break
        }
        // Move down
        for (i in (x + 1)..7) {
            if (addMoveOrStop(i, y, board, moves)) break
        }
    }

    // Helper function to add a move or stop if the path is blocked
    private fun addMoveOrStop(
        x: Int,
        y: Int,
        board: Array<Array<Square>>,
        moves: MutableList<Pair<Int, Int>>
    ): Boolean {
        val square = board[x][y]
        val piece = square.piece
        if (piece == null) {
            moves.add(Pair(x, y))
            return false // Continue moving
        } else if (piece.color != this.color) {
            moves.add(Pair(x, y)) // Capture the opponent piece
            return true // Stop as the path is blocked
        }
        return true // Stop if there is a piece of the same color
    }
}
