package workshop.exercises.ex03;

import workshop.Db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class SafeCustomerSearchExercise {
    private SafeCustomerSearchExercise() {
    }

    public static void main(String[] args) throws SQLException {
        String email = args.length == 0 ? "o'brien@example.com" : args[0];

        try (Connection connection = Db.connect()) {
            findCustomerNames(connection, email, 0).forEach(System.out::println);
        }
    }

    private static List<String> findCustomerNames(
            Connection connection,
            String email,
            int minimumPoints) throws SQLException {
        // TODO: Query by email and minimum points using two bind parameters.
        // Do not concatenate either value into the SQL string.
        throw new UnsupportedOperationException("Complete exercise 3");
    }
}
