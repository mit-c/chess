
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import java.lang.reflect.*;
import java.lang.reflect.Field;

import java.awt.*;
class Piece {
    int x;
    int y;
    boolean white;
    String name;
    String chessPos;

    boolean dragged = false;
    Point mousePos = new Point(-1,-1);

    Piece(int x, int y, boolean white) {
        this.x = x;
        this.y = y;
        this.white = white;
        this.chessPos = indexToChessPos(x, y);
        

    }


    public Image getImg() {
        Image imageOut = null;
        Class<?> pieceClass = this.getClass();
        
        Field imgField;
        try {
            if(this.white)
            {
                imgField = pieceClass.getField("whiteImg");
            } else {
                imgField = pieceClass.getField("blackImg");
            }
            
        } catch (NoSuchFieldException nsfe) {
            throw new RuntimeException(nsfe);
        }

        try {
            imageOut = (Image) imgField.get(pieceClass);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return imageOut;


        
   }

    public boolean isEqual(Piece piece) {
        // Can make this more strict but not really necessary.
        if (piece.x == this.x) {
            if (piece.y == this.y) {
                return true;
            }
        }
        return false;
    }

    public static String indexToChessPos(int xIndex, int yIndex) {
        // A == 65 in ascii
        // We want x=0 => horz = "A"
        String horz = Character.toString((char) xIndex + 65);
        String vert = Integer.toString(yIndex + 1);
        return horz + vert;
    }

    public static int[] chessPosToIndex(String _chessPos) {
        Character letter = _chessPos.charAt(0);
        Character number = _chessPos.charAt(1);
        int x = ((int) letter) - 65;
        int y = ((int) number) - 65;
        int[] pos = { x, y };
        return pos;
    }

    public int[] getPosition() {
        int[] pos = { x, y };
        return pos;
    }

    public ArrayList<int[]> findMoves() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        System.out.println("we are in bad function");
        int[] moveNowhere = { this.x, this.y };
        moves.add(moveNowhere);
        return moves;
    }

    public ArrayList<int[]> getMovePathVert(Move move) {
        ArrayList<int[]> movePath = new ArrayList<int[]>();
        int[] start = { move.piece.x, move.piece.y };

        int[] end = move.moveTo;
        int xDiff = end[0] - start[0];
        int yDiff = end[1] - start[1];
        if (xDiff == 0) {
            // vertical movement
            int change;
            if (yDiff > 0) {

                change = 1;

            } else {

                change = -1;
            }
            // non-inclusive because we don't care if there is a piece where we are moving
            // to (we have checked that already).
            // We start at start + change because we don't want to include no move.
            for (int y = start[1] + change; y != end[1]; y += change) {
                movePath.add(new int[] { start[0], y });
            }

        } else if (yDiff == 0) {

            // horizontal movement

            int change;
            if (xDiff > 0) {
                change = 1;

            } else {
                change = -1;
            }

            for (int x = start[0] + change; x != end[0]; x += change) {

                movePath.add(new int[] { x, start[1] });
            }

        }

        return movePath;
    }

    public Piece copyPiece() {
        Piece newPiece = new Piece(this.x, this.y, this.white);
        return newPiece;
    }

    public ArrayList<int[]> getMovePathDiag(Move move) {
        ArrayList<int[]> movePath = new ArrayList<int[]>();
        int[] start = { move.piece.x, move.piece.y };

        int[] end = move.moveTo;
        int xDiff = end[0] - start[0];
        int yDiff = end[1] - start[1];
        int xChange, yChange;
        if (xDiff > 0) {
            xChange = 1;
        } else {
            xChange = -1;
        }

        if (yDiff > 0) {
            yChange = 1;
        } else {
            yChange = -1;
        }

        if (Math.abs(xDiff) == 1) {
            return movePath;
        }
        int xPos = move.piece.x + xChange;
        int yPos = move.piece.y + yChange;
        int[] startPos = { xPos, yPos };
        movePath.add(startPos);

        for (int i = 1; i < Math.abs(xDiff); i++) {
            xPos += xChange;
            yPos += yChange;
            int[] currentPos = { xPos, yPos };
            movePath.add(currentPos);
        }

        return movePath;

    }

    public ArrayList<int[]> getMovePath(Move move) {
        ArrayList<int[]> movePath = new ArrayList<int[]>();

        if (move.piece.name == "Knight") {
            throw new Error("should not get Knights in getMovePath as they have no move path.");
        }

        int[] start = { move.piece.x, move.piece.y };
        int[] end = move.moveTo;
        int xDiff = end[0] - start[0];
        int yDiff = end[1] - start[1];

        if (xDiff == 0 || yDiff == 0) // if there is no change in either x or y the move is horizontal.
        {

            movePath.addAll(getMovePathVert(move));
        } else {
            movePath.addAll(getMovePathDiag(move));
        }

        return movePath;
    }

