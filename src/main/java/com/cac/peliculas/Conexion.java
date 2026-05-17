package com.cac.peliculas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private Connection connection;

    public Conexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    ConfiguracionJdbc.getJdbcUrl(),
                    ConfiguracionJdbc.getUser(),
                    ConfiguracionJdbc.getPassword());
        } catch (ClassNotFoundException | SQLException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
