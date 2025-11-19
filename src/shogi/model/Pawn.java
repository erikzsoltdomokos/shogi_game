package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Gyalog (歩兵/Pawn) - a legalapvetőbb bábu.
 * Normál mozgás: 1 előre (csak üres mezőre, NEM üt frontálisan!).
 * Promótált forma: Arany tábornok mozgása (Tokin - と金).
 * 
 * @author Domokos Erik Zsolt
 */
public class Pawn extends Piece {

    /**
     * Létrehoz egy új gyalogot.
     * 
     * @param color a bábu színe
     * @param pos a kezdeti pozíció
     */
    public Pawn(Color color, Position pos) {
        super(color, pos);
    }

    /**
     * Meghatározza a gyalog lehetséges lépéseit.
     * Promótált állapotban Arany tábornokként mozog (Tokin).
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        return promoted ? getPromotedMoves(board) : getNormalMoves(board);
    }

    /**
     * Normál gyalog mozgás: 1 előre, csak üres mezőre.
     * FONTOS: a gyalog NEM üthet frontálisan (ellentétben a sakkgyaloggal).
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    private List<Position> getNormalMoves(Board board) {

        List<Position> moves = new ArrayList<>();
        int d = (color == Color.BLACK ? -1 : 1);

        int nr = position.getRow() + d;
        int nc = position.getCol();

        if (board.isInside(nr, nc)) {
            Piece p = board.getPieceAt(nr, nc);
            // Gyalog csak üres mezőre léphet, NEM üthet frontálisan
            if (p == null)
                moves.add(new Position(nr, nc));
        }

        return moves;
    }

    // PROMOTED PAWN = GOLD GENERAL
    private List<Position> getPromotedMoves(Board board) {

        List<Position> moves = new ArrayList<>();
        int d = (color == Color.BLACK ? -1 : 1);

        int[][] dirs = {
                {d, 0},       // előre
                {0, -1}, {0, 1}, // bal/jobb
                {-d, 0},      // hátra
                {d, -1}, {d, 1}   // átlós előre
        };

        for (int[] dir : dirs) {
            int nr = position.getRow() + dir[0];
            int nc = position.getCol() + dir[1];

            if (!board.isInside(nr, nc)) continue;

            Piece p = board.getPieceAt(nr, nc);
            if (p == null || p.getColor() != this.color)
                moves.add(new Position(nr, nc));
        }

        return moves;
    }

    /**
     * @return "P"/"p" (normál) vagy "+P"/"+p" (promótált Tokin)
     */
    @Override
    public String getSymbol() {
        if (promoted) {
            return color == Color.BLACK ? "+P" : "+p";
        } else {
            return color == Color.BLACK ? "P" : "p";
        }
    }
}
