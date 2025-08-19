package com.library.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Student class extending User with student-specific properties
 */
public class Student extends User {
    private String major;
    private LocalDate enrollmentDate;
    private boolean isActive;
    private static final int MAX_BORROW_LIMIT = 5;
    
    public Student() {
        super();
        this.isActive = true;
    }
    
    public Student(String name, String id, String major) {
        super(name, id);
        this.major = major;
        this.enrollmentDate = LocalDate.now();
        this.isActive = true;
    }
    
    public Student(String name, String id, String major, LocalDate enrollmentDate) {
        super(name, id);
        this.major = major;
        this.enrollmentDate = enrollmentDate;
        this.isActive = true;
    }
    
    // Getters
    public String getMajor() {
        return major;
    }
    
    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public int getMaxBorrowLimit() {
        return MAX_BORROW_LIMIT;
    }
    
    // Setters
    public void setMajor(String major) {
        this.major = major;
    }
    
    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    @Override
    public String getRole() {
        return "Sinh viên";
    }
    
    /**
     * Check if student can borrow more books
     */
    public boolean canBorrowMore() {
        return isActive && getBorrowedBookCount() < MAX_BORROW_LIMIT;
    }
    
    /**
     * Get remaining borrow capacity
     */
    public int getRemainingBorrowCapacity() {
        return MAX_BORROW_LIMIT - getBorrowedBookCount();
    }
    
    /**
     * Performance role as string for display
     */
    public String performRole() {
        return "Đang học tại khoa " + (major != null ? major : "N/A");
    }
    
    @Override
    public String toString() {
        return super.toString() + " - Khoa: " + (major != null ? major : "N/A");
    }
}
