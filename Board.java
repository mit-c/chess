
import java.awt.Image;
import java.util.ArrayList;

class Board {
    // boardArray is oriented so [0][0] represents top left [][]
    String[][] boardArray = new String[8][8]; // Visual aid
    ArrayList<Piece> whitePieces = new ArrayList<Piece>();
    ArrayList<Piece> blackPieces = new ArrayList<Piece>();

    ArrayList<ArrayList<Piece>> whitePosHistory = new ArrayList<ArrayList<Piece>>();
    ArrayList<ArrayList<Piece>> blackPosHistory = new ArrayList<ArrayList<Piece>>();

    // Each piece will have it's own methods for moving etc but these methods will
    // be called form the board class.
    // Anything which affects the game should happen in this class, including all
    // rules e.g. castling.
    Board() {
        // White pieces L to R (not pawns).
        int yPos = 7;
        int yPosPawn = 6;
        boolean bool = false;
        while (true) {
            initPiece(new Rook(0, yPos, bool));
            initPiece(new Knight(1, yPos, bool));
            initPiece(new Bishop(2, yPos, bool));
            initPiece(new Queen(4, yPos, bool));
            initPiece(new King(3, yPos, bool));
            initPiece(new Bishop(5, yPos, bool));
            initPiece(new Knight(6, yPos, bool));
            initPiece(new Rook(7, yPos, bool));
           
            // White pawns
            for (int i = 0; i < 8; i++) {

                initPiece(new Pawn(i, yPosPawn, bool));
            }
            if (bool) {
                break;
            }

            bool = true;
            yPos = 0;
            yPosPawn = 1;

        }
        this.addChessPos();

        whitePosHistory.add(copyPieceArray(whitePieces));
        blackPosHistory.add(copyPieceArray(blackPieces));
    }

    public void addChessPos() {
        for (Piece piece : whitePieces) {
            piece.chessPos = Piece.indexToChessPos(piece.x, piece.y);
        }
        for (Piece piece : blackPieces) {
            piece.chessPos = Piece.indexToChessPos(piece.x, piece.y);
        }
    }

    public ArrayList<Piece> copyPieceArray(ArrayList<Piece> pieces) {
        ArrayList<Piece> copiedArray = new ArrayList<Piece>();

        for (Piece piece : pieces) {
            copiedArray.add(piece.copyPiece());
        }
        return copiedArray;
    }

    public ArrayList<ArrayList<Piece>> copyPieceHistory(ArrayList<ArrayList<Piece>> pieceHistory) {
        ArrayList<ArrayList<Piece>> copiedHistory = new ArrayList<ArrayList<Piece>>();
        for (ArrayList<Piece> pieceArray : pieceHistory) {
            ArrayList<Piece> copiedArray = copyPieceArray(pieceArray);
            copiedHistory.add(copiedArray);
        }
        return copiedHistory;
    }

    public int[] getKingPos(boolean isWhite) {
        ArrayList<Piece> pieces = getPieceColour(isWhite);

        for (Piece piece : pieces) {
            if (piece.name == "King") {
                return piece.getPosition();
            }
        }
        throw new IllegalAccessError("King not here");
    }

    public Piece findPiece(Piece piece) {
        return piece;
    }

    public ArrayList<Piece> getPieceColour(boolean white) {
        ArrayList<Piece> pieces;

        if (white) {
            pieces = whitePieces;
        } else {
            pieces = blackPieces;
        }

        return pieces;
    }

    public void addToHistory() {

        whitePosHistory.add(copyPieceArray(whitePieces));
        blackPosHistory.add(copyPieceArray(blackPieces));
    }

    public Move findSimilarMove(Move move) {
        // only useful if copying board.
        ArrayList<Piece> pieces = getPieceColour(move.piece.white);
        for (Piece piece : pieces) {
            if (move.piece.x == piece.x && move.piece.y == piece.y) {
                return new Move(piece, move.moveTo, move.takePiece); // This move will now affect copyBoard
            }
        }
        throw new IllegalStateException("bad");

    }

    public void buildBoardArray() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                boardArray[x][y] = null;
            }
        }
        for (Piece piece : whitePieces) {
            boardArray[piece.x][piece.y] = "White " + piece.name;
        }

        for (Piece piece : blackPieces) {
            boardArray[piece.x][piece.y] = "Black " + piece.name;
        }
    }

    public Board copyBoard() {
        Board gameCopy = new Board();
        gameCopy.whitePieces = copyPieceArray(this.whitePieces);
        gameCopy.blackPieces = copyPieceArray(this.blackPieces);

        gameCopy.blackPosHistory = copyPieceHistory(this.blackPosHistory);
        gameCopy.whitePosHistory = copyPieceHistory(this.whitePosHistory);

        gameCopy.buildBoardArray();
        return gameCopy;
    }

    public King findKing(boolean white) {
        ArrayList<Piece> pieces = getPieceColour(white);
        for (Piece piece : pieces) {

            if (piece instanceof King) {
                King king = (King) piece;
                return king;
            }
        }
        throw new IllegalAccessError("either findKing isn't working or King doesn't exist");

    }

    public void showPieces() {
        for (Piece piece : whitePieces) {
            System.out.println("pos: " + piece.chessPos + " name: " + piece.name + " " + piece.white);
        }
        for (Piece piece : blackPieces) {
            System.out.println("pos: " + piece.chessPos + " name: " + piece.name + " " + piece.white);
        }
    }

    public void showBoard() {
        String line = Helper.multiplyString("*", 153);
        System.out.print(line);
        String letters = "\n" + "*" + "\t" + "\t" + "     A    " + "\t" + "     B    " + "\t" + "     C    " + "\t"
                + "     D    " + "\t" + "     E    " + "\t" + "     F    " + "\t" + "     G    " + "\t" + "     H    "
                + "\t" + "\t" + "*";
        System.out.print(letters);
        System.out.print("\n");
        for (int row = 0; row < boardArray.length; row++) {
            System.out.print("*" + "\t" + Integer.toString(row + 1) + "\t");
            for (int column = 0; column < boardArray.length; column++) {
                if (boardArray[column][row] == null) {
                    System.out.print("     x    " + "\t"); // Spaces for White Pawn.
                } else {
                    System.out.print(boardArray[column][row] + "\t");
                }
            }
            System.out.print(Integer.toString(row + 1) + "\t" + "*");
            if (row != boardArray.length - 1) {
                System.out.println();
            }

        }
        System.out.print(letters);
        System.out.print("\n");
        System.out.print(line);
        System.out.print("\n");
    }

    private void initPiece(Piece piece) {
        String name = piece.name;
        if (piece.white) {
            this.whitePieces.add(piece);
            name = "White " + name;
        } else {
            this.blackPieces.add(piece);
            name = "Black " + name;
        }
        this.boardArray[piece.x][piece.y] = name;
    }

}
