import java.util.Collections;

// 1 is white 0 is black.
public class mainProg {
    public static void main(String[] args){
        ChessBot bot = new ChessBot();
        int depth = 3;
       
        Evaluation evalObj = bot.findOptimalMove(depth);
        System.out.println("bot finished");
        bot.game.board.showBoard();
    
        // Possible proble is that moves are from a copy of the game.
        // Therefore  we need to find the pieces

        for(Move move: evalObj.movePath){
            System.out.println("before execute move");
            bot.executeBotMove(move);
            bot.game.whiteToMove = !bot.game.whiteToMove;
            bot.game.board.showBoard();
        }
        //gameBoard.playGame();
        
        //gameBoard.showBoard();
    
        //TODO: fix isKingInCheck it's outputting weird invalid stuff.
    
    
    
    
    }
}
