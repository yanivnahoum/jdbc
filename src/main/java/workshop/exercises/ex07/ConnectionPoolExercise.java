package workshop.exercises.ex07;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConnectionPoolExercise {
    private ConnectionPoolExercise() {
    }

    public static void main(String[] args) throws SQLException {
        try (HikariDataSource dataSource = createDataSource();
             Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            resultSet.next();
            System.out.println("Database returned " + resultSet.getInt(1));
        }
    }

    private static HikariDataSource createDataSource() {
        // TODO: Configure HikariCP with workshop.Db and a maximum pool size of 10.
        throw new UnsupportedOperationException("Complete exercise 7");
    }
}
