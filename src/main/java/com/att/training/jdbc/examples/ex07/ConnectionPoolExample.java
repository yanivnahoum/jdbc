package com.att.training.jdbc.examples.ex07;

import com.att.training.jdbc.Db;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class ConnectionPoolExample {
    private static final Logger log = LoggerFactory.getLogger(ConnectionPoolExample.class);
    private static final int WARM_UP_ITERATIONS = 5;
    private static final int MEASURED_ITERATIONS = 100;

    private ConnectionPoolExample() {
    }

    static void main() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Db.URL);
        config.setUsername(Db.USER);
        config.setPassword(Db.PASSWORD);
        config.setMaximumPoolSize(10);

        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            runWithPool(dataSource, WARM_UP_ITERATIONS);
            runWithDriverManager(WARM_UP_ITERATIONS);

            long poolMillis = time(() -> runWithPool(dataSource, MEASURED_ITERATIONS));
            long driverManagerMillis = time(
                    () -> runWithDriverManager(MEASURED_ITERATIONS));

            log.atInfo().log("pool: {} ms", poolMillis);
            log.atInfo().log("DriverManager: {} ms", driverManagerMillis);
            log.atInfo().log("These are illustrative local measurements, not a benchmark.");
        }
    }

    private static void runWithPool(DataSource dataSource, int iterations) throws SQLException {
        for (int i = 0; i < iterations; i++) {
            try (Connection connection = dataSource.getConnection()) {
                selectOne(connection);
            }
        }
    }

    private static void runWithDriverManager(int iterations) throws SQLException {
        for (int i = 0; i < iterations; i++) {
            try (Connection connection = DriverManager.getConnection(
                    Db.URL,
                    Db.USER,
                    Db.PASSWORD)) {
                selectOne(connection);
            }
        }
    }

    private static void selectOne(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT 1")) {
            if (!resultSet.next() || resultSet.getInt(1) != 1) {
                throw new SQLException("SELECT 1 returned an unexpected result");
            }
        }
    }

    private static long time(SqlAction action) throws SQLException {
        long startedAt = System.nanoTime();
        action.run();
        return (System.nanoTime() - startedAt) / 1_000_000;
    }

    @FunctionalInterface
    private interface SqlAction {
        void run() throws SQLException;
    }
}
