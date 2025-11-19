package shogi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Lándzsa (香車/Lance) - erős egyenes vonalú bábu.
 * Normál mozgás: végtelen előre (akadályig), mint a bástya 1 irányban.
 * Promótált forma: Arany tábornok mozgása.
 * 
 * @author Domokos Erik Zsolt
 */
public class Lance extends Piece {

    /**
     * Létrehoz egy új lándzsát.
     * 
     * @param color a bábu színe
     * @param pos a kezdeti pozíció
     */
    public Lance(Color color, Position pos) {
        super(color, pos);
    }

    /**
     * Meghatározza a lándzsa lehetséges lépéseit.
     * Promótált állapotban Arany tábornokként mozog.
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    @Override
    public List<Position> getLegalMoves(Board board) {
        if (!promoted) {
            return getNormalLanceMoves(board);
        } else {
            return getPromotedLanceMoves(board);
        }
    }
    
    /**
     * Normál lándzsa mozgás: egyenesen előre akadályig.
     * 
     * @param board a játéktábla
     * @return a lehetséges célpozíciók listája
     */
    public List<Position> getNormalLanceMoves(Board board) {
        List<Position> moves = new ArrayList<>();

        int dir = (color == Color.BLACK ? -1 : 1);

        int nr = position.getRow() + dir;
        int nc = position.getCol();

        while (board.isInside(nr, nc)) {
            Piece p = board.getPieceAt(nr, nc);

            if (p == null) {
                moves.add(new Position(nr, nc));
            } else {
                if (p.getColor() != this.color)
                    moves.add(new Position(nr, nc));
                break;
            }

            nr += dir;
        }

        return moves;
    }
    
    private List<Position> getPromotedLanceMoves(Board board) {
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
     * @return "L"/"l" (normál) vagy "+L"/"+l" (promótált)
     */
    @Override
    public String getSymbol() {
        if (promoted) {
            return color == Color.BLACK ? "+L" : "+l";
        } else {
            return color == Color.BLACK ? "L" : "l";
        }
    }

}
