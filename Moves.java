


class Move {
    Piece piece;
    int[] moveTo;
    boolean takePiece; // this is a question

    Move(Piece piece, int[] moveTo, boolean takePiece) {
        this.piece = piece;
        this.moveTo = moveTo;
        this.takePiece = takePiece;
    }

    public void showMove() {
        String col;

        if (this.piece.white) {
            col = "White";
        } else {
            col = "Black";
        }
        System.out.println(col + " " + this.piece.name + " " + this.piece.chessPos + " -> "
                + Piece.indexToChessPos(moveTo[0], moveTo[1]));

    }

    public Move copyMove() {
        int[] newMoveTo = new int[] { moveTo[0], moveTo[1] };
        Move outMove = new Move(piece.copyPiece(), newMoveTo, takePiece);
        return outMove;
    }
}