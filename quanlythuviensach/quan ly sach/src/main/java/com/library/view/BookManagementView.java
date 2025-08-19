package com.library.view;

import com.library.controller.LibraryController;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Placeholder view for Book Management
 */
public class BookManagementView {
    private LibraryController controller;
    
    public BookManagementView(LibraryController controller) {
        this.controller = controller;
    }
    
    public VBox createView() {
        VBox container = new VBox();
        container.getChildren().add(new Label("Giao diện Quản lý Sách - Đang phát triển"));
        return container;
    }
    
    public void refresh() {
        // Implementation will be added later
    }
}
