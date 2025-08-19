package com.library.model;

/**
 * Abstract base class for all documents in the library system
 */
public abstract class Document {
    protected String title;
    protected String author;
    protected String isbn;
    protected boolean isBorrowed;
    
    public Document() {}
    
    public Document(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.isBorrowed = false;
    }
    
    // Getters
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public boolean isBorrowed() {
        return isBorrowed;
    }
    
    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public void setBorrowed(boolean borrowed) {
        this.isBorrowed = borrowed;
    }
    
    @Override
    public String toString() {
        return title + " by " + author + " (ISBN: " + isbn + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Document document = (Document) obj;
        return isbn != null ? isbn.equals(document.isbn) : document.isbn == null;
    }
    
    @Override
    public int hashCode() {
        return isbn != null ? isbn.hashCode() : 0;
    }
}
