package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Király (玉将/King) - a legfontosabb bábu.
 * Mozgás: 1 mező bármely irányba (8 irány).
 * Nem promótálható. Ha mattot kap, a játék véget ér.
 * 
 * @author Domokos Erik Zsolt
 */
public class King extends Piece {

    /**
     * Létrehoz egy új királyt.
     * 
     * @param color a bábu színe
     * @param pos a kezdeti pozíció
     */
    public King(Color color, Position pos) {
        super(color, pos);
    }

    /**
     * Meghatározza a király lehetséges lépéseit.
     * A király 1 mezőt léphet bármely irányba (8 irány).
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        // 8 irány: fel-le, bal-jobb, és az átlók
        int[][] dirs = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},          {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };

        for (int[] d : dirs) {
            int nr = position.getRow() + d[0];
            int nc = position.getCol() + d[1];

            if (board.isInside(nr, nc)) {
                Piece targetPiece = board.getPieceAt(nr, nc);
                // Léphet üres mezőre vagy ellenséges bábura
                if (targetPiece == null || targetPiece.getColor() != this.color) {
                    moves.add(new Position(nr, nc));
                }
            }
        }
        return moves;
    }

    /**
     * @return "K" (fekete) vagy "k" (fehér)
     */
    @Override
    public String getSymbol() {
        return color == Color.BLACK ? "K" : "k";
    }
}
