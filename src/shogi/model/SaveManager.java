package shogi.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;

/**
 * Játékállás mentése és betöltése JSON formátumban.
 * Gson könyvtárat használ a szerializációhoz/deszerializációhoz.
 * 
 * Használat:
 * - SaveManager.save(game, "mentes.json") - játék mentése
 * - ShogiGame game = SaveManager.load("mentes.json") - játék betöltése
 * 
 * @author Domokos Erik Zsolt
 */
public class SaveManager {
    
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    
    // Privát konstruktor - csak statikus metódusok
    private SaveManager() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * Játékállás mentése fájlba.
     * 
     * @param game A mentendő játék
     * @param filePath A mentési fájl útvonala
     * @throws IOException Ha a fájlírás sikertelen
     */
    public static void save(ShogiGame game, String filePath) throws IOException {
        GameState state = convertToGameState(game);
        
        try (Writer writer = new FileWriter(filePath)) {
            gson.toJson(state, writer);
        }
    }
    
    /**
     * Játékállás betöltése fájlból.
     * 
     * @param filePath A betöltendő fájl útvonala
     * @return Betöltött játék objektum
     * @throws IOException Ha a fájl olvasása sikertelen
     */
    public static ShogiGame load(String filePath) throws IOException {
        try (Reader reader = new FileReader(filePath)) {
            GameState state = gson.fromJson(reader, GameState.class);
            return convertFromGameState(state);
        }
    }
    
    /**
     * ShogiGame objektum átalakítása GameState-té (szerializálható formába).
     */
    private static GameState convertToGameState(ShogiGame game) {
        GameState state = new GameState();
        
        // Jelenlegi játékos
        state.currentPlayer = game.getCurrentPlayer().name();
        
        // Tábla állapota
        state.board = new GameState.PieceData[9][9];
        Board board = game.getBoard();
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null) {
                    state.board[row][col] = convertPieceToData(piece);
                }
            }
        }
        
        // Kéz (fogott bábuk)
        state.blackHand = new ArrayList<>();
        for (Piece piece : game.getBlackHand()) {
            state.blackHand.add(convertPieceToData(piece));
        }
        
        state.whiteHand = new ArrayList<>();
        for (Piece piece : game.getWhiteHand()) {
            state.whiteHand.add(convertPieceToData(piece));
        }
        
        return state;
    }
    
    /**
     * GameState visszaalakítása ShogiGame objektummá.
     */
    private static ShogiGame convertFromGameState(GameState state) {
        ShogiGame game = new ShogiGame();
        game.clearBoard(); // Üres táblával kezdünk
        
        // Jelenlegi játékos beállítása
        game.setCurrentPlayer(Piece.Color.valueOf(state.currentPlayer));
        
        // Tábla feltöltése
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                GameState.PieceData data = state.board[row][col];
                if (data != null) {
                    Piece piece = convertDataToPiece(data);
                    game.getBoard().setPieceAt(row, col, piece);
                }
            }
        }
        
        // Kéz feltöltése
        for (GameState.PieceData data : state.blackHand) {
            game.addToHand(Piece.Color.BLACK, convertDataToPiece(data));
        }
        
        for (GameState.PieceData data : state.whiteHand) {
            game.addToHand(Piece.Color.WHITE, convertDataToPiece(data));
        }
        
        return game;
    }
    
    /**
     * Piece objektum átalakítása PieceData-vá.
     */
    private static GameState.PieceData convertPieceToData(Piece piece) {
        String type = piece.getClass().getSimpleName();
        String color = piece.getColor().name();
        Position pos = piece.getPosition();
        boolean promoted = piece.isPromoted();
        
        return new GameState.PieceData(
            type, 
            color, 
            pos != null ? pos.getRow() : -1, 
            pos != null ? pos.getCol() : -1, 
            promoted
        );
    }
    
    /**
     * PieceData visszaalakítása Piece objektummá.
     */
    private static Piece convertDataToPiece(GameState.PieceData data) {
        Piece.Color color = Piece.Color.valueOf(data.color);
        Position pos = new Position(data.row, data.col);
        
        Piece piece = switch (data.type) {
            case "King" -> new King(color, pos);
            case "Rook" -> new Rook(color, pos);
            case "Bishop" -> new Bishop(color, pos);
            case "GoldGeneral" -> new GoldGeneral(color, pos);
            case "SilverGeneral" -> new SilverGeneral(color, pos);
            case "Knight" -> new Knight(color, pos);
            case "Lance" -> new Lance(color, pos);
            case "Pawn" -> new Pawn(color, pos);
            default -> throw new IllegalArgumentException("Unknown piece type: " + data.type);
        };
        
        if (data.promoted) {
            piece.promote();
        }
        
        return piece;
    }
}
