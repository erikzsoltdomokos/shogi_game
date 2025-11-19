package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Lovag (桂馬/Knight) - ugró bábu korlátozott mozgással.
 * Normál mozgás: 2 előre + 1 oldalra (csak 2 célpont lehetséges).
 * Átlép más bábukat. Promótált: Arany tábornok mozgása.
 * 
 * @author Domokos Erik Zsolt
 */
public class Knight extends Piece {

    /**
     * Létrehoz egy új lovagot.
     * 
     * @param color a bábu színe
     * @param pos a kezdeti pozíció
     */
    public Knight(Color color, Position pos) {
        super(color, pos);
    }

    /**
     * Meghatározza a lovag lehetséges lépéseit.
     * Promótált állapotban Arany tábornokként mozog.
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        if (!promoted) {
            return getNormalKnightMoves(board);
        } else {
            return getPromotedKnightMoves(board);
        }
    }

    /**
     * Normál lovag mozgás: 2 előre, 1 oldalra (L alak, csak előre).
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    private List<Position> getNormalKnightMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        int dir = (color == Color.BLACK ? -2 : 2);
        int row = position.getRow() + dir;

        int col1 = position.getCol() - 1;
        int col2 = position.getCol() + 1;

        if (board.isInside(row, col1)) {
            Piece p = board.getPieceAt(row, col1);
            if (p == null || p.getColor() != color)
                moves.add(new Position(row, col1));
        }

        if (board.isInside(row, col2)) {
            Piece p = board.getPieceAt(row, col2);
            if (p == null || p.getColor() != color)
                moves.add(new Position(row, col2));
        }

        return moves;
    }

    private List<Position> getPromotedKnightMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = (color == Color.BLACK ? -1 : 1);

        int[][] dirs = {
                {direction, 0},
                {0, -1}, {0, 1},
                {-direction, 0},
                {direction, -1}, {direction, 1}
        };

        for (int[] d : dirs) {
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
     * @return "N"/"n" (normál) vagy "+N"/"+n" (promótált)
     */
    @Override
    public String getSymbol() {
        if (promoted) {
            return color == Color.BLACK ? "+N" : "+n";
        } else {
            return color == Color.BLACK ? "N" : "n";
        }
    }

 
}
