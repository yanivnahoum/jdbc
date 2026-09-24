package workshop.examples.ex04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workshop.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;

public final class WriteCustomersExample {
    private static final Logger log = LoggerFactory.getLogger(WriteCustomersExample.class);

    private WriteCustomersExample() {
    }

    static void main() throws SQLException {
        try (Connection connection = Db.connect()) {
            long id = insertCustomer(connection);
            log.atInfo().log("Inserted customer id={}", id);

            int updated = updatePoints(connection, "nobody@example.com", 500);
            log.atInfo().log("Updated rows={}", updated);

            int deleted = deleteCustomer(connection, "delete.me@example.com");
            log.atInfo().log("Deleted rows={}", deleted);
        }
    }

    private static long insertCustomer(Connection connection) throws SQLException {
        String sql = """
                INSERT INTO customers (email, full_name, birth_date, loyalty_pts)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, "ada@example.com");
            statement.setString(2, "Ada Lovelace");
            statement.setObject(3, LocalDate.of(1990, 3, 12));
            statement.setObject(4, null, Types.INTEGER);

            int rows = statement.executeUpdate();
            if (rows != 1) {
                throw new SQLException("Expected one inserted row, got " + rows);
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("The database did not return a generated key");
                }
                return keys.getLong(1);
            }
        }
    }

    private static int updatePoints(Connection connection, String email, int points)
            throws SQLException {
        String sql = """
                UPDATE customers
                SET loyalty_pts = ?
                WHERE email = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, points);
            statement.setString(2, email);
            return statement.executeUpdate();
        }
    }

    private static int deleteCustomer(Connection connection, String email) throws SQLException {
        String sql = "DELETE FROM customers WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            return statement.executeUpdate();
        }
    }
}
