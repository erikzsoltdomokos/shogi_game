package shogi;

import shogi.model.Piece;

/**
 * Játékos reprezentációja.
 * Tárolja a játékos nevét és színét (BLACK vagy WHITE).
 * Azért különálló osztály, hogy az AIPlayer örökölhessen belőle.
 * 
 * @author Domokos Erik Zsolt
 */
public class Player {
    
    private String name;
    private Piece.Color color;
    
    /**
     * Létrehoz egy új játékost.
     * @param name A játékos neve
     * @param color A játékos színe (BLACK vagy WHITE)
     */
    public Player(String name, Piece.Color color) {
        this.name = name;
        this.color = color;
    }
    
    /**
     * @return A játékos neve
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return A játékos színe
     */
    public Piece.Color getColor() {
        return color;
    }
    
    /**
     * Beállítja a játékos nevét.
     * @param name Az új név
     */
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name + " (" + color + ")";
    }
}
