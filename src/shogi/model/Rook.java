package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Bástya (飛車/Rook) - erős bábu egyenes vonalú mozgással.
 * Normál mozgás: végtelen egyenes irányban (4 irány: fel, le, bal, jobb).
 * Promótált forma: +4 átlós irány (1 lépés) - Dragon King (竜王).
 * 
 * @author Domokos Erik Zsolt
 */
public class Rook extends Piece {

    /**
     * Létrehoz egy új bástyát.
     * 
     * @param color a bábu színe
     * @param pos a kezdeti pozíció
     */
    public Rook(Color color, Position pos) {
        super(color, pos);
    }

    /**
     * Meghatározza a bástya lehetséges lépéseit.
     * Promótált állapotban kombinál bástya + király átlós lépéseket.
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        if (!promoted) {
            return getNormalRookMoves(board);
        } else {
            return getPromotedRookMoves(board);
        }
    }
    
    /**
     * Normál bástya mozgás: egyenes vonalban akadályig (4 irány).
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    public List<Position> getNormalRookMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        int[][] dirs = {
                {-1, 0}, // fel
                {1, 0},  // le
                {0, -1}, // bal
                {0, 1}   // jobb
        };

        for (int[] d : dirs) {
            int nr = position.getRow();
            int nc = position.getCol();

            while (true) {
                nr += d[0];
                nc += d[1];

                if (!board.isInside(nr, nc))
                    break;

                Piece p = board.getPieceAt(nr, nc);

                if (p == null) {
                    moves.add(new Position(nr, nc));
                } else {
                    if (p.getColor() != this.color)
                        moves.add(new Position(nr, nc)); // üthet
                    break; // akadály
                }
            }
        }
        return moves;
    }
    
    private List<Position> getPromotedRookMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        // 1) normál rook moves
        moves.addAll(getNormalRookMoves(board));

        // 2) királyi mozgás 4 átlós irányba (csak 4 irány!)
        int[][] diag = {
                {-1,-1}, {-1,1},
                { 1,-1}, { 1,1}
        };

        for (int[] d : diag) {
            int nr = position.getRow() + d[0];
            int nc = position.getCol() + d[1];

            if (board.isInside(nr, nc)) {
                Piece p = board.getPieceAt(nr, nc);
                if (p == null || p.getColor() != color)
                    moves.add(new Position(nr, nc));
            }
        }

        return moves;
    }

    /**
     * @return "R"/"r" (normál) vagy "+R"/"+r" (promótált)
     */
    @Override
    public String getSymbol() {
        if (promoted) {
            return color == Color.BLACK ? "+R" : "+r";
        } else {
            return color == Color.BLACK ? "R" : "r";
        }
    }

}
