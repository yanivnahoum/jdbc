package com.att.training.jdbc.exercises.ex08;

import com.att.training.jdbc.Db;
import com.att.training.jdbc.model.Customer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class CustomerDaoExercise {
    private final DataSource dataSource;

    public CustomerDaoExercise(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Customer> findAll() throws SQLException {
        // Intentionally broken: find and fix the resource, query, mapping, and
        // connection-management problems described in the README.
        // TODO: Use dataSource instead of opening a physical connection here.
        Connection connection = Db.connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM customers");

        List<Customer> customers = new ArrayList<>();
        while (resultSet.next()) {
            customers.add(new Customer(
                    resultSet.getLong(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getObject(4, LocalDate.class),
                    resultSet.getInt(5)));
        }

        connection.close();
        return customers;
    }
}
