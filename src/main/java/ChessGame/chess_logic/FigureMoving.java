package ChessGame.chess_logic;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FigureMoving {
    private Figure figure;
    private Square from;
    private Square to;
    private Figure promotion;

    public FigureMoving(FigureOnSquare fs, Square to, Figure promotion) {
        this.figure = fs.getFigure();
        this.from = fs.getSquare();
        this.to = to;
        this.promotion = promotion;
    }

    public FigureMoving(FigureOnSquare fs, Square to) {
        this.figure = fs.getFigure();
        this.from = fs.getSquare();
        this.to = to;
        this.promotion = Figure.none;
    }

    public FigureMoving(String notation) { // notation = "Pe2e4Q", "Bc1f4" ...

        for(Figure fg : Figure.values()) {
            if(fg.getFigureLetter() == notation.charAt(0)) {
                this.figure = fg;
                break;
            }
        }

        this.from = new Square(notation.substring(1, 3));
        this.to = new Square(notation.substring(3, 5));

        this.promotion = Figure.none;
        pawnPromotion(notation);
    }

    private void pawnPromotion(String notation) {
        Figure[] legalWhitePromotions = { Figure.whiteQueen, Figure.whiteRook, Figure.whiteBishop, Figure.whiteKnight };
        Figure[] legalBlackPromotions = { Figure.blackQueen, Figure.blackRook, Figure.blackBishop, Figure.blackKnight };

        for(Figure fg : legalWhitePromotions) { // для белых фигур
            if(notation.length() == 6 &&
                    (this.figure == Figure.whitePawn && this.to.getY() == 7) &&
                    fg.getFigureLetter() == notation.charAt(5))
            {
                this.promotion = fg;
                break;
            }
            else if(notation.length() == 6 &&
                    (this.figure == Figure.whitePawn && this.to.getY() == 7) &&
                    fg.getFigureLetter() != notation.charAt(5))
            {
                this.promotion = Figure.whiteQueen;
            }
            if(notation.length() != 6 &&
                    (this.figure == Figure.whitePawn && this.to.getY() == 7))
            {
                this.promotion = Figure.whiteQueen;
            }
        }

        for(Figure fg : legalBlackPromotions) { // для черных фигур
            if(notation.length() == 6 &&
                    (this.figure == Figure.blackPawn && this.to.getY() == 0) &&
                    fg.getFigureLetter() == notation.charAt(5))
            {
                this.promotion = fg;
                break;
            }
            else if(notation.length() == 6 &&
                    (this.figure == Figure.blackPawn && this.to.getY() == 0) &&
                    fg.getFigureLetter() != notation.charAt(5))
            {
                this.promotion = Figure.blackQueen;
            }
            if(notation.length() != 6 &&
                    (this.figure == Figure.blackPawn && this.to.getY() == 0))
            {
                this.promotion = Figure.blackQueen;
            }
        }
    }

    public int deltaX() {
        return to.getX() - from.getX();
    }

    public int deltaY() {
        return to.getY() - from.getY();
    }

    public int absDeltaX() {
        return Math.abs(deltaX());
    }

    public int absDeltaY() {
        return Math.abs(deltaY());
    }

    public int signX() {
        return (int) Math.signum(deltaX());
    }

    public int signY() {
        return (int) Math.signum(deltaY());
    }

    @Override
    public String toString() {
        String text = figure.getFigureLetter() + from.name() + to.name();
        if(promotion != Figure.none) {
            text += promotion.getFigureLetter();
        }
        return text;
    }

}
