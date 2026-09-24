package workshop.examples.ex01;

import workshop.Db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public final class ConnectExample {
    private ConnectExample() {
    }

    public static void main(String[] args) throws SQLException {
        try (Connection connection = Db.connect()) {
            DatabaseMetaData metadata = connection.getMetaData();
            System.out.printf(
                    "Connected to %s %s%n",
                    metadata.getDatabaseProductName(),
                    metadata.getDatabaseProductVersion());
        }
    }
}
