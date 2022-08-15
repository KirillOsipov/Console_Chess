package ChessGame.chess_logic;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FigureOnSquare {
    private Figure figure;
    private Square square;

    public FigureOnSquare(Figure figure, Square square) {
        this.figure = figure;
        this.square = square;
    }

}
