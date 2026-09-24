package workshop.examples.ex08;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import workshop.Db;

import java.sql.SQLException;

public final class DaoExample {
    private static final String UNIQUE_VIOLATION = "23505";

    private DaoExample() {
    }

    public static void main(String[] args) throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Db.URL);
        config.setUsername(Db.USER);
        config.setPassword(Db.PASSWORD);

        try (HikariDataSource dataSource = new HikariDataSource(config)) {
            CustomerDao customerDao = new CustomerDao(dataSource);
            customerDao.findAll().forEach(System.out::println);

            try {
                customerDao.insert("alice@example.com", "Duplicate Alice");
            } catch (SQLException exception) {
                if (UNIQUE_VIOLATION.equals(exception.getSQLState())) {
                    System.out.println("That email address is already registered.");
                } else {
                    throw exception;
                }
            }
        }
    }
}
