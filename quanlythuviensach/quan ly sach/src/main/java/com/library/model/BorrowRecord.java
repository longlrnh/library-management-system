package com.library.model;

import java.time.LocalDateTime;

/**
 * BorrowRecord class to track book borrowing transactions
 */
public class BorrowRecord {
    private int recordId;
    private String userId;
    private String bookIsbn;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private boolean isReturned;
    private double fineAmount;
    private String notes;
    
    public BorrowRecord() {
        this.isReturned = false;
        this.fineAmount = 0.0;
    }
    
    public BorrowRecord(String userId, String bookIsbn, LocalDateTime borrowDate, LocalDateTime dueDate) {
        this.userId = userId;
        this.bookIsbn = bookIsbn;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.isReturned = false;
        this.fineAmount = 0.0;
    }
    
    // Getters
    public int getRecordId() {
        return recordId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getBookIsbn() {
        return bookIsbn;
    }
    
    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }
    
    public LocalDateTime getDueDate() {
        return dueDate;
    }
    
    public LocalDateTime getReturnDate() {
        return returnDate;
    }
    
    public boolean isReturned() {
        return isReturned;
    }
    
    public double getFineAmount() {
        return fineAmount;
    }
    
    public String getNotes() {
        return notes;
    }
    
    // Setters
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }
    
    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }
    
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
    
    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }
    
    public void setReturned(boolean returned) {
        this.isReturned = returned;
    }
    
    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    /**
     * Check if the book is overdue
     */
    public boolean isOverdue() {
        if (isReturned) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDateTime.now().isAfter(dueDate);
    }
    
    /**
     * Calculate fine amount for overdue books
     */
    public double calculateFine(double finePerDay) {
        if (!isOverdue()) {
            return 0.0;
        }
        
        LocalDateTime compareDate = isReturned ? returnDate : LocalDateTime.now();
        long overdueDays = java.time.Duration.between(dueDate, compareDate).toDays();
        
        return Math.max(0, overdueDays * finePerDay);
    }
    
    /**
     * Get days until due (negative if overdue)
     */
    public long getDaysUntilDue() {
        return java.time.Duration.between(LocalDateTime.now(), dueDate).toDays();
    }
    
    @Override
    public String toString() {
        return String.format("Record #%d: User %s borrowed %s on %s", 
                recordId, userId, bookIsbn, borrowDate.toLocalDate());
    }
}
