package shogi.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * JUnit tesztek a Piece osztályok canMoveTo() és getLegalMoves() metódusaihoz.
 * Teszteli különböző figurák mozgási szabályait.
 */
class PieceTest {
    
    private Board board;
    
    @BeforeEach
    void setUp() {
        board = new Board();
    }
    
    // ===================================================================
    //                      PAWN TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("Pawn egyszerű előrelépés")
    void testPawnSimpleMove() {
        Pawn blackPawn = new Pawn(Piece.Color.BLACK, new Position(6, 4));
        board.setPieceAt(6, 4, blackPawn);
        
        List<Position> moves = blackPawn.getLegalMoves(board);
        
        assertTrue(moves.contains(new Position(5, 4)), 
            "BLACK Pawn-nak előre kell tudnia lépni");
        assertEquals(1, moves.size(), "Pawn csak egy mezőt léphet előre");
    }
    
    @Test
    @DisplayName("Pawn nem léphet hátra")
    void testPawnCannotMoveBackward() {
        Pawn blackPawn = new Pawn(Piece.Color.BLACK, new Position(5, 4));
        board.setPieceAt(5, 4, blackPawn);
        
        List<Position> moves = blackPawn.getLegalMoves(board);
        
        assertFalse(moves.contains(new Position(6, 4)), 
            "Pawn nem léphet hátra");
    }
    
    @Test
    @DisplayName("Pawn nem léphet elfoglalt mezőre")
    void testPawnCannotMoveToOccupiedSquare() {
        Pawn blackPawn = new Pawn(Piece.Color.BLACK, new Position(6, 4));
        Pawn whitePawn = new Pawn(Piece.Color.WHITE, new Position(5, 4));
        
        board.setPieceAt(6, 4, blackPawn);
        board.setPieceAt(5, 4, whitePawn);
        
        List<Position> moves = blackPawn.getLegalMoves(board);
        
        assertFalse(moves.contains(new Position(5, 4)), 
            "Pawn nem léphet ellenséges bábura (nem ütheti le frontálisan)");
    }
    
    @Test
    @DisplayName("Promoted Pawn (Tokin) Gold General-ként mozog")
    void testPromotedPawnMovement() {
        Pawn blackPawn = new Pawn(Piece.Color.BLACK, new Position(4, 4));
        blackPawn.promote();
        board.setPieceAt(4, 4, blackPawn);
        
        List<Position> moves = blackPawn.getLegalMoves(board);
        
        // Gold General mozgás: előre, oldalra, átlósan előre, hátra
        assertTrue(moves.contains(new Position(3, 4)), "Tokin lép előre");
        assertTrue(moves.contains(new Position(4, 3)), "Tokin lép oldalra");
        assertTrue(moves.contains(new Position(4, 5)), "Tokin lép oldalra");
        assertTrue(moves.contains(new Position(3, 3)), "Tokin lép átlósan előre");
        assertTrue(moves.contains(new Position(3, 5)), "Tokin lép átlósan előre");
        assertTrue(moves.contains(new Position(5, 4)), "Tokin lép hátra");
    }
    
    // ===================================================================
    //                      BISHOP TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("Bishop átlósan mozog")
    void testBishopDiagonalMovement() {
        Bishop whiteBishop = new Bishop(Piece.Color.WHITE, new Position(4, 4));
        board.setPieceAt(4, 4, whiteBishop);
        
        List<Position> moves = whiteBishop.getLegalMoves(board);
        
        // Átlós irányok
        assertTrue(moves.contains(new Position(5, 5)), "Bishop átlósan le-jobbra");
        assertTrue(moves.contains(new Position(3, 3)), "Bishop átlósan fel-balra");
        assertTrue(moves.contains(new Position(5, 3)), "Bishop átlósan le-balra");
        assertTrue(moves.contains(new Position(3, 5)), "Bishop átlósan fel-jobbra");
        
        // Több lépés is lehetséges
        assertTrue(moves.contains(new Position(6, 6)), "Bishop több mezőt is léphet");
        assertTrue(moves.contains(new Position(2, 2)), "Bishop több mezőt is léphet");
    }
    
    @Test
    @DisplayName("Bishop mozgását akadály blokkolja")
    void testBishopBlockedByPiece() {
        Bishop whiteBishop = new Bishop(Piece.Color.WHITE, new Position(4, 4));
        Pawn whitePawn = new Pawn(Piece.Color.WHITE, new Position(5, 5));
        
        board.setPieceAt(4, 4, whiteBishop);
        board.setPieceAt(5, 5, whitePawn);
        
        List<Position> moves = whiteBishop.getLegalMoves(board);
        
        assertFalse(moves.contains(new Position(5, 5)), 
            "Bishop nem léphet saját bábura");
        assertFalse(moves.contains(new Position(6, 6)), 
            "Bishop nem léphet akadály mögé");
    }
    
