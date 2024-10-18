class Board {

    // Board is now an 8x8 array of Square objects, each containing a piece and a color
    val board: Array<Array<Square>> = Array(8) { row ->
        Array(8) { col ->
            val color = if ((row + col) % 2 == 0) "White" else "Gray"
            Square(null, color)
        }
    }

    init {
        setupBoard()
    }

    // Sets up the initial position of pieces on the board
    private fun setupBoard() {

        // Set up white pieces
        board[0][7].piece = Rook("White")
        board[0][6].piece = Knight("White")
        board[0][5].piece = Bishop("White")
        board[0][4].piece = King("White")
        board[0][3].piece = Queen("White")
        board[0][2].piece = Bishop("White")
        board[0][1].piece = Knight("White")
        board[0][0].piece = Rook("White")

        // Set up black pieces
        board[7][7].piece = Rook("Black")
        board[7][6].piece = Knight("Black")
        board[7][5].piece = Bishop("Black")
        board[7][4].piece = King("Black")
        board[7][3].piece = Queen("Black")
        board[7][2].piece = Bishop("Black")
        board[7][1].piece = Knight("Black")
        board[7][0].piece = Rook("Black")

        // Set up pawns
        for (i in 0..7) {
            board[1][i].piece = Pawn("White")
            board[6][i].piece = Pawn("Black")
        }
    }
    // Check if a given position is within the bounds of the board
    // Function to get a piece from a specific square
    fun getPiece(x: Int, y: Int): Piece? {
        return board[x][y].piece
    }

    // Function to move a piece from one square to another
    fun movePiece(fromX: Int, fromY: Int, toX: Int, toY: Int, enPassant: Boolean = false, castling: Boolean = false): Boolean {
        val piece = getPiece(fromX, fromY)
        if (piece != null && isWithinBounds(toX, toY)) {
            if (castling) {
                val piece2 = getPiece(toX, toY)
                if (piece2 is Rook) {
                    if (!piece2.hasAlreadyMoved) {
                        board[fromX][fromY].piece = null // Clear the king original position
                        board[toX][toY].piece = null // Clear the rook original position
                        board[toX - 1][toY].piece = piece  // Move the king to the destination
                        board[fromX + 1][fromY].piece = piece2 // Move the rook to the destination
                    }
                }
            }
            board[toX][toY].piece = piece  // Move the piece to the destination
            board[fromX][fromY].piece = null  // Clear the original position
            if (enPassant) {
                // Handle en passant capture
                board[fromX][toY].piece = null  // Remove the opponent's pawn for en passant
                println("En passant capture!")
            }
            return true
        }
        return false
    }

    // Function to check if a square is within the board bounds
    fun isWithinBounds(x: Int, y: Int): Boolean {
        return x in 0..7 && y in 0..7
    }

    // Clone function to create a deep copy of the board
    fun clone(): Board {
        val newBoard = Board()
        for (i in 0..7) {
            for (j in 0..7) {
                val square = this.board[i][j]
                newBoard.board[i][j] = square.copy()// Assuming `Piece` has a `copy()` method
            }
        }
        return newBoard
    }
}