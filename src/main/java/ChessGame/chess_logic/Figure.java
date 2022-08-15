package ChessGame.chess_logic;

import lombok.Getter;

public enum Figure {
    none('1'),

    whiteKing('K'),
    whiteQueen('Q'),
    whiteRook('R'),
    whiteBishop('B'),
    whiteKnight('N'),
    whitePawn('P'),

    blackKing('k'),
    blackQueen('q'),
    blackRook('r'),
    blackBishop('b'),
    blackKnight('n'),
    blackPawn('p');

    @Getter
    private final char figureLetter;

    Figure(char figureLetter) {
        this.figureLetter = figureLetter;
    }

    public static Color getColor(Figure figure) {
        if(figure == Figure.none)
            return Color.none;
        return (figure == Figure.whiteKing ||
                figure == Figure.whiteQueen ||
                figure == Figure.whiteRook ||
                figure == Figure.whiteBishop ||
                figure == Figure.whiteKnight ||
                figure == Figure.whitePawn
        ) ? Color.white : Color.black;
    }

}
