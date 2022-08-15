package ChessGame.chess_logic;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

public class Square {
    public static final Square none = new Square(-1, -1);
    @Getter @Setter
    private int x, y;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Square(String notation) {
        if (notation.length() == 2 &&
                notation.charAt(0) >= 'a' && notation.charAt(0) <= 'h' &&
                notation.charAt(1) >= '1' && notation.charAt(1) <= '8')
        {
            x = notation.charAt(0) - 'a';
            y = notation.charAt(1) - '1';
        } else {
            this.x = -1;
            this.y = -1;
        }
    }

    public boolean isOnBoard() {
        return  x >= 0 && x < 8 &&
                y >= 0 && y < 8;
    }

    public static List<Square> yieldSquares() {
        List<Square> listOfSquares = new LinkedList<>();
        for(int y = 0; y < 8; y++) {
            for(int x = 0; x < 8; x++) {
                listOfSquares.add(new Square(x, y));
            }
        }
        return listOfSquares;
    }

    public String name() {
        return (char)('a' + x) + String.valueOf(y + 1);
    }

    public boolean equals(Square other) {
        return this.getX() == other.getX() && this.getY() == other.getY();
    }

}
