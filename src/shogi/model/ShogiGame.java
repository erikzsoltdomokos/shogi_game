package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A Shogi játék fő logikai osztálya.
 * Kezeli a táblát, a játékosok kezét, a lépéseket és a játék szabályait.
 * 
 * @author Domokos Erik Zsolt
 */
public class ShogiGame {

    /** A játéktábla (9×9 rács) */
    private Board board;
    
    /** Az aktuális játékos (BLACK vagy WHITE) */
    private Piece.Color currentPlayer;
    
    /** Fekete játékos leütött bábui (kezében lévő bábuk) */
    private List<Piece> blackHand = new ArrayList<>();
    
    /** Fehér játékos leütött bábui (kezében lévő bábuk) */
    private List<Piece> whiteHand = new ArrayList<>();

    /**
     * Új játék létrehozása kezdési állással.
     * Fekete játékos kezd.
     */
    public ShogiGame() {
        board = new Board();
        currentPlayer = Piece.Color.BLACK;
        setupInitialPosition();
    }

    /**
     * @return a játéktábla
     */
    public Board getBoard() {
        return board;
    }

    /**
     * @return az aktuális játékos színe
     */
    public Piece.Color getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return fekete játékos kezében lévő bábuk másolata
     */
    public List<Piece> getBlackHand() {
        return new ArrayList<>(blackHand);
    }

    /**
     * @return fehér játékos kezében lévő bábuk másolata
     */
    public List<Piece> getWhiteHand() {
        return new ArrayList<>(whiteHand);
    }

    /**
     * Vált a következő játékosra.
     */
    private void switchPlayer() {
        currentPlayer = (currentPlayer == Piece.Color.BLACK ? Piece.Color.WHITE : Piece.Color.BLACK);
    }

    // ===================================================================
    //                        KEZDŐÁLLÁS BEÁLLÍTÁSA
    // ===================================================================
    
    /**
     * Beállítja a standard shogi kezdési pozíciót.
     * Fekete (BLACK) a 8-6. sorokon, Fehér (WHITE) a 0-2. sorokon.
     */
    private void setupInitialPosition() {

        // --- BLACK oldal ---
        board.setPieceAt(8, 0, new Lance(Piece.Color.BLACK, new Position(8, 0)));
        board.setPieceAt(8, 1, new Knight(Piece.Color.BLACK, new Position(8, 1)));
        board.setPieceAt(8, 2, new SilverGeneral(Piece.Color.BLACK, new Position(8, 2)));
        board.setPieceAt(8, 3, new GoldGeneral(Piece.Color.BLACK, new Position(8, 3)));
        board.setPieceAt(8, 4, new King(Piece.Color.BLACK, new Position(8, 4)));
        board.setPieceAt(8, 5, new GoldGeneral(Piece.Color.BLACK, new Position(8, 5)));
        board.setPieceAt(8, 6, new SilverGeneral(Piece.Color.BLACK, new Position(8, 6)));
        board.setPieceAt(8, 7, new Knight(Piece.Color.BLACK, new Position(8, 7)));
        board.setPieceAt(8, 8, new Lance(Piece.Color.BLACK, new Position(8, 8)));

        board.setPieceAt(7, 1, new Rook(Piece.Color.BLACK, new Position(7, 1)));
        board.setPieceAt(7, 7, new Bishop(Piece.Color.BLACK, new Position(7, 7)));

        for (int c = 0; c < 9; c++)
            board.setPieceAt(6, c, new Pawn(Piece.Color.BLACK, new Position(6, c)));

        // --- WHITE oldal ---
        board.setPieceAt(0, 0, new Lance(Piece.Color.WHITE, new Position(0, 0)));
        board.setPieceAt(0, 1, new Knight(Piece.Color.WHITE, new Position(0, 1)));
        board.setPieceAt(0, 2, new SilverGeneral(Piece.Color.WHITE, new Position(0, 2)));
        board.setPieceAt(0, 3, new GoldGeneral(Piece.Color.WHITE, new Position(0, 3)));
        board.setPieceAt(0, 4, new King(Piece.Color.WHITE, new Position(0, 4)));
        board.setPieceAt(0, 5, new GoldGeneral(Piece.Color.WHITE, new Position(0, 5)));
        board.setPieceAt(0, 6, new SilverGeneral(Piece.Color.WHITE, new Position(0, 6)));
        board.setPieceAt(0, 7, new Knight(Piece.Color.WHITE, new Position(0, 7)));
        board.setPieceAt(0, 8, new Lance(Piece.Color.WHITE, new Position(0, 8)));

        board.setPieceAt(1, 1, new Bishop(Piece.Color.WHITE, new Position(1, 1)));
        board.setPieceAt(1, 7, new Rook(Piece.Color.WHITE, new Position(1, 7)));

        for (int c = 0; c < 9; c++)
            board.setPieceAt(2, c, new Pawn(Piece.Color.WHITE, new Position(2, c)));
    }


