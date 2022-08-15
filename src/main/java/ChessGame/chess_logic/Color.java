package ChessGame.chess_logic;

import lombok.Getter;

public enum Color {
    none('1'),
    white('w'),
    black('b');

    @Getter
    private final char colorLetter;

    Color(char colorLetter) {
        this.colorLetter = colorLetter;
    }

    public static Color flipColor(Color color) {
        if(color == Color.black) return Color.white;
        if(color == Color.white) return Color.black;
        return Color.none;
    }
}
