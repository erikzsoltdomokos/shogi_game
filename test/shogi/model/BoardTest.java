package shogi.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tesztek a Board osztály metódusaihoz.
 * Teszteli: isOccupied(), movePiece(), setPieceAt(), getPieceAt()
 */
class BoardTest {
    
    private Board board;
    
    @BeforeEach
    void setUp() {
        board = new Board();
    }
    
    // ===================================================================
    //                  isOccupied() TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("Üres mező nem foglalt")
    void testEmptySquareIsNotOccupied() {
        assertFalse(board.isOccupied(4, 4), 
            "Üres mező nem lehet foglalt");
    }
    
    @Test
    @DisplayName("Bábuval elfoglalt mező foglalt")
    void testOccupiedSquare() {
        Pawn pawn = new Pawn(Piece.Color.BLACK, new Position(4, 4));
        board.setPieceAt(4, 4, pawn);
        
        assertTrue(board.isOccupied(4, 4), 
            "Bábuval elfoglalt mező foglalt kell legyen");
    }
    
    @Test
    @DisplayName("isOccupied() hibás koordinátákkal")
    void testIsOccupiedWithInvalidCoordinates() {
        assertFalse(board.isOccupied(-1, 4), 
            "Negatív koordináta nem lehet foglalt");
        assertFalse(board.isOccupied(9, 4), 
            "Táblán kívüli koordináta nem lehet foglalt");
        assertFalse(board.isOccupied(4, 10), 
            "Táblán kívüli koordináta nem lehet foglalt");
    }
    
    // ===================================================================
    //                  setPieceAt() és getPieceAt() TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("setPieceAt() és getPieceAt() helyesen működik")
    void testSetAndGetPiece() {
        Pawn pawn = new Pawn(Piece.Color.BLACK, new Position(3, 5));
        board.setPieceAt(3, 5, pawn);
        
        Piece retrieved = board.getPieceAt(3, 5);
        
        assertNotNull(retrieved, "A visszakapott bábu nem lehet null");
        assertEquals(pawn, retrieved, "A visszakapott bábu megegyezik a beállítottal");
        assertEquals(Piece.Color.BLACK, retrieved.getColor(), "Szín egyezik");
    }
    
    @Test
    @DisplayName("getPieceAt() null-t ad vissza üres mezőnél")
    void testGetPieceAtEmptySquare() {
        Piece piece = board.getPieceAt(4, 4);
        
        assertNull(piece, "Üres mezőn null-t kell visszaadni");
    }
    
    @Test
    @DisplayName("setPieceAt() null-lal törli a mezőt")
    void testSetPieceAtWithNull() {
        Pawn pawn = new Pawn(Piece.Color.BLACK, new Position(3, 5));
        board.setPieceAt(3, 5, pawn);
        
        assertTrue(board.isOccupied(3, 5), "Mező foglalt");
        
        board.setPieceAt(3, 5, null);
        
        assertFalse(board.isOccupied(3, 5), "Mező üres lett");
        assertNull(board.getPieceAt(3, 5), "Nincs bábu a mezőn");
    }
    
    // ===================================================================
    //                  movePiece() TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("movePiece() áthelyezi a bábut")
    void testMovePiece() {
        Pawn pawn = new Pawn(Piece.Color.BLACK, new Position(6, 4));
        board.setPieceAt(6, 4, pawn);
        
        Piece captured = board.movePiece(new Position(6, 4), new Position(5, 4));
        
        assertNull(captured, "Nem történt leütés");
        assertNull(board.getPieceAt(6, 4), "Eredeti mező üres");
        assertNotNull(board.getPieceAt(5, 4), "Új helyen van a bábu");
        assertEquals(pawn, board.getPieceAt(5, 4), "Ugyanaz a bábu");
    }
    
    @Test
    @DisplayName("movePiece() leütéssel visszaadja a leütött bábut")
    void testMovePieceWithCapture() {
        Pawn blackPawn = new Pawn(Piece.Color.BLACK, new Position(5, 4));
        Pawn whitePawn = new Pawn(Piece.Color.WHITE, new Position(4, 4));
        
        board.setPieceAt(5, 4, blackPawn);
        board.setPieceAt(4, 4, whitePawn);
        
        Piece captured = board.movePiece(new Position(5, 4), new Position(4, 4));
        
        assertNotNull(captured, "Leütés történt");
        assertEquals(whitePawn, captured, "A leütött bábu a WHITE Pawn");
        assertEquals(Piece.Color.WHITE, captured.getColor(), "Leütött bábu színe");
        assertNotNull(board.getPieceAt(4, 4), "Új helyen a BLACK Pawn van");
        assertEquals(blackPawn, board.getPieceAt(4, 4), "Mozgó bábu az új helyen");
    }
    
