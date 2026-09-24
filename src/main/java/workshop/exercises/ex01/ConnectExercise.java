package workshop.exercises.ex01;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public final class ConnectExercise {
    private ConnectExercise() {
    }

    public static void main(String[] args) throws SQLException {
        try (Connection connection = openConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            System.out.printf(
                    "Connected to %s %s%n",
                    metadata.getDatabaseProductName(),
                    metadata.getDatabaseProductVersion());
        }
    }

    private static Connection openConnection() throws SQLException {
        // TODO: Open and return a connection using DriverManager and workshop.Db.
        throw new UnsupportedOperationException("Complete exercise 1");
    }
}
