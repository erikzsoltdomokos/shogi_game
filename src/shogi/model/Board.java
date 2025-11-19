package shogi.model;

/**
 * Shogi játéktábla reprezentációja.
 * 9×9-es rács, amely tárolja a figurák pozícióit.
 * 
 * @author Domokos Erik Zsolt
 */
public class Board {

    /** A tábla rácsszerkezete - 9×9-es mátrix */
    private Piece[][] grid = new Piece[9][9];

    /**
     * Ellenőrzi, hogy egy koordináta a táblán belül van-e.
     * 
     * @param row sor index (0-8)
     * @param col oszlop index (0-8)
     * @return true, ha a koordináta érvényes
     */
    public boolean isInside(int row, int col) {
        return row >= 0 && col >= 0 && row < 9 && col < 9;
    }

    /**
     * Alias az isInside() metódushoz.
     * 
     * @param row sor index
     * @param col oszlop index
     * @return true, ha a koordináta a táblán belül van
     */
    public boolean isOnBoard(int row, int col) {
        return isInside(row, col);
    }

    /**
     * Alias az isInside() metódushoz.
     * 
     * @param row sor index
     * @param col oszlop index
     * @return true, ha a koordináta érvényes
     */
    public boolean isInBounds(int row, int col) {
        return isInside(row, col);
    }

    /**
     * Ellenőrzi, hogy egy mező foglalt-e.
     * 
     * @param row sor index
     * @param col oszlop index
     * @return true, ha a mezőn van bábu (és a koordináta érvényes)
     */
    public boolean isOccupied(int row, int col) {
        if (!isInBounds(row, col)) {
            return false;
        }
        return getPieceAt(row, col) != null;
    }

    /**
     * Lekéri egy adott mezőn lévő bábut.
     * 
     * @param row sor index
     * @param col oszlop index
     * @return a mezőn lévő bábu, vagy null ha üres
     */
    public Piece getPieceAt(int row, int col) {
        return grid[row][col];
    }

    /**
     * Elhelyez egy bábut a megadott mezőre.
     * Automatikusan beállítja a bábu pozícióját és irányát.
     * 
     * @param row sor index
     * @param col oszlop index
     * @param piece az elhelyezendő bábu (null esetén törli a mezőt)
     */
    public void setPieceAt(int row, int col, Piece piece) {
        grid[row][col] = piece;

        if (piece != null) {
            piece.setPosition(new Position(row, col));
            piece.updateForwardDirection();
        }
    }

    /**
     * Mozgat egy bábut egyik pozícióról a másikra.
     * Ha a célmezőn bábu van, azt leüti (visszaadja).
     * 
     * @param from kiindulási pozíció
     * @param to célpozíció
     * @return a leütött bábu, vagy null ha nem volt ott semmi
     */
    public Piece movePiece(Position from, Position to) {
        Piece movingPiece = getPieceAt(from.getRow(), from.getCol());
        if (movingPiece == null) {
            return null;
        }

        // Leütött bábu mentése
        Piece capturedPiece = getPieceAt(to.getRow(), to.getCol());
        
        // Bábu mozgatása
        setPieceAt(to.getRow(), to.getCol(), movingPiece);
        
        // Forrás mező ürítése
        grid[from.getRow()][from.getCol()] = null;
        
        return capturedPiece;
    }
}
