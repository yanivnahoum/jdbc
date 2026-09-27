package com.att.training.jdbc.examples.ex03;

import com.att.training.jdbc.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class SqlInjectionExample {
    private static final Logger log = LoggerFactory.getLogger(SqlInjectionExample.class);

    private SqlInjectionExample() {
    }

    static void main(String[] args) throws SQLException {
        String email = switch (args.length == 0 ? "apostrophe" : args[0]) {
            case "apostrophe" -> "o'brien@example.com";
            case "attack" -> "x' OR 1=1 -- ";
            default -> args[0];
        };
        int minimumPoints = args.length < 2 ? 0 : Integer.parseInt(args[1]);

        try (Connection connection = Db.connect()) {
            try {
                log.atInfo()
                        .setMessage("Vulnerable result: {}")
                        .addArgument(findByEmailVulnerable(connection, email, minimumPoints))
                        .log();
            } catch (SQLException exception) {
                log.atInfo()
                        .setMessage("Vulnerable query failed: {}")
                        .addArgument(exception.getMessage())
                        .log();
            }

            log.atInfo()
                    .setMessage("Safe result: {}")
                    .addArgument(findByEmail(connection, email, minimumPoints))
                    .log();
        }
    }

    public static List<CustomerSummary> findByEmailVulnerable(
            Connection connection,
            String email,
            int minimumPoints) throws SQLException {
        String sql = "SELECT id, full_name FROM customers WHERE email = '"
                     + email + "' AND loyalty_pts > " + minimumPoints;
        log.atInfo().log("Executing vulnerable SQL: {}", sql);

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

    public record CustomerSummary(long id, String fullName) {}
}
