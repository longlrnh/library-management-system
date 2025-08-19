package com.library;

import com.library.view.MainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.logging.Logger;

/**
 * Main application class for Library Management System
 */
public class LibraryApplication extends Application {
    private static final Logger LOGGER = Logger.getLogger(LibraryApplication.class.getName());
    
    @Override
    public void start(Stage primaryStage) {
        try {
            MainView mainView = new MainView();
            mainView.start(primaryStage);
            
            // Set up application close handler
            primaryStage.setOnCloseRequest(event -> {
                LOGGER.info("Application closing...");
                Platform.exit();
                System.exit(0);
            });
            
        } catch (Exception e) {
            LOGGER.severe("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            Platform.exit();
        }
    }
    
    @Override
    public void stop() throws Exception {
        LOGGER.info("Application stopped");
        super.stop();
    }
    
    public static void main(String[] args) {
        LOGGER.info("Starting Library Management System...");
        launch(args);
    }
}
