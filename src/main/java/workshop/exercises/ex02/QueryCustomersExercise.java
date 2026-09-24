package workshop.exercises.ex02;

import workshop.Db;
import workshop.model.Customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class QueryCustomersExercise {
    private QueryCustomersExercise() {
    }

    public static void main(String[] args) throws SQLException {
        try (Connection connection = Db.connect()) {
            findCustomers(connection, 100).forEach(System.out::println);
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
