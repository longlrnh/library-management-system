package com.library.model;

import java.time.LocalDateTime;

/**
 * BookReview class for user reviews and ratings
 */
public class BookReview {
    private int reviewId;
    private String bookIsbn;
    private String userId;
    private double rating;
    private String comment;
    private LocalDateTime reviewDate;
    private boolean isApproved;
    
    public BookReview() {
        this.reviewDate = LocalDateTime.now();
        this.isApproved = true; // Auto-approve by default
    }
    
    public BookReview(String bookIsbn, String userId, double rating, String comment) {
        this.bookIsbn = bookIsbn;
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
        this.reviewDate = LocalDateTime.now();
        this.isApproved = true;
    }
    
    // Getters
    public int getReviewId() {
        return reviewId;
    }
    
    public String getBookIsbn() {
        return bookIsbn;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public double getRating() {
        return rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public LocalDateTime getReviewDate() {
        return reviewDate;
    }
    
    public boolean isApproved() {
        return isApproved;
    }
    
    // Setters
    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }
    
    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setRating(double rating) {
        if (rating >= 1.0 && rating <= 5.0) {
            this.rating = rating;
        }
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }
    
    public void setApproved(boolean approved) {
        this.isApproved = approved;
    }
    
    /**
     * Get formatted rating as stars
     */
    public String getFormattedRating() {
        return String.format("%.1f★", rating);
    }
    
    /**
     * Check if review is valid
     */
    public boolean isValid() {
        return rating >= 1.0 && rating <= 5.0 && 
               comment != null && !comment.trim().isEmpty() &&
               bookIsbn != null && !bookIsbn.trim().isEmpty() &&
               userId != null && !userId.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("Review by %s: %.1f★ - %s", userId, rating, 
                comment != null && comment.length() > 50 ? 
                comment.substring(0, 50) + "..." : comment);
    }
}
