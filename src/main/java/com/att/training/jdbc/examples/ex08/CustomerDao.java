package com.att.training.jdbc.examples.ex08;

import com.att.training.jdbc.model.Customer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CustomerDao {
    private final DataSource dataSource;

    public CustomerDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Customer> findAll() throws SQLException {
        String sql = """
                SELECT id, email, full_name, birth_date, loyalty_pts
                FROM customers
                ORDER BY id
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Customer> customers = new ArrayList<>();
            while (resultSet.next()) {
                customers.add(new Customer(
                        resultSet.getLong("id"),
                        resultSet.getString("email"),
                        resultSet.getString("full_name"),
                        resultSet.getObject("birth_date", LocalDate.class),
                        resultSet.getObject("loyalty_pts", Integer.class)));
            }
            return customers;
        }
    }

    public void insert(String email, String fullName) throws SQLException {
        String sql = """
                INSERT INTO customers (email, full_name)
                VALUES (?, ?)
                """;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, fullName);
            statement.executeUpdate();
        }
    }
}
