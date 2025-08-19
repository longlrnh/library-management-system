package com.library.view;

import com.library.controller.LibraryController;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Placeholder view for Borrow/Return operations
 */
public class BorrowReturnView {
    private LibraryController controller;
    
    public BorrowReturnView(LibraryController controller) {
        this.controller = controller;
    }
    
    public VBox createView() {
        VBox container = new VBox();
        container.getChildren().add(new Label("Giao diện Mượn/Trả sách - Đang phát triển"));
        return container;
    }
    
    public void refresh() {
        // Implementation will be added later
    }
}
