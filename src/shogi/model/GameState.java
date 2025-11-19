package shogi.model;

import java.util.List;

/**
 * Játékállás adatai JSON szerializációhoz.
 * Egyszerű POJO osztály, amely tartalmazza a játék teljes állapotát.
 * A SaveManager használja a mentés/betöltés során.
 * 
 * @author Domokos Erik Zsolt
 */
public class GameState {
    
    /** Tábla állása: 9×9-es mező, minden mező lehet null vagy PieceData */
    public PieceData[][] board;
    
    /** Jelenlegi játékos ("BLACK" vagy "WHITE") */
    public String currentPlayer;
    
    /** Fekete játékos fogott bábui (kéz) */
    public List<PieceData> blackHand;
    
    /** Fehér játékos fogott bábui (kéz) */
    public List<PieceData> whiteHand;
    
    /**
     * Egy bábu adatait reprezentálja szerializálható formában.
     * Tartalmazza a bábu típusát, színét, pozícióját és promóciós állapotát.
     */
    public static class PieceData {
        public String type;      // pl. "King", "Rook", "Pawn", stb.
        public String color;     // "BLACK" vagy "WHITE"
        public int row;
        public int col;
        public boolean promoted;
        
        public PieceData() {}
        
        public PieceData(String type, String color, int row, int col, boolean promoted) {
            this.type = type;
            this.color = color;
            this.row = row;
            this.col = col;
            this.promoted = promoted;
        }
    }
}