    @Test
    @DisplayName("Bishop leüthet ellenséges bábut")
    void testBishopCanCapture() {
        Bishop whiteBishop = new Bishop(Piece.Color.WHITE, new Position(4, 4));
        Pawn blackPawn = new Pawn(Piece.Color.BLACK, new Position(5, 5));
        
        board.setPieceAt(4, 4, whiteBishop);
        board.setPieceAt(5, 5, blackPawn);
        
        List<Position> moves = whiteBishop.getLegalMoves(board);
        
        assertTrue(moves.contains(new Position(5, 5)), 
            "Bishop leüthet ellenséges bábut");
        assertFalse(moves.contains(new Position(6, 6)), 
            "Bishop nem léphet leütött bábu mögé");
    }
    
    // ===================================================================
    //                      ROOK TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("Rook egyenesen mozog (vízszintes és függőleges)")
    void testRookStraightMovement() {
        Rook blackRook = new Rook(Piece.Color.BLACK, new Position(4, 4));
        board.setPieceAt(4, 4, blackRook);
        
        List<Position> moves = blackRook.getLegalMoves(board);
        
        // Függőleges irányok
        assertTrue(moves.contains(new Position(3, 4)), "Rook felfelé");
        assertTrue(moves.contains(new Position(5, 4)), "Rook lefelé");
        assertTrue(moves.contains(new Position(0, 4)), "Rook több mezőt felfelé");
        assertTrue(moves.contains(new Position(8, 4)), "Rook több mezőt lefelé");
        
        // Vízszintes irányok
        assertTrue(moves.contains(new Position(4, 3)), "Rook balra");
        assertTrue(moves.contains(new Position(4, 5)), "Rook jobbra");
        assertTrue(moves.contains(new Position(4, 0)), "Rook több mezőt balra");
        assertTrue(moves.contains(new Position(4, 8)), "Rook több mezőt jobbra");
    }
    
    @Test
    @DisplayName("Rook nem mozog átlósan")
    void testRookCannotMoveDiagonally() {
        Rook blackRook = new Rook(Piece.Color.BLACK, new Position(4, 4));
        board.setPieceAt(4, 4, blackRook);
        
        List<Position> moves = blackRook.getLegalMoves(board);
        
        assertFalse(moves.contains(new Position(5, 5)), "Rook nem léphet átlósan");
        assertFalse(moves.contains(new Position(3, 3)), "Rook nem léphet átlósan");
    }
    
    // ===================================================================
    //                      KING TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("King egy mezőt mozog minden irányba")
    void testKingMovement() {
        King blackKing = new King(Piece.Color.BLACK, new Position(4, 4));
        board.setPieceAt(4, 4, blackKing);
        
        List<Position> moves = blackKing.getLegalMoves(board);
        
        assertEquals(8, moves.size(), "King 8 irányba léphet");
        
        // Mind a 8 irány
        assertTrue(moves.contains(new Position(3, 4)), "King fel");
        assertTrue(moves.contains(new Position(5, 4)), "King le");
        assertTrue(moves.contains(new Position(4, 3)), "King balra");
        assertTrue(moves.contains(new Position(4, 5)), "King jobbra");
        assertTrue(moves.contains(new Position(3, 3)), "King átlósan fel-balra");
        assertTrue(moves.contains(new Position(3, 5)), "King átlósan fel-jobbra");
        assertTrue(moves.contains(new Position(5, 3)), "King átlósan le-balra");
        assertTrue(moves.contains(new Position(5, 5)), "King átlósan le-jobbra");
    }
    
    @Test
    @DisplayName("King nem léphet több mezőt")
    void testKingCannotMoveMultipleSquares() {
        King blackKing = new King(Piece.Color.BLACK, new Position(4, 4));
        board.setPieceAt(4, 4, blackKing);
        
        List<Position> moves = blackKing.getLegalMoves(board);
        
        assertFalse(moves.contains(new Position(2, 4)), 
            "King nem léphet 2 mezőt");
        assertFalse(moves.contains(new Position(6, 6)), 
            "King nem léphet 2 mezőt átlósan");
    }
    
    // ===================================================================
    //                  GOLD GENERAL TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("Gold General mozgása helyes")
    void testGoldGeneralMovement() {
        GoldGeneral blackGold = new GoldGeneral(Piece.Color.BLACK, new Position(4, 4));
        board.setPieceAt(4, 4, blackGold);
        
        List<Position> moves = blackGold.getLegalMoves(board);
        
        assertEquals(6, moves.size(), "Gold General 6 irányba léphet");
        
        assertTrue(moves.contains(new Position(3, 4)), "Gold előre");
        assertTrue(moves.contains(new Position(5, 4)), "Gold hátra");
        assertTrue(moves.contains(new Position(4, 3)), "Gold balra");
        assertTrue(moves.contains(new Position(4, 5)), "Gold jobbra");
        assertTrue(moves.contains(new Position(3, 3)), "Gold átlósan előre-balra");
        assertTrue(moves.contains(new Position(3, 5)), "Gold átlósan előre-jobbra");
        
        // NEM léphet hátra-átlósan
        assertFalse(moves.contains(new Position(5, 3)), 
            "Gold nem léphet hátra-átlósan");
        assertFalse(moves.contains(new Position(5, 5)), 
            "Gold nem léphet hátra-átlósan");
    }
    
