package workshop.exercises.ex07;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConnectionPoolExercise {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPoolExercise.class);

    private ConnectionPoolExercise() {
    }

    static void main() throws SQLException {
        try (HikariDataSource dataSource = createDataSource();
             Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            resultSet.next();
            log.atInfo().log("Database returned {}", resultSet.getInt(1));
        }
    }

    private static HikariDataSource createDataSource() {
        // TODO: Configure HikariCP with workshop.Db and a maximum pool size of 10.
        throw new UnsupportedOperationException("Complete exercise 7");
    }
}
