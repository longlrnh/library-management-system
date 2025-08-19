package com.library.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.library.model.Book;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service for integrating with Google Books API
 */
public class GoogleBooksService {
    private static final Logger LOGGER = Logger.getLogger(GoogleBooksService.class.getName());
    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final String API_KEY = ""; // Add your API key here if needed
    
    private final OkHttpClient httpClient;
    
    public GoogleBooksService() {
        this.httpClient = new OkHttpClient();
    }
    
    /**
     * Search books by ISBN asynchronously
     */
    public CompletableFuture<Book> searchByIsbn(String isbn) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = GOOGLE_BOOKS_API_URL + "?q=isbn:" + isbn;
                if (!API_KEY.isEmpty()) {
                    url += "&key=" + API_KEY;
                }
                
                Request request = new Request.Builder()
                    .url(url)
                    .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        return parseBookFromJson(jsonResponse, isbn);
                    } else {
                        LOGGER.warning("Google Books API request failed: " + response.code());
                        return null;
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error searching book by ISBN: " + isbn, e);
                return null;
            }
        });
    }
    
    /**
     * Search books by title asynchronously
     */
    public CompletableFuture<Book[]> searchByTitle(String title) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = GOOGLE_BOOKS_API_URL + "?q=intitle:" + title.replace(" ", "+");
                if (!API_KEY.isEmpty()) {
                    url += "&key=" + API_KEY;
                }
                
                Request request = new Request.Builder()
                    .url(url)
                    .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        return parseBooksFromJson(jsonResponse);
                    } else {
                        LOGGER.warning("Google Books API request failed: " + response.code());
                        return new Book[0];
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error searching books by title: " + title, e);
                return new Book[0];
            }
        });
    }
    
    /**
     * Search books by author asynchronously
     */
    public CompletableFuture<Book[]> searchByAuthor(String author) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = GOOGLE_BOOKS_API_URL + "?q=inauthor:" + author.replace(" ", "+");
                if (!API_KEY.isEmpty()) {
                    url += "&key=" + API_KEY;
                }
                
                Request request = new Request.Builder()
                    .url(url)
                    .build();
                
                try (Response response = httpClient.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().string();
                        return parseBooksFromJson(jsonResponse);
                    } else {
                        LOGGER.warning("Google Books API request failed: " + response.code());
                        return new Book[0];
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error searching books by author: " + author, e);
                return new Book[0];
            }
        });
    }
    
    /**
     * Parse single book from JSON response
     */
    private Book parseBookFromJson(String jsonResponse, String originalIsbn) {
        try {
            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = root.getAsJsonArray("items");
            
            if (items != null && items.size() > 0) {
                JsonObject item = items.get(0).getAsJsonObject();
                JsonObject volumeInfo = item.getAsJsonObject("volumeInfo");
                
                return createBookFromVolumeInfo(volumeInfo, originalIsbn);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing JSON response", e);
        }
        
        return null;
    }
    
    /**
     * Parse multiple books from JSON response
     */
    private Book[] parseBooksFromJson(String jsonResponse) {
        try {
            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            JsonArray items = root.getAsJsonArray("items");
            
            if (items != null && items.size() > 0) {
                Book[] books = new Book[Math.min(items.size(), 10)]; // Limit to 10 results
                
                for (int i = 0; i < books.length; i++) {
                    JsonObject item = items.get(i).getAsJsonObject();
                    JsonObject volumeInfo = item.getAsJsonObject("volumeInfo");
                    
                    books[i] = createBookFromVolumeInfo(volumeInfo, null);
                }
                
                return books;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing JSON response", e);
        }
        
        return new Book[0];
    }
    
    /**
     * Create Book object from Google Books volumeInfo
     */
    private Book createBookFromVolumeInfo(JsonObject volumeInfo, String fallbackIsbn) {
        Book book = new Book();
        
        // Title
        if (volumeInfo.has("title")) {
            book.setTitle(volumeInfo.get("title").getAsString());
        }
        
        // Authors
        if (volumeInfo.has("authors")) {
            JsonArray authors = volumeInfo.getAsJsonArray("authors");
            if (authors.size() > 0) {
                StringBuilder authorBuilder = new StringBuilder();
                for (int i = 0; i < authors.size(); i++) {
                    if (i > 0) authorBuilder.append(", ");
                    authorBuilder.append(authors.get(i).getAsString());
                }
                book.setAuthor(authorBuilder.toString());
            }
        }
        
        // ISBN
        String isbn = fallbackIsbn;
        if (volumeInfo.has("industryIdentifiers")) {
            JsonArray identifiers = volumeInfo.getAsJsonArray("industryIdentifiers");
            for (JsonElement identifier : identifiers) {
                JsonObject id = identifier.getAsJsonObject();
                String type = id.get("type").getAsString();
                if ("ISBN_13".equals(type) || "ISBN_10".equals(type)) {
                    isbn = id.get("identifier").getAsString();
                    break;
                }
            }
        }
        book.setIsbn(isbn);
        
        // Publisher
        if (volumeInfo.has("publisher")) {
            book.setPublisher(volumeInfo.get("publisher").getAsString());
        }
        
        // Published Date
        if (volumeInfo.has("publishedDate")) {
            String dateStr = volumeInfo.get("publishedDate").getAsString();
            try {
                // Try different date formats
                if (dateStr.length() == 4) {
                    // Year only
                    book.setPublishDate(LocalDate.of(Integer.parseInt(dateStr), 1, 1));
                } else if (dateStr.length() == 7) {
                    // Year-Month
                    String[] parts = dateStr.split("-");
                    book.setPublishDate(LocalDate.of(Integer.parseInt(parts[0]), 
                        Integer.parseInt(parts[1]), 1));
                } else {
                    // Full date
                    book.setPublishDate(LocalDate.parse(dateStr));
                }
            } catch (DateTimeParseException | NumberFormatException e) {
                LOGGER.warning("Could not parse date: " + dateStr);
            }
        }
        
        // Page Count
        if (volumeInfo.has("pageCount")) {
            book.setPageCount(volumeInfo.get("pageCount").getAsInt());
        }
        
        // Categories (Genre)
        if (volumeInfo.has("categories")) {
            JsonArray categories = volumeInfo.getAsJsonArray("categories");
            if (categories.size() > 0) {
                book.setGenre(categories.get(0).getAsString());
            }
        }
        
        // Language
        if (volumeInfo.has("language")) {
            book.setLanguage(volumeInfo.get("language").getAsString());
        }
        
        // Description
        if (volumeInfo.has("description")) {
            String description = volumeInfo.get("description").getAsString();
            // Limit description length
            if (description.length() > 1000) {
                description = description.substring(0, 1000) + "...";
            }
            book.setDescription(description);
        }
        
        return book;
    }
    
    /**
     * Close HTTP client resources
     */
    public void shutdown() {
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }
}
