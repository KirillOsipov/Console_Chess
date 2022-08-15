package ChessGame;

import ChessGame.chess_logic.Chess;
import ChessGame.chess_logic.Color;
import ChessGame.data_source.Player;
import ChessGame.data_source.PlayersRepository;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while(true) {
            System.out.print("Enter command: ");
            String command = sc.nextLine();
            switch (command) {
                case "new game" -> {
                    System.out.println("Login white pieces player:");
                    String logWhite = sc.nextLine();
                    System.out.println("Password white pieces player:");
                    String passWhite = sc.nextLine();
                    boolean isLoggedWhite = PlayersRepository.login(logWhite, passWhite);

                    System.out.println("Login black pieces player:");
                    String logBlack = sc.nextLine();
                    System.out.println("Password black pieces player:");
                    String passBlack = sc.nextLine();
                    boolean isLoggedBlack = PlayersRepository.login(logBlack, passBlack);

                    if(isLoggedWhite && isLoggedBlack) {

                        Player whitePlayer = PlayersRepository.findByName(logWhite);
                        Player blackPlayer = PlayersRepository.findByName(logBlack);

                        whitePlayer.setFiguresColor(Color.white);
                        blackPlayer.setFiguresColor(Color.black);

                        whitePlayer.setOpponentName(logBlack);
                        blackPlayer.setOpponentName(logWhite);

                        // Старт игры. Для тестирования разных игровых ситуаций
                        // вместо fromPosition = null можно подставить любой FEN.
                        // Расставить фигуры и получить FEN можно с помощью https://lichess.org/editor
                        Chess resultPosition = ConsoleChess.startGame(null, null);

                        checkGameResult(resultPosition, whitePlayer, blackPlayer);

                        PlayersRepository.updatePlayer(whitePlayer);
                        PlayersRepository.updatePlayer(blackPlayer);

                    } else {
                        if(!isLoggedWhite)
                            System.err.println("Failed to login white pieces player!");
                        if(!isLoggedBlack)
                            System.err.println("Failed to login black pieces player!");
                    }
                }
                case "continue game" -> {
                    System.out.println("Login 1st player:");
                    String log1 = sc.nextLine();
                    System.out.println("Password 1st player:");
                    String pass1 = sc.nextLine();
                    boolean isLoggedPlayer1 = PlayersRepository.login(log1, pass1);

                    System.out.println("Login 2nd player:");
                    String log2 = sc.nextLine();
                    System.out.println("Password 2nd player:");
                    String pass2 = sc.nextLine();
                    boolean isLoggedPlayer2 = PlayersRepository.login(log2, pass2);

                    if(isLoggedPlayer1 && isLoggedPlayer2) {

                        Player firstPlayer = PlayersRepository.findByName(log1);
                        Player secondPlayer = PlayersRepository.findByName(log2);

                        if(firstPlayer.getOpponentName() != null &&
                                secondPlayer.getOpponentName() != null &&
                                firstPlayer.getOpponentName().equals(secondPlayer.getName()) &&
                                secondPlayer.getOpponentName().equals(firstPlayer.getName())) {

                            // Продолжение игры с сохраненной в БД позиции
                            Chess resultPosition = ConsoleChess.startGame(firstPlayer.getFen(), firstPlayer.getMovesMade());

                            checkGameResult(resultPosition, firstPlayer, secondPlayer);

                            PlayersRepository.updatePlayer(firstPlayer);
                            PlayersRepository.updatePlayer(secondPlayer);

                        } else {
                            System.err.println("No current game exist between these 2 players!");
                        }
                    } else {
                        if(!isLoggedPlayer1)
                            System.err.println("Failed to login first player!");
                        if(!isLoggedPlayer2)
                            System.err.println("Failed to login second player!");
                    }
                }
                case "register" -> {
                    System.out.println("Login:");
                    String log = sc.nextLine();
                    System.out.println("Password:");
                    String pass = sc.nextLine();
                    System.out.println("Repeat password:");
                    String passRepeat = sc.nextLine();
                    PlayersRepository.register(log, pass, passRepeat);
                }
                case "show stats" -> PlayersRepository.showPlayersStats();
                case "show full info" -> PlayersRepository.showPlayersFullInfo();
                case "exit" -> {
                    return;
                }

            }

        }

    }

    private static void checkGameResult(Chess resultPosition, Player firstPlayer, Player secondPlayer) {
        if(resultPosition.isDraw()) {

            firstPlayer.setDrawsCount(firstPlayer.getDrawsCount() + 1);
            secondPlayer.setDrawsCount(secondPlayer.getDrawsCount() + 1);

            firstPlayer.setOpponentName(null);
            firstPlayer.setFen(null);
            firstPlayer.setMovesMade(null);
            firstPlayer.setFiguresColor(null);

            secondPlayer.setOpponentName(null);
            secondPlayer.setFen(null);
            secondPlayer.setMovesMade(null);
            secondPlayer.setFiguresColor(null);

        }
        if(resultPosition.getWinner() == firstPlayer.getFiguresColor()) {

            firstPlayer.setWinsCount(firstPlayer.getWinsCount() + 1);
            secondPlayer.setDefeatsCount(secondPlayer.getDefeatsCount() + 1);

            firstPlayer.setOpponentName(null);
            firstPlayer.setFen(null);
            firstPlayer.setMovesMade(null);
            firstPlayer.setFiguresColor(null);

            secondPlayer.setOpponentName(null);
            secondPlayer.setFen(null);
            secondPlayer.setMovesMade(null);
            secondPlayer.setFiguresColor(null);

        }
        if(resultPosition.getWinner() == secondPlayer.getFiguresColor()) {

            firstPlayer.setDefeatsCount(firstPlayer.getDefeatsCount() + 1);
            secondPlayer.setWinsCount(secondPlayer.getWinsCount() + 1);

            firstPlayer.setOpponentName(null);
            firstPlayer.setFen(null);
            firstPlayer.setMovesMade(null);
            firstPlayer.setFiguresColor(null);

            secondPlayer.setOpponentName(null);
            secondPlayer.setFen(null);
            secondPlayer.setMovesMade(null);
            secondPlayer.setFiguresColor(null);

        }
        if(!resultPosition.isDraw() && resultPosition.getWinner() == Color.none) {

            firstPlayer.setFen(resultPosition.getFen());
            secondPlayer.setFen(resultPosition.getFen());

            firstPlayer.setMovesMade(resultPosition.getMovesMade().toString());
            secondPlayer.setMovesMade(resultPosition.getMovesMade().toString());
        }
    }
}
