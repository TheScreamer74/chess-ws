data class Square(var piece: Piece?, var color: String) {
    // Copy function to create a deep copy of the square
    fun copy(): Square {
        return Square(piece?.copy(), color) // Create a copy of the piece and keep the color
    }
}


