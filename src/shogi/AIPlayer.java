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
        
        // Csak egyszer vizsgáljuk meg minden bábu típust (nem ismétlődve)
        java.util.Set<String> processedTypes = new java.util.HashSet<>();
        
        for (Piece piece : hand) {
            String pieceType = piece.getClass().getSimpleName();
            
            // Ha már feldolgoztuk ezt a típust, ugorjuk át
            if (processedTypes.contains(pieceType)) {
                continue;
            }
            processedTypes.add(pieceType);
            
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    Position to = new Position(row, col);
                    
                    // Csak üres mezőkre próbálhatunk drop-olni
                    if (board.getPieceAt(row, col) == null) {
                        // Teszteljük, hogy a dropPiece elfogadná-e ezt a lépést
                        // (ellenőrzi a pawn szabályokat, sakk helyzetet, stb.)
                        if (isValidDrop(game, pieceType, to)) {
                            moves.add(new Move(pieceType, to));
                        }
                    }
                }
            }
        }
        
        return moves;
    }
    
    /**
     * Ellenőrzi, hogy egy drop lépés érvényes-e.
     * @param game A játék
     * @param pieceType A bábu típusa
     * @param to Hová helyezünk
     * @return true, ha a drop lépés szabályos
     */
    private boolean isValidDrop(ShogiGame game, String pieceType, Position to) {
        // Szimulálunk egy drop-ot és ellenőrizzük, hogy sikeres-e
        // Ezt nem tudjuk közvetlenül, ezért egyszerűsített ellenőrzés:
        
        Board board = game.getBoard();
        
        // Alapvető ellenőrzések (ezeket a dropPiece is csinálja):
        // 1. Mező üres?
        if (board.getPieceAt(to.getRow(), to.getCol()) != null) {
            return false;
        }
        
        // 2. Pawn speciális szabályok
        if ("Pawn".equals(pieceType)) {
            // Nem lehet olyan oszlopba, ahol már van saját gyalog
            for (int r = 0; r < 9; r++) {
                Piece p = board.getPieceAt(r, to.getCol());
                if (p instanceof Pawn && 
                    p.getColor() == getColor() && !p.isPromoted()) {
                    return false;
                }
            }
            
            // Knight és Lance nem mehet olyan helyre, ahonnan nem tud lépni
            // (Pawn esetén utolsó sor, Lance/Knight esetén speciális)
            if (getColor() == Piece.Color.BLACK && to.getRow() == 0) {
                return false; // BLACK pawn nem mehet a 0. sorba
            }
            if (getColor() == Piece.Color.WHITE && to.getRow() == 8) {
                return false; // WHITE pawn nem mehet a 8. sorba
            }
        }
        
        // 3. Lance nem mehet olyan helyre, ahonnan nem tud lépni
        if ("Lance".equals(pieceType)) {
            if (getColor() == Piece.Color.BLACK && to.getRow() == 0) {
                return false;
            }
            if (getColor() == Piece.Color.WHITE && to.getRow() == 8) {
                return false;
            }
        }
        
        // 4. Knight nem mehet olyan helyre, ahonnan nem tud lépni
        if ("Knight".equals(pieceType)) {
            if (getColor() == Piece.Color.BLACK && to.getRow() <= 1) {
                return false;
            }
            if (getColor() == Piece.Color.WHITE && to.getRow() >= 7) {
                return false;
            }
        }
        
        // 5. KRITIKUS: Sakkban hagyás ellenőrzés
        // Ha sakkban vagyunk, a drop NEM oldja meg a sakkot
        // (csak király mozgás vagy blokkolás/leütés oldhatja meg)
        if (game.isInCheck(getColor())) {
            // Szimulálunk egy drop-ot
            try {
                // Ideiglenesen létrehozzuk a bábut
                Piece tempPiece = createPieceByType(pieceType, getColor(), to);
                board.setPieceAt(to.getRow(), to.getCol(), tempPiece);
                
                // Ellenőrizzük, hogy még mindig sakkban vagyunk-e
                boolean stillInCheck = game.isInCheck(getColor());
                
                // Visszaállítjuk
                board.setPieceAt(to.getRow(), to.getCol(), null);
                
                return !stillInCheck; // Csak akkor OK, ha már nincs sakkban
            } catch (Exception e) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Létrehoz egy bábu példányt típus alapján.
     * @param pieceType A bábu típusa
     * @param color A bábu színe
     * @param position A bábu pozíciója
     * @return Az új bábu példány
     */
    private Piece createPieceByType(String pieceType, Piece.Color color, Position position) {
        switch (pieceType) {
            case "Pawn": return new Pawn(color, position);
            case "Lance": return new Lance(color, position);
            case "Knight": return new Knight(color, position);
            case "SilverGeneral": return new SilverGeneral(color, position);
            case "GoldGeneral": return new GoldGeneral(color, position);
            case "Bishop": return new Bishop(color, position);
            case "Rook": return new Rook(color, position);
            case "King": return new King(color, position);
            default: throw new IllegalArgumentException("Unknown piece type: " + pieceType);
        }
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