    public void printInt2(int[]... arrays) {
        for (int[] array : arrays) {
            System.out.println(" x: " + array[0] + " y:" + array[1]);
        }
    }

    public ArrayList<int[]> moveDiag() {
        ArrayList<int[]> possibleMoves = new ArrayList<int[]>();

        // if we are at point (5,0) we should get 5
        int upLeft = Math.min(7 - this.y, this.x); // x- y+
        int upRight = Math.min(7 - this.y, 7 - this.x);// x+ y+
        int downLeft = Math.min(this.y, this.x); // x- y-
        int downRight = Math.min(this.y, 7 - this.x);// x+ y-

        for (int diag = 1; diag <= upLeft; diag++) {
            int[] move = { this.x - diag, this.y + diag }; // - +
            possibleMoves.add(move);
        }

        for (int diag = 1; diag <= upRight; diag++) {
            int[] move = { this.x + diag, this.y + diag }; // + +
            possibleMoves.add(move);
        }

        for (int diag = 1; diag <= downLeft; diag++) {

            int[] move = { this.x - diag, this.y - diag }; // - -
            possibleMoves.add(move);
        }

        for (int diag = 1; diag <= downRight; diag++) {
            int[] move = { this.x + diag, this.y - diag }; // + -

            possibleMoves.add(move);
        }
        return possibleMoves;

    }

    public ArrayList<int[]> moveVert() {

    
        // This function outputs all possible horizontal and vertical moves
        // from this.x, this.y
        // The error is almost definitely here
        //
        // I fix the y coord

        ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
        for (int column = 0; column <= 7; column++) {
            if (column == this.x) {
                continue;
            }
            int[] move = { column, this.y };
            possibleMoves.add(move);
        }
        // rows
        for (int row = 0; row <= 7; row++) {
            if (row == this.y) {
                continue;
            }
            int[] move = { this.x, row };
            possibleMoves.add(move);
        }

        return possibleMoves;
    }

    public static Image loadWhiteImage(String pieceName) {
        Image whiteImg=null;
        try {
            whiteImg = ImageIO.read(new File("chessSprites/" + pieceName + "_White.png"));
            
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return whiteImg;

    }

    public static Image loadBlackImage(String pieceName) {
        Image blackImg=null;
        try {
            blackImg = ImageIO.read(new File("chessSprites/" + pieceName + "_White.png"));
            
        } catch(IOException ex) {
            ex.printStackTrace();
        }
        return blackImg;

    }
    
}


class King extends Piece {

    
    public static Image whiteImg = loadWhiteImage("King");  
    public static Image blackImg = loadBlackImage("King");  

    King(int x, int y, boolean white) {
        super(x, y, white);
        this.name = "King";
    
        
    }

 

    public ArrayList<int[]> findMoves() {

        ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
        for (int xDiff = -1; xDiff <= 1; xDiff++) {
            for (int yDiff = -1; yDiff <= 1; yDiff++) {
                if (xDiff == 0 && yDiff == 0) // ignore no move.
                {
                    continue;
                }
                if (x + xDiff > 7 || x + xDiff < 0 || y + yDiff > 7 || y + yDiff < 0) {
                    continue;
                }
                int[] move = { x + xDiff, y + yDiff };
                possibleMoves.add(move);

            }
        }
        if (this.white && y == 0 && x == 3) {
            int whiteCastleLeft[] = { 1, 0 };
            int whiteCastleRight[] = { 5, 0 };
            possibleMoves.add(whiteCastleLeft);
            possibleMoves.add(whiteCastleRight);

        } else if (!this.white && y == 7 && x == 3) {
            int blackCastleLeft[] = { 1, 7 };
            int blackCastleRight[] = { 5, 7 };
            possibleMoves.add(blackCastleLeft);
            possibleMoves.add(blackCastleRight);
        }

        return possibleMoves;
    }

    public Piece copyPiece() {
        Piece newPiece = new King(this.x, this.y,  this.white);
        return newPiece;
    }

}

class Queen extends Piece {
    public static Image whiteImg = loadWhiteImage("Queen");  
    public static Image blackImg = loadBlackImage("Queen");  
    Queen(int x, int y, boolean white) {
        super(x, y, white);
        name = "Queen";
    }

    public ArrayList<int[]> findMoves() {
        // this is bugged
        ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
        // System.out.print("xstart: " + x + " ystart" + y);
        possibleMoves.addAll(moveVert());
        possibleMoves.addAll(moveDiag());
        return possibleMoves;

    }

