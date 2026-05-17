package com.cac.peliculas.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DatabaseConnection() {}

    public static Connection open() throws SQLException {
        return DriverManager.getConnection(
                ConfiguracionJdbc.getJdbcUrl(),
                ConfiguracionJdbc.getUser(),
                ConfiguracionJdbc.getPassword());
    }
}
