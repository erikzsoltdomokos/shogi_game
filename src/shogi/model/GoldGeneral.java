package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Arany tábornok (金将/Gold General) - sokoldalru védelmi bábu.
 * Mozgás: 6 irány - előre (3 irány), oldalra (2), hátra (1).
 * Nem promótálható. Promótált bábuk szintén így mozognak.
 * 
 * @author Domokos Erik Zsolt
 */
public class GoldGeneral extends Piece {

    /**
     * Létrehoz egy új Arany tábornokot.
     * 
     * @param color a bábu színe
     * @param pos a kezdeti pozíció
     */
    public GoldGeneral(Color color, Position pos) {
        super(color, pos);
    }

    /**
     * Meghatározza az Arany tábornok lehetséges lépéseit.
     * 6 irány: előre (3), oldalra (2), hátra (1). Nem léphet átlósan hátra.
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    @Override
    public List<Position> getLegalMoves(Board board) {

        List<Position> moves = new ArrayList<>();
        int direction = (color == Color.BLACK ? -1 : 1);

        // ARANY TÁBORNOK mozgási mintázata (6 irány)
        int[][] dirs = {
                {direction, 0},       // előre
                {0, -1}, {0, 1},      // bal/jobb
                {-direction, 0},      // hátra
                {direction, -1}, {direction, 1}   // átlós előre
        };

        for (int[] dir : dirs) {
            int nr = position.getRow() + dir[0];
            int nc = position.getCol() + dir[1];
            if (board.isInside(nr, nc)) {
                Piece targetPiece = board.getPieceAt(nr, nc);
                if (targetPiece == null || targetPiece.getColor() != color)
                    moves.add(new Position(nr, nc));
            }
        }

        return moves;
    }

    /**
     * @return "G" (fekete) vagy "g" (fehér)
     */
    @Override
    public String getSymbol() {
        return color == Color.BLACK ? "G" : "g";
    }
}