    public Piece copyPiece() {
        Piece newPiece = new Queen(this.x, this.y, this.white);
        
        return newPiece;
    }
}

class Knight extends Piece {
    public static Image whiteImg = loadWhiteImage("Knight");  
    public static Image blackImg = loadBlackImage("Knight");  
    Knight(int x, int y, boolean white) {
        super(x, y, white);
        name = "Knight";

        
    }

    public ArrayList<int[]> findMoves() {
        ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
        // Knight movement is +/- 2 +/- 1
        // Could probably do this more nicely but it works.
        int[][] moves = new int[8][2];
        moves[0] = new int[] { x - 1, y + 2 };
        moves[1] = new int[] { x + 1, y + 2 };
        moves[2] = new int[] { x + 2, y + 1 };
        moves[3] = new int[] { x + 2, y - 1 };
        moves[4] = new int[] { x - 1, y - 2 };
        moves[5] = new int[] { x + 1, y - 2 };
        moves[6] = new int[] { x - 2, y + 1 };
        moves[7] = new int[] { x - 2, y - 1 };

        for (int[] move : moves) {
            if (move[0] >= 0 && move[1] >= 0 && move[0] <= 7 && move[1] <= 7) {
                possibleMoves.add(move);
            }
        }
        return possibleMoves;
    }

    public Piece copyPiece() {
        Piece newPiece = new Knight(this.x, this.y, this.white);
        
        return newPiece;
    }
}

class Bishop extends Piece {
    public static Image whiteImg = loadWhiteImage("Bishop");  
    public static Image blackImg = loadBlackImage("Bishop");  
    Bishop(int x, int y, boolean white) {
        super(x, y, white);
        name = "Bishop";

    }

    public ArrayList<int[]> findMoves() {
        ArrayList<int[]> possibleMoves = this.moveDiag();
        return possibleMoves;
    }

    public Piece copyPiece() {
        Piece newPiece = new Bishop(this.x, this.y, this.white);
      
        return newPiece;
    }
}

class Rook extends Piece {
    boolean hasMoved;
    public static Image whiteImg = loadWhiteImage("Rook");  
    public static Image blackImg = loadBlackImage("Rook");  

    Rook(int x, int y, boolean white) {
        super(x, y, white);
        name = "Rook";
 

    }

    public ArrayList<int[]> findMoves() {
        ArrayList<int[]> possibleMoves = this.moveVert();

        return possibleMoves;
    }

    public Piece copyPiece() {
        Piece newPiece = new Rook(this.x, this.y, this.white);
     
        return newPiece;
    }

}

class Pawn extends Piece {
    boolean enPassantThisTurn = false;
    boolean enPassantStillPossible = true;
    public static Image whiteImg = loadWhiteImage("Pawn");  
    public static Image blackImg = loadBlackImage("Pawn");  
    Pawn(int x, int y, boolean white) {
        super(x, y, white);
        name = "Pawn";


    }

    public Piece copyPiece() {
        Piece newPiece = new Pawn(this.x, this.y, this.white);
     
        return newPiece;
    }

    public boolean canMoveTwo() {
        boolean canMoveTwo = false;
        if (this.white) {
            if (this.y == 1) {
                canMoveTwo = true;
            }
        } else {
            if (this.y == 6) {
                canMoveTwo = true;
            }
        }
        return canMoveTwo;
    }

    public ArrayList<int[]> findMoves() {
        ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
        int sign;
        if (this.white) {
            sign = 1;
        } else {
            sign = -1;
        }
        if (canMoveTwo()) {
            possibleMoves.add(new int[] { this.x, this.y + 2 * sign });
        }
        possibleMoves.add(new int[] { this.x, this.y + 1 * sign });
        possibleMoves.add(new int[] { this.x - 1, this.y + 1 * sign });
        possibleMoves.add(new int[] { this.x + 1, this.y + 1 * sign });
        ArrayList<int[]> outMoves = new ArrayList<int[]>();
        for (int[] moveTo : possibleMoves) {
            if (moveTo[0] >= 0 && moveTo[1] >= 0 && moveTo[0] <= 7 && moveTo[1] <= 7) {
                outMoves.add(moveTo);
            }
        }

        return outMoves;
    }
}

class Helper {
    public static String multiplyString(String str, int mult) {
        int N = str.length() * mult;
        if (mult == 0) {
            return " ";
        } else if (mult < 0) {
            throw new IllegalAccessError("mult must be non-negative");
        }
        StringBuffer outputBuffer = new StringBuffer(N);
        for (int i = 0; i < N; i++) {
            outputBuffer.append(str);
        }
        return outputBuffer.toString();
    }
}