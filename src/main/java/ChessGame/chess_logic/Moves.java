package ChessGame.chess_logic;

public class Moves {
    FigureMoving fm;
    Board board;

    public Moves(Board board) {
        this.board = board;
    }

    public boolean canMove(FigureMoving fm) {
        this.fm = fm;
        return canMoveFrom() &&
                canMoveTo() &&
                canFigureMove();
    }

    private boolean canMoveFrom() {
        return fm.getFrom().isOnBoard() &&
                board.getFigureAt(new Square(fm.getFrom().getX(), fm.getFrom().getY())) == fm.getFigure() &&
                Figure.getColor(fm.getFigure()) == board.getMoveColor();
    }

    private boolean canMoveTo() {
        return fm.getTo().isOnBoard() &&
                fm.getFrom() != fm.getTo() &&
                Figure.getColor(board.getFigureAt(fm.getTo())) != board.getMoveColor();
    }

    private boolean canFigureMove () {
        switch (fm.getFigure()) {

            case whiteKing, blackKing -> {
                return canKingMove();
            }
            case whiteQueen, blackQueen -> {
                return canStraightMove();
            }
            case whiteRook, blackRook -> {
                return (fm.signX() == 0 || fm.signY() == 0) &&
                        canStraightMove();
            }
            case whiteBishop, blackBishop -> {
                return (fm.signX() != 0 && fm.signY() != 0) &&
                        canStraightMove();
            }
            case whiteKnight, blackKnight -> {
                return canKnightMove();
            }
            case whitePawn, blackPawn -> {
                return canPawnMove();
            }

            default -> {
                return false;
            }
        }
    }

    private boolean canKingMove() {
        return (fm.absDeltaX() <= 1 && fm.absDeltaY() <= 1) ||
                canKingShortCastle() ||
                canKingLongCastle();
    }

    private boolean canKingShortCastle() {
        FigureMoving passedField; // проходимое поле

        if(fm.getFigure() == Figure.whiteKing) {
            passedField = new FigureMoving("Ke1f1");

            return board.getCastlingFlags()[0] &&
                    fm.getFrom().equals(new Square("e1")) &&
                    fm.getTo().equals(new Square("g1")) &&
                    board.getFigureAt(new Square("h1")) == Figure.whiteRook &&
                    board.getFigureAt(new Square("f1")) == Figure.none &&
                    board.getFigureAt(new Square("g1")) == Figure.none &&
                    !board.isCheck() &&
                    !board.isCheckAfterMove(passedField) &&
                    !board.isCheckAfterMove(fm);
        }
        if(fm.getFigure() == Figure.blackKing) {
            passedField = new FigureMoving("ke8f8");

            return board.getCastlingFlags()[2] &&
                    fm.getFrom().equals(new Square("e8")) &&
                    fm.getTo().equals(new Square("g8")) &&
                    board.getFigureAt(new Square("h8")) == Figure.blackRook &&
                    board.getFigureAt(new Square("f8")) == Figure.none &&
                    board.getFigureAt(new Square("g8")) == Figure.none &&
                    !board.isCheck() &&
                    !board.isCheckAfterMove(passedField) &&
                    !board.isCheckAfterMove(fm);
        }
        return false;
    }

    private boolean canKingLongCastle() {
        FigureMoving passedField; // проходимое поле

        if(fm.getFigure() == Figure.whiteKing) {
            passedField = new FigureMoving("Ke1d1");

            return board.getCastlingFlags()[1] &&
                    fm.getFrom().equals(new Square("e1")) &&
                    fm.getTo().equals(new Square("c1")) &&
                    board.getFigureAt(new Square("a1")) == Figure.whiteRook &&
                    board.getFigureAt(new Square("d1")) == Figure.none &&
                    board.getFigureAt(new Square("c1")) == Figure.none &&
                    !board.isCheck() &&
                    !board.isCheckAfterMove(passedField) &&
                    !board.isCheckAfterMove(fm);
        }
        if(fm.getFigure() == Figure.blackKing) {
            passedField = new FigureMoving("ke8d8");

            return board.getCastlingFlags()[3] &&
                    fm.getFrom().equals(new Square("e8")) &&
                    fm.getTo().equals(new Square("c8")) &&
                    board.getFigureAt(new Square("a8")) == Figure.blackRook &&
                    board.getFigureAt(new Square("d8")) == Figure.none &&
                    board.getFigureAt(new Square("c8")) == Figure.none &&
                    !board.isCheck() &&
                    !board.isCheckAfterMove(passedField) &&
                    !board.isCheckAfterMove(fm);
        }
        return false;
    }

    private boolean canKnightMove() {
        return (fm.absDeltaX() == 1 && fm.absDeltaY() == 2) ||
                (fm.absDeltaX() == 2 && fm.absDeltaY() == 1);
    }

    private boolean canStraightMove() {
        Square currentSquare = fm.getFrom();
        do {
            currentSquare = new Square(currentSquare.getX() + fm.signX(),
                    currentSquare.getY() + fm.signY());
            if(currentSquare.equals(fm.getTo()))
                return true;
        } while (currentSquare.isOnBoard() &&
                 board.getFigureAt(currentSquare) == Figure.none);
        return false;
    }

    private boolean canPawnMove() {
        if(fm.getFrom().getY() < 1 || fm.getFrom().getY() > 6) // необязательно
            return false;
        int stepY = Figure.getColor(fm.getFigure()) == Color.white ? 1 : -1;
        return canPawnGo(stepY) ||
                canPawnJump(stepY) ||
                canPawnEat(stepY) ||
                canPawnDoEnPassant(stepY);
    }

    private boolean canPawnGo(int stepY) {
        return (board.getFigureAt(fm.getTo()) == Figure.none) &&
                fm.deltaX() == 0 &&
                fm.deltaY() == stepY;
    }

    private boolean canPawnJump(int stepY) {
        return (board.getFigureAt(fm.getTo()) == Figure.none) &&
                (board.getFigureAt(new Square(fm.getFrom().getX(),
                        fm.getFrom().getY() + stepY)) == Figure.none) &&
                fm.deltaX() == 0 &&
                fm.deltaY() == (2 * stepY) &&
                (fm.getFrom().getY() == 1 || fm.getFrom().getY() == 6);

    }

    private boolean canPawnEat(int stepY) {
        return (board.getFigureAt(fm.getTo()) != Figure.none) &&
                fm.absDeltaX() == 1 &&
                fm.deltaY() == stepY;
    }

    private boolean canPawnDoEnPassant(int stepY) {
        return (board.getFigureAt(fm.getTo()) == Figure.none) &&
                fm.absDeltaX() == 1 &&
                fm.deltaY() == stepY &&
                ((fm.getFrom().getY() == 4 && fm.getFigure() == Figure.whitePawn) ||
                        (fm.getFrom().getY() == 3 && fm.getFigure() == Figure.blackPawn)) &&
                board.getEnPassantSquare().equals(fm.getTo());
    }

}
