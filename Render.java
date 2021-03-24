import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;


class MouseInBoard {
    int xIndex;
    int yIndex;
    boolean isMouseInBoard;
    MouseInBoard(int xIndex, int yIndex, boolean isMouseInBoard) {

        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.isMouseInBoard = isMouseInBoard;
    } 

    MouseInBoard(boolean isMouseInBoard) {
        this.isMouseInBoard = isMouseInBoard;
    }
}


class myMouseAdapter extends MouseAdapter {
    ChessCanvas panel;
    ChessBot bot;
    Play play;
    JFrame jFrame;
    boolean mousePressed = false;
    public myMouseAdapter(ChessCanvas panel, Play play) {
        this.panel = panel;
        this.play = play;
        this.bot = play.bot;
        
    }

    public void mousePressed(MouseEvent e) {
        this.play.allMoves = bot.game.allAvailableMoves(bot.game.whiteToMove);
        for(Move move:play.allMoves) {
            move.showMove();
        }
        System.out.println(e.getX() + " " + e.getY());
        MouseInBoard check = isMouseOverSquare(e);
        int xIndex = check.xIndex;
        int yIndex = check.yIndex;
        if(check.isMouseInBoard) {
            ArrayList<Piece> pieces = this.bot.game.board.getPieceColour(this.bot.game.whiteToMove);
            for(Piece piece: pieces) {
                if(piece.x == xIndex && piece.y == yIndex) {
                    System.out.println(piece.name);
                    panel.mousePressed = true;
                    panel.pieceSelected = piece;
                    panel.pieceSelected.dragged = true;
                    break;
                   
                }
            }
        }
        
    }

    private MouseInBoard isMouseOverSquare(MouseEvent e) {
        Point xy = e.getPoint();
        int x,y;
        x = xy.x;
        y = xy.y;

        int xMin,xMax, yMin,yMax, xIndex=0, yIndex=0;
        boolean inXRange = false;
        boolean inYRange = false;
        for(int i = 0; i<8; i++) {
            xMin = panel.xOffset + i * panel.squareWidth;
            xMax = panel.xOffset + (i+1) * panel.squareWidth;
            if(x > xMin && x < xMax) {
                inXRange = true;
                xIndex = i;
                break;
            }
        }

        for(int j=0; j<8;j++) {
            yMin = panel.yOffset + j* panel.squareHeight;
            yMax = panel.yOffset + (j+1)*panel.squareHeight;
            if(y > yMin && y<yMax) {
                inYRange = true;
                yIndex = 7-j;
                break;
            }
        }

        if(inXRange && inYRange) {
            MouseInBoard outObj = new MouseInBoard(xIndex,yIndex,true);
            return outObj;
        }
        
        MouseInBoard outObj = new MouseInBoard(false);
        return outObj;
    }

    public void mouseReleased(MouseEvent e) {
        
        mousePressed = false;
        if(panel.pieceSelected != null && panel.pieceSelected.dragged) {
            panel.pieceSelected.dragged = false;
            MouseInBoard check = isMouseOverSquare(e);
            if(check.isMouseInBoard) {
                int xIndex = check.xIndex;
                int yIndex = check.yIndex;
                for(Move move: play.allMoves) {
                    if(move.moveTo[0] == xIndex && move.moveTo[1] == yIndex && move.piece == panel.pieceSelected) {
                        // Move is valid so execute move.
                        // As only human moves can be executed with mouse can just use executeMove.
                        bot.game.executeMove(move, true);
                        bot.game.whiteToMove = !bot.game.whiteToMove;
                        play.allMoves = play.bot.game.allAvailableMoves(bot.game.whiteToMove);
                        
                    }
                }
            }
            
        }
        this.panel.repaint();
        

    }



}

class myMouseMotionAdapter extends MouseMotionAdapter {
    ChessCanvas panel;
    ChessBot bot;
    Play play;
    

    public myMouseMotionAdapter(ChessCanvas panel, Play play) {
        this.panel = panel;
        this.bot = play.bot;
        this.play = play;
        
    }

    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        panel.lastMousePos = p;
        if(panel.pieceSelected != null && panel.pieceSelected.dragged) {
            panel.pieceSelected.mousePos = p;
            panel.repaint();
        }
    }

}



class Render extends JPanel {
    
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Play play;
    ChessBot bot;
    int windowWidth;
    int windowHeight;
    JFrame f;
    float perc;
    Render(Play play,int windowWidth, int windowHeight,  float perc) {
        this.perc = perc;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.play = play;
        this.bot = this.play.bot;
        this.f = new JFrame("Chess Game");
       
        
        f.add(new ChessCanvas(this.play,windowWidth, windowHeight,perc));
      
        f.setLayout(null);
        
        f.setSize(this.windowWidth, this.windowHeight);
        
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                f.dispose();
            }
        });
        f.setVisible(true);
        
        
        
    }


}

class ChessCanvas extends JPanel  {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    Play play;
    ChessBot bot;
    float perc;
    Point lastMousePos;
    boolean mousePressed = false;
    Piece pieceSelected;
    int height, width, squareHeight, squareWidth, xOffset, yOffset, chessHeight, chessWidth;

