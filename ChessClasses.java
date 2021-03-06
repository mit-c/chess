import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



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
        System.out.println(this.piece.white + " " + this.piece.name + " " + this.piece.x + " " + this.piece.y + " -> " + this.moveTo[0] + " "
                + this.moveTo[1]);

    }

    public Move copyMove() { 
        int[] newMoveTo = new int[]{moveTo[0],moveTo[1]};
        Move outMove = new Move(piece.copyPiece(), newMoveTo, takePiece);
        return outMove;
    }
}

class Board {
    // boardArray is oriented so [0][0] represents top left [][]
    String[][] boardArray = new String[8][8]; // Visual aid
    ArrayList<Piece> whitePieces = new ArrayList<Piece>();
    ArrayList<Piece> blackPieces = new ArrayList<Piece>();
    boolean whiteToMove = true;
    boolean whiteCastled = false;
    boolean blackCastled = false;
    boolean gameOver = false;
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
            initPiece(new Queen(3, yPos, bool));
            initPiece(new King(4, yPos, bool));
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

        whitePosHistory.add(copyPieceArray(whitePieces));
        blackPosHistory.add(copyPieceArray(blackPieces));
    }

    public void playGame() {
        int i = 0;
        // TODO: isKingInCheck sometimes the queen can take the King without check realised.
        Random random = new Random(552);
        showBoard();
        
        while (!gameOver) {
            i++;
            System.out.println("game loop: " + i);

            ArrayList<Move> allMoves = new ArrayList<Move>();
            // allMoves from this are just to same spot need to debug.
            allMoves = allAvailableMoves(whiteToMove); 

            
            String checkColour;
            if(whiteToMove){
                checkColour = "Black";
            } else {
                checkColour = "White";
            }
            System.out.println(allMoves.size());
            if(allMoves.size() == 0) 
            {
                /*
                ArrayList<Move> enemyMoves = allAvailableMoves(!whiteToMove);
                
                int[] kingPos = getKingPos(whiteToMove);
                // System.out.println("king pos: " + kingPos[0] + kingPos[1]);
                boolean checkMate = false;
                for (Move enemyMove : enemyMoves) {
                    if (enemyMove.moveTo[0] == kingPos[0] && enemyMove.moveTo[1] == kingPos[1]) {
                        checkMate = true;
                        
                    }
                } 
                */
                if(isKingInCheck(whiteToMove)) {
                    System.out.println("Checkmate for " + checkColour);
                } else {
                    System.out.println("Stalemate");
                }
                showBoard();
                break;
            }
            int randomIndex = random.nextInt(allMoves.size());
            Move move = allMoves.get(randomIndex);
            executeMove(move);
            showBoard();
  
            this.whiteToMove = !this.whiteToMove;
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

    public ArrayList<Move> allAvailableMoves(boolean white) {
        
        ArrayList<Move> moveList = new ArrayList<Move>();
        ArrayList<Piece> pieceList = getPieceColour(white);

        // error here

        for (Piece piece : pieceList) {
            // Queen never shows up if white != whiteToMove
            if(white != whiteToMove){
                System.out.println(piece.name);
            }
            ArrayList<Move> availableMoves = availableMoves(piece);
            for(Move move: availableMoves){ 
               // moves are bad in this function
            
                moveList.add(move);
               
            }
            // moves are being made but why is showBoard not working in playGame().
            // I think moves aren't being undone correctly.
            
        }

        return moveList;

    }

    public ArrayList<Move> availableMoves(Piece piece) {

        
        ArrayList<int[]> moveList = piece.findMoves();
        ArrayList<Move> possibleMoves = new ArrayList<Move>();

        for (int[] moveTo : moveList) {
            // We don't need to copy the piece we just need to be careful manipulating piece.
            Move candidateMove = new Move(piece, moveTo, false); 
            
            if (isMovePossible(candidateMove)) {
                possibleMoves.add(candidateMove);
                //System.out.println("c move below");;
                //candidateMove.showMove();
            }
        }

      
      
        return possibleMoves;
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
        
        if (white) // using this boolean instead of whiteToMove to be safer.
        {
            pieces = whitePieces;
        } else {
            pieces = blackPieces;
        }


        return pieces;
    }

    public void executeMove(Move move) {
       
        ArrayList<Piece> pieces;
        pieces = getPieceColour(!move.piece.white);
        boolean takePiece = false;
        Piece pieceTaken = new Piece(0, 0, false);
        for (Piece piece : pieces) {
            if (move.moveTo[0] == piece.x && move.moveTo[1] == piece.y) {
                takePiece = true;
                pieceTaken = piece;
                break;
            }
        }
        if (takePiece) {
            pieces.remove(pieceTaken);
        }
        move.piece.x = move.moveTo[0]; //
        move.piece.y = move.moveTo[1]; // 
        // Do I need to copy these -- shouldn't mattter too much.
        blackPosHistory.add(copyPieceArray(blackPieces)); // each time we execute a move we add it to history.
        whitePosHistory.add(copyPieceArray(whitePieces));
        buildBoardArray();
        
        return;
    }

    public void undoMove() {

        if (blackPosHistory.size() <= 1 || whitePosHistory.size() <= 1) {
            throw new IllegalArgumentException("PosHistory must be at least size 2 to undo a move");
        } else {

            blackPieces = copyPieceArray(blackPosHistory.get(blackPosHistory.size() - 2));
            whitePieces = copyPieceArray(whitePosHistory.get(blackPosHistory.size() - 2));
        }
        buildBoardArray();
        return;
    }

    public Move findSimilarMove(Move move) {
        // only useful if copying board.
        ArrayList<Piece> pieces= getPieceColour(move.piece.white);
        for(Piece piece: pieces) {
            if(move.piece.x == piece.x && move.piece.y == piece.y) {
                return new Move(piece, move.moveTo, move.takePiece); // This move will now affect copyBoard
            }
        }
        throw new IllegalStateException("bad");

    }

    public boolean doChecksPreventMove(Move move) {
        
        // Move newMove = move.copyMove();
        Board copyBoard = this.copyBoard();
        Move newMove = copyBoard.findSimilarMove(move); 
    
        copyBoard.executeMove(newMove); // This should add to the blackHistory and white history etc.
        boolean output = false;
      
        if(copyBoard.isKingInCheck(newMove.piece.white)) {
            // checks if after move king is being attacked.
            output = true;
            return output;
        }
        copyBoard.undoMove();
        return output;
    }


    public boolean isKingInCheck(boolean white) { 
        // Check if in the current board state the white / black king is in check.

        // 
    
        ArrayList<Move> enemyMoves = this.allAvailableMoves(!white);
        int[] kingPos = this.getKingPos(white);
        
        //System.out.println("king pos: " + kingPos[0] + " " + kingPos[1]);
        for (Move enemyMove : enemyMoves) {

            if (enemyMove.moveTo[0] == kingPos[0] && enemyMove.moveTo[1] == kingPos[1]) {
              
                return true;
            }
        }
        return false;
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
        // https://stackoverflow.com/questions/715650/how-to-clone-arraylist-and-also-clone-its-contents
        // Need to clone correctly otherwise will be changing objects.
        Board gameCopy = new Board();
        gameCopy.whitePieces = copyPieceArray(this.whitePieces);
        gameCopy.blackPieces = copyPieceArray(this.blackPieces);

        gameCopy.blackPosHistory = copyPieceHistory(this.blackPosHistory);
        gameCopy.whitePosHistory = copyPieceHistory(this.whitePosHistory);

  
        gameCopy.blackCastled = this.blackCastled;
        gameCopy.whiteCastled = this.whiteCastled;
        gameCopy.whiteToMove = this.whiteToMove;
        gameCopy.buildBoardArray();
        return gameCopy;

    }

    public boolean isMoveOntoOwnPiece(Move move) {
        ArrayList<Piece> pieces = getPieceColour(move.piece.white);
        for (Piece piece : pieces) {
            int[] piecePos = piece.getPosition();
            if (move.moveTo[0] == piecePos[0] && move.moveTo[1] == piecePos[1]) {
                return true;
            }
        }
        return false;
    }

    public boolean isPieceInbetween(Move move) {
        // We have diagonal case.
        // We have line case.
        // Knight case is easy.
        //
        if (move.piece.name == "Knight") {
            // System.out.println("Knight logic");
            return false;
        }
        ArrayList<Piece> allPieces = copyPieceArray(whitePieces); // need all pieces to be copy of white pieces.
        
        allPieces.addAll(copyPieceArray(blackPieces));
        // don't need to remove piece because all findMoves() functions do not 
        // Let's check these moves are okay - they are not.
        // System.out.println("**************************************************");
        ArrayList<int[]> path = move.piece.getMovePath(move);
    
        for (int[] pos : path) {
            for (Piece piece : allPieces) {
                if (pos[0] == piece.x && pos[1] == piece.y) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isEnemyPieceAt(Move move) {
        ArrayList<Piece> enemyPieces = getPieceColour(!move.piece.white);
        for (Piece piece : enemyPieces) {
            if (piece.x == move.moveTo[0] && piece.y == move.moveTo[1]) {
                return true;
            }
        }
        return false;
    }

    public boolean isMovePossible(Move move) {
       
        // TODO: check for any changes to move.
        //System.out.println("1");
        if (isMoveOntoOwnPiece(move)) {
            // System.out.println("move onto own piece");
            // System.out.println(move.piece.name);
            return false;
        }
        //System.out.println("2");
        if (isPieceInbetween(move)) {
            // System.out.println("piece blocking");
            // System.out.println(move.piece.name);
            return false;

        }
       // System.out.println("3");
        if (move.piece.name == "Pawn") {
            if (move.piece.x != move.moveTo[0]) // diagonal
            {
                if (!isEnemyPieceAt(move)) {
                    return false; // can't move diagonal unless taking.
                }
            }
        }
        // only do this when same turn colour
        
        
        if (move.piece.white == whiteToMove) {
            if (doChecksPreventMove(move)) {
                return false;
            }
        }
        return true;
       
        
    }

    public void showPieces() {
        for (Piece piece : whitePieces) {
            System.out.println("pos: " + piece.x + " " + piece.y + " name: " + piece.name + " " + piece.white);
        }
        for (Piece piece : blackPieces) {
            System.out.println("pos: " + piece.x + " " + piece.y + " name: " + piece.name + " " + piece.white);
        }
    }

    public void showBoard() {

        System.out.print("***********************************************************\n");
        for (int row = 0; row < boardArray.length; row++) {
            for (int column = 0; column < boardArray.length; column++) {
                if (boardArray[column][row] == null) {
                    System.out.print("     x    " + "\t"); // Spaces for White Pawn.
                } else {
                    System.out.print(boardArray[column][row] + "\t");
                }

            }
            System.out.println();

        }
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

class Piece {
    int x;
    int y;
    boolean white;
    String name;

    Piece(int x, int y, boolean white) {
        this.x = x;
        this.y = y;
        this.white = white;
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

        if(Math.abs(xDiff) == 1) {
            return movePath;
        }
        int xPos = move.piece.x + xChange;
        int yPos = move.piece.y + yChange;
        int[] startPos = {xPos, yPos};
        movePath.add(startPos);

        for(int i = 1; i<Math.abs(xDiff);i++)
        {
            xPos+=xChange;
            yPos += yChange;
            int[] currentPos = {xPos,yPos};
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
        // System.out.println("start: " + x + " " + y);
        for (int[] moveTo : possibleMoves) {
            // System.out.println("diag moveTo: " + moveTo[0] + " " + moveTo[1]);
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
        // System.out.println("og: " + x + " " + y);
        for (int[] moveTo : possibleMoves) {
            // System.out.println("vert moveTo: " + moveTo[0] + " " + moveTo[1] + "
            // moveFrom: " + x + " " + y);
        }

        return possibleMoves;
    }
}

class King extends Piece {
    King(int x, int y, boolean white) {
        super(x, y, white);
        name = "King";
    }
    // setup debugging for moving pawn and then check findMoves.

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
        return possibleMoves;
    }
    public Piece copyPiece() {
        Piece newPiece = new King(this.x, this.y, this.white);
        return newPiece;
    }

}

class Queen extends Piece {
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
    Rook(int x, int y, boolean white) {
        super(x, y, white);
        name = "Rook";
    }

    public ArrayList<int[]> findMoves() {
        ArrayList<int[]> possibleMoves = this.moveVert();
        for (int[] moveTo : possibleMoves) {
            Move move = new Move(this, moveTo, false);
            
        }

        return possibleMoves;
    }

    public Piece copyPiece() {
        Piece newPiece = new Rook(this.x, this.y, this.white);
        return newPiece;
    }

}

class Pawn extends Piece {
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
