package com.library.database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection manager using SQLite
 */
public class Database {
    private static final String DB_NAME = "library.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
    
    private static Database instance;
    private Connection connection;
    
    private Database() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            connect();
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error initializing database", e);
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
    
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    private void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
            LOGGER.info("Database connected successfully");
        }
    }
    
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }
    
    private void createTables() {
        try {
            createBooksTable();
            createUsersTable();
            createBorrowRecordsTable();
            createBookReviewsTable();
            LOGGER.info("Database tables created successfully");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating database tables", e);
            throw new RuntimeException("Failed to create database tables", e);
        }
    }
    
    private void createBooksTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS books (
                isbn TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                publisher TEXT,
                publish_date TEXT,
                page_count INTEGER,
                genre TEXT,
                language TEXT,
                rating REAL DEFAULT 0.0,
                rating_count INTEGER DEFAULT 0,
                description TEXT,
                is_borrowed BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private void createUsersTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                user_type TEXT NOT NULL CHECK (user_type IN ('student', 'staff')),
                major_department TEXT,
                enrollment_hire_date TEXT,
                is_active BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private void createBorrowRecordsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS borrow_records (
                record_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id TEXT NOT NULL,
                book_isbn TEXT NOT NULL,
                borrow_date TIMESTAMP NOT NULL,
                due_date TIMESTAMP NOT NULL,
                return_date TIMESTAMP,
                is_returned BOOLEAN DEFAULT FALSE,
                fine_amount REAL DEFAULT 0.0,
                notes TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id),
                FOREIGN KEY (book_isbn) REFERENCES books(isbn)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    private void createBookReviewsTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS book_reviews (
                review_id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_isbn TEXT NOT NULL,
                user_id TEXT NOT NULL,
                rating REAL NOT NULL CHECK (rating >= 1.0 AND rating <= 5.0),
                comment TEXT,
                review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_approved BOOLEAN DEFAULT TRUE,
                FOREIGN KEY (book_isbn) REFERENCES books(isbn),
                FOREIGN KEY (user_id) REFERENCES users(id),
                UNIQUE(book_isbn, user_id)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
    
    /**
     * Execute a query and return ResultSet
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement pstmt = connection.prepareStatement(sql);
        
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
        
        return pstmt.executeQuery();
    }
    
    /**
     * Execute an update query (INSERT, UPDATE, DELETE)
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            return pstmt.executeUpdate();
        }
    }
    
    /**
     * Begin transaction
     */
    public void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }
    
    /**
     * Commit transaction
     */
    public void commitTransaction() throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
    }
    
    /**
     * Rollback transaction
     */
    public void rollbackTransaction() throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
    }
    
    /**
     * Close database connection
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Database connection closed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error closing database connection", e);
        }
    }
    
    /**
     * Check if database connection is valid
     */
    public boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}
