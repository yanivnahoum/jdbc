package workshop.exercises.ex04;

import workshop.Db;

import java.sql.Connection;
import java.sql.SQLException;

public final class WriteCustomersExercise {
    private WriteCustomersExercise() {
    }

    public static void main(String[] args) throws SQLException {
        try (Connection connection = Db.connect()) {
            long id = insertCustomer(connection);
            System.out.println("Inserted customer id=" + id);

            int updated = updatePoints(connection, "nobody@example.com", 500);
            System.out.println("Updated rows=" + updated);

            int deleted = deleteCustomer(connection, "delete.me@example.com");
            System.out.println("Deleted rows=" + deleted);
        }
    }

    private static long insertCustomer(Connection connection) throws SQLException {
        // TODO: Insert Ada, bind a SQL NULL for loyalty_pts, and return the generated id.
        throw new UnsupportedOperationException("Complete the insert");
    }

    private static int updatePoints(Connection connection, String email, int points)
            throws SQLException {
        // TODO: Update by email and return the affected-row count.
        throw new UnsupportedOperationException("Complete the update");
    }

    private static int deleteCustomer(Connection connection, String email) throws SQLException {
        // TODO: Delete by email and return the affected-row count.
        throw new UnsupportedOperationException("Complete the delete");
    }
}
