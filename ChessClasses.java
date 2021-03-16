
import java.util.ArrayList;

import java.util.Random;
import java.util.Scanner;

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

    public void playUserGame() {
        // Similar to play game but ask for user inputs.
        // Then check if this input is on list of available move
        // -- this might seem inefficient but this is how chess.com
        // implements it as you can see the possible moves.
        Scanner scanner = null;
        Scanner moveScanner = null;
        while (!gameOver) {

            this.showBoard();
            if (whiteToMove) {
                System.out.println("White to move");
            } else {
                System.out.println("Black to move");
            }

            ArrayList<Move> allMoves = new ArrayList<Move>();
            allMoves = allAvailableMoves(whiteToMove);
            // Check for checkmate.
            String checkColour;
            if (whiteToMove) {
                checkColour = "Black";
            } else {
                checkColour = "White";
            }
            if (allMoves.size() == 0) {

                if (isCheckmate(whiteToMove)) {
                    System.out.println("Checkmate for " + checkColour);
                } else {
                    System.out.println("Stalemate");
                }
                showBoard();
                break;
            }
            // Find all unique pieces
            ArrayList<Piece> uniquePieces = new ArrayList<Piece>();
            for (Move move : allMoves) {
                if (uniquePieces.contains(move.piece)) {
                    continue;
                } else {
                    uniquePieces.add(move.piece);
                }
            }
            // Give user list of pieces including position.
            for (int j = 0; j < uniquePieces.size(); j++) {
                Piece piece = uniquePieces.get(j);
                String line = Integer.toString(j + 1) + ". " + piece.name + " at "
                        + Piece.indexToChessPos(piece.x, piece.y);
                System.out.println(line);

            }
            System.out.println("Choose which piece to move by typing number");
            // Get user input
            scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            // Choose the piece they chose.
            Piece userPiece = uniquePieces.get(choice - 1);

            // Find all available moves for that piece.
            ArrayList<Move> moves = new ArrayList<Move>();
            for (Move move : allMoves) {
                if (userPiece.isEqual(move.piece)) {
                    moves.add(move);
                }
            }
            // Ask user where they want to move chosen piece.
            this.showBoard();
            for (int j = 0; j < moves.size(); j++) {
                Move move = moves.get(j);
                String line = Integer.toString(j + 1) + ". " + move.piece.name + " at "
                        + Piece.indexToChessPos(move.piece.x, move.piece.y) + " to "
                        + Piece.indexToChessPos(move.moveTo[0], move.moveTo[1]);
                System.out.println(line);

            }

            System.out.println("Choose where to move by typing number");
            moveScanner = new Scanner(System.in);
            int moveChoice = moveScanner.nextInt();
            // Choose the piece they chose.
            Move moveToPlay = moves.get(moveChoice - 1);

            // Execute move
            executeMove(moveToPlay);
            whiteToMove = !whiteToMove;

        }

        moveScanner.close();
        scanner.close();

    }

    public void playGame() {
        int i = 0;
        // TODO: isKingInCheck sometimes the queen can take the King without check
        // realised.
        int seed = 5;
        Random random = new Random(seed);
        showBoard();

        while (!gameOver) {
            i++;
            System.out.println("game loop: " + i);

            ArrayList<Move> allMoves = new ArrayList<Move>();
            // allMoves from this are just to same spot need to debug.
            allMoves = allAvailableMoves(whiteToMove);

            String checkColour;
            if (whiteToMove) {
                checkColour = "Black";
            } else {
                checkColour = "White";
            }
            System.out.println(allMoves.size());
            if (allMoves.size() == 0) {

                if (isCheckmate(whiteToMove)) {
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

    public boolean isCheckmate(boolean white) {
        whiteToMove = !white;
        return isKingInCheck(white);
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

            ArrayList<Move> availableMoves = availableMoves(piece);
            for (Move move : availableMoves) {
                // move.showMove();

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
            // We don't need to copy the piece we just need to be careful manipulating
            // piece.
            Move candidateMove = new Move(piece, moveTo, false);

            if (isMovePossible(candidateMove)) {
                possibleMoves.add(candidateMove);
                // System.out.println("c move below");;
                // candidateMove.showMove();
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

        if(move.piece instanceof Pawn) {
            Pawn pawn = (Pawn) move.piece;
            if(pawn.enPassantThisTurn) {
                
                pawn.enPassantThisTurn = false;
                int change;
                ArrayList<Piece> enemyPieces = null;
                if(move.piece.white) {
                    change = 1;
                    enemyPieces = blackPieces;
                } else {
                    change = -1;
                    enemyPieces = whitePieces;
                }
                int[] pieceToBeTakenPos = new int[] {move.moveTo[0], move.moveTo[1] - change};
                for(Piece piece: enemyPieces) {
                    if(piece.x == pieceToBeTakenPos[0] && piece.y == pieceToBeTakenPos[1]) {
                        enemyPieces.remove(piece);
                        break;
                    }
                }
                move.piece.x = move.moveTo[0];
                move.piece.y = move.moveTo[1];
                move.piece.chessPos = Piece.indexToChessPos(move.piece.x, move.piece.y);
                // Do I need to copy these -- shouldn't mattter too much.
                blackPosHistory.add(copyPieceArray(blackPieces)); // each time we execute a move we add it to history.
                whitePosHistory.add(copyPieceArray(whitePieces));
                buildBoardArray();
                return;
            }
        }
        if (move.piece instanceof Rook) {
            Rook rook = (Rook) move.piece;
            rook.hasMoved = true;
        }

        if (move.piece instanceof King) {
            this.whiteCastled = true;
        }
        ArrayList<Piece> currentPieces = new ArrayList<Piece>();
        int rookPosY;

        if (isCastle(move)) {
            if (move.piece.white) {
                currentPieces = whitePieces;
                rookPosY = 0;

            } else {
                currentPieces = blackPieces;
                rookPosY = 7;
            }
            int[] rookMoveTo;
            int[] currentRookPos;
            if (isCastleLeft(move)) {
                rookMoveTo = new int[] { 2, rookPosY };
                currentRookPos = new int[] { 0, rookPosY };
            } else {
                rookMoveTo = new int[] { 4, rookPosY };
                currentRookPos = new int[] { 7, rookPosY };
            }
            for (Piece piece : currentPieces) {
                if (piece.x == currentRookPos[0] && piece.y == currentRookPos[1]) {
                    Rook castlingRook = (Rook) piece;
                    castlingRook.x = rookMoveTo[0];
                    castlingRook.y = rookMoveTo[1];
                    castlingRook.chessPos = Piece.indexToChessPos(castlingRook.x, castlingRook.y);
                    break;
                    // We don't need to check if takes piece.
                }
                // Do I need to copy these -- shouldn't mattter too much.
                blackPosHistory.add(copyPieceArray(blackPieces)); // each time we execute a move we add it to history.
                whitePosHistory.add(copyPieceArray(whitePieces));
                buildBoardArray();
                return;
            }

        }
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
        move.piece.chessPos = Piece.indexToChessPos(move.piece.x, move.piece.y);
        // Promotion check
        if (move.piece instanceof Pawn) {
            int promotionY;
            if (move.piece.white) {
                promotionY = 7;
            } else {
                promotionY = 0;
            }
            if (move.piece.y == promotionY) {
                // Here we might ask user what they want to promote to.
                ArrayList<String> promotionOptions = new ArrayList<String>();
                promotionOptions.add("Queen");
                promotionOptions.add("Knight");
                promotionOptions.add("Rook");
                promotionOptions.add("Bishop");
                int i = 1;
                System.out.println("Type number to choose promotion: ");
                for (String promotionOption : promotionOptions) {

                    System.out.println(i + ". " + promotionOption);
                    i++;
                }
                Scanner scanner = new Scanner(System.in);
                int promoteChoice = scanner.nextInt();
                scanner.close();

                promote((Pawn) move.piece, promotionOptions.get(promoteChoice - 1)); // valid because of first if but
                                                                                     // might cause problems.
            }
        }
        // Do I need to copy these -- shouldn't mattter too much.
        blackPosHistory.add(copyPieceArray(blackPieces)); // each time we execute a move we add it to history.
        whitePosHistory.add(copyPieceArray(whitePieces));
        buildBoardArray();

        return;
    }

    public void promote(Pawn pawn, String pieceTarget) {
        Piece newPiece;
        if (pieceTarget == "Queen") {
            newPiece = new Queen(pawn.x, pawn.y, pawn.white);
        } else if (pieceTarget == "Knight") {
            newPiece = new Knight(pawn.x, pawn.y, pawn.white);
        } else if (pieceTarget == "Rook") {
            Rook rook = new Rook(pawn.x, pawn.y, pawn.white);
            rook.hasMoved = true;
            newPiece = rook;
        } else {
            newPiece = new Bishop(pawn.x, pawn.y, pawn.white);
        }
        ArrayList<Piece> currentPieces = getPieceColour(pawn.white);
        currentPieces.remove(pawn);
        currentPieces.add(newPiece);

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
        ArrayList<Piece> pieces = getPieceColour(move.piece.white);
        for (Piece piece : pieces) {
            if (move.piece.x == piece.x && move.piece.y == piece.y) {
                return new Move(piece, move.moveTo, move.takePiece); // This move will now affect copyBoard
            }
        }
        throw new IllegalStateException("bad");

    }

    public boolean doChecksPreventMove(Move move) {
        if (move.piece.white != whiteToMove) {
            return false;
        }
        Board copyBoard = this.copyBoard();
        Move newMove = copyBoard.findSimilarMove(move);

        copyBoard.executeMove(newMove); // This should add to the blackHistory and white history etc.
        boolean output = false;

        if (copyBoard.isKingInCheck(newMove.piece.white)) {
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

        // System.out.println("king pos: " + kingPos[0] + " " + kingPos[1]);
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

    public boolean isCastle(Move move) {
        if (move.piece instanceof King) {
            if (Math.abs(move.piece.x - move.moveTo[0]) > 1) {

                return true;
            }
        }
        return false;
    }

    public boolean isCastleLeft(Move move) {

        if (move.piece.x > move.moveTo[0]) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isCastlePossible(Move move) {
        // We ignore if enemy is trying to castle as doesn't make sense.
        if (move.piece.white != whiteToMove) {
            return false;
        }

        if (this.whiteCastled == true) {
            return false;
        }

        int rookY;
        ArrayList<Piece> pieces = new ArrayList<Piece>();
        int[] rookPos;
        int[] rookMoveTo;
        if (move.piece.white) {
            pieces = whitePieces;
            rookY = 0;
        } else {
            rookY = 7;
            pieces = blackPieces;
        }
        int change;
        if (isCastleLeft(move)) {
            rookPos = new int[] { 0, rookY };
            rookMoveTo = new int[] { move.moveTo[0] + 1, rookY };
            change = -1;
        } else {
            rookPos = new int[] { 7, rookY };
            rookMoveTo = new int[] { move.moveTo[0] - 1, rookY };
            change = 1;
        }

        // Get Rook if doesn't exist return false.
        boolean rookFound = false;
        for (Piece piece : pieces) {
            if (piece.x == rookPos[0] && piece.y == rookPos[1]) {
                Rook rook = (Rook) piece;
                rookFound = true;
                if (rook.hasMoved) {
                    return false;
                }
            }
        }
      
        if (!rookFound) {
            return false;
        }
        
        // Check if any pieces in between:
        // Construct a test piece:
        Piece testRook = new Rook(move.piece.x, move.piece.y, true);

        Move testMove = new Move(testRook, rookPos, false);
        ArrayList<int[]> possiblePiecePos = testRook.getMovePath(testMove);

        for (Piece piece : whitePieces) {
            int[] checkPos = new int[] { piece.x, piece.y };
            for (int[] testPos : possiblePiecePos) {
                if (checkPos[0] == testPos[0] && checkPos[1] == testPos[1]) {
                    return false;
                }
            }
        }

        for (Piece piece : blackPieces) {
            int[] checkPos = new int[] { piece.x, piece.y };
            for (int[] testPos : possiblePiecePos) {
                if (checkPos[0] == testPos[0] && checkPos[1] == testPos[1]) {
                    return false;
                }
            }
        }
  

        // Now we need to check if there are checks in between king.
        // Idea: copy code for kingCheck thing.
        // Iterate the King to the moveTo location checking for checks.
        Board copyBoard = this.copyBoard();
        for (int i = 0; i < 2; i++) {
            King king = copyBoard.findKing(move.piece.white);
            int[] kingMoveTo = new int[] { king.x + change, king.y };
            Move newMove = new Move(king, kingMoveTo, false);

            copyBoard.executeMove(newMove); // This should add to the blackHistory and white history etc.

            if (copyBoard.isKingInCheck(newMove.piece.white)) {
                // checks if after move king is being attacked.
                return false;
            }
        }
        

        if (isKingInCheck(move.piece.white)) {
            return false;
        }
        

        // If we have got this far then we can castle!
        System.out.println(move.piece.white + " Castling");
        return true;

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

    public boolean isEnPassant(Move move) {
        Pawn attackingPawn = (Pawn) move.piece;
        int yEnPassant;
        ArrayList<Piece> pieces;
        ArrayList<Piece> enemyPieces;
        ArrayList<ArrayList<Piece>> pieceHistory;
        int change;
      
        if(move.piece.white) {
            pieces = whitePieces;
            enemyPieces = blackPieces;
            yEnPassant = 4; 
            pieceHistory = blackPosHistory;
            change = 1;
            
        } else {
            pieces = blackPieces;
            enemyPieces = whitePieces;
            yEnPassant = 3;
            pieceHistory = whitePosHistory;
            change = -1;
        }

        if(move.piece.y != yEnPassant) {
            return false;
        }


        
        // Need to see if on current board there is a pawn in the relevant position.
        // The relevant position is x +/- 1;
        int[] enPassantRight = new int[] {move.piece.x-1,move.piece.y};
        int[] enPassantLeft = new int[] {move.piece.x+1,move.piece.y};
        
        Pawn enPassantPiece = new Pawn(-1,-1,false);
        boolean enemyPieceInCorrectPos = false;
        for(Piece piece: enemyPieces) {
            if(piece instanceof Pawn && piece.x == enPassantRight[0] && piece.y == enPassantRight[1]) {
                enPassantPiece = (Pawn)piece;
                enemyPieceInCorrectPos = true;
            }
            if(piece instanceof Pawn && piece.x == enPassantLeft[0] && piece.y == enPassantLeft[1]) {
                enPassantPiece = (Pawn)piece;
                enemyPieceInCorrectPos = true;
            }
        }
        if(!attackingPawn.enPassantStillPossible) {
            return false;
        }
   
        

        if(!enemyPieceInCorrectPos) {
            return false;
        }
        System.out.println("4");
        // 

        // Need to check last move was a +2 pawn move on relevant column.
        if(pieceHistory.size() < 2) {
            return false;
        }
        System.out.println("5");
        ArrayList<Piece> enemysTurnPieces = pieceHistory.get(pieceHistory.size() - 2); 
        // piece history is updated after an executed move.
        // Therefore the en Passant move will be in this history hence the -2.
        // Checks: 
        // 1. Check no Pawn on same row e.g. doublestacked pawns.
        
        for(Piece piece: enemyPieces) {
            if(piece instanceof Pawn && piece.x == enPassantPiece.x && piece.y == enPassantPiece.y - 2 * change) {
                return false;
            }
        }
        System.out.println("6");
        // 2. Now check in pieceHistory whether there is a pawn in the expected place.
        
        for(Piece piece: enemysTurnPieces) {
            if(piece instanceof Pawn && piece.x == enPassantPiece.x && piece.y == enPassantPiece.y + 2 * change) {
                attackingPawn.enPassantThisTurn = true;
                attackingPawn.enPassantStillPossible = false; 
                // If we are checking whether en Passant is possible and it is. Then it will not be possible next turn.
                return true;
            }
        }
       
        return false;
    }

    public boolean isInvalidPawnMove(Move move){
        if (move.piece instanceof Pawn) {
            if (move.piece.x != move.moveTo[0]) // diagonal
            {
                if(isEnPassant(move)) {
                    return false;
                }

                if (!isEnemyPieceAt(move)) {
                    return true; // can't move diagonal unless taking.
                }
            } else {
                if(isEnemyPieceAt(move)) {
                    return true; // can't move straight unless square free.
                }
            }
        }
        return false;
    }

    public boolean isMovePossible(Move move) {
        if(isCastle(move)) {
            if(isCastlePossible(move)) {
                return true;
            } else {
                return false;
            }
        }

        if (isMoveOntoOwnPiece(move)) {
            return false;
        }
        
        if (isPieceInbetween(move)) {

            return false;

        }
        
        if(isInvalidPawnMove(move)){
            return false;
        }


        if (doChecksPreventMove(move)) {
            return false;
        }
       
        return true;
       
        
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

class Piece {
    int x;
    int y;
    boolean white;
    String name;
    String chessPos;

    Piece(int x, int y, boolean white) {
        this.x = x;
        this.y = y;
        this.white = white;
        this.chessPos = indexToChessPos(x, y);
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

        return possibleMoves;
    }
}

class King extends Piece {

    King(int x, int y, boolean white) {
        super(x, y, white);
        name = "King";
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

        int flag = 1;
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
    boolean hasMoved;

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