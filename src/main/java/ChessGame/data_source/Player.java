package ChessGame.data_source;

import ChessGame.chess_logic.Color;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Player {
    private String name; // логин
    private String password; // пароль
    private int winsCount; // победы
    private int drawsCount; // ничьи
    private int defeatsCount; // поражения
    private String opponentName; // с кем играет
    private String fen; // FEN текущей партии
    private String movesMade; // запись ходов в партии
    private Color figuresColor; // каким цветом играет

    public Player(String name, int winsCount, int drawsCount, int defeatsCount, String opponentName,
                  String fen, String movesMade, Color figuresColor, String password) {
        this.name = name;
        this.winsCount = winsCount;
        this.drawsCount = drawsCount;
        this.defeatsCount = defeatsCount;
        this.opponentName = opponentName;
        this.fen = fen;
        this.movesMade = movesMade;
        this.figuresColor = figuresColor;
        this.password = password;
    }

    public Player(String name, int winsCount, int drawsCount, int defeatsCount, String password) {
        this.name = name;
        this.winsCount = winsCount;
        this.drawsCount = drawsCount;
        this.defeatsCount = defeatsCount;
        this.opponentName = null;
        this.fen = null;
        this.movesMade = null;
        this.figuresColor = null;
        this.password = password;
    }

    public Player(String name, String password) {
        this.name = name;
        this.winsCount = 0;
        this.drawsCount = 0;
        this.defeatsCount = 0;
        this.opponentName = null;
        this.fen = null;
        this.movesMade = null;
        this.figuresColor = null;
        this.password = password;
    }

}