    // ===================================================================
    //                            MOVE
    // ===================================================================
    public boolean makeMove(Position from, Position to) {
        Piece p = board.getPieceAt(from.getRow(), from.getCol());
        if (p == null) {
            return false;
        }

        if (p.getColor() != currentPlayer) {
            return false;
        }

        List<Position> moves = p.getLegalMoves(board);

        if (!moves.contains(to)) {
            return false;
        }

        // Ellenőrizzük, hogy a lépés után ne legyen sakkban a saját király
        if (!wouldEscapeCheck(from, to, currentPlayer)) {
            return false; // Ez a lépés sakkban hagyná a királyt
        }

        Piece target = board.getPieceAt(to.getRow(), to.getCol());
        
        // KRITIKUS: Királyt nem lehet kiütni!
        if (target instanceof King) {
            return false; // Illegális lépés - király kiütése nem megengedett
        }
        
        if (target != null) {
            capturePiece(target, p.getColor());
        }

        board.movePiece(from, to);
        handlePromotion(p, from, to);

        switchPlayer();
        return true;
    }


    // ===================================================================
    //                           CAPTURE
    // ===================================================================
    private void capturePiece(Piece target, Piece.Color capturerColor) {
        // 1) ELTÁVOLÍTJUK A TÁBLÁRÓL
        int r = target.getPosition().getRow();
        int c = target.getPosition().getCol();
        board.setPieceAt(r, c, null);

        // 2) PROMÓCIÓ TÖRLÉSE
        target.unpromote();

        // 3) SZÍNVÁLTÁS (kézbe kerül)
        target.color = capturerColor;
        target.updateForwardDirection();

        // 4) POZÍCIÓ TÖRLÉSE (kézben nincs pozíció)
        target.setPosition(null);

        // 5) KÉZHEZ ADÁS
        if (capturerColor == Piece.Color.BLACK) {
            blackHand.add(target);
        } else {
            whiteHand.add(target);
        }
    }



    // ===================================================================
    //                         DROP
    // ===================================================================
    public boolean dropPiece(String pieceType, Position to) {
        List<Piece> hand = (currentPlayer == Piece.Color.BLACK) ? blackHand : whiteHand;

        Class<?> cls;
        try {
            cls = Class.forName("shogi.model." + pieceType);
        } catch (Exception e) {
            return false;
        }

        Piece found = null;
        for (Piece p : hand) {
            if (cls.isInstance(p)) {
                found = p;
                break;
            }
        }

        if (found == null) {
            return false;
        }

        if (board.getPieceAt(to.getRow(), to.getCol()) != null) {
            return false;
        }

        if (!isDropAllowed(found, to)) {
            return false;
        }

        found.setPosition(to);
        found.updateForwardDirection();
        board.setPieceAt(to.getRow(), to.getCol(), found);
        hand.remove(found);

        // KRITIKUS: Pawn Drop Mate ellenőrzés
        // Gyalog drop-pal NEM lehet mattot adni (Shogi szabály)
        if (found instanceof Pawn && !found.isPromoted()) {
            Piece.Color opponent = (currentPlayer == Piece.Color.BLACK) 
                ? Piece.Color.WHITE : Piece.Color.BLACK;
            
            if (isInCheck(opponent) && isCheckmate(opponent)) {
                // Ez Pawn Drop Mate lenne - ILLEGÁLIS!
                // Visszavonjuk a drop-ot
                board.setPieceAt(to.getRow(), to.getCol(), null);
                found.setPosition(null);
                hand.add(found);
                return false;
            }
        }

        switchPlayer();
        return true;
    }


