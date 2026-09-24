package workshop.exercises.ex08;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import workshop.Db;

import java.sql.SQLException;

public final class DaoExerciseRunner {
    private DaoExerciseRunner() {
    }

    public static void main(String[] args) throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Db.URL);
        config.setUsername(Db.USER);
        config.setPassword(Db.PASSWORD);

        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            CustomerDaoExercise customerDao = new CustomerDaoExercise(dataSource);
            customerDao.findAll().forEach(System.out::println);
        }
    }
}
