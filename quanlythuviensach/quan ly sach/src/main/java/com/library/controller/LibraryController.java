package com.library.controller;

import com.library.database.BookDAO;
import com.library.database.UserDAO;
import com.library.model.*;
import com.library.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Main controller for the Library Management System
 */
public class LibraryController {
    private static final Logger LOGGER = Logger.getLogger(LibraryController.class.getName());
    
    private final BookDAO bookDAO;
    private final UserDAO userDAO;
    private final BorrowService borrowService;
    private final GoogleBooksService googleBooksService;
    
    public LibraryController() {
        this.bookDAO = new BookDAO();
        this.userDAO = new UserDAO();
        this.borrowService = new BorrowService();
        this.googleBooksService = new GoogleBooksService();
    }
    
    // Book Management Methods
    
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }
    
    public List<Book> getAvailableBooks() {
        return bookDAO.findAvailableBooks();
    }
    
    public List<Book> searchBooks(String searchTerm) {
        return bookDAO.search(searchTerm);
    }
    
    public List<Book> getBooksByGenre(String genre) {
        return bookDAO.findByGenre(genre);
    }
    
    public List<String> getAllGenres() {
        return bookDAO.getAllGenres();
    }
    
    public Book getBookByIsbn(String isbn) {
        return bookDAO.findByIsbn(isbn);
    }
    
    public boolean addBook(Book book) {
        return bookDAO.save(book);
    }
    
    public boolean updateBook(Book book) {
        return bookDAO.update(book);
    }
    
    public boolean deleteBook(String isbn) {
        return bookDAO.delete(isbn);
    }
     
    // User Management Methods
    
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    public List<User> searchUsers(String searchTerm) {
        return userDAO.search(searchTerm);
    }
    
    public List<User> getUsersByType(String userType) {
        return userDAO.findByType(userType);
    }
    
    public List<User> getActiveUsers() {
        return userDAO.findActiveUsers();
    }
    
    public User getUserById(String userId) {
        return userDAO.findById(userId);
    }
    
    public boolean addUser(User user) {
        return userDAO.save(user);
    }
    
    public boolean updateUser(User user) {
        return userDAO.update(user);
    }
    
    public boolean deleteUser(String userId) {
        return userDAO.delete(userId);
    }
    
    public boolean updateUserActiveStatus(String userId, boolean isActive) {
        return userDAO.updateActiveStatus(userId, isActive);
    }
    
    public int getUserCount(String userType) {
        return userDAO.getUserCount(userType);
    }
    
    public boolean userExists(String userId) {
        return userDAO.existsById(userId);
    }
    
    // Borrow/Return Methods
    
    public boolean borrowBook(String userId, String bookIsbn) {
        return borrowService.borrowBook(userId, bookIsbn);
    }
    
    public boolean returnBook(String userId, String bookIsbn) {
        return borrowService.returnBook(userId, bookIsbn);
    }
    
    public List<BorrowRecord> getUserCurrentBorrows(String userId) {
        return borrowService.getUserCurrentBorrows(userId);
    }
    
    public List<BorrowRecord> getUserBorrowHistory(String userId) {
        return borrowService.getUserBorrowHistory(userId);
    }
    
    public List<BorrowRecord> getAllActiveBorrows() {
        return borrowService.getAllActiveBorrows();
    }
    
    public List<BorrowRecord> getOverdueBooks() {
        return borrowService.getOverdueBooks();
    }
    
    public boolean extendDueDate(int recordId, int additionalDays) {
        return borrowService.extendDueDate(recordId, additionalDays);
    }
    
    public double calculateUserTotalFine(String userId) {
        return borrowService.calculateUserTotalFine(userId);
    }
    
    public BorrowService.BorrowStatistics getBorrowingStatistics() {
        return borrowService.getBorrowingStatistics();
    }
    
    // Google Books API Methods
    
    public CompletableFuture<Book> searchBookByIsbn(String isbn) {
        return googleBooksService.searchByIsbn(isbn);
    }
    
    public CompletableFuture<Book[]> searchBooksByTitle(String title) {
        return googleBooksService.searchByTitle(title);
    }
    
    public CompletableFuture<Book[]> searchBooksByAuthor(String author) {
        return googleBooksService.searchByAuthor(author);
    }
    
    // Statistics Methods
    
    public LibraryStatistics getLibraryStatistics() {
        LibraryStatistics stats = new LibraryStatistics();
        
        // Book statistics
        stats.setTotalBooks(bookDAO.getBookCount(false) + bookDAO.getBookCount(true));
        stats.setAvailableBooks(bookDAO.getBookCount(false));
        stats.setBorrowedBooks(bookDAO.getBookCount(true));
        
        // User statistics
        stats.setTotalStudents(userDAO.getUserCount("student"));
        stats.setTotalStaff(userDAO.getUserCount("staff"));
        
        // Borrow statistics
        BorrowService.BorrowStatistics borrowStats = borrowService.getBorrowingStatistics();
        stats.setActiveBorrows(borrowStats.getActiveBorrows());
        stats.setOverdueBooks(borrowStats.getOverdueBooks());
        stats.setTotalFines(borrowStats.getTotalFines());
        
        return stats;
    }
    
    // Data Initialization
    
    public void initializeSampleData() {
        try {
            // Check if data already exists
            if (!getAllBooks().isEmpty()) {
                return; // Data already exists
            }
            
            LOGGER.info("Initializing sample data...");
            
            // Add sample books
            addSampleBooks();
            
            // Add sample users
            addSampleUsers();
            
            LOGGER.info("Sample data initialized successfully");
            
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize sample data: " + e.getMessage());
        }
    }
    
    private void addSampleBooks() {
        List<Book> sampleBooks = createSampleBooks();
        for (Book book : sampleBooks) {
            addBook(book);
        }
    }
    
    private void addSampleUsers() {
        List<User> sampleUsers = createSampleUsers();
        for (User user : sampleUsers) {
            addUser(user);
        }
    }
    
    private List<Book> createSampleBooks() {
        List<Book> books = new ArrayList<>();
        
        // Sample Vietnamese literature books
        Book book1 = new Book("Số đỏ", "Vũ Trọng Phụng", "9786041012345", 
            "NXB Văn học", LocalDate.of(2020, 1, 15), 280, "Văn học", "Tiếng Việt");
        book1.setDescription("Tiểu thuyết nổi tiếng của Vũ Trọng Phụng về xã hội Việt Nam thời thuộc địa.");
        
        Book book2 = new Book("Tắt đèn", "Ngô Tất Tố", "9786041012346",
            "NXB Văn học", LocalDate.of(2019, 5, 20), 320, "Văn học", "Tiếng Việt");
        book2.setDescription("Tác phẩm kinh điển của văn học Việt Nam hiện đại.");
        
        // Sample programming books
        Book book3 = new Book("Java: The Complete Reference", "Herbert Schildt", "9781260440232",
            "McGraw-Hill Education", LocalDate.of(2021, 3, 10), 1248, "Công nghệ", "English");
        book3.setDescription("Comprehensive guide to Java programming language.");
        
        Book book4 = new Book("Clean Code", "Robert C. Martin", "9780132350884",
            "Prentice Hall", LocalDate.of(2008, 8, 1), 464, "Công nghệ", "English");
        book4.setDescription("A handbook of agile software craftsmanship.");
        
        // Sample science books
        Book book5 = new Book("Vật lý đại cương", "Nguyễn Văn A", "9786041012347",
            "NXB Giáo dục", LocalDate.of(2020, 9, 15), 450, "Khoa học", "Tiếng Việt");
        book5.setDescription("Giáo trình vật lý đại cương cho sinh viên.");
        
        books.add(book1);
        books.add(book2);
        books.add(book3);
        books.add(book4);
        books.add(book5);
        
        return books;
    }
    
    private List<User> createSampleUsers() {
        List<User> users = new ArrayList<>();
        
        // Sample students
        Student student1 = new Student("Nguyễn Văn Nam", "SV001", "Khoa học máy tính");
        Student student2 = new Student("Trần Thị Hoa", "SV002", "Văn học");
        Student student3 = new Student("Lê Minh Đức", "SV003", "Vật lý");
        
        // Sample staff
        Staff staff1 = new Staff("Dr. Phạm Thu Lan", "GV001", "Khoa Công nghệ thông tin");
        Staff staff2 = new Staff("ThS. Hoàng Minh Tuấn", "GV002", "Khoa Văn học");
        
        users.add(student1);
        users.add(student2);
        users.add(student3);
        users.add(staff1);
        users.add(staff2);
        
        return users;
    }
    
    // Shutdown method
    
    public void shutdown() {
        if (googleBooksService != null) {
            googleBooksService.shutdown();
        }
    }
    
    // Inner class for library statistics
    
    public static class LibraryStatistics {
        private int totalBooks;
        private int availableBooks;
        private int borrowedBooks;
        private int totalStudents;
        private int totalStaff;
        private int activeBorrows;
        private int overdueBooks;
        private double totalFines;
        
        // Getters and setters
        public int getTotalBooks() { return totalBooks; }
        public void setTotalBooks(int totalBooks) { this.totalBooks = totalBooks; }
        
        public int getAvailableBooks() { return availableBooks; }
        public void setAvailableBooks(int availableBooks) { this.availableBooks = availableBooks; }
        
        public int getBorrowedBooks() { return borrowedBooks; }
        public void setBorrowedBooks(int borrowedBooks) { this.borrowedBooks = borrowedBooks; }
        
        public int getTotalStudents() { return totalStudents; }
        public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
        
        public int getTotalStaff() { return totalStaff; }
        public void setTotalStaff(int totalStaff) { this.totalStaff = totalStaff; }
        
        public int getActiveBorrows() { return activeBorrows; }
        public void setActiveBorrows(int activeBorrows) { this.activeBorrows = activeBorrows; }
        
        public int getOverdueBooks() { return overdueBooks; }
        public void setOverdueBooks(int overdueBooks) { this.overdueBooks = overdueBooks; }
        
        public double getTotalFines() { return totalFines; }
        public void setTotalFines(double totalFines) { this.totalFines = totalFines; }
        
        @Override
        public String toString() {
            return String.format("LibraryStatistics{totalBooks=%d, availableBooks=%d, borrowedBooks=%d, " +
                "totalStudents=%d, totalStaff=%d, activeBorrows=%d, overdueBooks=%d, totalFines=%.2f}",
                totalBooks, availableBooks, borrowedBooks, totalStudents, totalStaff, 
                activeBorrows, overdueBooks, totalFines);
        }
    }
}
