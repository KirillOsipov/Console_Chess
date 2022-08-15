package ChessGame;

import ChessGame.chess_logic.Chess;
import ChessGame.chess_logic.Color;
import ChessGame.chess_logic.Square;

import java.util.Scanner;

public final class ConsoleChess {
    public static Chess startGame(String fromPosition, String movesMade) {
        Chess chess;
        if(fromPosition == null)
            chess = new Chess();
        else
            chess = new Chess(fromPosition); // rb2q3/3k4/1n6/8/8/5N2/3K4/1BQ4R b - - 0 1

        if(movesMade != null)
            chess.setMovesMade(movesMade);

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("\nFEN: ");
            System.out.println(chess.getFen());
            System.out.println();
            System.out.println(chessToAscii(chess));

            System.out.println("Possible moves:\n");
            for (String move : chess.getAllMoves()) {
                System.out.print(move + "\t");
            }
            System.out.println();

            if(chess.isCheck() && !chess.isCheckMate()) {
                System.out.println("\nCHECK");
            }
            if(chess.isCheckMate()) {
                System.out.println("\nCHECKMATE");
                System.out.println(chess.getWinner() == Color.white ? "White won!" : "Black won!");
                break;
            }
            if(chess.isStaleMate()) {
                System.out.println("\nSTALEMATE");
            }
            if(chess.isDraw()) {
                System.out.println("\nDraw");
                break;
            }

            System.out.print("\nEnter your move: ");
            String move = sc.nextLine();
            if(move.equals("")) break;
            chess = chess.move(move);

            System.out.println(chess.getMovesMadeAsString());
        }
        return chess;
    }

    private static String chessToAscii(Chess chess) {
        StringBuilder sb = new StringBuilder("  +-----------------+\n");
        for(int y = 7; y >= 0; y--) {
            sb.append(y + 1);
            sb.append(" | ");
            for(int x = 0; x < 8; x++) {
                sb.append(chess.getFigureAt(new Square(x, y))).append(" ");
            }
            sb.append("|\n");
        }
        sb.append("  +-----------------+\n");
        sb.append("    a b c d e f g h\n");
        return sb.toString();
    }
}
