package shogi;

import shogi.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * AI játékos, amely automatikusan választ véletlenszerű, de szabályos lépést.
 * 
 * Implementációs stratégia:
 * 1. Összes legális lépés összegyűjtése (táblán lévő bábuk és drop lépések)
 * 2. Véletlenszerű lépés választása a listaból
 * 3. Lépés végrehajtása
 * 
 * Jelenleg nincs stratégiai értékelés vagy előretekintés.
 * 
 * @author Domokos Erik Zsolt
 */
public class AIPlayer extends Player {
    
    private Random random;
    
    /**
     * Létrehoz egy új AI játékost.
     * @param name Az AI neve
     * @param color Az AI színe (BLACK vagy WHITE)
     */
    public AIPlayer(String name, Piece.Color color) {
        super(name, color);
        this.random = new Random();
    }
    
    /**
     * Az AI választ egy véletlenszerű, de szabályos lépést.
     * @param game A jelenlegi játékállás
     * @return true, ha sikerült lépést választani és végrehajtani
     */
    public boolean makeMove(ShogiGame game) {
        // Összes lehetséges lépés összegyűjtése
        List<Move> possibleMoves = getAllPossibleMoves(game);
        
        if (possibleMoves.isEmpty()) {
            return false; // Nincs legális lépés
        }
        
        // Véletlenszerű lépés kiválasztása
        Move selectedMove = possibleMoves.get(random.nextInt(possibleMoves.size()));
        
        // Lépés végrehajtása
        if (selectedMove.isDropMove()) {
            return game.dropPiece(selectedMove.getPieceType(), selectedMove.getTo());
        } else {
            return game.makeMove(selectedMove.getFrom(), selectedMove.getTo());
        }
    }
    
    /**
     * Összegyűjti az összes lehetséges lépést a jelenlegi pozícióban.
     * @param game A jelenlegi játékállás
     * @return A lehetséges lépések listája
     */
    private List<Move> getAllPossibleMoves(ShogiGame game) {
        List<Move> moves = new ArrayList<>();
        Board board = game.getBoard();
        
        // 1. Táblán lévő bábuk lépései
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getColor() == getColor()) {
                    Position from = new Position(row, col);
                    List<Position> legalMoves = piece.getLegalMoves(board);
                    
                    for (Position to : legalMoves) {
                        // Ellenőrizzük, hogy a lépés után nem maradunk-e sakkban
                        if (isLegalMove(game, from, to)) {
                            moves.add(new Move(from, to));
                        }
                    }
                }
            }
        }
        
        // 2. Drop lépések (kézben lévő bábuk visszahelyezése)
        List<Piece> hand = (getColor() == Piece.Color.BLACK) 
            ? game.getBlackHand() 
            : game.getWhiteHand();
        
        for (Piece piece : hand) {
            String pieceType = piece.getClass().getSimpleName();
            
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Position to = new Position(row, col);
                    
                    // Próbáljuk meg elhelyezni (dropPiece saját maga ellenőrzi a szabályokat)
                    if (board.getPieceAt(row, col) == null) {
                        // Ideiglenesen tároljuk az állapotot
                        Piece.Color currentPlayer = game.getCurrentPlayer();
                        
                        // Ha ez a mi körünk, próbáljuk meg
                        if (currentPlayer == getColor()) {
                            moves.add(new Move(pieceType, to));
                        }
                    }
                }
            }
        }
        
        return moves;
    }
    
    /**
     * Ellenőrzi, hogy egy lépés legális-e (nem hagy minket sakkban).
     * @param game A játék
     * @param from Honnan
     * @param to Hová
     * @return true, ha a lépés szabályos
     */
    private boolean isLegalMove(ShogiGame game, Position from, Position to) {
        Board board = game.getBoard();
        
        // Mentsük el az állapotot
        Piece moving = board.getPieceAt(from.getRow(), from.getCol());
        Piece target = board.getPieceAt(to.getRow(), to.getCol());
        
        // Ideiglenesen végrehajtjuk
        board.setPieceAt(to.getRow(), to.getCol(), moving);
        board.setPieceAt(from.getRow(), from.getCol(), null);
        
        // Ellenőrizzük a sakkot
        boolean inCheck = game.isInCheck(getColor());
        
        // Visszaállítjuk
        board.setPieceAt(from.getRow(), from.getCol(), moving);
        board.setPieceAt(to.getRow(), to.getCol(), target);
        
        return !inCheck;
    }
    
    /**
     * Belső osztály a lépések tárolására.
     */
    private static class Move {
        private Position from;
        private Position to;
        private String pieceType; // Drop esetén
        private boolean isDrop;
        
        // Normál lépés
        public Move(Position from, Position to) {
            this.from = from;
            this.to = to;
            this.isDrop = false;
        }
        
        // Drop lépés
        public Move(String pieceType, Position to) {
            this.pieceType = pieceType;
            this.to = to;
            this.isDrop = true;
        }
        
        public Position getFrom() {
            return from;
        }
        
        public Position getTo() {
            return to;
        }
        
        public String getPieceType() {
            return pieceType;
        }
        
        public boolean isDropMove() {
            return isDrop;
        }
    }
}
