package workshop.exercises.ex03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workshop.Db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public final class SafeCustomerSearchExercise {
    private static final Logger log = LoggerFactory.getLogger(SafeCustomerSearchExercise.class);

    private SafeCustomerSearchExercise() {
    }

    static void main(String[] args) throws SQLException {
        String email = args.length == 0 ? "o'brien@example.com" : args[0];

        try (Connection connection = Db.connect()) {
            findCustomerNames(connection, email, 0)
                    .forEach(name -> log.atInfo().log("{}", name));
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
