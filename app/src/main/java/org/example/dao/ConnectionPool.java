package org.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;

public class ConnectionPool {
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/AndersenDB";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "root";
    private static final int MAX_CONNECTIONS = 15;
    private static ConnectionPool instance;
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final Deque<Connection> connectionPool;
    private final int maxConnections;

    private ConnectionPool(String jdbcUrl, String username, String password, int maxConnections) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.maxConnections = maxConnections;
        this.connectionPool = new ArrayDeque<>(maxConnections);
        initializeConnectionPool();
    }

    public static synchronized ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool(JDBC_URL,USERNAME,PASSWORD,MAX_CONNECTIONS);
        }
        return instance;
    }

    private void initializeConnectionPool() {
        try {
            DriverManager.registerDriver(new org.postgresql.Driver());
            for (int i = 0; i < maxConnections; i++) {
                Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                connectionPool.add(connection);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing connection pool", e);
        }
    }

    public synchronized Connection getConnection() {
        if (connectionPool.isEmpty()) {
            throw new IllegalStateException("Connection pool is empty");
        }
        return connectionPool.removeFirst();
    }

    public synchronized void releaseConnection(Connection connection) {
        if (connection != null) {
            connectionPool.addLast(connection);
        }
    }

    public void closeAllConnections() {
        for (Connection connection : connectionPool) {
            try {
                connection.close();
            } catch (SQLException e) {

            }
        }
    }
}
