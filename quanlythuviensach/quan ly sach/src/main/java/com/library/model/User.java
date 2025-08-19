package com.library.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract User class representing library users
 */
public abstract class User {
    protected String name;
    protected String id;
    protected List<Book> borrowedBooks;
    
    public User() {
        this.borrowedBooks = new ArrayList<>();
    }
    
    public User(String name, String id) {
        this.name = name;
        this.id = id;
        this.borrowedBooks = new ArrayList<>();
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }
    
    public List<Book> getBorrowedBooks() {
        return new ArrayList<>(borrowedBooks);
    }
    
    // Setters
    public void setName(String name) {
        this.name = name;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    // Abstract methods
    public abstract String getRole();
    
    // Book management methods
    public void addBookToList(Book book) {
        if (!borrowedBooks.contains(book)) {
            borrowedBooks.add(book);
        }
    }
    
    public void removeBookFromList(Book book) {
        borrowedBooks.remove(book);
    }
    
    public int getBorrowedBookCount() {
        return borrowedBooks.size();
    }
    
    @Override
    public String toString() {
        return name + " (" + id + ") - " + getRole();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return id != null ? id.equals(user.id) : user.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
