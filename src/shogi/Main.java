package shogi;

import javax.swing.SwingUtilities;

/**
 * Shogi játék fő belépési pontja.
 * Indítja a grafikus felületet Swing segítségével.
 * 
 * @author Domokos Erik Zsolt
 */
public class Main {
    
    /**
     * Program belépési pontja.
     * Létrehozza a GUI-t az Event Dispatch Thread-en (EDT).
     * 
     * @param args Parancssori argumentumok (nem használt)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ShogiGUI::new);
    }
}
