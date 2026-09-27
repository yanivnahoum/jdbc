package com.att.training.jdbc.exercises.ex02;

import com.att.training.jdbc.Db;
import com.att.training.jdbc.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class QueryCustomersExercise {
    private static final Logger log = LoggerFactory.getLogger(QueryCustomersExercise.class);

    private QueryCustomersExercise() {
    }

    static void main() throws SQLException {
        try (Connection connection = Db.connect()) {
            findCustomers(connection, 100)
                    .forEach(customer -> log.atInfo().log("{}", customer));
        }
    }

    private static List<Customer> findCustomers(Connection connection, int minimumPoints)
            throws SQLException {
        // TODO:
        // 1. Prepare the query from the README.
        // 2. Bind minimumPoints.
        // 3. Iterate the ResultSet and map every row to Customer.
        // 4. Preserve the difference between NULL and zero loyalty points.
        throw new UnsupportedOperationException("Complete exercise 2");
    }
}
