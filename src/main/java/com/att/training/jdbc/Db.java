package com.att.training.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    public static final String URL = envOrDefault(
            "JDBC_URL",
            "jdbc:postgresql://localhost:5432/workshop");
    public static final String USER = envOrDefault("JDBC_USER", "com/att/training/jdbc");
    public static final String PASSWORD = envOrDefault("JDBC_PASSWORD", "com/att/training/jdbc");

    private Db() {
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String urlWithParameter(String name, String value) {
        String separator = URL.contains("?") ? "&" : "?";
        return URL + separator + name + "=" + value;
    }

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
