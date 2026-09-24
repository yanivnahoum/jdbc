package workshop.examples.ex05;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import workshop.Db;
import workshop.model.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class TransferExample {
    private static final Logger log = LoggerFactory.getLogger(TransferExample.class);

    private TransferExample() {
    }

    static void main(String[] args) throws SQLException {
        BigDecimal amount = new BigDecimal(args.length == 0 ? "25.00" : args[0]);

        try (Connection connection = Db.connect()) {
            log.atInfo().log("Before: {}", findAccounts(connection));
            transfer(connection, 1, 2, amount);
            log.atInfo().log("After: {}", findAccounts(connection));
        }
    }

    public static void transfer(
            Connection connection,
            long fromAccount,
            long toAccount,
            BigDecimal amount) throws SQLException {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (fromAccount == toAccount) {
            throw new IllegalArgumentException("source and destination must differ");
        }

        boolean originalAutoCommit = connection.getAutoCommit();
        if (!originalAutoCommit) {
            throw new SQLException("transfer requires a connection with auto-commit enabled");
        }

        connection.setAutoCommit(false);
        SQLException failure = null;
        boolean transactionEnded = false;
        try {
            credit(connection, toAccount, amount);
            debit(connection, fromAccount, amount);
            connection.commit();
            transactionEnded = true;
        } catch (SQLException exception) {
            failure = exception;
            try {
                connection.rollback();
                transactionEnded = true;
            } catch (SQLException rollbackFailure) {
                exception.addSuppressed(rollbackFailure);
            }
            throw exception;
        } finally {
            if (transactionEnded) {
                try {
                    connection.setAutoCommit(originalAutoCommit);
                } catch (SQLException restoreFailure) {
                    if (failure != null) {
                        failure.addSuppressed(restoreFailure);
                    } else {
                        throw restoreFailure;
                    }
                }
            }
        }
    }

    private static void credit(Connection connection, long accountId, BigDecimal amount)
            throws SQLException {
        updateBalance(connection, accountId, amount);
    }

    private static void debit(Connection connection, long accountId, BigDecimal amount)
            throws SQLException {
        updateBalance(connection, accountId, amount.negate());
    }

    private static void updateBalance(
            Connection connection,
            long accountId,
            BigDecimal change) throws SQLException {
        String sql = """
                UPDATE accounts
                SET balance = balance + ?
                WHERE id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, change);
            statement.setLong(2, accountId);
            int rows = statement.executeUpdate();
            if (rows != 1) {
                throw new SQLException("Expected one account, found " + rows);
            }
        }
    }

    private static List<Account> findAccounts(Connection connection) throws SQLException {
        String sql = """
                SELECT id, owner_id, balance
                FROM accounts
                ORDER BY id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            var accounts = new java.util.ArrayList<Account>();
            while (resultSet.next()) {
                accounts.add(new Account(
                        resultSet.getLong("id"),
                        resultSet.getLong("owner_id"),
                        resultSet.getBigDecimal("balance")));
            }
            return accounts;
        }
    }
}
