import java.util.ArrayList;
import java.util.HashMap;


class ChessBot {
    Game game;
    double whiteEval;
    HashMap<String,Double> pieceValues = new HashMap<String,Double>();
    public ChessBot() {
        game = new Game();

        pieceValues.put("King", 0.0);
        pieceValues.put("Queen", 9.0);
        pieceValues.put("Knight", 3.0);
        pieceValues.put("Rook", 5.0);
        pieceValues.put("Bishop", 3.0);
        pieceValues.put("Pawn", 1.0);

        whiteEval = whiteEval();

        
    }


    public void executeBotMove(Move move) {
        // This solves the problem of the pieces being copies.
        // find actual piece.
        ArrayList<Piece> pieces = game.board.getPieceColour(move.piece.white);
        Piece foundPiece = null;
        for(Piece piece: pieces) {
            if(piece.x == move.piece.x && piece.y == move.piece.y) {
                foundPiece = piece;
                Move correctMove = new Move(foundPiece, move.moveTo, false);
                game.executeMove(correctMove, true);
                return;
            }
        }
        throw new IllegalAccessError("Piece should be found");  
    }


    public Evaluation findOptimalMove(int depth) {
        ArrayList<Evaluation> evaluationsObj = findMovePathValues(depth);
        int sign;
        double bestEval = - Double.MAX_VALUE;
        if(game.whiteToMove) {
            sign = 1;
        } else {
            sign = -1;
        }
        ArrayList<Move> movePath;
        ArrayList<Move> bestMovePath = new ArrayList<Move>();
        for(Evaluation evaluation: evaluationsObj ) {
            movePath = evaluation.movePath;
            double eval = sign * evaluation.eval;
            if(eval > bestEval) {
                bestEval = eval;
                bestMovePath = movePath;
            }

        }
        Evaluation bestEvaluation = new Evaluation(bestMovePath, sign*bestEval);
        
        return bestEvaluation;
    }

    public ArrayList<Evaluation> findMovePathValues(int depth) {
        ArrayList<Move> allMoves = game.allAvailableMoves(game.whiteToMove);
        ArrayList<Evaluation> output = new ArrayList<Evaluation>();
        ArrayList<Move> movePath = new ArrayList<Move>();
        double initialEval = whiteEval();
        if(allMoves.size() == 0) {
            if(game.isCheckmate(game.whiteToMove)) {
                double checkMateEval;
                if(game.whiteToMove) {
                    checkMateEval = - Double.MAX_VALUE; 
                } else {
                    checkMateEval = Double.MAX_VALUE;
                }
                Evaluation checkMateEvalObj = new Evaluation(movePath, checkMateEval);
                output.add(checkMateEvalObj); // checkmate is optimal
            } else {
                Evaluation staleMateEvalObj = new Evaluation(movePath, 0);
                output.add(staleMateEvalObj); // stalemate evals to 0.
            }
            
        }

        if(depth == 0) {
            Evaluation zeroDepthEvalObj = new Evaluation(movePath, initialEval);
            output.add(zeroDepthEvalObj);
            return output;
        } 

        for(Move move: allMoves) {
            ChessBot copyBot = this.copyBot(); 
            Move copyMove = move.copyMove();
            copyBot.executeBotMove(move);
            copyBot.game.whiteToMove = !copyBot.game.whiteToMove;
            ArrayList<Evaluation> temp = copyBot.findMovePathValues(depth - 1);
            
            for(Evaluation evalationObj: temp) {
                movePath = new ArrayList<Move>();
                movePath.add(copyMove);
                Double eval = evalationObj.eval;
                movePath.addAll(evalationObj.movePath);
                Evaluation recursiveEvalObj = new Evaluation(movePath, eval);
                output.add(recursiveEvalObj);
                /*
                boolean betterThanBeforeWhite = (eval > initialEval);
                boolean betterThanBeforeBlack = (eval < initialEval);
                // Point of this is to make much more efficient (if we improve the position we stop looking).
                if(game.whiteToMove) {
                    if(betterThanBeforeWhite) {
                        return output;
                    }
                } else {
                    if(betterThanBeforeBlack) {
                        return output;
                    }
                }
                */
                
            }
        }
        return output;
        
    }

    public ChessBot copyBot() {
        ChessBot newBot = new ChessBot();
        newBot.game = this.game.copyGame();
        newBot.whiteEval = this.whiteEval;
        return newBot;
    }

    public double whiteEval() {
        double blackPieceSum = countEval(this.game.board.blackPieces);
        double whitePieceSum = countEval(this.game.board.whitePieces);
        return whitePieceSum - blackPieceSum;

    }

    public double countEval(ArrayList<Piece> pieces) {
        double sum = 0;
        for(Piece piece: pieces) {
            sum += pieceValues.get(piece.name);
        }
        return sum;
    }

}

class Evaluation {
    ArrayList<Move> movePath;
    double eval;
    public Evaluation(ArrayList<Move> movePath, double eval) {
        this.movePath = movePath;
        this.eval = eval;
    }

}