import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

class Play {
    ChessBot bot;
    
    public Play() {
        bot = new ChessBot();
    }

    public void againstBot(int depth) {
        Game game = bot.game;
        game.board.showBoard();
        while(!game.gameOver) {
            // Bot move
            long startTime = System.nanoTime();
            Evaluation evalObj = bot.findOptimalMove(depth);
            long endTime = System.nanoTime();
            long timeElapsed = (endTime - startTime)/1000000;
            System.out.println("Bot finished in " + timeElapsed + " ms.");
            Move move = evalObj.movePath.get(0);
            
            bot.executeBotMove(move);
            game.board.showBoard();
            move.showMove();
            game.whiteToMove = !game.whiteToMove;

            // User move
            ArrayList<Move> allMoves = game.allAvailableMoves(game.whiteToMove);
            if(isGameOver(allMoves)) {
                break;
            }
            // Find unique pieces
            ArrayList<Piece> uniquePieces = findUniquePieces(allMoves);
            printUniquePieces(uniquePieces);
            int choice = getUserChoice();
            Piece userPiece = uniquePieces.get(choice-1);

            ArrayList<Move> moves = findMovesForPiece(allMoves, userPiece);
            printMoves(moves);
            int moveChoice = getUserChoice();

            Move moveToPlay = moves.get(moveChoice-1);
            game.executeMove(moveToPlay,true);
            game.whiteToMove = !game.whiteToMove;

        }
    }

    public void againstPerson() {
        // Similar to play game but ask for user inputs.
        // Then check if this input is on list of available move
        // -- this might seem inefficient but this is how chess.com
        // implements it as you can see the possible moves.
        Game game = this.bot.game;
        while (!game.gameOver) {

            game.board.showBoard();
            if (game.whiteToMove) {
                System.out.println("White to move");
            } else {
                System.out.println("Black to move");
            }

            ArrayList<Move> allMoves = new ArrayList<Move>();
            allMoves = game.allAvailableMoves(game.whiteToMove);
            if(isGameOver(allMoves)){
                break;
            }
            // Find all unique pieces
            ArrayList<Piece> uniquePieces = findUniquePieces(allMoves);
            // Give user list of pieces including position.
            printUniquePieces(uniquePieces);
            
            // Get user input

            // Choose the piece they chose.
            int choice = getUserChoice();
            Piece userPiece = uniquePieces.get(choice - 1);

            // Find all available moves for that piece.
            ArrayList<Move> moves = findMovesForPiece(allMoves, userPiece);
            // Ask user where they want to move chosen piece.
            printMoves(moves);
            int moveChoice = getUserChoice();
            // Choose the piece they chose.
            Move moveToPlay = moves.get(moveChoice - 1);

            // Execute move
            game.executeMove(moveToPlay,true);
            game.whiteToMove = !game.whiteToMove;

        }

       

    }


    public void againstRandom() {
        int i = 0;
        int seed = 5;
        Random random = new Random(seed);
        Game game = this.bot.game;
        game.board.showBoard();

        while (!game.gameOver) {
            i++;
            System.out.println("game loop: " + i);

            ArrayList<Move> allMoves = game.allAvailableMoves(game.whiteToMove);
            if(isGameOver(allMoves)){
                break;
            }

            int randomIndex = random.nextInt(allMoves.size());
            Move move = allMoves.get(randomIndex);
            game.executeMove(move,true);
            game.board.showBoard();

            game.whiteToMove = !game.whiteToMove;
        }
     
    }

    public boolean isGameOver(ArrayList<Move> allMoves) {
        String checkColour;
        Game game = this.bot.game;
        if (game.whiteToMove) {
            checkColour = "Black";
        } else {
            checkColour = "White";
        }
        System.out.println(allMoves.size());
        if (allMoves.size() == 0) {

            if (game.isCheckmate(game.whiteToMove)) {
                System.out.println("Checkmate for " + checkColour);
            } else {
                System.out.println("Stalemate");
            }
            game.board.showBoard();
            return true;
        }
        return false;
    }




    
    public ArrayList<Piece> findUniquePieces(ArrayList<Move> allMoves){
        ArrayList<Piece> uniquePieces = new ArrayList<Piece>();
        for (Move move : allMoves) {
            if (uniquePieces.contains(move.piece)) {
                continue;
            } else {
                uniquePieces.add(move.piece);
            }
        }
        return uniquePieces;
    }

    public void printUniquePieces(ArrayList<Piece> uniquePieces) {
        for (int j = 0; j < uniquePieces.size(); j++) {
            Piece piece = uniquePieces.get(j);
            String line = Integer.toString(j + 1) + ". " + piece.name + " at "
                    + Piece.indexToChessPos(piece.x, piece.y);
            System.out.println(line);

        }
        System.out.println("Choose which piece to move by typing number");
    }

    public ArrayList<Move> findMovesForPiece(ArrayList<Move> allMoves, Piece userChoice) {
        ArrayList<Move> moves = new ArrayList<Move>();
        for (Move move : allMoves) {
            if (userChoice.isEqual(move.piece)) {
                moves.add(move);
            }
        }
        return moves;
    }

    public void printMoves(ArrayList<Move> moves) {
        Game game = this.bot.game;
        game.board.showBoard();
        for (int j = 0; j < moves.size(); j++) {
            Move move = moves.get(j);
            String line = Integer.toString(j + 1) + ". " + move.piece.name + " at "
                    + Piece.indexToChessPos(move.piece.x, move.piece.y) + " to "
                    + Piece.indexToChessPos(move.moveTo[0], move.moveTo[1]);
            System.out.println(line);
            
        }
        System.out.println("Choose where to move by typing number");
    }

    public int getUserChoice() {
        Scanner in = bot.game.getScanner();
        int choice = in.nextInt();
        return choice;
    }

 






}