    // ===================================================================
    //                     DROP RULES (NIFU)
    // ===================================================================
    private boolean isDropAllowed(Piece p, Position to) {
        if (p instanceof Pawn) {
            if (isPawnInColumn(currentPlayer, to.getCol())) {
                return false;
            }

            if (isAtLastRank(p, to)) {
                return false;
            }
        }

        return true;
    }


    private boolean isPawnInColumn(Piece.Color color, int col) {
        for (int r = 0; r < 9; r++) {
            Piece piece = board.getPieceAt(r, col);
            if (piece instanceof Pawn && !piece.isPromoted() && piece.getColor() == color) {
                return true;
            }
        }
        return false;
    }


    // ===================================================================
    //                         PROMOTION
    // ===================================================================
    private void handlePromotion(Piece p, Position from, Position to) {

        if (p.isPromoted()) return;

        boolean promotable =
                p instanceof Pawn ||
                p instanceof Lance ||
                p instanceof Knight ||
                p instanceof SilverGeneral ||
                p instanceof Bishop ||
                p instanceof Rook;

        if (!promotable) return;

        boolean fromZone = isInPromotionZone(p.getColor(), from);
        boolean toZone   = isInPromotionZone(p.getColor(), to);

        if (!fromZone && !toZone) return;

        if (p instanceof Pawn || p instanceof Lance) {
            if (isAtLastRank(p, to)) {
                p.promote();
                return;
            }
        }

        if (p instanceof Knight) {
            if (isAtCantMoveRank(p, to)) {
                p.promote();
                return;
            }
        }

        p.promote();
    }


    private boolean isInPromotionZone(Piece.Color color, Position pos) {
        if (color == Piece.Color.BLACK)
            return pos.getRow() <= 2;
        else
            return pos.getRow() >= 6;
    }

    private boolean isAtLastRank(Piece p, Position to) {
        if (p.getColor() == Piece.Color.BLACK)
            return to.getRow() == 0;
        else
            return to.getRow() == 8;
    }

    private boolean isAtCantMoveRank(Piece p, Position to) {
        if (p.getColor() == Piece.Color.BLACK)
            return to.getRow() <= 1;
        else
            return to.getRow() >= 7;
    }


    // ===================================================================
    //                         CHECK / CHECKMATE
    // ===================================================================
    
