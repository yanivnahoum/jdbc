package com.att.training.jdbc.exercises.ex08;

import com.att.training.jdbc.Db;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public final class DaoExerciseRunner {
    private static final Logger log = LoggerFactory.getLogger(DaoExerciseRunner.class);

    private DaoExerciseRunner() {
    }

    static void main() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Db.URL);
        config.setUsername(Db.USER);
        config.setPassword(Db.PASSWORD);

        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            CustomerDaoExercise customerDao = new CustomerDaoExercise(dataSource);
            customerDao.findAll().forEach(customer -> log.atInfo().log("{}", customer));
        }
    }
}
