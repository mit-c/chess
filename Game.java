import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class Game {
    // TODO: refactor some stuff from board into this.
    Board board;
    boolean whiteToMove = true;
    boolean whiteCastled = false;
    boolean blackCastled = false;
    boolean gameOver = false;
    private Scanner scanner = new Scanner(System.in);


    public Game() {
        board = new Board();
    }

    public void playBotGame() {
        int i = 0;
        int seed = 5;
        Random random = new Random(seed);
        board.showBoard();

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
                board.showBoard();
                break;
            }
            int randomIndex = random.nextInt(allMoves.size());
            Move move = allMoves.get(randomIndex);
            executeMove(move,true);
            board.showBoard();

            whiteToMove = !whiteToMove;
        }
     
    }

    public void playUserGame() {
        // Similar to play game but ask for user inputs.
        // Then check if this input is on list of available move
        // -- this might seem inefficient but this is how chess.com
        // implements it as you can see the possible moves.

        while (!gameOver) {

            board.showBoard();
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
                board.showBoard();
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
            Scanner in = getScanner();
            int choice = in.nextInt();
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
            board.showBoard();
            for (int j = 0; j < moves.size(); j++) {
                Move move = moves.get(j);
                String line = Integer.toString(j + 1) + ". " + move.piece.name + " at "
                        + Piece.indexToChessPos(move.piece.x, move.piece.y) + " to "
                        + Piece.indexToChessPos(move.moveTo[0], move.moveTo[1]);
                System.out.println(line);

            }

            System.out.println("Choose where to move by typing number");
            Scanner moveScanner = getScanner();
            int moveChoice = moveScanner.nextInt();
            // Choose the piece they chose.
            Move moveToPlay = moves.get(moveChoice - 1);

            // Execute move
            executeMove(moveToPlay,true);
            whiteToMove = !whiteToMove;

        }

       

    }

    public Scanner getScanner() {
        return scanner;
    }
    public boolean isCheckmate(boolean white) {
        boolean tmpWhiteToMove = whiteToMove;
        whiteToMove = !white;
        boolean checkMate = isKingInCheck(white);
        whiteToMove = tmpWhiteToMove;
        return checkMate;

    }

    public boolean isKingInCheck(boolean white) {
        // Check if in the current board state the white / black king is in check.

        //

        ArrayList<Move> enemyMoves = this.allAvailableMoves(!white);
        int[] kingPos = board.getKingPos(white);

        // System.out.println("king pos: " + kingPos[0] + " " + kingPos[1]);
        for (Move enemyMove : enemyMoves) {

            if (enemyMove.moveTo[0] == kingPos[0] && enemyMove.moveTo[1] == kingPos[1]) {

                return true;
            }
        }
        return false;
    }

    public ArrayList<Move> allAvailableMoves(boolean white) {

        ArrayList<Move> moveList = new ArrayList<Move>();
        ArrayList<Piece> pieceList = board.getPieceColour(white);

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
            Move candidateMove = new Move(piece, moveTo, false);

            if (isMovePossible(candidateMove)) {
                possibleMoves.add(candidateMove);
            }
        }

        return possibleMoves;
    }

    public boolean isMovePossible(Move move) {
        if (isCastle(move)) {
            if (isCastlePossible(move)) {
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

        if (isInvalidPawnMove(move)) {
            return false;
        }

        if (doChecksPreventMove(move)) {
            return false;
        }

        return true;

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
        ArrayList<Piece> allPieces = board.copyPieceArray(board.whitePieces); // need all pieces to be copy of
                                                                                      // white pieces.

        allPieces.addAll(board.copyPieceArray(board.blackPieces));
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
        ArrayList<Piece> enemyPieces = board.getPieceColour(!move.piece.white);
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
            pieces = board.whitePieces;
            rookY = 0;
        } else {
            rookY = 7;
            pieces = board.blackPieces;
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
            if (piece.x == rookPos[0] && piece.y == rookPos[1] && piece instanceof Rook) {
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

        for (Piece piece : board.whitePieces) {
            int[] checkPos = new int[] { piece.x, piece.y };
            for (int[] testPos : possiblePiecePos) {
                if (checkPos[0] == testPos[0] && checkPos[1] == testPos[1]) {
                    return false;
                }
            }
        }

        for (Piece piece : board.blackPieces) {
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
        Board copyBoard = board.copyBoard();
        Game copyGame = copyGame();
        copyGame.board = copyBoard;
        for (int i = 0; i < 2; i++) {
            King king = copyBoard.findKing(move.piece.white);
            int[] kingMoveTo = new int[] { king.x + change, king.y };
            Move newMove = new Move(king, kingMoveTo, false);

            executeMove(newMove,false); // This should add to the blackHistory and white history etc.

            if (copyGame.isKingInCheck(newMove.piece.white)) {
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

    public boolean isEnPassant(Move move) {
        Pawn attackingPawn = (Pawn) move.piece;
        int yEnPassant;
        ArrayList<Piece> pieces;
        ArrayList<Piece> enemyPieces;
        ArrayList<ArrayList<Piece>> pieceHistory;
        int change;

        if (move.piece.white) {
            pieces = board.whitePieces;
            enemyPieces = board.blackPieces;
            yEnPassant = 4;
            pieceHistory = board.blackPosHistory;
            change = 1;

        } else {
            pieces = board.blackPieces;
            enemyPieces = board.whitePieces;
            yEnPassant = 3;
            pieceHistory = board.whitePosHistory;
            change = -1;
        }

        if (move.piece.y != yEnPassant) {
            return false;
        }

        // Need to see if on current board there is a pawn in the relevant position.
        // The relevant position is x +/- 1;
        int[] enPassantRight = new int[] { move.piece.x - 1, move.piece.y };
        int[] enPassantLeft = new int[] { move.piece.x + 1, move.piece.y };

        Pawn enPassantPiece = new Pawn(-1, -1, false);
        boolean enemyPieceInCorrectPos = false;
        for (Piece piece : enemyPieces) {
            if (piece instanceof Pawn && piece.x == enPassantRight[0] && piece.y == enPassantRight[1]) {
                enPassantPiece = (Pawn) piece;
                enemyPieceInCorrectPos = true;
            }
            if (piece instanceof Pawn && piece.x == enPassantLeft[0] && piece.y == enPassantLeft[1]) {
                enPassantPiece = (Pawn) piece;
                enemyPieceInCorrectPos = true;
            }
        }
        if (!attackingPawn.enPassantStillPossible) {
            return false;
        }

        if (!enemyPieceInCorrectPos) {
            return false;
        }
      
        //

        // Need to check last move was a +2 pawn move on relevant column.
        if (pieceHistory.size() < 2) {
            return false;
        }
    
        ArrayList<Piece> enemysTurnPieces = pieceHistory.get(pieceHistory.size() - 2);
        // piece history is updated after an executed move.
        // Therefore the en Passant move will be in this history hence the -2.
        // Checks:
        // 1. Check no Pawn on same row e.g. doublestacked pawns.

        for (Piece piece : enemyPieces) {
            if (piece instanceof Pawn && piece.x == enPassantPiece.x && piece.y == enPassantPiece.y - 2 * change) {
                return false;
            }
        }
      
        // 2. Now check in pieceHistory whether there is a pawn in the expected place.

        for (Piece piece : enemysTurnPieces) {
            if (piece instanceof Pawn && piece.x == enPassantPiece.x && piece.y == enPassantPiece.y + 2 * change) {
                attackingPawn.enPassantThisTurn = true;
                attackingPawn.enPassantStillPossible = false;
                // If we are checking whether en Passant is possible and it is. Then it will not
                // be possible next turn.
                return true;
            }
        }

        return false;
    }

    public boolean isInvalidPawnMove(Move move) {
        if (move.piece instanceof Pawn) {
            if (move.piece.x != move.moveTo[0]) // diagonal
            {
                if (isEnPassant(move)) {
                    return false;
                }

                if (!isEnemyPieceAt(move)) {
                    return true; // can't move diagonal unless taking.
                }
            } else {
                if (isEnemyPieceAt(move)) {
                    return true; // can't move straight unless square free.
                }
            }
        }
        return false;
    }

    public boolean isMoveOntoOwnPiece(Move move) {
        ArrayList<Piece> pieces = board.getPieceColour(move.piece.white);
        for (Piece piece : pieces) {
            int[] piecePos = piece.getPosition();
            if (move.moveTo[0] == piecePos[0] && move.moveTo[1] == piecePos[1]) {
                return true;
            }
        }
        return false;
    }

    public Game copyGame() {
        Game newGame = new Game();
        newGame.board = this.board.copyBoard();
        newGame.whiteToMove = this.whiteToMove;
        newGame.whiteCastled = this.whiteCastled;
        newGame.blackCastled = this.blackCastled;
        newGame.gameOver = this.gameOver;
        // Not sure about copying scanner (might cause weird behaviour).
        return newGame;
    }

    public boolean doChecksPreventMove(Move move) {
        if (move.piece.white != whiteToMove) {
            return false;
        }

        Board copyBoard = board.copyBoard();
        Move newMove = copyBoard.findSimilarMove(move);
        Game copyGame = copyGame();
        copyGame.board = copyBoard; // Made changes he
        boolean isActualMove = false;
        copyGame.executeMove(newMove, isActualMove); // This should add to the blackHistory and white history etc.
        boolean output = false;

        if (copyGame.isKingInCheck(newMove.piece.white)) {
            // checks if after move king is being attacked.
            output = true;
            return output;
        }
        copyGame.undoMove();
        return output;
    }

    public void executeMove(Move move, boolean promotionCheck) {
        // promotion check shows if we are actually executing a move (in which case we ask about promotion).
        // if not actual move then it is not necessary to check which promotion.
        if (move.piece instanceof Pawn) {
            Pawn pawn = (Pawn) move.piece;
            if (pawn.enPassantThisTurn) {

                pawn.enPassantThisTurn = false;
                int change;
                ArrayList<Piece> enemyPieces = null;
                if (move.piece.white) {
                    change = 1;
                    enemyPieces = board.blackPieces;
                } else {
                    change = -1;
                    enemyPieces = board.whitePieces;
                }
                int[] pieceToBeTakenPos = new int[] { move.moveTo[0], move.moveTo[1] - change };
                for (Piece piece : enemyPieces) {
                    if (piece.x == pieceToBeTakenPos[0] && piece.y == pieceToBeTakenPos[1]) {
                        enemyPieces.remove(piece);
                        break;
                    }
                }
                move.piece.x = move.moveTo[0];
                move.piece.y = move.moveTo[1];
                move.piece.chessPos = Piece.indexToChessPos(move.piece.x, move.piece.y);

                board.addToHistory();
                board.buildBoardArray();
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
                currentPieces = board.whitePieces;
                rookPosY = 0;

            } else {
                currentPieces = board.blackPieces;
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
                }
                // Do I need to copy these -- shouldn't mattter too much.
                board.addToHistory();
                board.buildBoardArray();
                return;
            }

        }
        ArrayList<Piece> pieces;
        pieces = board.getPieceColour(!move.piece.white);
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
        if (move.piece instanceof Pawn && promotionCheck) {
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
                Scanner in = getScanner();
                int promoteChoice = in.nextInt();
                

                promote((Pawn) move.piece, promotionOptions.get(promoteChoice - 1));
            }
        }
        // Do I need to copy these -- shouldn't mattter too much.
        board.addToHistory();
        board.buildBoardArray();

        return;
    }

    public void undoMove() {

        if (board.blackPosHistory.size() <= 1 || board.whitePosHistory.size() <= 1) {
            throw new IllegalArgumentException("PosHistory must be at least size 2 to undo a move");
        } else {

            board.blackPieces = board
                    .copyPieceArray(board.blackPosHistory.get(board.blackPosHistory.size() - 2));
            board.whitePieces = board
                    .copyPieceArray(board.whitePosHistory.get(board.blackPosHistory.size() - 2));
        }
        board.buildBoardArray();
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
        ArrayList<Piece> currentPieces = board.getPieceColour(pawn.white);
        currentPieces.remove(pawn);
        currentPieces.add(newPiece);

    }

}
