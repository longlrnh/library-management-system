package com.library.model;

import java.time.LocalDate;

/**
 * Staff class extending User with staff-specific properties
 */
public class Staff extends User {
    private String department;
    private LocalDate hireDate;
    private boolean isActive;
    private static final int MAX_BORROW_LIMIT = 10;
    
    public Staff() {
        super();
        this.isActive = true;
    }
    
    public Staff(String name, String id, String department) {
        super(name, id);
        this.department = department;
        this.hireDate = LocalDate.now();
        this.isActive = true;
    }
    
    public Staff(String name, String id, String department, LocalDate hireDate) {
        super(name, id);
        this.department = department;
        this.hireDate = hireDate;
        this.isActive = true;
    }
    
    // Getters
    public String getDepartment() {
        return department;
    }
    
    public LocalDate getHireDate() {
        return hireDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public int getMaxBorrowLimit() {
        return MAX_BORROW_LIMIT;
    }
    
    // Setters
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    @Override
    public String getRole() {
        return "Nhân viên";
    }
    
    /**
     * Check if staff can borrow more books
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
        return "Làm việc tại phòng ban " + (department != null ? department : "N/A");
    }
    
    @Override
    public String toString() {
        return super.toString() + " - Phòng ban: " + (department != null ? department : "N/A");
    }
}
