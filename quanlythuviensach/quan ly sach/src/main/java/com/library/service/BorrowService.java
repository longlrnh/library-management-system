package com.library.service;

import com.library.database.Database;
import com.library.model.BorrowRecord;
import com.library.model.Book;
import com.library.model.User;
import com.library.model.Student;
import com.library.model.Staff;
import com.library.database.BookDAO;
import com.library.database.UserDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for managing book borrowing and returning operations
 */
public class BorrowService {
    private static final Logger LOGGER = Logger.getLogger(BorrowService.class.getName());
    private static final int DEFAULT_BORROW_DAYS = 14; // Default borrowing period
    private static final double FINE_PER_DAY = 5000.0; // Fine amount per overdue day in VND
    
    private final Database database;
    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    
    public BorrowService() {
        this.database = Database.getInstance();
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
    }
    
    /**
     * Borrow a book
     */
    public boolean borrowBook(String userId, String bookIsbn) {
        try {
            database.beginTransaction();
            
            // Validate user and book
            User user = userDAO.findById(userId);
            Book book = bookDAO.findByIsbn(bookIsbn);
            
            if (user == null) {
                LOGGER.warning("User not found: " + userId);
                database.rollbackTransaction();
                return false;
            }
            
            if (book == null) {
                LOGGER.warning("Book not found: " + bookIsbn);
                database.rollbackTransaction();
                return false;
            }
            
            if (book.isBorrowed()) {
                LOGGER.warning("Book already borrowed: " + bookIsbn);
                database.rollbackTransaction();
                return false;
            }
            
            // Check user's borrow limit
            if (!canUserBorrowMore(user)) {
                LOGGER.warning("User reached borrow limit: " + userId);
                database.rollbackTransaction();
                return false;
            }
            
            // Create borrow record
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dueDate = now.plusDays(DEFAULT_BORROW_DAYS);
            
            String sql = """
                INSERT INTO borrow_records (user_id, book_isbn, borrow_date, due_date, is_returned)
                VALUES (?, ?, ?, ?, FALSE)
            """;
            
            int result = database.executeUpdate(sql, userId, bookIsbn, 
                now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                dueDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            if (result > 0) {
                // Update book status
                boolean updateResult = bookDAO.updateBorrowStatus(bookIsbn, true);
                if (updateResult) {
                    database.commitTransaction();
                    LOGGER.info(String.format("Book borrowed successfully: User %s borrowed %s", userId, bookIsbn));
                    return true;
                } else {
                    database.rollbackTransaction();
                    return false;
                }
            } else {
                database.rollbackTransaction();
                return false;
            }
            
        } catch (SQLException e) {
            try {
                database.rollbackTransaction();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Error rolling back transaction", rollbackEx);
            }
            LOGGER.log(Level.SEVERE, "Error borrowing book", e);
            return false;
        }
    }
    
    /**
     * Return a book
     */
    public boolean returnBook(String userId, String bookIsbn) {
        try {
            database.beginTransaction();
            
            // Find active borrow record
            BorrowRecord record = getActiveBorrowRecord(userId, bookIsbn);
            if (record == null) {
                LOGGER.warning("No active borrow record found for user " + userId + " and book " + bookIsbn);
                database.rollbackTransaction();
                return false;
            }
            
            LocalDateTime returnDate = LocalDateTime.now();
            double fineAmount = record.calculateFine(FINE_PER_DAY);
            
            // Update borrow record
            String sql = """
                UPDATE borrow_records 
                SET return_date = ?, is_returned = TRUE, fine_amount = ?
                WHERE record_id = ?
            """;
            
            int result = database.executeUpdate(sql,
                returnDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                fineAmount,
                record.getRecordId());
            
            if (result > 0) {
                // Update book status
                boolean updateResult = bookDAO.updateBorrowStatus(bookIsbn, false);
                if (updateResult) {
                    database.commitTransaction();
                    LOGGER.info(String.format("Book returned successfully: User %s returned %s with fine %.2f", 
                        userId, bookIsbn, fineAmount));
                    return true;
                } else {
                    database.rollbackTransaction();
                    return false;
                }
            } else {
                database.rollbackTransaction();
                return false;
            }
            
        } catch (SQLException e) {
            try {
                database.rollbackTransaction();
            } catch (SQLException rollbackEx) {
                LOGGER.log(Level.SEVERE, "Error rolling back transaction", rollbackEx);
            }
            LOGGER.log(Level.SEVERE, "Error returning book", e);
            return false;
        }
    }
    
    /**
     * Get active borrow record for a user and book
     */
    private BorrowRecord getActiveBorrowRecord(String userId, String bookIsbn) {
        String sql = """
            SELECT * FROM borrow_records 
            WHERE user_id = ? AND book_isbn = ? AND is_returned = FALSE
            ORDER BY borrow_date DESC LIMIT 1
        """;
        
        try (ResultSet rs = database.executeQuery(sql, userId, bookIsbn)) {
            if (rs.next()) {
                return mapResultSetToBorrowRecord(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting active borrow record", e);
        }
        
        return null;
    }
    
    /**
     * Check if user can borrow more books
     */
    private boolean canUserBorrowMore(User user) {
        if (user instanceof Student) {
            return ((Student) user).canBorrowMore();
        } else if (user instanceof Staff) {
            return ((Staff) user).canBorrowMore();
        }
        return false;
    }
    
    /**
     * Get user's current borrowed books
     */
    public List<BorrowRecord> getUserCurrentBorrows(String userId) {
        String sql = """
            SELECT * FROM borrow_records 
            WHERE user_id = ? AND is_returned = FALSE
            ORDER BY borrow_date DESC
        """;
        
        List<BorrowRecord> records = new ArrayList<>();
        
        try (ResultSet rs = database.executeQuery(sql, userId)) {
            while (rs.next()) {
                records.add(mapResultSetToBorrowRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user current borrows", e);
        }
        
        return records;
    }
    
    /**
     * Get user's borrowing history
     */
    public List<BorrowRecord> getUserBorrowHistory(String userId) {
        String sql = """
            SELECT * FROM borrow_records 
            WHERE user_id = ?
            ORDER BY borrow_date DESC
        """;
        
        List<BorrowRecord> records = new ArrayList<>();
        
        try (ResultSet rs = database.executeQuery(sql, userId)) {
            while (rs.next()) {
                records.add(mapResultSetToBorrowRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting user borrow history", e);
        }
        
        return records;
    }
    
    /**
     * Get all overdue books
     */
    public List<BorrowRecord> getOverdueBooks() {
        String sql = """
            SELECT * FROM borrow_records 
            WHERE is_returned = FALSE AND due_date < ?
            ORDER BY due_date ASC
        """;
        
        List<BorrowRecord> records = new ArrayList<>();
        String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        try (ResultSet rs = database.executeQuery(sql, now)) {
            while (rs.next()) {
                records.add(mapResultSetToBorrowRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting overdue books", e);
        }
        
        return records;
    }
    
    /**
     * Get all active borrow records
     */
    public List<BorrowRecord> getAllActiveBorrows() {
        String sql = """
            SELECT * FROM borrow_records 
            WHERE is_returned = FALSE
            ORDER BY borrow_date DESC
        """;
        
        List<BorrowRecord> records = new ArrayList<>();
        
        try (ResultSet rs = database.executeQuery(sql)) {
            while (rs.next()) {
                records.add(mapResultSetToBorrowRecord(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting all active borrows", e);
        }
        
        return records;
    }
    
    /**
     * Extend due date for a borrow record
     */
    public boolean extendDueDate(int recordId, int additionalDays) {
        String sql = """
            UPDATE borrow_records 
            SET due_date = datetime(due_date, '+' || ? || ' days')
            WHERE record_id = ? AND is_returned = FALSE
        """;
        
        try {
            int result = database.executeUpdate(sql, additionalDays, recordId);
            if (result > 0) {
                LOGGER.info("Due date extended for record: " + recordId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error extending due date", e);
        }
        
        return false;
    }
    
    /**
     * Calculate total fine for a user
     */
    public double calculateUserTotalFine(String userId) {
        String sql = """
            SELECT SUM(fine_amount) FROM borrow_records 
            WHERE user_id = ? AND fine_amount > 0
        """;
        
        try (ResultSet rs = database.executeQuery(sql, userId)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating user total fine", e);
        }
        
        return 0.0;
    }
    
    /**
     * Get borrowing statistics
     */
    public BorrowStatistics getBorrowingStatistics() {
        BorrowStatistics stats = new BorrowStatistics();
        
        try {
            // Total active borrows
            String sql1 = "SELECT COUNT(*) FROM borrow_records WHERE is_returned = FALSE";
            try (ResultSet rs = database.executeQuery(sql1)) {
                if (rs.next()) {
                    stats.setActiveBorrows(rs.getInt(1));
                }
            }
            
            // Total overdue books
            String sql2 = """
                SELECT COUNT(*) FROM borrow_records 
                WHERE is_returned = FALSE AND due_date < ?
            """;
            String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            try (ResultSet rs = database.executeQuery(sql2, now)) {
                if (rs.next()) {
                    stats.setOverdueBooks(rs.getInt(1));
                }
            }
            
            // Total fines
            String sql3 = "SELECT SUM(fine_amount) FROM borrow_records WHERE fine_amount > 0";
            try (ResultSet rs = database.executeQuery(sql3)) {
                if (rs.next()) {
                    stats.setTotalFines(rs.getDouble(1));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting borrowing statistics", e);
        }
        
        return stats;
    }
    
    /**
     * Map ResultSet to BorrowRecord object
     */
    private BorrowRecord mapResultSetToBorrowRecord(ResultSet rs) throws SQLException {
        BorrowRecord record = new BorrowRecord();
        record.setRecordId(rs.getInt("record_id"));
        record.setUserId(rs.getString("user_id"));
        record.setBookIsbn(rs.getString("book_isbn"));
        
        String borrowDateStr = rs.getString("borrow_date");
        if (borrowDateStr != null) {
            record.setBorrowDate(LocalDateTime.parse(borrowDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        String dueDateStr = rs.getString("due_date");
        if (dueDateStr != null) {
            record.setDueDate(LocalDateTime.parse(dueDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        String returnDateStr = rs.getString("return_date");
        if (returnDateStr != null) {
            record.setReturnDate(LocalDateTime.parse(returnDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        
        record.setReturned(rs.getBoolean("is_returned"));
        record.setFineAmount(rs.getDouble("fine_amount"));
        record.setNotes(rs.getString("notes"));
        
        return record;
    }
    
    /**
     * Inner class for borrowing statistics
     */
    public static class BorrowStatistics {
        private int activeBorrows;
        private int overdueBooks;
        private double totalFines;
        
        // Getters and setters
        public int getActiveBorrows() { return activeBorrows; }
        public void setActiveBorrows(int activeBorrows) { this.activeBorrows = activeBorrows; }
        
        public int getOverdueBooks() { return overdueBooks; }
        public void setOverdueBooks(int overdueBooks) { this.overdueBooks = overdueBooks; }
        
        public double getTotalFines() { return totalFines; }
        public void setTotalFines(double totalFines) { this.totalFines = totalFines; }
        
        @Override
        public String toString() {
            return String.format("BorrowStatistics{activeBorrows=%d, overdueBooks=%d, totalFines=%.2f}", 
                activeBorrows, overdueBooks, totalFines);
        }
    }
}
