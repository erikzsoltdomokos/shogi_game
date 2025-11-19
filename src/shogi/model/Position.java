package shogi.model;

/**
 * Táblán lévő pozíció reprezentációja.
 * Immutable (megváltoztathatatlan) objektum a biztonságos használat érdekében.
 * 
 * @author Domokos Erik Zsolt
 */
public class Position {

    /** Sor index (0-8) */
    private final int row;
    
    /** Oszlop index (0-8) */
    private final int col;

    /**
     * Létrehoz egy új pozíciót.
     * 
     * @param row sor index (0-8)
     * @param col oszlop index (0-8)
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return sor index
     */
    public int getRow() {
        return row;
    }

    /**
     * @return oszlop index
     */
    public int getCol() {
        return col;
    }

    /**
     * Két pozíció egyenlőségét vizsgálja.
     * 
     * @param o összehasonlítandó objektum
     * @return true, ha mindkét koordináta megegyezik
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
            return false;
        }
        Position pos = (Position) o;
        return row == pos.row && col == pos.col;
    }

    /**
     * Hash kód generálása.
     * 
     * @return hash kód
     */
    @Override
    public int hashCode() {
        return row * 31 + col;
    }

    /**
     * Szöveges reprezentáció.
     * 
     * @return pozíció stringként, pl. "(3,5)"
     */
    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