    // ===================================================================
    //                  SILVER GENERAL TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("Silver General mozgása helyes")
    void testSilverGeneralMovement() {
        SilverGeneral blackSilver = new SilverGeneral(Piece.Color.BLACK, new Position(4, 4));
        board.setPieceAt(4, 4, blackSilver);
        
        List<Position> moves = blackSilver.getLegalMoves(board);
        
        assertEquals(5, moves.size(), "Silver General 5 irányba léphet");
        
        assertTrue(moves.contains(new Position(3, 4)), "Silver előre");
        assertTrue(moves.contains(new Position(3, 3)), "Silver átlósan előre-balra");
        assertTrue(moves.contains(new Position(3, 5)), "Silver átlósan előre-jobbra");
        assertTrue(moves.contains(new Position(5, 3)), "Silver átlósan hátra-balra");
        assertTrue(moves.contains(new Position(5, 5)), "Silver átlósan hátra-jobbra");
        
        // NEM léphet oldalra vagy egyenesen hátra
        assertFalse(moves.contains(new Position(4, 3)), 
            "Silver nem léphet oldalra");
        assertFalse(moves.contains(new Position(4, 5)), 
            "Silver nem léphet oldalra");
        assertFalse(moves.contains(new Position(5, 4)), 
            "Silver nem léphet egyenesen hátra");
    }
    
    // ===================================================================
    //                      KNIGHT TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("Knight L-alakban ugrik")
    void testKnightMovement() {
        Knight blackKnight = new Knight(Piece.Color.BLACK, new Position(7, 4));
        board.setPieceAt(7, 4, blackKnight);
        
        List<Position> moves = blackKnight.getLegalMoves(board);
        
        // BLACK Knight: 2 előre + 1 oldalra
        assertTrue(moves.contains(new Position(5, 3)), "Knight L-alakban balra");
        assertTrue(moves.contains(new Position(5, 5)), "Knight L-alakban jobbra");
        assertEquals(2, moves.size(), "Knight csak 2 irányba ugorhat (előre)");
    }
    
    @Test
    @DisplayName("Knight átugrik akadályokat")
    void testKnightJumpsOverPieces() {
        Knight blackKnight = new Knight(Piece.Color.BLACK, new Position(7, 4));
        Pawn blockingPawn = new Pawn(Piece.Color.BLACK, new Position(6, 4));
        
        board.setPieceAt(7, 4, blackKnight);
        board.setPieceAt(6, 4, blockingPawn);
        
        List<Position> moves = blackKnight.getLegalMoves(board);
        
        // Knight még mindig léphet, mert átugrik
        assertEquals(2, moves.size(), "Knight átugrik akadályokon");
        assertTrue(moves.contains(new Position(5, 3)));
        assertTrue(moves.contains(new Position(5, 5)));
    }
    
    // ===================================================================
    //                      LANCE TESZTEK
    // ===================================================================
    
    @Test
    @DisplayName("Lance egyenesen előre mozog")
    void testLanceForwardMovement() {
        Lance blackLance = new Lance(Piece.Color.BLACK, new Position(8, 0));
        board.setPieceAt(8, 0, blackLance);
        
        List<Position> moves = blackLance.getLegalMoves(board);
        
        // Lance előre léphet több mezőt is
        assertTrue(moves.contains(new Position(7, 0)), "Lance 1 előre");
        assertTrue(moves.contains(new Position(6, 0)), "Lance 2 előre");
        assertTrue(moves.contains(new Position(5, 0)), "Lance 3 előre");
        assertTrue(moves.contains(new Position(0, 0)), "Lance a tábla végéig");
    }
    
    @Test
    @DisplayName("Lance nem léphet oldalra vagy hátra")
    void testLanceCannotMoveSidewaysOrBackward() {
        Lance blackLance = new Lance(Piece.Color.BLACK, new Position(4, 4));
        board.setPieceAt(4, 4, blackLance);
        
        List<Position> moves = blackLance.getLegalMoves(board);
        
        // Csak előre
        assertFalse(moves.contains(new Position(4, 3)), "Lance nem lép oldalra");
        assertFalse(moves.contains(new Position(4, 5)), "Lance nem lép oldalra");
        assertFalse(moves.contains(new Position(5, 4)), "Lance nem lép hátra");
    }
}
