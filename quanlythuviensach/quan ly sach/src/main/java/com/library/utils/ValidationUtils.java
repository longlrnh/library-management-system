package com.library.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtils {
    
    // ISBN validation patterns
    private static final Pattern ISBN_10_PATTERN = Pattern.compile("^\\d{9}[\\dXx]$");
    private static final Pattern ISBN_13_PATTERN = Pattern.compile("^\\d{13}$");
    private static final Pattern ISBN_FORMATTED_PATTERN = Pattern.compile("^\\d{3}-\\d{1,5}-\\d{1,7}-\\d{1,7}-[\\dXx]$");
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Phone number pattern (Vietnamese format)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+84|0)[0-9]{9,10}$");
    
    // Student ID pattern
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,15}$");
    
    // Staff ID pattern
    private static final Pattern STAFF_ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{5,12}$");
    
    /**
     * Validate ISBN format
     */
    public static boolean isValidISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        
        // Remove spaces and hyphens
        String cleanIsbn = isbn.replaceAll("[\\s-]", "");
        
        return ISBN_10_PATTERN.matcher(cleanIsbn).matches() || 
               ISBN_13_PATTERN.matcher(cleanIsbn).matches();
    }
    
    /**
     * Validate ISBN-13 with checksum
     */
    public static boolean isValidISBN13(String isbn) {
        if (isbn == null) return false;
        
        String cleanIsbn = isbn.replaceAll("[\\s-]", "");
        
        if (!ISBN_13_PATTERN.matcher(cleanIsbn).matches()) {
            return false;
        }
        
        // Calculate checksum
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(cleanIsbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == Character.getNumericValue(cleanIsbn.charAt(12));
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Validate student ID format
     */
    public static boolean isValidStudentId(String studentId) {
        return studentId != null && STUDENT_ID_PATTERN.matcher(studentId.trim()).matches();
    }
    
    /**
     * Validate staff ID format
     */
    public static boolean isValidStaffId(String staffId) {
        return staffId != null && STAFF_ID_PATTERN.matcher(staffId.trim()).matches();
    }
    
    /**
     * Validate name (not empty, reasonable length)
     */
    public static boolean isValidName(String name) {
        return name != null && 
               !name.trim().isEmpty() && 
               name.trim().length() >= 2 && 
               name.trim().length() <= 100;
    }
    
    /**
     * Validate book title
     */
    public static boolean isValidBookTitle(String title) {
        return title != null && 
               !title.trim().isEmpty() && 
               title.trim().length() >= 1 && 
               title.trim().length() <= 500;
    }
    
    /**
     * Validate author name
     */
    public static boolean isValidAuthor(String author) {
        return author != null && 
               !author.trim().isEmpty() && 
               author.trim().length() >= 2 && 
               author.trim().length() <= 200;
    }
    
    /**
     * Validate rating (1.0 to 5.0)
     */
    public static boolean isValidRating(double rating) {
        return rating >= 1.0 && rating <= 5.0;
    }
    
    /**
     * Validate page count
     */
    public static boolean isValidPageCount(int pageCount) {
        return pageCount > 0 && pageCount <= 10000;
    }
    
    /**
     * Validate date string
     */
    public static boolean isValidDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return false;
        }
        
        try {
            LocalDate.parse(dateString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Validate date time string
     */
    public static boolean isValidDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return false;
        }
        
        try {
            LocalDateTime.parse(dateTimeString);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    
    /**
     * Validate date is not in the future
     */
    public static boolean isNotFutureDate(LocalDate date) {
        return date != null && !date.isAfter(LocalDate.now());
    }
    
    /**
     * Validate date is within reasonable range for books
     */
    public static boolean isReasonablePublishDate(LocalDate date) {
        if (date == null) return true; // null is acceptable
        
        LocalDate earliestDate = LocalDate.of(1000, 1, 1);
        LocalDate latestDate = LocalDate.now().plusYears(2);
        
        return !date.isBefore(earliestDate) && !date.isAfter(latestDate);
    }
    
    /**
     * Validate comment length
     */
    public static boolean isValidComment(String comment) {
        return comment != null && 
               comment.trim().length() >= 1 && 
               comment.trim().length() <= 1000;
    }
    
    /**
     * Validate genre
     */
    public static boolean isValidGenre(String genre) {
        return genre != null && 
               !genre.trim().isEmpty() && 
               genre.trim().length() <= 100;
    }
    
    /**
     * Validate language code
     */
    public static boolean isValidLanguage(String language) {
        return language != null && 
               !language.trim().isEmpty() && 
               language.trim().length() >= 2 && 
               language.trim().length() <= 10;
    }
    
    /**
     * Validate publisher name
     */
    public static boolean isValidPublisher(String publisher) {
        return publisher != null && 
               !publisher.trim().isEmpty() && 
               publisher.trim().length() >= 2 && 
               publisher.trim().length() <= 200;
    }
    
    /**
     * Validate description length
     */
    public static boolean isValidDescription(String description) {
        return description == null || description.trim().length() <= 2000;
    }
    
    /**
     * Validate major/department name
     */
    public static boolean isValidMajorDepartment(String majorDepartment) {
        return majorDepartment != null && 
               !majorDepartment.trim().isEmpty() && 
               majorDepartment.trim().length() >= 2 && 
               majorDepartment.trim().length() <= 100;
    }
    
    /**
     * Clean and format ISBN
     */
    public static String cleanISBN(String isbn) {
        if (isbn == null) return null;
        return isbn.replaceAll("[\\s-]", "");
    }
    
    /**
     * Format ISBN for display
     */
    public static String formatISBN(String isbn) {
        if (isbn == null) return null;
        
        String clean = cleanISBN(isbn);
        if (clean.length() == 13) {
            return String.format("%s-%s-%s-%s-%s", 
                clean.substring(0, 3),
                clean.substring(3, 4),
                clean.substring(4, 8),
                clean.substring(8, 12),
                clean.substring(12, 13));
        } else if (clean.length() == 10) {
            return String.format("%s-%s-%s-%s", 
                clean.substring(0, 1),
                clean.substring(1, 4),
                clean.substring(4, 9),
                clean.substring(9, 10));
        }
        
        return clean;
    }
    
    /**
     * Sanitize input string (remove dangerous characters)
     */
    public static String sanitizeInput(String input) {
        if (input == null) return null;
        
        return input.trim()
                   .replaceAll("[<>\"'&]", "") // Remove potential XSS characters
                   .replaceAll("\\s+", " "); // Normalize whitespace
    }
    
    /**
     * Check if string contains only alphanumeric characters and common punctuation
     */
    public static boolean isSafeString(String input) {
        if (input == null) return false;
        
        Pattern safePattern = Pattern.compile("^[a-zA-Z0-9\\s.,;:!?()\\-_'\"]+$");
        return safePattern.matcher(input).matches();
    }
    
    /**
     * Validate password strength (if implementing user authentication)
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
