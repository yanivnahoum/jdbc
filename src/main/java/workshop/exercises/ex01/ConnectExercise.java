package workshop.exercises.ex01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public final class ConnectExercise {
    private static final Logger log = LoggerFactory.getLogger(ConnectExercise.class);

    private ConnectExercise() {
    }

    static void main() throws SQLException {
        try (Connection connection = openConnection()) {
            DatabaseMetaData metadata = connection.getMetaData();
            log.atInfo()
                    .setMessage("Connected to {} {}")
                    .addArgument(metadata.getDatabaseProductName())
                    .addArgument(metadata.getDatabaseProductVersion())
                    .log();
        }
    }

    private static Connection openConnection() throws SQLException {
        // TODO: Open and return a connection using DriverManager and workshop.Db.
        throw new UnsupportedOperationException("Complete exercise 1");
    }
}
