package ChessGame.data_source;

import ChessGame.chess_logic.Color;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

public class PlayersRepository {
    public static List<Player> findAll() {
        try (var st = AppDataSource.getConnection()
                .prepareStatement("SELECT * FROM \"Players\" ORDER BY (\"Players\".\"Wins\"*1+\"Players\".\"Draws\"*0.5) DESC;")) {
            // сортировка по соотношению побед/ничьих/поражений
            // из расчета 1 очко за победу, 0.5 очков за ничью и 0 за поражение

            ResultSet result = st.executeQuery();
            return mapResultSetToPlayers(result);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Player findByName(String name) {
        try (var st = AppDataSource.getConnection()
                .prepareStatement("SELECT * FROM \"Players\" WHERE \"Name\"=?;")) {

            st.setString(1, name);
            ResultSet result = st.executeQuery();
            return mapResultSetToPlayers(result).get(0);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private static List<Player> mapResultSetToPlayers(ResultSet resultSet) {
        List<Player> players = new LinkedList<>();

        while(resultSet.next()) {
            String name = resultSet.getString("Name");
            String password = resultSet.getString("Password");
            int wins = resultSet.getInt("Wins");
            int draws = resultSet.getInt("Draws");
            int defeats = resultSet.getInt("Defeats");
            String opponent = resultSet.getString("Opponent");
            String fen = resultSet.getString("FEN");
            String colorLetter = resultSet.getString("Color");
            String moves = resultSet.getString("Moves");
            Color color = null;
            if(colorLetter != null && colorLetter.equals("w"))
                color = Color.white;
            if(colorLetter != null && colorLetter.equals("b"))
                color = Color.black;

            players.add(new Player(name, wins, draws, defeats, opponent, fen, moves, color, password));
        }
        return players;
    }

    public static void showPlayersStats() {
        try{
            var players = PlayersRepository.findAll().stream().toList();

            System.out.println("\tName\t|  Wins | Draws | Defeats");
            System.out.println("--------------------------------------");
            for (Player player : players) {
                System.out.println(
                        player.getName() + "\t\t|\t" +
                                player.getWinsCount()  + "\t|\t " +
                                player.getDrawsCount()  + "\t|\t " +
                                player.getDefeatsCount()
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showPlayersFullInfo() {
        try{
            var players = PlayersRepository.findAll().stream().toList();

            System.out.println("\tName\t|  Wins | Draws | Defeats | Opponent | FEN | Moves | Color | Password");
            System.out.println("--------------------------------------");
            for (Player player : players) {
                System.out.println(
                        player.getName() + "\t\t|\t" +
                                player.getWinsCount() + "\t|\t " +
                                player.getDrawsCount() + "\t|\t " +
                                player.getDefeatsCount() + "\t|\t " +
                                player.getOpponentName() + "\t|\t " +
                                player.getFen() + "\t|\t " +
                                player.getMovesMade() + "\t|\t " +
                                player.getFiguresColor() + "\t|\t " +
                                player.getPassword()
                );
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean register(String log, String pass, String passRepeat) {
        if(!pass.equals(passRepeat)) {
            System.err.println("Passwords don't match!");
            return false;
        }

        try (var st = AppDataSource.getConnection()
                .prepareStatement("INSERT INTO \"Players\" (\"Name\", \"Password\") VALUES (?, ?);")) {

            st.setString(1, log);
            st.setString(2, pass);
            st.execute();

            System.out.println("Registration completed!");
            return true;
        }
        catch (SQLException e) {
            System.err.println("Registration failed!");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean login(String log, String pass) {
        try (var st = AppDataSource.getConnection()
                .prepareStatement("SELECT \"Name\" FROM \"Players\" WHERE \"Name\"=? AND \"Password\"=?;")) {

            st.setString(1, log);
            st.setString(2, pass);
            ResultSet result = st.executeQuery();

            if(result.next()) {
                System.out.println("Login success!");
                return true;
            } else {
                System.err.println("Login failed!");
                return false;
            }
        }
        catch (SQLException e) {
            System.err.println("Login failed!");
            e.printStackTrace();
            return false;
        }
    }

    public static void updatePlayer(Player player) {
        try (var st = AppDataSource.getConnection()
                .prepareStatement("UPDATE \"Players\" SET " +
                        "\"Wins\"=?, " +
                        "\"Draws\"=?, " +
                        "\"Defeats\"=?, " +
                        "\"Opponent\"=?, " +
                        "\"FEN\"=?, " +
                        "\"Moves\"=?, " +
                        "\"Color\"=? " +
                        "WHERE \"Name\"=?;")) {

            st.setInt(1, player.getWinsCount());
            st.setInt(2, player.getDrawsCount());
            st.setInt(3, player.getDefeatsCount());

            if (player.getOpponentName() == null || player.getOpponentName().equals("")) {
                st.setNull(4, Types.VARCHAR);
            } else {
                st.setString(4, player.getOpponentName());
            }
            if (player.getFen() == null || player.getFen().equals("")) {
                st.setNull(5, Types.VARCHAR);
            } else {
                st.setString(5, player.getFen());
            }
            if (player.getMovesMade() == null || player.getMovesMade().equals("")) {
                st.setNull(6, Types.VARCHAR);
            } else {
                st.setString(6, player.getMovesMade());
            }
            if (player.getFiguresColor() == null || player.getFiguresColor() == Color.none) {
                st.setNull(7, Types.CHAR);
            } else {
                st.setString(7, String.valueOf(player.getFiguresColor().getColorLetter()));
            }

            st.setString(8, player.getName());

            st.execute();
        }
        catch (SQLException e) {
            System.err.println("Update failed!");
            e.printStackTrace();
        }
    }

}
