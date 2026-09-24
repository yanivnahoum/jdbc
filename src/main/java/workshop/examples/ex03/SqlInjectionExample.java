package workshop.examples.ex03;

import workshop.Db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SqlInjectionExample {
    private SqlInjectionExample() {
    }

    public static void main(String[] args) throws SQLException {
        String email = switch (args.length == 0 ? "apostrophe" : args[0]) {
            case "apostrophe" -> "o'brien@example.com";
            case "attack" -> "x' OR 1=1 -- ";
            default -> args[0];
        };
        int minimumPoints = args.length < 2 ? 0 : Integer.parseInt(args[1]);

        try (Connection connection = Db.connect()) {
            try {
                System.out.println("Vulnerable result: "
                                   + findByEmailVulnerable(connection, email, minimumPoints));
            } catch (SQLException exception) {
                System.out.println("Vulnerable query failed: " + exception.getMessage());
            }

            System.out.println("Safe result: "
                               + findByEmail(connection, email, minimumPoints));
        }
    }

    public static List<CustomerSummary> findByEmailVulnerable(
            Connection connection,
            String email,
            int minimumPoints) throws SQLException {
        String sql = "SELECT id, full_name FROM customers WHERE email = '"
                     + email + "' AND loyalty_pts > " + minimumPoints;
        System.out.println("Executing vulnerable SQL: " + sql);

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            return readCustomers(resultSet);
        }
    }

    public static List<CustomerSummary> findByEmail(
            Connection connection,
            String email,
            int minimumPoints) throws SQLException {
        String sql = """
                SELECT id, full_name
                FROM customers
                WHERE email = ? AND loyalty_pts > ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setInt(2, minimumPoints);

            try (ResultSet resultSet = statement.executeQuery()) {
                return readCustomers(resultSet);
            }
        }
    }

    private static List<CustomerSummary> readCustomers(ResultSet resultSet) throws SQLException {
        List<CustomerSummary> customers = new ArrayList<>();
        while (resultSet.next()) {
            customers.add(new CustomerSummary(
                    resultSet.getLong("id"),
                    resultSet.getString("full_name")));
        }
        return customers;
    }

    public record CustomerSummary(long id, String fullName) {
    }
}
