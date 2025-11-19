package shogi.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

/**
 * JUnit tesztek a SaveManager osztály save() és load() metódusaihoz.
 * Teszteli a játékállás mentését és betöltését JSON formátumban.
 */
class SaveManagerTest {
    
    private static final String TEST_SAVE_FILE = "test_save.json";
    private ShogiGame game;
    
    @BeforeEach
    void setUp() {
        game = new ShogiGame();
        // Töröljük a teszt fájlt, ha létezik
        new File(TEST_SAVE_FILE).delete();
    }
    
    // ===================================================================
    //                      MENTÉS TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("save() létrehozza a mentési fájlt")
    void testSaveCreatesFile() throws IOException {
        SaveManager.save(game, TEST_SAVE_FILE);
        
        File file = new File(TEST_SAVE_FILE);
        assertTrue(file.exists(), "Mentési fájl létrejött");
        assertTrue(file.length() > 0, "Fájl nem üres");
        
        // Cleanup
        file.delete();
    }
    
    @Test
    @DisplayName("save() helyes JSON formátumot hoz létre")
    void testSaveCreatesValidJson() throws IOException {
        SaveManager.save(game, TEST_SAVE_FILE);
        
        // Próbáljuk betölteni - ha nem dobódik kivétel, akkor valid JSON
        assertDoesNotThrow(() -> SaveManager.load(TEST_SAVE_FILE), 
            "A mentett fájl valid JSON");
        
        // Cleanup
        new File(TEST_SAVE_FILE).delete();
    }
    
    @Test
    @DisplayName("save() hibát dob érvénytelen útvonallal")
    void testSaveWithInvalidPath() {
        assertThrows(IOException.class, () -> {
            SaveManager.save(game, "/invalid/path/that/does/not/exist/save.json");
        }, "Érvénytelen útvonal IOException-t dob");
    }
    
    // ===================================================================
    //                      BETÖLTÉS TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("load() visszaállítja a játékállást")
    void testLoadRestoresGameState() throws IOException {
        // Eredeti játék módosítása
        game.makeMove(new Position(6, 0), new Position(5, 0));
        game.makeMove(new Position(2, 0), new Position(3, 0));
        
        Piece.Color originalPlayer = game.getCurrentPlayer();
        
        // Mentés
        SaveManager.save(game, TEST_SAVE_FILE);
        
        // Betöltés
        ShogiGame loadedGame = SaveManager.load(TEST_SAVE_FILE);
        
        assertNotNull(loadedGame, "Betöltött játék nem null");
        assertEquals(originalPlayer, loadedGame.getCurrentPlayer(), 
            "Jelenlegi játékos egyezik");
        
        // Cleanup
        new File(TEST_SAVE_FILE).delete();
    }
    
    @Test
    @DisplayName("load() visszaállítja a tábla állapotát")
    void testLoadRestoresBoardState() throws IOException {
        // Módosítjuk a táblát
        game.makeMove(new Position(6, 4), new Position(5, 4));
        
        SaveManager.save(game, TEST_SAVE_FILE);
        ShogiGame loadedGame = SaveManager.load(TEST_SAVE_FILE);
        
        // Ellenőrizzük, hogy ugyanaz a tábla
        assertNull(loadedGame.getBoard().getPieceAt(6, 4), 
            "Eredeti pozíció üres");
        assertNotNull(loadedGame.getBoard().getPieceAt(5, 4), 
            "Új pozíción van bábu");
        
        Piece movedPiece = loadedGame.getBoard().getPieceAt(5, 4);
        assertEquals(Piece.Color.BLACK, movedPiece.getColor(), 
            "Bábu színe helyes");
        
        // Cleanup
        new File(TEST_SAVE_FILE).delete();
    }
    
    @Test
    @DisplayName("load() visszaállítja a fogott bábukat")
    void testLoadRestoresHands() throws IOException {
        // Leütünk egy bábut
        game.makeMove(new Position(6, 2), new Position(5, 2));
        game.makeMove(new Position(2, 2), new Position(3, 2));
        game.makeMove(new Position(5, 2), new Position(4, 2));
        game.makeMove(new Position(2, 0), new Position(3, 0));
        game.makeMove(new Position(4, 2), new Position(3, 2)); // Leütés
        
        int blackHandSize = game.getBlackHand().size();
        int whiteHandSize = game.getWhiteHand().size();
        
        SaveManager.save(game, TEST_SAVE_FILE);
        ShogiGame loadedGame = SaveManager.load(TEST_SAVE_FILE);
        
        assertEquals(blackHandSize, loadedGame.getBlackHand().size(), 
            "BLACK kéz mérete egyezik");
        assertEquals(whiteHandSize, loadedGame.getWhiteHand().size(), 
            "WHITE kéz mérete egyezik");
        
        // Cleanup
        new File(TEST_SAVE_FILE).delete();
    }
    
