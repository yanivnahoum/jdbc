package workshop.examples.ex02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workshop.Db;
import workshop.model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class QueryCustomersExample {
    private static final Logger log = LoggerFactory.getLogger(QueryCustomersExample.class);

    private QueryCustomersExample() {
    }

    static void main(String[] args) throws SQLException {
        int minimumPoints = args.length == 0 ? 100 : Integer.parseInt(args[0]);

        try (Connection connection = Db.connect()) {
            findCustomers(connection, minimumPoints)
                    .forEach(customer -> log.atInfo().log("{}", customer));
        }
    }

    public static List<Customer> findCustomers(Connection connection, int minimumPoints)
            throws SQLException {
        String sql = """
                SELECT id, email, full_name, birth_date, loyalty_pts
                FROM customers
                WHERE loyalty_pts IS NULL OR loyalty_pts > ?
                ORDER BY id
                """;

        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, minimumPoints);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    customers.add(mapCustomer(resultSet));
                }
            }
        }
        return customers;
    }

    private static Customer mapCustomer(ResultSet resultSet) throws SQLException {
        return new Customer(
                resultSet.getLong("id"),
                resultSet.getString("email"),
                resultSet.getString("full_name"),
                resultSet.getObject("birth_date", LocalDate.class),
                resultSet.getObject("loyalty_pts", Integer.class));
    }
}
