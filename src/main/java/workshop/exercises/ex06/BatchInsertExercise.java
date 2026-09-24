package workshop.exercises.ex06;

import workshop.Db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class BatchInsertExercise {
    private static final int ROWS = 10_000;
    private static final int BATCH_SIZE = 1_000;

    private BatchInsertExercise() {
    }

    public static void main(String[] args) throws SQLException {
        String url = Db.urlWithParameter("reWriteBatchedInserts", "true");

        try (Connection connection = DriverManager.getConnection(url, Db.USER, Db.PASSWORD)) {
            connection.setAutoCommit(false);
            try {
                long startedAt = System.nanoTime();
                insertBatch(connection);
                connection.commit();
                System.out.printf(
                        "batched: %d ms%n",
                        (System.nanoTime() - startedAt) / 1_000_000);
            } catch (SQLException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackFailure) {
                    exception.addSuppressed(rollbackFailure);
                }
                throw exception;
            }
        }
    }

    private static void insertBatch(Connection connection) throws SQLException {
        // TODO:
        // 1. Prepare one INSERT statement.
        // 2. Bind a unique email and name ROWS times.
        // 3. Add each parameter set to the batch.
        // 4. Execute every BATCH_SIZE rows, then flush any remainder.
        throw new UnsupportedOperationException("Complete exercise 6");
    }
}
