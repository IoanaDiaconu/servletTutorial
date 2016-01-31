package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Ioana on 1/31/2016.
 * Responsible for the management of database
 */
public class DatabaseManager {
    // JDBC driver name and database URL
    private static final String DB_URL = "jdbc:mysql://localhost/school";

    //  Database credentials
    private static final String USER = "root";
    private static final String PASS = "passw0rd";

    private static Connection conn = null;

    public static Connection getConnection() {
        // Register JDBC driver
        try {
            if (getConn() == null || conn.isClosed()) {

                Class.forName("com.mysql.jdbc.Driver");
                // Open a connection
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
            } else {
                return getConn();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    private static Connection getConn() {
        return conn;
    }
}
