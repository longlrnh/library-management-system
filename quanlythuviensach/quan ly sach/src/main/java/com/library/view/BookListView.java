package com.library.view;

import com.library.controller.LibraryController;
import com.library.model.Book;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * View for displaying and managing the list of books
 */
public class BookListView {
    private LibraryController controller;
    private TableView<Book> bookTable;
    private ObservableList<Book> bookList;
    private TextField searchField;
    private ComboBox<String> genreFilter;
    private Label totalBooksLabel;
    private Label availableBooksLabel;
    
    public BookListView(LibraryController controller) {
        this.controller = controller;
        this.bookList = FXCollections.observableArrayList();
    }
    
    public VBox createView() {
        VBox mainContainer = new VBox(10);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getStyleClass().add("book-list-container");
        
        // Title
        Label titleLabel = new Label("DANH SÁCH SÁCH");
        titleLabel.getStyleClass().add("section-title");
        
        // Search and filter section
        HBox searchSection = createSearchSection();
        
        // Statistics section
        HBox statsSection = createStatsSection();
        
        // Book table
        bookTable = createBookTable();
        VBox.setVgrow(bookTable, Priority.ALWAYS);
        
        // Action buttons
        HBox buttonSection = createButtonSection();
        
        mainContainer.getChildren().addAll(
            titleLabel,
            searchSection,
            statsSection,
            bookTable,
            buttonSection
        );
        
        // Load initial data
        loadBooks();
        
        return mainContainer;
    }
    
