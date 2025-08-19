package com.library.model;

import java.time.LocalDate;

/**
 * Book class extending Document with additional book-specific properties
 */
public class Book extends Document {
    private String publisher;
    private LocalDate publishDate;
    private int pageCount;
    private String genre;
    private String language;
    private double rating;
    private int ratingCount;
    private String description;
    private int soLuong;
    
    public Book() {
        super();
        this.rating = 0.0;
        this.ratingCount = 0;
    }
    
    public Book(String title, String author, String isbn, String publisher, 
                LocalDate publishDate, int pageCount, String genre, String language) {
        super(title, author, isbn);
        this.publisher = publisher;
        this.publishDate = publishDate;
        this.pageCount = pageCount;
        this.genre = genre;
        this.language = language;
        this.rating = 0.0;
        this.ratingCount = 0;
    }

    public Book(String isbn, String title, String author, String publisher, int publishYear, String genre, int soLuong) {
        super(title, author, isbn);
        this.publisher = publisher;
        this.publishDate = LocalDate.of(publishYear, 1, 1);
        this.genre = genre;
        this.soLuong = soLuong;
    }

    // Getters
    public String getPublisher() {
        return publisher;
    }
    
    public LocalDate getPublishDate() {
        return publishDate;
    }

    public int getPublishYear() {
        return publishDate != null ? publishDate.getYear() : 0;
    }

    public int getPageCount() {
        return pageCount;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public double getRating() {
        return rating;
    }
    
    public int getRatingCount() {
        return ratingCount;
    }
    
    public String getDescription() {
        return description;
    }

    public int getSoLuong() { return soLuong; }
    
    // Setters
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }
    
    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public void setRating(double rating) {
        this.rating = rating;
    }
    
    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    
    /**
     * Add a new rating to the book
     */
    public void addRating(double newRating) {
        if (newRating >= 1.0 && newRating <= 5.0) {
            double totalRating = this.rating * this.ratingCount;
            this.ratingCount++;
            this.rating = (totalRating + newRating) / this.ratingCount;
        }
    }
    
    /**
     * Get formatted rating string
     */
    public String getFormattedRating() {
        if (ratingCount == 0) {
            return "Chưa có đánh giá";
        }
        return String.format("%.1f★ (%d đánh giá)", rating, ratingCount);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", title, author, genre != null ? genre : "N/A");
    }
}
