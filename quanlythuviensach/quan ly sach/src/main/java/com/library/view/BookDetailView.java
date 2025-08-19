package com.library.view;

import com.library.controller.LibraryController;
import com.library.model.Book;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.format.DateTimeFormatter;

/**
 * View for displaying detailed information about a book
 */
public class BookDetailView {
    private Book book;
    private LibraryController controller;
    
    public BookDetailView(Book book, LibraryController controller) {
        this.book = book;
        this.controller = controller;
    }
    
    public ScrollPane createView() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("book-detail-scroll");
        
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.getStyleClass().add("book-detail-container");
        
        // Title section
        VBox titleSection = createTitleSection();
        
        // Basic information section
        VBox basicInfoSection = createBasicInfoSection();
        
        // Additional information section
        VBox additionalInfoSection = createAdditionalInfoSection();
        
        // Status and rating section
        VBox statusSection = createStatusSection();
        
        // Action buttons section
        HBox actionSection = createActionSection();
        
        mainContainer.getChildren().addAll(
            titleSection,
            new Separator(),
            basicInfoSection,
            new Separator(),
            additionalInfoSection,
            new Separator(),
            statusSection,
            new Separator(),
            actionSection
        );
        
        scrollPane.setContent(mainContainer);
        return scrollPane;
    }
    
    private VBox createTitleSection() {
        VBox titleSection = new VBox(10);
        titleSection.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(book.getTitle() != null ? book.getTitle() : "N/A");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.getStyleClass().add("book-title");
        titleLabel.setWrapText(true);
        
        Label authorLabel = new Label("Tác giả: " + (book.getAuthor() != null ? book.getAuthor() : "N/A"));
        authorLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 16));
        authorLabel.getStyleClass().add("book-author");
        authorLabel.setWrapText(true);
        
        titleSection.getChildren().addAll(titleLabel, authorLabel);
        return titleSection;
    }
    
    private VBox createBasicInfoSection() {
        VBox basicSection = new VBox(15);
        
        Label sectionTitle = new Label("THÔNG TIN CƠ BẢN");
        sectionTitle.getStyleClass().add("section-header");
        
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(20);
        infoGrid.setVgap(10);
        infoGrid.getStyleClass().add("info-grid");
        
        int row = 0;
        
        // ISBN
        addInfoRow(infoGrid, row++, "ISBN:", book.getIsbn());
        
        // Publisher
        addInfoRow(infoGrid, row++, "Nhà xuất bản:", book.getPublisher());
        
        // Publish Date
        String publishDate = book.getPublishDate() != null ? 
            book.getPublishDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
        addInfoRow(infoGrid, row++, "Ngày xuất bản:", publishDate);
        
        // Page Count
        String pageCount = book.getPageCount() > 0 ? String.valueOf(book.getPageCount()) : "N/A";
        addInfoRow(infoGrid, row++, "Số trang:", pageCount);
        
        // Genre
        addInfoRow(infoGrid, row++, "Thể loại:", book.getGenre());
        
        // Language
        addInfoRow(infoGrid, row++, "Ngôn ngữ:", book.getLanguage());
        
        basicSection.getChildren().addAll(sectionTitle, infoGrid);
        return basicSection;
    }
    
    private VBox createAdditionalInfoSection() {
        VBox additionalSection = new VBox(15);
        
        Label sectionTitle = new Label("MÔ TẢ");
        sectionTitle.getStyleClass().add("section-header");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setText(book.getDescription() != null ? book.getDescription() : "Chưa có mô tả");
        descriptionArea.setEditable(false);
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(5);
        descriptionArea.getStyleClass().add("description-area");
        
        additionalSection.getChildren().addAll(sectionTitle, descriptionArea);
        return additionalSection;
    }
    
    private VBox createStatusSection() {
        VBox statusSection = new VBox(15);
        
        Label sectionTitle = new Label("TRẠNG THÁI & ĐÁNH GIÁ");
        sectionTitle.getStyleClass().add("section-header");
        
        GridPane statusGrid = new GridPane();
        statusGrid.setHgap(20);
        statusGrid.setVgap(10);
        statusGrid.getStyleClass().add("info-grid");
        
        int row = 0;
        
        // Availability status
        String status = book.isBorrowed() ? "Đã được mượn" : "Có sẵn";
        Label statusLabel = new Label(status);
        if (book.isBorrowed()) {
            statusLabel.getStyleClass().add("status-borrowed");
        } else {
            statusLabel.getStyleClass().add("status-available");
        }
        addInfoRow(statusGrid, row++, "Trạng thái:", statusLabel);
        
        // Rating
        String ratingText = book.getFormattedRating();
        addInfoRow(statusGrid, row++, "Đánh giá:", ratingText);
        
        // Rating count
        String ratingCount = book.getRatingCount() > 0 ? 
            String.valueOf(book.getRatingCount()) + " lượt đánh giá" : "Chưa có đánh giá";
        addInfoRow(statusGrid, row++, "Số lượt đánh giá:", ratingCount);
        
        statusSection.getChildren().addAll(sectionTitle, statusGrid);
        return statusSection;
    }
    
    private HBox createActionSection() {
        HBox actionSection = new HBox(15);
        actionSection.setAlignment(Pos.CENTER);
        
        Button borrowButton = new Button("Mượn sách");
        borrowButton.getStyleClass().add("action-button");
        borrowButton.setDisable(book.isBorrowed());
        borrowButton.setOnAction(e -> borrowBook());
        
        Button returnButton = new Button("Trả sách");
        returnButton.getStyleClass().add("action-button");
        returnButton.setDisable(!book.isBorrowed());
        returnButton.setOnAction(e -> returnBook());
        
        Button editButton = new Button("Chỉnh sửa");
        editButton.getStyleClass().add("action-button");
        editButton.setOnAction(e -> editBook());
               
        Button reviewButton = new Button("Đánh giá & Nhận xét");
        reviewButton.getStyleClass().add("action-button");
        reviewButton.setOnAction(e -> openReviewDialog());
        
        actionSection.getChildren().addAll(borrowButton, returnButton, editButton, reviewButton);
        return actionSection;
    }
    
    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelControl = new Label(label);
        labelControl.getStyleClass().add("info-label");
        
        Label valueControl = new Label(value != null ? value : "N/A");
        valueControl.getStyleClass().add("info-value");
        valueControl.setWrapText(true);
        
        grid.add(labelControl, 0, row);
        grid.add(valueControl, 1, row);
    }
    
    private void addInfoRow(GridPane grid, int row, String label, Label valueControl) {
        Label labelControl = new Label(label);
        labelControl.getStyleClass().add("info-label");
        
        grid.add(labelControl, 0, row);
        grid.add(valueControl, 1, row);
    }
    
    private void borrowBook() {
        // Open dialog to select user for borrowing
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Mượn sách");
        dialog.setHeaderText("Mượn sách: " + book.getTitle());
        dialog.setContentText("Nhập mã thành viên:");
        
        dialog.showAndWait().ifPresent(userId -> {
            if (userId.trim().isEmpty()) {
                showWarning("Vui lòng nhập mã thành viên.");
                return;
            }
            
            boolean success = controller.borrowBook(userId.trim(), book.getIsbn());
            if (success) {
                showInfo("Mượn sách thành công!");
                book.setBorrowed(true); // Update local state
                // Refresh the view would be better
            } else {
                showError("Không thể mượn sách. Vui lòng kiểm tra lại thông tin.");
            }
        });
    }
    
    private void returnBook() {
        // Open dialog to confirm return
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Trả sách");
        confirmDialog.setHeaderText("Xác nhận trả sách");
        confirmDialog.setContentText("Bạn có chắc chắn muốn trả sách \"" + book.getTitle() + "\"?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // For simplicity, we'll assume we know the user ID
                // In a real implementation, you'd look up the current borrower
                showInfo("Chức năng trả sách sẽ được triển khai khi tích hợp với hệ thống quản lý mượn trả.");
            }
        });
    }
    
    private void editBook() {
        showInfo("Chức năng chỉnh sửa sách sẽ được triển khai trong giao diện quản lý sách.");
    }
    
    private void openReviewDialog() {
        showInfo("Chức năng đánh giá và nhận xét sẽ được triển khai trong phiên bản tiếp theo.");
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
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
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText("Đã xảy ra lỗi");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