    @Test
    @DisplayName("load() hibát dob nem létező fájllal")
    void testLoadWithNonExistentFile() {
        assertThrows(IOException.class, () -> {
            SaveManager.load("non_existent_file.json");
        }, "Nem létező fájl IOException-t dob");
    }
    
    // ===================================================================
    //                  MENTÉS ÉS BETÖLTÉS ÖSSZETETT TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("Mentés és betöltés több lépés után")
    void testSaveAndLoadAfterMultipleMoves() throws IOException {
        // Egyszerű lépések végrehajtása
        game.makeMove(new Position(6, 4), new Position(5, 4)); // BLACK Pawn középen előre
        game.makeMove(new Position(2, 4), new Position(3, 4)); // WHITE Pawn középen előre
        
        Piece.Color playerBeforeSave = game.getCurrentPlayer();
        Piece pawnBlack = game.getBoard().getPieceAt(5, 4);
        Piece pawnWhite = game.getBoard().getPieceAt(3, 4);
        
        // Mentés és betöltés
        SaveManager.save(game, TEST_SAVE_FILE);
        ShogiGame loadedGame = SaveManager.load(TEST_SAVE_FILE);
        
        // Ellenőrzések
        assertEquals(playerBeforeSave, loadedGame.getCurrentPlayer(), 
            "Játékos egyezik");
        
        Piece pawnBlackAfter = loadedGame.getBoard().getPieceAt(5, 4);
        Piece pawnWhiteAfter = loadedGame.getBoard().getPieceAt(3, 4);
        
        assertNotNull(pawnBlackAfter, "BLACK Pawn betöltve");
        assertNotNull(pawnWhiteAfter, "WHITE Pawn betöltve");
        
        assertEquals(pawnBlack.getClass(), pawnBlackAfter.getClass(), 
            "BLACK Pawn típusa egyezik");
        assertEquals(pawnWhite.getClass(), pawnWhiteAfter.getClass(), 
            "WHITE Pawn típusa egyezik");
        assertEquals(pawnBlack.getColor(), pawnBlackAfter.getColor(), 
            "BLACK Pawn színe egyezik");
        assertEquals(pawnWhite.getColor(), pawnWhiteAfter.getColor(), 
            "WHITE Pawn színe egyezik");
        
        // Cleanup
        new File(TEST_SAVE_FILE).delete();
    }
    
    @Test
    @DisplayName("Promóció megőrzése mentés/betöltés során")
    void testSaveAndLoadPreservesPromotion() throws IOException {
        // Szimuláljunk egy promóciót manuálisan
        Pawn pawn = new Pawn(Piece.Color.BLACK, new Position(2, 4));
        pawn.promote();
        game.getBoard().setPieceAt(2, 4, pawn);
        
        SaveManager.save(game, TEST_SAVE_FILE);
        ShogiGame loadedGame = SaveManager.load(TEST_SAVE_FILE);
        
        Piece loadedPawn = loadedGame.getBoard().getPieceAt(2, 4);
        assertNotNull(loadedPawn, "Promótált Pawn betöltve");
        assertTrue(loadedPawn.isPromoted(), "Promóció megmaradt");
        
        // Cleanup
        new File(TEST_SAVE_FILE).delete();
    }
    
    @Test
    @DisplayName("Üres tábla mentése és betöltése")
    void testSaveAndLoadEmptyBoard() throws IOException {
        ShogiGame emptyGame = new ShogiGame();
        emptyGame.clearBoard();
        
        SaveManager.save(emptyGame, TEST_SAVE_FILE);
        ShogiGame loadedGame = SaveManager.load(TEST_SAVE_FILE);
        
        assertNotNull(loadedGame, "Üres játék betöltve");
        
        // Ellenőrizzük, hogy tényleg üres
        boolean isEmpty = true;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (loadedGame.getBoard().getPieceAt(r, c) != null) {
                    isEmpty = false;
                    break;
                }
            }
        }
        
        assertTrue(isEmpty, "Betöltött tábla üres");
        
        // Cleanup
        new File(TEST_SAVE_FILE).delete();
    }
}
