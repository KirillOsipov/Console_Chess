package ChessGame.data_source;

import lombok.Getter;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class AppDataSource {
    private static final PGSimpleDataSource ds = new PGSimpleDataSource();
    @Getter
    private static final Connection connection;

    static {
        ds.setServerNames(new String[] { "localhost:5432" });
        ds.setDatabaseName("ChessDB");
        ds.setUser("postgres");
        ds.setPassword("password12345");

        try {
            connection = ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
