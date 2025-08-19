package com.library.view;

import com.library.controller.LibraryController;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Placeholder view for User Management
 */
public class UserManagementView {
    private LibraryController controller;
    
    public UserManagementView(LibraryController controller) {
        this.controller = controller;
    }
    
    public VBox createView() {
        VBox container = new VBox();
        container.getChildren().add(new Label("Giao diện Quản lý Thành viên - Đang phát triển"));
        return container;
    }
    
    public void refresh() {
        // Implementation will be added later
    }
}
