package shogi.model;

import java.util.List;

/**
 * Absztrakt ősosztály minden shogi bábunak.
 * Definiálja a közös tulajdonságokat és viselkedéseket.
 * 
 * @author Domokos Erik Zsolt
 */
public abstract class Piece {

    /**
     * Bábu színe - BLACK (fekete/下手) vagy WHITE (fehér/上手).
     */
    public enum Color { 
        /** Fekete játékos (kezdő) */
        BLACK, 
        /** Fehér játékos */
        WHITE 
    }

    /** A bábu színe */
    protected Color color;
    
    /** A bábu jelenlegi pozíciója a táblán (null ha kézben van) */
    protected Position position;
    
    /** Előrelépési irány: -1 (BLACK, felfelé) vagy +1 (WHITE, lefelé) */
    protected int forward;

    /** Promóció jelző - true ha a bábu promótált */
    protected boolean promoted = false;

    /**
     * Létrehoz egy új bábut.
     * 
     * @param color a bábu színe
     * @param position a bábu kezdeti pozíciója
     */
    protected Piece(Color color, Position position) {
        this.color = color;
        this.position = position;
        updateForwardDirection();
    }

    /**
     * @return a bábu színe
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return a bábu jelenlegi pozíciója (lehet null)
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Beállítja a bábu pozícióját.
     * 
     * @param newPos az új pozíció (null ha kézben van)
     */
    public void setPosition(Position newPos) {
        this.position = newPos;
    }

    /**
     * @return true, ha a bábu promótált
     */
    public boolean isPromoted() {
        return promoted;
    }

    /**
     * Promótálja a bábut (átalakítja).
     */
    public void promote() {
        this.promoted = true;
    }

    /**
     * Visszaalakítja a bábut alaphelyzetbe (pl. leütés után).
     */
    public void unpromote() {
        this.promoted = false;
    }

    /**
     * Meghatározza a bábu lehetséges legális lépéseit
     * a jelenlegi táblaállás alapján.
     * 
     * @param board a játéktábla aktuális állapota
     * @return a lehetséges célpozíciók listája
     */
    public abstract List<Position> getLegalMoves(Board board);

    /**
     * A bábu karakteres/kanji szimbóluma megjelenítéshez.
     * 
     * @return a bábu szimbóluma stringként
     */
    public abstract String getSymbol();
    
    /**
     * Frissíti a bábu előrelépési irányát a színe alapján.
     * BLACK → -1 (felfelé, csökkenő sor index)
     * WHITE → +1 (lefelé, növekvő sor index)
     */
    public void updateForwardDirection() {
        this.forward = (color == Color.BLACK ? -1 : +1);
    }
}