    /**
     * Megkeresi a megadott színű királyt a táblán.
     * @param color Melyik király?
     * @return A király pozíciója vagy null, ha nincs meg
     */
    private Position findKing(Piece.Color color) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Piece p = board.getPieceAt(r, c);
                if (p instanceof King && p.getColor() == color) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }
    
    /**
     * Ellenőrzi, hogy egy adott mezőt támad-e az ellenfél.
     * @param pos Melyik mezőt vizsgáljuk?
     * @param defender Kinek a mezőjét védjük? (az ellenfél fog támadni)
     * @return true, ha az ellenfél támadja ezt a mezőt
     */
    private boolean isSquareUnderAttack(Position pos, Piece.Color defender) {
        Piece.Color attacker = (defender == Piece.Color.BLACK) ? Piece.Color.WHITE : Piece.Color.BLACK;
        
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Piece p = board.getPieceAt(r, c);
                if (p != null && p.getColor() == attacker) {
                    // A bábu jelenlegi pozícióját be kell állítani
                    Position originalPos = p.getPosition();
                    p.setPosition(new Position(r, c));
                    
                    List<Position> moves = p.getLegalMoves(board);
                    
                    // Visszaállítjuk az eredeti pozíciót
                    p.setPosition(originalPos);
                    
                    if (moves.contains(pos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Ellenőrzi, hogy a megadott színű király sakkban van-e.
     * @param color Melyik király?
     * @return true, ha sakkban van
     */
    public boolean isInCheck(Piece.Color color) {
        Position kingPos = findKing(color);
        if (kingPos == null) {
            return false; // Nincs király (nem kellene előfordulnia)
        }
        return isSquareUnderAttack(kingPos, color);
    }
    
    /**
     * Ellenőrzi, hogy a játék véget ért-e valamelyik játékosnak.
     * A játék véget ér, ha:
     * 1. A király ki lett ütve (nincs többé a táblán)
     * 2. Sakkmatt áll fenn
     * 
     * @param color Melyik játékos?
     * @return true, ha a játék véget ért ennek a játékosnak
     */
    public boolean isGameOver(Piece.Color color) {
        // Ellenőrizzük, hogy létezik-e még a király
        Position kingPos = findKing(color);
        if (kingPos == null) {
            return true; // Király kiütve = játék vége
        }
        
        // Ellenőrizzük a sakkmattot
        return isCheckmate(color);
    }
    
    /**
     * Ellenőrzi, hogy a megadott színű király sakk-mattban van-e.
     * Sakk-matt = sakkban van ÉS nincs olyan legális lépés, ami megmentené.
     * @param color Melyik király?
     * @return true, ha sakk-matt
     */
    public boolean isCheckmate(Piece.Color color) {
        // Ha nincs király, akkor nem sakkmatt, hanem vége a játéknak
        Position kingPos = findKing(color);
        if (kingPos == null) {
            return false;
        }
        
        if (!isInCheck(color)) {
            return false; // Nincs sakkban, akkor nem lehet matt sem
        }
        
        // Végignézzük a játékos összes bábuját és minden lehetséges lépését
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                Piece p = board.getPieceAt(r, c);
                if (p == null || p.getColor() != color) {
                    continue;
                }
                
                Position from = new Position(r, c);
                // A bábu jelenlegi pozícióját be kell állítani
                Position originalPos = p.getPosition();
                p.setPosition(from);
                
                List<Position> moves = p.getLegalMoves(board);
                
                // Visszaállítjuk az eredeti pozíciót
                p.setPosition(originalPos);
                
                for (Position to : moves) {
                    // Szimulálunk egy lépést
                    if (wouldEscapeCheck(from, to, color)) {
                        return false; // Van legális lépés → nincs matt
                    }
                }
            }
        }
        
        return true; // Nincs megmenekülés → matt
    }
    
    /**
     * Szimulálja egy lépést, és ellenőrzi, hogy utána már nem lenne-e sakkban a király.
     * @param from Honnan
     * @param to Hová
     * @param color Melyik játékos királya?
     * @return true, ha a lépés után már nem vagyunk sakkban
     */
    private boolean wouldEscapeCheck(Position from, Position to, Piece.Color color) {
        // Elmentsük az állapotot
        Piece moving = board.getPieceAt(from.getRow(), from.getCol());
        Piece target = board.getPieceAt(to.getRow(), to.getCol());
        
        // Végrehajtjuk ideiglenesen a lépést
        board.setPieceAt(to.getRow(), to.getCol(), moving);
        board.setPieceAt(from.getRow(), from.getCol(), null);
        
        // Ellenőrizzük a sakkot
        boolean stillInCheck = isInCheck(color);
        
        // Visszaállítjuk az eredeti állapotot
        board.setPieceAt(from.getRow(), from.getCol(), moving);
        board.setPieceAt(to.getRow(), to.getCol(), target);
        
        return !stillInCheck;
    }
    
    // ===================================================================
    //                  SAVE/LOAD SUPPORT METHODS
    // ===================================================================
    
    /**
     * Tábla ürítése (mentés/betöltéshez használt).
     */
    public void clearBoard() {
        board = new Board();
        blackHand.clear();
        whiteHand.clear();
    }
    
    /**
     * Jelenlegi játékos beállítása.
     */
    public void setCurrentPlayer(Piece.Color player) {
        this.currentPlayer = player;
    }
    
    /**
     * Bábu hozzáadása egy játékos kezéhez.
     */
    public void addToHand(Piece.Color color, Piece piece) {
        if (color == Piece.Color.BLACK) {
            blackHand.add(piece);
        } else {
            whiteHand.add(piece);
        }
    }
    
    // ===================================================================
    //                    IMPASSE (入玉) RULE
    // ===================================================================
    
    /**
     * Ellenőrzi az Impasse (入玉) helyzetet.
     * Ha mindkét király átjutott az ellenfél térfelére és nincs sakk,
     * akkor pont alapján döntődik el a győztes.
     * 
     * BLACK király: 0-2. sor = ellenfél tere
     * WHITE király: 6-8. sor = ellenfél tere
     * 
     * Pontozás:
     * - Király: számít, de nem kap pontot
     * - Rook/Bishop: 5 pont
     * - Többi bábu: 1 pont
     * - Promótált bábuk: dupla pont
     * 
     * Minimális követelmény:
     * - 28+ pont → döntetlen javaslat
     * - 31+ pont → győzelem igénylés (ha mindkét király ellenfél terén)
     * 
     * @return ImpasseResult objektum az eredménnyel
     */
    public ImpasseResult checkImpasse() {
        // Mindkét király pozíciójának keresése
        Position blackKingPos = findKing(Piece.Color.BLACK);
        Position whiteKingPos = findKing(Piece.Color.WHITE);
        
        if (blackKingPos == null || whiteKingPos == null) {
            return new ImpasseResult(false, null, 0, 0);
        }
        
        // Ellenőrizzük, hogy mindkét király az ellenfél térfelén van-e
        boolean blackKingInEnemyTerritory = blackKingPos.getRow() <= 2;
        boolean whiteKingInEnemyTerritory = whiteKingPos.getRow() >= 6;
        
        if (!blackKingInEnemyTerritory && !whiteKingInEnemyTerritory) {
            return new ImpasseResult(false, null, 0, 0);
        }
        
        // Egyik vagy mindkét király ellenfél terén → számoljuk a pontokat
        int blackPoints = calculateImpassePoints(Piece.Color.BLACK);
        int whitePoints = calculateImpassePoints(Piece.Color.WHITE);
        
        // Ha mindkét király ellenfél terén van
        if (blackKingInEnemyTerritory && whiteKingInEnemyTerritory) {
            if (blackPoints >= 31 && whitePoints >= 31) {
                return new ImpasseResult(true, null, blackPoints, whitePoints); // Döntetlen
            } else if (blackPoints >= 31) {
                return new ImpasseResult(true, Piece.Color.BLACK, blackPoints, whitePoints);
            } else if (whitePoints >= 31) {
                return new ImpasseResult(true, Piece.Color.WHITE, blackPoints, whitePoints);
            } else if (blackPoints >= 28 && whitePoints >= 28) {
                return new ImpasseResult(true, null, blackPoints, whitePoints); // Döntetlen javaslat
            }
        }
        
        return new ImpasseResult(false, null, blackPoints, whitePoints);
    }
    
    /**
     * Kiszámolja egy játékos pontjait az Impasse szabály szerint.
     * Csak az ellenfél térfelén lévő és kézben tartott bábuk számítanak.
     * 
     * @param color Melyik játékos?
     * @return A pontok száma
     */
    private int calculateImpassePoints(Piece.Color color) {
        int points = 0;
        
        // Táblán lévő bábuk az ellenfél térfelén
        int enemyStart = (color == Piece.Color.BLACK) ? 0 : 6;
        int enemyEnd = (color == Piece.Color.BLACK) ? 2 : 8;
        
        for (int row = enemyStart; row <= enemyEnd; row++) {
            for (int col = 0; col < 9; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getColor() == color && !(piece instanceof King)) {
                    points += getPieceValue(piece);
                }
            }
        }
        
        // Kézben tartott bábuk
        List<Piece> hand = (color == Piece.Color.BLACK) ? blackHand : whiteHand;
        for (Piece piece : hand) {
            points += getPieceValue(piece);
        }
        
        return points;
    }
    
    /**
     * Meghatározza egy bábu pontértékét az Impasse szabály szerint.
     * 
     * @param piece A bábu
     * @return A pontérték
     */
    private int getPieceValue(Piece piece) {
        if (piece instanceof King) {
            return 0; // Király nem számít
        }
        
        int baseValue;
        if (piece instanceof Rook || piece instanceof Bishop) {
            baseValue = 5;
        } else {
            baseValue = 1; // Pawn, Lance, Knight, Silver, Gold
        }
        
        // Promótált bábuk nem kapnak extra pontot az Impasse számításban
        // (a szabály szerint csak a bábu típusa számít)
        return baseValue;
    }
    
    /**
     * Impasse eredmény tároló osztály.
     */
    public static class ImpasseResult {
        /** True ha Impasse helyzet áll fenn */
        public final boolean isImpasse;
        
        /** Győztes szín (null ha döntetlen) */
        public final Piece.Color winner;
        
        /** BLACK pontszám */
        public final int blackPoints;
        
        /** WHITE pontszám */
        public final int whitePoints;
        
        public ImpasseResult(boolean isImpasse, Piece.Color winner, int blackPoints, int whitePoints) {
            this.isImpasse = isImpasse;
            this.winner = winner;
            this.blackPoints = blackPoints;
            this.whitePoints = whitePoints;
        }
    }

}
