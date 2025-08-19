package com.library.database;

import com.library.model.Book;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Book operations
 */
public class BookDAO {
    private static final Logger LOGGER = Logger.getLogger(BookDAO.class.getName());
    private final Database database;
    
    public BookDAO() {
        this.database = Database.getInstance();
    }
    
    /**
     * Save a new book to the database
     */
    public boolean save(Book book) {
        String sql = """
            INSERT INTO books (isbn, title, author, publisher, publish_date, page_count, quantity, 
                              genre, language, rating, rating_count, description, is_borrowed)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try {
            int result = database.executeUpdate(sql,
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishDate() != null ? book.getPublishDate().toString() : null,
                book.getPageCount(),
                book.getSoLuong(),
                book.getGenre(),
                book.getLanguage(),
                book.getRating(),
                book.getRatingCount(),
                book.getDescription(),
                book.isBorrowed()
            );
            
            LOGGER.info("Book saved successfully: " + book.getTitle());
            return result > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving book: " + book.getTitle(), e);
            return false;
        }
    }
    
    /**
     * Update an existing book
     */
    public boolean update(Book book) {
        String sql = """
            UPDATE books SET title=?, author=?, publisher=?, publish_date=?, page_count=?, quantity=?,
                           genre=?, language=?, rating=?, rating_count=?, description=?, is_borrowed=?
            WHERE isbn=?
        """;
        
        try {
            int result = database.executeUpdate(sql,
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishDate() != null ? book.getPublishDate().toString() : null,
                book.getPageCount(),
                book.getSoLuong(),
                book.getGenre(),
                book.getLanguage(),
                book.getRating(),
                book.getRatingCount(),
                book.getDescription(),
                book.isBorrowed(),
                book.getIsbn()
            );
            
            LOGGER.info("Book updated successfully: " + book.getTitle());
            return result > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating book: " + book.getTitle(), e);
            return false;
        }
    }
    
    /**
     * Delete a book by ISBN
     */
    public boolean delete(String isbn) {
        String sql = "DELETE FROM books WHERE isbn = ?";
        
        try {
            int result = database.executeUpdate(sql, isbn);
            LOGGER.info("Book deleted successfully: " + isbn);
            return result > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting book: " + isbn, e);
            return false;
        }
    }
    
    /**
     * Find a book by ISBN
     */
    public Book findByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        
        try (ResultSet rs = database.executeQuery(sql, isbn)) {
            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding book by ISBN: " + isbn, e);
        }
        
        return null;
    }
    
    /**
     * Find all books
     */
    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (ResultSet rs = database.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all books", e);
        }
        
        return books;
    }
    
    /**
     * Search books by title or author
     */
    public List<Book> search(String searchTerm) {
        String sql = """
            SELECT * FROM books 
            WHERE LOWER(title) LIKE LOWER(?) OR LOWER(author) LIKE LOWER(?)
            ORDER BY title
        """;
        
        List<Book> books = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (ResultSet rs = database.executeQuery(sql, searchPattern, searchPattern)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching books", e);
        }
        
        return books;
    }
    
    /**
     * Find books by genre
     */
    public List<Book> findByGenre(String genre) {
        String sql = "SELECT * FROM books WHERE LOWER(genre) = LOWER(?) ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (ResultSet rs = database.executeQuery(sql, genre)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding books by genre: " + genre, e);
        }
        
        return books;
    }
    
    /**
     * Find available books (not borrowed)
     */
    public List<Book> findAvailableBooks() {
        String sql = "SELECT * FROM books WHERE is_borrowed = FALSE ORDER BY title";
        List<Book> books = new ArrayList<>();
        
        try (ResultSet rs = database.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding available books", e);
        }
        
        return books;
    }
    
    /**
     * Update book borrow status
     */
    public boolean updateBorrowStatus(String isbn, boolean isBorrowed) {
        String sql = "UPDATE books SET is_borrowed = ? WHERE isbn = ?";
        
        try {
            int result = database.executeUpdate(sql, isBorrowed, isbn);
            return result > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating borrow status for book: " + isbn, e);
            return false;
        }
    }
    
    /**
     * Get book count by status
     */
    public int getBookCount(boolean isBorrowed) {
        String sql = "SELECT COUNT(*) FROM books WHERE is_borrowed = ?";
        
        try (ResultSet rs = database.executeQuery(sql, isBorrowed)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting book count", e);
        }
        
        return 0;
    }
    
    /**
     * Get all unique genres
     */
    public List<String> getAllGenres() {
        String sql = "SELECT DISTINCT genre FROM books WHERE genre IS NOT NULL ORDER BY genre";
        List<String> genres = new ArrayList<>();
        
        try (ResultSet rs = database.executeQuery(sql)) {
            while (rs.next()) {
                String genre = rs.getString("genre");
                if (genre != null && !genre.trim().isEmpty()) {
                    genres.add(genre);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting genres", e);
        }
        
        return genres;
    }
    
    /**
     * Map ResultSet to Book object
     */
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setIsbn(rs.getString("isbn"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setPublisher(rs.getString("publisher"));
        
        String publishDateStr = rs.getString("publish_date");
        if (publishDateStr != null) {
            try {
                book.setPublishDate(LocalDate.parse(publishDateStr));
            } catch (Exception e) {
                // Handle invalid date format
                LOGGER.warning("Invalid date format for book: " + book.getIsbn());
            }
        }
        
        book.setPageCount(rs.getInt("page_count"));
        book.setSoLuong(rs.getInt("quantity"));
        book.setGenre(rs.getString("genre"));
        book.setLanguage(rs.getString("language"));
        book.setRating(rs.getDouble("rating"));
        book.setRatingCount(rs.getInt("rating_count"));
        book.setDescription(rs.getString("description"));
        book.setBorrowed(rs.getBoolean("is_borrowed"));
        
        return book;
    }
}
