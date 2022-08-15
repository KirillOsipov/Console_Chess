package ChessGame.chess_logic;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class Board {
    private String fen; // нотация Форсайта — Эдвардса
    private final Figure[][] figures; // расстановка фигур
    private Color moveColor; // чей ход
    private boolean[] castlingFlags; // флаги рокировки
    private Square enPassantSquare; // битое поле для взятия на проходе
    private int drawCount; // счетчик до ничьей по правилу 50 ходов
    private int moveNumber; // номер текущего хода

    public Board(String fen) {
        this.fen = fen;
        this.figures = new Figure[8][8];
        init();
    }

    private void init() { // парсинг fen

        // rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq d6 0 1
        // 0                                           1 2    3  4 5

        try {
            String[] fenParts = fen.split(" ");
            if (fenParts.length != 6) return;
            initFigures(fenParts[0]);
            moveColor = (fenParts[1].equals("b")) ? Color.black : Color.white;
            initCastling(fenParts[2]);
            initEnPassant(fenParts[3]);
            drawCount = Integer.parseInt(fenParts[4]);
            moveNumber = Integer.parseInt(fenParts[5]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initFigures(String fenFigures) {
        for(int i = 8; i >= 2; i--) {
            fenFigures = fenFigures.replace(String.valueOf(i), (i - 1) + "1");
        }
        String[] ranks = fenFigures.split("/");
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                for(Figure fg : Figure.values()) {
                    if(fg.getFigureLetter() == ranks[7-y].charAt(x)) {
                        figures[x][y] = fg;
                        break;
                    }
                }
            }
        }
    }

    private void initCastling(String fenCastling) {
        // KQkq
        castlingFlags = new boolean[] {
                fenCastling.contains("K"), fenCastling.contains("Q"),
                fenCastling.contains("k"), fenCastling.contains("q")
        };
    }

    private void initEnPassant (String fenEnPassant) {
        if(fenEnPassant.length() != 2 || (fenEnPassant.charAt(1) != '3' && fenEnPassant.charAt(1) != '6')) {
            enPassantSquare = Square.none;
        } else {
            enPassantSquare = new Square(fenEnPassant);
        }
    }

    public List<FigureOnSquare> yieldFigures() {
        List<FigureOnSquare> figureList = new LinkedList<>();
        for (Square square : Square.yieldSquares()) {
            if(Figure.getColor(getFigureAt(square)) == moveColor) {
                figureList.add(new FigureOnSquare(getFigureAt(square), square));
            }
        }
        return figureList;
    }

    private int countEmptyFields() {
        int count = 0;
        for(Figure[] fgArray : figures) {
            for(Figure fg : fgArray) {
                if(fg == Figure.none) {
                    count++;
                }
            }
        }
        return count;
    }

    private void generateFen() {
        fen = fenFigures() + " " +
                (moveColor == Color.white ? "w" : "b") + " " +
                fenCastling() + " " +
                fenEnPassant() + " " +
                drawCount + " " +
                moveNumber;
    }

    private String fenFigures() {
        StringBuilder sb = new StringBuilder();
        for(int y = 7; y >= 0; y--) {
            for(int x = 0; x < 8; x++) {
                sb.append(figures[x][y] == Figure.none ? "1" : figures[x][y].getFigureLetter());
            }
            if(y > 0) {
                sb.append("/");
            }
        }
        String eightOfOnes = "11111111";
        String str = sb.toString();
        for(int i = 8; i >= 2; i--) {
            str = str.replaceAll(eightOfOnes.substring(0, i), String.valueOf(i));
        }
        return str;
    }

    private String fenCastling() {
        StringBuilder sb = new StringBuilder();

        if(castlingFlags[0])
            sb.append('K');
        if(castlingFlags[1])
            sb.append('Q');
        if(castlingFlags[2])
            sb.append('k');
        if(castlingFlags[3])
            sb.append('q');

        return sb.isEmpty() ? "-" : sb.toString();
    }

    private String fenEnPassant() {
        return enPassantSquare.equals(Square.none) ? "-" : enPassantSquare.name();
    }

    public Figure getFigureAt(Square square) {
        if(square.isOnBoard()) {
            return figures[square.getX()][square.getY()];
        }
        return Figure.none;
    }

    void setFigureAt(Figure figure, Square at) {
        if(at.isOnBoard()) {
            figures[at.getX()][at.getY()] = figure;
        }
    }

    public Board move(FigureMoving fm) {

        // создание новой позиции
        Board nextBoard = new Board(fen);

        // если рокировка
        moveCastling(fm, nextBoard);

        // откуда и куда делаем ход
        nextBoard.setFigureAt(Figure.none, fm.getFrom());
        nextBoard.setFigureAt((fm.getPromotion() == Figure.none) ? fm.getFigure() : fm.getPromotion(), fm.getTo());

        // сброс флагов рокировки
        dropCastlingFlags(fm, nextBoard);

        // если взятие на проходе
        moveEnPassant(fm, nextBoard);

        // подсчет drawCount
        if((fm.getFigure() == Figure.whitePawn || fm.getFigure() == Figure.blackPawn) ||
                nextBoard.countEmptyFields() > this.countEmptyFields())
            nextBoard.drawCount = 0;
        else
            nextBoard.drawCount++;

        // увеличение счетчика ходов
        if(moveColor == Color.black)
            nextBoard.moveNumber++;

        // конец хода
        nextBoard.moveColor = Color.flipColor(moveColor);
        nextBoard.generateFen();
        return nextBoard;
    }

    private void dropCastlingFlags(FigureMoving fm, Board nextBoard) { // проверки на предыдущие ходы короля и состояние ладей

        if(fm.getFigure() == Figure.whiteKing && (fm.absDeltaX() <= 1 && fm.absDeltaY() <= 1)) // если белый король уже ходил
        {
            nextBoard.castlingFlags[0] = false;
            nextBoard.castlingFlags[1] = false;
        }

        if(nextBoard.getFigureAt(new Square("h1")) != Figure.whiteRook) // если на h1 нет ладьи
        {
            nextBoard.castlingFlags[0] = false;
        }

        if(nextBoard.getFigureAt(new Square("a1")) != Figure.whiteRook) // если на a1 нет ладьи
        {
            nextBoard.castlingFlags[1] = false;
        }

        if(fm.getFigure() == Figure.blackKing && (fm.absDeltaX() <= 1 && fm.absDeltaY() <= 1)) // если черный король уже ходил
        {
            nextBoard.castlingFlags[2] = false;
            nextBoard.castlingFlags[3] = false;
        }

        if(nextBoard.getFigureAt(new Square("h8")) != Figure.blackRook) // если на h8 нет ладьи
        {
            nextBoard.castlingFlags[2] = false;
        }

        if(nextBoard.getFigureAt(new Square("a8")) != Figure.blackRook) // если на a8 нет ладьи
        {
            nextBoard.castlingFlags[3] = false;
        }
    }

    private void moveCastling(FigureMoving fm, Board nextBoard) { // реализация рокировок

        // короткая для белого короля
        if(fm.getFigure() == Figure.whiteKing &&
                fm.getFrom().equals(new Square("e1")) &&
                fm.getTo().equals(new Square("g1")))
        {
            nextBoard.setFigureAt(Figure.none, new Square("h1"));
            nextBoard.setFigureAt(Figure.whiteRook, new Square("f1"));
            nextBoard.castlingFlags[0] = false;
            nextBoard.castlingFlags[1] = false;
        }

        // длинная для белого короля
        if(fm.getFigure() == Figure.whiteKing &&
                fm.getFrom().equals(new Square("e1")) &&
                fm.getTo().equals(new Square("c1")))
        {
            nextBoard.setFigureAt(Figure.none, new Square("a1"));
            nextBoard.setFigureAt(Figure.whiteRook, new Square("d1"));
            nextBoard.castlingFlags[0] = false;
            nextBoard.castlingFlags[1] = false;
        }

        // короткая для черного короля
        if(fm.getFigure() == Figure.blackKing &&
                fm.getFrom().equals(new Square("e8")) &&
                fm.getTo().equals(new Square("g8")))
        {
            nextBoard.setFigureAt(Figure.none, new Square("h8"));
            nextBoard.setFigureAt(Figure.blackRook, new Square("f8"));
            nextBoard.castlingFlags[2] = false;
            nextBoard.castlingFlags[3] = false;
        }

        // длинная для черного короля
        if(fm.getFigure() == Figure.blackKing &&
                fm.getFrom().equals(new Square("e8")) &&
                fm.getTo().equals(new Square("c8")))
        {
            nextBoard.setFigureAt(Figure.none, new Square("a8"));
            nextBoard.setFigureAt(Figure.blackRook, new Square("d8"));
            nextBoard.castlingFlags[2] = false;
            nextBoard.castlingFlags[3] = false;
        }
    }

    private void moveEnPassant(FigureMoving fm, Board nextBoard) { // реализация взятия на проходе
        int stepY = Figure.getColor(fm.getFigure()) == Color.white ? 1 : -1;

        if((fm.getFigure() == Figure.whitePawn || fm.getFigure() == Figure.blackPawn) &&
                fm.deltaY() == (2 * stepY)) // если прыжок пешкой на 2 поля
        {
            nextBoard.enPassantSquare = new Square(fm.getTo().getX(), fm.getTo().getY() - stepY);
        } else {
            if(fm.absDeltaX() == 1 &&
                    fm.deltaY() == stepY &&
                    ((fm.getFrom().getY() == 4 && fm.getFigure() == Figure.whitePawn) ||
                            (fm.getFrom().getY() == 3 && fm.getFigure() == Figure.blackPawn)) &&
                    nextBoard.enPassantSquare.equals(fm.getTo()))
            {
                nextBoard.setFigureAt(Figure.none,
                        new Square(enPassantSquare.getX(),enPassantSquare.getY() - stepY));
            }
            nextBoard.enPassantSquare = Square.none;
        }
    }

    private boolean canEatKing() { // находится ли король соперника под боем
        Square foreignKing = findForeignKing();
        Moves moves = new Moves(this);
        for(FigureOnSquare fs : yieldFigures()) {
            FigureMoving fm = new FigureMoving(fs, foreignKing);
            if(moves.canMove(fm))
                return true;
        }
        return false;
    }

    private Square findForeignKing() { // поиск клетки чужого короля
        Figure foreignKing = moveColor == Color.black ? Figure.whiteKing : Figure.blackKing;
        for(Square square : Square.yieldSquares()) {
            if(getFigureAt(square) == foreignKing)
                return square;
        }
        return Square.none;
    }

    public boolean isCheck () { // проверка на шах
        Board afterMove = new Board(fen);
        afterMove.moveColor = Color.flipColor(moveColor);
        return afterMove.canEatKing();
    }

    public boolean isCheckAfterMove(FigureMoving fm) { // проверка на шах после определенного хода
        Board afterMove = move(fm);
        return afterMove.canEatKing();
    }

    public boolean isDrawBy50Moves() { // правило ничьей после 50 ходов
        return drawCount >= 50;
    }

}
