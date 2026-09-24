package workshop.examples.ex06;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workshop.Db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class BatchInsertExample {
    private static final Logger log = LoggerFactory.getLogger(BatchInsertExample.class);
    private static final int WARM_UP_ROWS = 100;
    private static final int MEASURED_ROWS = 10_000;
    private static final int BATCH_SIZE = 1_000;

    private BatchInsertExample() {
    }

    static void main() throws SQLException {
        String url = Db.urlWithParameter("reWriteBatchedInserts", "true");

        long oneByOneMillis = measure(url, false);
        long batchMillis = measure(url, true);

        log.atInfo().log("one-by-one: {} ms", oneByOneMillis);
        log.atInfo().log("batched: {} ms", batchMillis);
        log.atInfo().log("These are illustrative local measurements, not a benchmark.");
    }

    private static long measure(String url, boolean batched) throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, Db.USER, Db.PASSWORD)) {
            connection.setAutoCommit(false);
            try {
                insertCustomers(connection, WARM_UP_ROWS, "warmup", batched);
                connection.rollback();

                long startedAt = System.nanoTime();
                insertCustomers(connection, MEASURED_ROWS, "measured", batched);
                long elapsed = System.nanoTime() - startedAt;
                connection.rollback();
                return elapsed / 1_000_000;
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

    private static void insertCustomers(
            Connection connection,
            int count,
            String prefix,
            boolean batched) throws SQLException {
        String sql = """
                INSERT INTO customers (email, full_name)
                VALUES (?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                statement.setString(1, prefix + i + "@example.com");
                statement.setString(2, "Customer " + i);

                if (batched) {
                    statement.addBatch();
                    if ((i + 1) % BATCH_SIZE == 0) {
                        statement.executeBatch();
                    }
                } else {
                    statement.executeUpdate();
                }
            }

            if (batched && count % BATCH_SIZE != 0) {
                statement.executeBatch();
            }
        }
    }
}
