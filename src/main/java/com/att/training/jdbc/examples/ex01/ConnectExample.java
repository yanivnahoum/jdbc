package com.att.training.jdbc.examples.ex01;

import com.att.training.jdbc.Db;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public final class ConnectExample {
    private static final Logger log = LoggerFactory.getLogger(ConnectExample.class);

    private ConnectExample() {
    }

    static void main() throws SQLException {
        try (Connection connection = Db.connect()) {
            DatabaseMetaData metadata = connection.getMetaData();
            log.atInfo()
                    .setMessage("Connected to {} {}")
                    .addArgument(metadata.getDatabaseProductName())
                    .addArgument(metadata.getDatabaseProductVersion())
                    .log();
        }
    }
}
