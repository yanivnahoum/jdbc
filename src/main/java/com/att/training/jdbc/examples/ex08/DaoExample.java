package com.att.training.jdbc.examples.ex08;

import com.att.training.jdbc.Db;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public final class DaoExample {
    private static final Logger log = LoggerFactory.getLogger(DaoExample.class);
    private static final String UNIQUE_VIOLATION = "23505";

    private DaoExample() {
    }

    static void main() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Db.URL);
        config.setUsername(Db.USER);
        config.setPassword(Db.PASSWORD);

        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            CustomerDao customerDao = new CustomerDao(dataSource);
            customerDao.findAll().forEach(customer -> log.atInfo().log("{}", customer));

            try {
                customerDao.insert("alice@example.com", "Duplicate Alice");
            } catch (SQLException exception) {
                if (UNIQUE_VIOLATION.equals(exception.getSQLState())) {
                    log.atInfo().log("That email address is already registered.");
                } else {
                    throw exception;
                }
            }
        }
    }
}