    @Test
    @DisplayName("movePiece() frissíti a bábu pozícióját")
    void testMovePieceUpdatesPosition() {
        Pawn pawn = new Pawn(Piece.Color.BLACK, new Position(6, 4));
        board.setPieceAt(6, 4, pawn);
        
        board.movePiece(new Position(6, 4), new Position(5, 4));
        
        Position newPos = pawn.getPosition();
        assertEquals(5, newPos.getRow(), "Pozíció sor frissült");
        assertEquals(4, newPos.getCol(), "Pozíció oszlop frissült");
    }
    
    @Test
    @DisplayName("movePiece() null bábut nem mozgat")
    void testMovePieceWithNullPiece() {
        Piece captured = board.movePiece(new Position(4, 4), new Position(5, 5));
        
        assertNull(captured, "Nincs leütés");
        assertNull(board.getPieceAt(5, 5), "Célon nincs bábu");
        assertNull(board.getPieceAt(4, 4), "Forrás üres maradt");
    }
    
    // ===================================================================
    //                  isInBounds() TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("isInBounds() helyes koordinátákkal")
    void testIsInBoundsValid() {
        assertTrue(board.isInBounds(0, 0), "Bal felső sarok");
        assertTrue(board.isInBounds(8, 8), "Jobb alsó sarok");
        assertTrue(board.isInBounds(4, 4), "Tábla közepe");
    }
    
    @Test
    @DisplayName("isInBounds() hibás koordinátákkal")
    void testIsInBoundsInvalid() {
        assertFalse(board.isInBounds(-1, 0), "Negatív sor");
        assertFalse(board.isInBounds(0, -1), "Negatív oszlop");
        assertFalse(board.isInBounds(9, 0), "Túl nagy sor");
        assertFalse(board.isInBounds(0, 9), "Túl nagy oszlop");
        assertFalse(board.isInBounds(10, 10), "Mindkettő túl nagy");
    }
    
    // ===================================================================
    //                  ÖSSZETETT TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("Több bábu elhelyezése és lekérdezése")
    void testMultiplePiecesPlacement() {
        Pawn pawn1 = new Pawn(Piece.Color.BLACK, new Position(6, 0));
        Pawn pawn2 = new Pawn(Piece.Color.BLACK, new Position(6, 1));
        Rook rook = new Rook(Piece.Color.WHITE, new Position(1, 7));
        
        board.setPieceAt(6, 0, pawn1);
        board.setPieceAt(6, 1, pawn2);
        board.setPieceAt(1, 7, rook);
        
        assertTrue(board.isOccupied(6, 0), "Pawn1 helye foglalt");
        assertTrue(board.isOccupied(6, 1), "Pawn2 helye foglalt");
        assertTrue(board.isOccupied(1, 7), "Rook helye foglalt");
        
        assertEquals(pawn1, board.getPieceAt(6, 0));
        assertEquals(pawn2, board.getPieceAt(6, 1));
        assertEquals(rook, board.getPieceAt(1, 7));
    }
    
    @Test
    @DisplayName("Lépéssorozat tesztelése")
    void testSequenceOfMoves() {
        Pawn pawn = new Pawn(Piece.Color.BLACK, new Position(6, 4));
        board.setPieceAt(6, 4, pawn);
        
        // 1. lépés
        board.movePiece(new Position(6, 4), new Position(5, 4));
        assertNull(board.getPieceAt(6, 4), "Eredeti hely üres");
        assertEquals(pawn, board.getPieceAt(5, 4), "Új helyen van");
        
        // 2. lépés
        board.movePiece(new Position(5, 4), new Position(4, 4));
        assertNull(board.getPieceAt(5, 4), "Előző hely üres");
        assertEquals(pawn, board.getPieceAt(4, 4), "Végső helyen van");
        
        Position finalPos = pawn.getPosition();
        assertEquals(4, finalPos.getRow(), "Pozíció helyesen frissült");
        assertEquals(4, finalPos.getCol(), "Pozíció helyesen frissült");
    }
}
