package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Ezüst tábornok (銀将/Silver General) - támadó orientált bábu.
 * Normál mozgás: 5 irány - előre (1), átlósan (4 irány).
 * Promótált forma: Arany tábornok mozgása (6 irány).
 * 
 * @author Domokos Erik Zsolt
 */
public class SilverGeneral extends Piece {

    /**
     * Létrehoz egy új Ezüst tábornokot.
     * 
     * @param color a bábu színe
     * @param pos a kezdeti pozíció
     */
    public SilverGeneral(Piece.Color color, Position pos) {
        super(color, pos);
    }

    /**
     * Meghatározza az Ezüst tábornok lehetséges lépéseit.
     * Promótált állapotban Arany tábornokként mozog.
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        if (!promoted) {
            return getNormalSilverMoves(board);
        } else {
            return getPromotedSilverMoves(board);
        }
    }

    /**
     * Normál Ezüst mozgás: előre (1) + átlósan (4 irány).
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    private List<Position> getNormalSilverMoves(Board board) {
        List<Position> moves = new ArrayList<>();
        int direction = (color == Color.BLACK ? -1 : 1);

        int[][] dirs = {
                {direction, 0},
                {direction, -1},
                {direction, 1},
                {-direction, -1},
                {-direction, 1}
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
     * Promótált Ezüst mozgás: Arany tábornok mintázata (6 irány).
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    private List<Position> getPromotedSilverMoves(Board board) {
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
     * @return "S"/"s" (normál) vagy "+S"/"+s" (promótált)
     */
    @Override
    public String getSymbol() {
        if (promoted) {
            return color == Color.BLACK ? "+S" : "+s";
        } else {
            return color == Color.BLACK ? "S" : "s";
        }
    }

}