    private HBox createSearchSection() {
        HBox searchSection = new HBox(10);
        searchSection.setAlignment(Pos.CENTER_LEFT);
        searchSection.setPadding(new Insets(10));
        searchSection.getStyleClass().add("search-section");
        
        Label searchLabel = new Label("Tìm kiếm:");
        
        searchField = new TextField();
        searchField.setPromptText("Nhập tên sách, tác giả hoặc ISBN...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldText, newText) -> searchBooks());
        
        Label filterLabel = new Label("Thể loại:");
        
        genreFilter = new ComboBox<>();
        genreFilter.setPromptText("Tất cả");
        genreFilter.setPrefWidth(150);
        genreFilter.setOnAction(e -> filterByGenre());
        
        Button clearButton = new Button("Xóa bộ lọc");
        clearButton.setOnAction(e -> clearFilters());
        
        searchSection.getChildren().addAll(
            searchLabel, searchField,
            new Separator(),
            filterLabel, genreFilter,
            clearButton
        );
        
        return searchSection;
    }
    
    private HBox createStatsSection() {
        HBox statsSection = new HBox(20);
        statsSection.setAlignment(Pos.CENTER_LEFT);
        statsSection.setPadding(new Insets(10));
        statsSection.getStyleClass().add("stats-section");
        
        totalBooksLabel = new Label("Tổng số sách: 0");
        totalBooksLabel.getStyleClass().add("stat-label");
        
        availableBooksLabel = new Label("Sách có sẵn: 0");
        availableBooksLabel.getStyleClass().add("stat-label");
        
        statsSection.getChildren().addAll(totalBooksLabel, availableBooksLabel);
        
        return statsSection;
    }
    
    private TableView<Book> createBookTable() {
        TableView<Book> table = new TableView<>();
        table.setItems(bookList);
        table.getStyleClass().add("book-table");
        
        // ISBN column
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        isbnCol.setPrefWidth(120);
        
        // Title column
        TableColumn<Book, String> titleCol = new TableColumn<>("Tiêu đề");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(250);
        
        // Author column
        TableColumn<Book, String> authorCol = new TableColumn<>("Tác giả");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        authorCol.setPrefWidth(200);
        
        // Genre column
        TableColumn<Book, String> genreCol = new TableColumn<>("Thể loại");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        genreCol.setPrefWidth(120);
        
        // Publisher column
        TableColumn<Book, String> publisherCol = new TableColumn<>("Nhà xuất bản");
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        publisherCol.setPrefWidth(150);
        
        // Status column
        TableColumn<Book, String> statusCol = new TableColumn<>("Trạng thái");
        statusCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isBorrowed() ? "Đã mượn" : "Có sẵn"));
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(column -> new TableCell<Book, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(status);
                    if ("Đã mượn".equals(status)) {
                        getStyleClass().add("status-borrowed");
                    } else {
                        getStyleClass().add("status-available");
                    }
                }
            }
        });
        
        // Rating column
        TableColumn<Book, String> ratingCol = new TableColumn<>("Đánh giá");
        ratingCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getFormattedRating()));
        ratingCol.setPrefWidth(120);
        
        table.getColumns().addAll(isbnCol, titleCol, authorCol, genreCol, publisherCol, statusCol, ratingCol);
        
        // Double click to view details
        table.setRowFactory(tv -> {
            TableRow<Book> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    showBookDetails(row.getItem());
                }
            });
            return row;
        });
        
        return table;
    }
    
    private HBox createButtonSection() {
        HBox buttonSection = new HBox(10);
        buttonSection.setAlignment(Pos.CENTER_RIGHT);
        buttonSection.setPadding(new Insets(10));
        
        Button viewDetailsBtn = new Button("Xem chi tiết");
        viewDetailsBtn.setOnAction(e -> viewSelectedBookDetails());
        
        Button refreshBtn = new Button("Làm mới");
        refreshBtn.setOnAction(e -> refresh());
        
        buttonSection.getChildren().addAll(viewDetailsBtn, refreshBtn);
        
        return buttonSection;
    }
    
    private void loadBooks() {
        Task<List<Book>> loadTask = new Task<List<Book>>() {
            @Override
            protected List<Book> call() throws Exception {
                return controller.getAllBooks();
            }
            
            @Override
            protected void succeeded() {
                bookList.setAll(getValue());
                updateGenreFilter();
                updateStatistics();
            }
            
            @Override
            protected void failed() {
                showError("Không thể tải danh sách sách: " + getException().getMessage());
            }
        };
        
        new Thread(loadTask).start();
    }
    
    private void searchBooks() {
        String searchText = searchField.getText();
        if (searchText == null || searchText.trim().isEmpty()) {
            loadBooks();
            return;
        }
        
        Task<List<Book>> searchTask = new Task<List<Book>>() {
            @Override
            protected List<Book> call() throws Exception {
                return controller.searchBooks(searchText.trim());
            }
            
            @Override
            protected void succeeded() {
                bookList.setAll(getValue());
                updateStatistics();
            }
            
            @Override
            protected void failed() {
                showError("Lỗi tìm kiếm: " + getException().getMessage());
            }
        };
        
        new Thread(searchTask).start();
    }
    
    private void filterByGenre() {
        String selectedGenre = genreFilter.getValue();
        if (selectedGenre == null || "Tất cả".equals(selectedGenre)) {
            loadBooks();
            return;
        }
        
        Task<List<Book>> filterTask = new Task<List<Book>>() {
            @Override
            protected List<Book> call() throws Exception {
                return controller.getBooksByGenre(selectedGenre);
            }
            
            @Override
            protected void succeeded() {
                bookList.setAll(getValue());
                updateStatistics();
            }
            
            @Override
            protected void failed() {
                showError("Lỗi lọc theo thể loại: " + getException().getMessage());
            }
        };
        
        new Thread(filterTask).start();
    }
    
    private void clearFilters() {
        searchField.clear();
        genreFilter.setValue(null);
        loadBooks();
    }
    
    private void updateGenreFilter() {
        Task<List<String>> genreTask = new Task<List<String>>() {
            @Override
            protected List<String> call() throws Exception {
                return controller.getAllGenres();
            }
            
            @Override
            protected void succeeded() {
                ObservableList<String> genres = FXCollections.observableArrayList("Tất cả");
                genres.addAll(getValue());
                genreFilter.setItems(genres);
            }
        };
        
        new Thread(genreTask).start();
    }
    
    private void updateStatistics() {
        int total = bookList.size();
        long available = bookList.stream().filter(book -> !book.isBorrowed()).count();
        
        totalBooksLabel.setText("Tổng số sách: " + total);
        availableBooksLabel.setText("Sách có sẵn: " + available);
    }
    
    private void viewSelectedBookDetails() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            showBookDetails(selectedBook);
        } else {
            showWarning("Vui lòng chọn một cuốn sách để xem chi tiết.");
        }
    }
    
    private void showBookDetails(Book book) {
        Stage detailStage = new Stage();
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.setTitle("Chi tiết sách - " + book.getTitle());
        
        BookDetailView detailView = new BookDetailView(book, controller);
        Scene scene = new Scene(detailView.createView(), 600, 500);
        scene.getStylesheets().add(getClass().getResource("/css/main-style.css").toExternalForm());
        
        detailStage.setScene(scene);
        detailStage.centerOnScreen();
        detailStage.showAndWait();
        
        // Refresh list after detail view closes
        refresh();
    }
    
    public void refresh() {
        loadBooks();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText("Đã xảy ra lỗi");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cảnh báo");
        alert.setHeaderText("Thông báo");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
