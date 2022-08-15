package ChessGame.chess_logic;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@Getter @Setter
public class Chess {
    private String fen; // нотация Форсайта — Эдвардса
    private final Board board; // доска
    private final Moves moves; // ходы
    private List<FigureMoving> allMoves; // список всех возможных ходов
    private static final List<String> movesMade = new ArrayList<>(); // список сделанных ходов

    public Chess(String fen) { // с любой позиции
        this.fen = fen;
        this.board = new Board(fen);
        this.moves = new Moves(board);
    }

    public Chess() { // со стартовой позиции
        this.fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        this.board = new Board(fen);
        this.moves = new Moves(board);
    }

    private Chess(Board board) {
        this.board = board;
        this.fen = board.getFen();
        this.moves = new Moves(board);
    }

    public Chess move(String notation) {
        if(!Pattern.matches("[KQRBNPkqrbnp][a-h]\\d[a-h]\\d[QRBNqrbn]?", notation)) {
            System.err.println("Incorrect move notation!");
            return this;
        }

        FigureMoving fm = new FigureMoving(notation);
        if(!moves.canMove(fm) || board.isCheckAfterMove(fm)) {
            System.err.println("Impossible move!");
            return this;
        }

        if(board.getMoveColor() == Color.black && board.getMoveNumber() == 1 && movesMade.isEmpty()) {
            movesMade.add("...");
        }
        movesMade.add(notation);

        Board nextBoard = board.move(fm);
        return new Chess(nextBoard);
    }

    public char getFigureAt(Square square) {
        Figure figure = board.getFigureAt(square);
        if(figure == null) {
            figure = Figure.none;
        }
        return (figure == Figure.none) ? '.' : figure.getFigureLetter();
    }

    private void findAllMoves() {
        allMoves = new LinkedList<>();
        for (FigureOnSquare fs : board.yieldFigures()) {
            for (Square to : Square.yieldSquares()) {
                FigureMoving fm = new FigureMoving(fs, to);
                if(moves.canMove(fm) && !board.isCheckAfterMove(fm)) {
                    allMoves.add(fm);
                }
            }
        }
    }

    public List<String> getAllMoves() {
        findAllMoves();
        List<String> movesList = new LinkedList<>();
        for(FigureMoving fm : allMoves) {
            movesList.add(fm.toString());
        }
        return movesList;
    }

    public List<String> getMovesMade() {
        return movesMade;
    }

    public void setMovesMade(String movesFromDB) {
        movesMade.clear();
        movesFromDB = movesFromDB.substring(1, movesFromDB.length() - 1);
        String[] temp = movesFromDB.split(", ");
        movesMade.addAll(Arrays.asList(temp));
    }

    public String getMovesMadeAsString() {
        StringBuilder sb = new StringBuilder("\nMoves made in the game:\n");
        for(int i = 0, num = 1; i < movesMade.size(); i+=2) {
            sb.append("\n").append(num++).append(". "); // номер хода
            sb.append(movesMade.get(i)).append(" "); // ход белых
            if(i + 1 < movesMade.size())
                sb.append(movesMade.get(i + 1)).append(";"); // ход черных
            else sb.append("...;");
        }
        sb.append("\n");
        return sb.toString();
    }

    public boolean isCheck() {
        return board.isCheck();
    }

    public boolean isCheckMate() {
        return isCheck() && getAllMoves().isEmpty();
    }

    public boolean isStaleMate() {
        return !isCheck() && getAllMoves().isEmpty();
    }

    public boolean isDraw() {
        return isStaleMate() || board.isDrawBy50Moves();
    }

    public Color getWinner() {
        if(isCheckMate() && board.getMoveColor() == Color.white)
            return Color.black;
        if(isCheckMate() && board.getMoveColor() == Color.black)
            return Color.white;
        return Color.none;
    }

}