    public ChessCanvas(Play play,int windowWidth, int windowHeight,float perc) {
        setBackground(Color.decode("#35654d"));
        setSize(windowWidth, windowHeight);
        this.play = play;
        this.bot = play.bot;
        this.perc = perc;
        height = this.getHeight();
        width = this.getWidth();

        chessHeight = Math.round(height * perc);
        chessWidth = Math.round(width * perc);
        squareHeight = (int)Math.round(chessHeight / 8);
        squareWidth  = (int)Math.round(chessWidth  / 8);
  

        scalePieceImages(squareWidth,squareHeight);


        


        xOffset = Math.round((height - chessHeight) / 2);
        yOffset = (int)Math.round((height - chessWidth)/ 2);
        this.addMouseListener(new myMouseAdapter(this,play));
        this.addMouseMotionListener(new myMouseMotionAdapter(this, play));
        
    }

    private void scalePieceImages(int squareWidth, int squareHeight) {   
        // There must be a better way of doing this but I don't know how.

        King.whiteImg = King.whiteImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);
        King.blackImg = King.blackImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);

        Queen.whiteImg = Queen.whiteImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);
        Queen.blackImg = Queen.blackImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);

        Rook.whiteImg = Rook.whiteImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);
        Rook.blackImg = Rook.blackImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);

        Knight.whiteImg = Knight.whiteImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);
        Knight.blackImg = Knight.blackImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);

        Pawn.whiteImg = Pawn.whiteImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);
        Pawn.blackImg = Pawn.blackImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);

        Bishop.whiteImg = Bishop.whiteImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);
        Bishop.blackImg = Bishop.blackImg.getScaledInstance(squareWidth, squareHeight, java.awt.Image.SCALE_SMOOTH);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintChessBoard(g,(float)perc);
        paintChessPieces(g,(float)perc);
        
        

    }


    public void paintChessPieces(Graphics g, float perc) {

        Board board = this.bot.game.board;
        int x,y;
        drawPiecesOfColor(true,g, squareHeight, squareWidth, xOffset, yOffset, board);
        drawPiecesOfColor(false, g, squareHeight, squareWidth, xOffset, yOffset, board);
        
       

    }

    private void drawPiecesOfColor(boolean white, Graphics g, int squareHeight, int squareWidth, int xOffset, int yOffset, Board board) {
        int x;
        int y;
        ArrayList<Piece> pieces = this.bot.game.board.getPieceColour(white);
        for(Piece piece: pieces) {
            
            
            Image img = piece.getImg();
            if(piece.dragged) {
                x = piece.mousePos.x - squareWidth/2;
                y = piece.mousePos.y - squareHeight/2;
            } else {
                x = piece.x * squareWidth + xOffset;
                y = (7-piece.y) * squareHeight+ yOffset;
            }
             // maybe 7 - piece.y
            if(img != null) {
                
                g.drawImage(img, x, y, this);
                
            }
        }
    }

    public BufferedImage getPieceImg( Piece piece) {
        String col;
        BufferedImage img = null;
        if(piece.white) {
            col = "White";
        } else {
            col = "Black";
        }
        String fileName = piece.name + "_" + col + ".png";
        try {
            img = ImageIO.read(new File("chessSprites/" + fileName));
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return img;

    }

    public void paintChessBoard(Graphics g, float perc) {
        int height = this.getHeight();
        int width = this.getWidth();

        int chessHeight = Math.round(height * perc);
        int chessWidth = Math.round(width * perc);

        int xOffset = Math.round((height - chessHeight) / 2);
        int yOffset = (int)Math.round((height - chessWidth)/ 2);

        Color darkColor = Color.gray;
        Color lightColor = new Color(255,255,255);

        int squareHeight = (int)Math.round(chessHeight / 8);
        int squareWidth  = (int)Math.round(chessWidth  / 8);
        g.setColor(lightColor);
        for(int evens = 0; evens < 32; evens++){
            int xNum = evens % 4;
            int yIndex = (int)Math.floor(evens / 4);
            int xIndex;
            if(yIndex % 2 == 0) {
                xIndex = xNum * 2; // 0 -> 0, 1->2, 2->4
            } else {
                xIndex = xNum * 2 + 1; // 0 -> 1, 1-> 3, 2-> 5
            }
            int x = xOffset + xIndex * squareWidth;
            int y = yOffset + yIndex * squareHeight; 

            g.fillRect(x, y, squareWidth, squareHeight);
        }
        g.setColor(darkColor);
        for(int odds = 0; odds < 32; odds++){
            int xNum = odds % 4;
            int yIndex = (int)Math.floor(odds / 4);
            int xIndex;
            if(yIndex % 2 == 0) {
                xIndex = xNum * 2 + 1; // 0 -> 0, 1->2, 2->4
            } else {
                xIndex = xNum * 2; // 0 -> 1, 1-> 3, 2-> 5
            }
            int x = xOffset + xIndex * squareWidth;
            int y = yOffset + yIndex * squareHeight; 

            g.fillRect(x, y, squareWidth, squareHeight);
        }


        
    }
}