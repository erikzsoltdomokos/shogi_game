package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Futó (角行/Bishop) - erős bábu átlós mozgással.
 * Normál mozgás: végtelen átlós irányban (4 átló).
 * Promótált forma: +4 egyenes irány (1 lépés) - Dragon Horse (竜馬).
 * 
 * @author Domokos Erik Zsolt
 */
public class Bishop extends Piece {

    /**
     * Létrehoz egy új futót.
     * 
     * @param color a bábu színe
     * @param pos a kezdeti pozíció
     */
    public Bishop(Color color, Position pos) {
        super(color, pos);
    }

    /**
     * Meghatározza a futó lehetséges lépéseit.
     * Promótált állapotban kombinál futó + király egyenes lépéseket.
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        if (!promoted) {
            return getNormalBishopMoves(board);
        } else {
            return getPromotedBishopMoves(board);
        }
    }
    
    /**
     * Normál futó mozgás: átlós vonalban akadályig (4 átló).
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    public List<Position> getNormalBishopMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        if (position == null) {
            return moves;
        }

        int[][] dirs = {
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] d : dirs) {
            int r = position.getRow() + d[0];
            int c = position.getCol() + d[1];

            while (board.isInside(r, c)) {
                Piece p = board.getPieceAt(r, c);

                if (p == null) {
                    moves.add(new Position(r, c));
                } else {
                    if (p.getColor() != color) {
                        moves.add(new Position(r, c));
                    }
                    break;
                }

                r += d[0];
                c += d[1];
            }
        }

        return moves;
    }

    
    private List<Position> getPromotedBishopMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        // 1) normál bishop mozgás
        moves.addAll(getNormalBishopMoves(board));

        // 2) királyi mozgások (8 irány 1 lépés)
        int[][] kingDirs = {
                {-1,-1}, {-1,0}, {-1,1},
                { 0,-1},         { 0,1},
                { 1,-1}, { 1,0}, { 1,1}
        };

        for (int[] d : kingDirs) {
            int nr = position.getRow() + d[0];
            int nc = position.getCol() + d[1];
            if (board.isInside(nr, nc)) {
                Piece p = board.getPieceAt(nr, nc);
                if (p == null || p.getColor() != color) {
                    moves.add(new Position(nr, nc));
                }
            }
        }

        return moves;
    }

    /**
     * @return "B"/"b" (normál) vagy "+B"/"+b" (promótált)
     */
    @Override
    public String getSymbol() {
        if (promoted) {
            return color == Color.BLACK ? "+B" : "+b";
        } else {
            return color == Color.BLACK ? "B" : "b";
        }
    }
}
