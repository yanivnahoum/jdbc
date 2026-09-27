package com.att.training.jdbc.exercises.ex05;

import com.att.training.jdbc.Db;
import com.att.training.jdbc.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class TransferExercise {
    private static final Logger log = LoggerFactory.getLogger(TransferExercise.class);

    private TransferExercise() {
    }

    static void main(String[] args) throws SQLException {
        BigDecimal amount = new BigDecimal(args.length == 0 ? "25.00" : args[0]);

        try (Connection connection = Db.connect()) {
            log.atInfo().log("Before: {}", findAccounts(connection));
            transfer(connection, 1, 2, amount);
            log.atInfo().log("After: {}", findAccounts(connection));
        }
    }

    private static void transfer(
            Connection connection,
            long fromAccount,
            long toAccount,
            BigDecimal amount) throws SQLException {
        // TODO:
        // 1. Validate amount and account ids.
        // 2. Disable auto-commit.
        // 3. Credit and debit as one transaction.
        // 4. Commit on success and roll back on SQLException.
        // 5. Restore the original auto-commit state only after the transaction ends.
        throw new UnsupportedOperationException("Complete exercise 5");
    }

    private static void credit(Connection connection, long accountId, BigDecimal amount)
            throws SQLException {
        changeBalance(connection, accountId, amount);
    }

    private static void debit(Connection connection, long accountId, BigDecimal amount)
            throws SQLException {
        changeBalance(connection, accountId, amount.negate());
    }

    private static void changeBalance(
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
            List<Account> accounts = new ArrayList<>();
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
