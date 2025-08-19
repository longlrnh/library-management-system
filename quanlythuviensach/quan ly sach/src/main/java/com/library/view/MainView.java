package com.library.view;

import com.library.controller.LibraryController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Main view for the Library Management System
 */
public class MainView {
    private Stage primaryStage;
    private LibraryController controller;
    private BorderPane mainLayout;
    private TabPane contentTabPane;
    
    // Views
    private BookListView bookListView;
    private UserManagementView userManagementView;
    private BorrowReturnView borrowReturnView;
    private BookManagementView bookManagementView;
    private ReportsView reportsView;
    
    public MainView() {
        this.controller = new LibraryController();
        initializeViews();
    }
    
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        primaryStage.setTitle("Hệ thống Quản lý Thư viện");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/naruto.png")));
        
        Scene scene = new Scene(createMainLayout(), 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/css/main-style.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        // Load initial data
        loadInitialData();
    }
    
    private void initializeViews() {
        bookListView = new BookListView(controller);
        userManagementView = new UserManagementView(controller);
        borrowReturnView = new BorrowReturnView(controller);
        bookManagementView = new BookManagementView(controller);
        reportsView = new ReportsView(controller);
    }
    
    private BorderPane createMainLayout() {
        mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("main-layout");
        
        // Top: Header with title and toolbar
        mainLayout.setTop(createHeader());
        
        // Left: Navigation menu
        mainLayout.setLeft(createNavigationPane());
        
        // Center: Content area with tabs
        mainLayout.setCenter(createContentArea());
        
        // Bottom: Status bar
        mainLayout.setBottom(createStatusBar());
        
        return mainLayout;
    }
    
    private VBox createHeader() {
        VBox header = new VBox();
        header.getStyleClass().add("header");
        header.setPadding(new Insets(10, 20, 10, 20));
        
        // Title
        Label titleLabel = new Label("HỆ THỐNG QUẢN LÝ THƯ VIỆN");        titleLabel.getStyleClass().add("main-title");
        
        // Toolbar
        ToolBar toolbar = new ToolBar();
        toolbar.getStyleClass().add("main-toolbar");
        
        Button refreshBtn = new Button("Làm mới");
        refreshBtn.getStyleClass().add("toolbar-button");
        refreshBtn.setOnAction(e -> refreshCurrentView());
        
        Button aboutBtn = new Button("Về chúng tôi");
        aboutBtn.getStyleClass().add("toolbar-button");
        aboutBtn.setOnAction(e -> showAboutDialog());
        
        // Add spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        toolbar.getItems().addAll(refreshBtn, new Separator(), aboutBtn, spacer);
        
        header.getChildren().addAll(titleLabel, toolbar);
        return header;
    }
    
    private VBox createNavigationPane() {
        VBox navigation = new VBox(10);
        navigation.getStyleClass().add("navigation-pane");
        navigation.setPadding(new Insets(20, 10, 20, 10));
        navigation.setPrefWidth(200);
        
        Label navTitle = new Label("DANH MỤC");
        navTitle.getStyleClass().add("nav-title");
        
        // Navigation buttons
        Button booksBtn = createNavButton("📚 Danh sách sách", () -> switchToTab(0));
        Button usersBtn = createNavButton("👥 Quản lý thành viên", () -> switchToTab(1));
        Button borrowBtn = createNavButton("📖 Mượn/Trả sách", () -> switchToTab(2));
        Button managementBtn = createNavButton("⚙️ Quản lý sách", () -> switchToTab(3));
        Button reportsBtn = createNavButton("📊 Báo cáo thống kê", () -> switchToTab(4));
        
        Separator separator = new Separator();
        
        Button helpBtn = createNavButton("❓ Trợ giúp", this::showHelp);
        
        navigation.getChildren().addAll(
            navTitle,
            new Separator(),
            booksBtn,
            usersBtn,
            borrowBtn,
            managementBtn,
            reportsBtn,
            separator,
            helpBtn
        );
        
        return navigation;
    }
    
    private Button createNavButton(String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setOnAction(e -> action.run());
        return button;
    }
    
    private TabPane createContentArea() {
        contentTabPane = new TabPane();
        contentTabPane.getStyleClass().add("content-tabs");
        contentTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Create tabs
        Tab bookListTab = new Tab("Danh sách sách", bookListView.createView());
        bookListTab.setGraphic(new Label("📚"));
        
        Tab userManagementTab = new Tab("Quản lý thành viên", userManagementView.createView());
        userManagementTab.setGraphic(new Label("👥"));
        
        Tab borrowReturnTab = new Tab("Mượn/Trả sách", borrowReturnView.createView());
        borrowReturnTab.setGraphic(new Label("📖"));
        
        Tab bookManagementTab = new Tab("Quản lý sách", bookManagementView.createView());
        bookManagementTab.setGraphic(new Label("⚙️"));
        
        Tab reportsTab = new Tab("Báo cáo", reportsView.createView());
        reportsTab.setGraphic(new Label("📊"));
        
        contentTabPane.getTabs().addAll(
            bookListTab,
            userManagementTab,
            borrowReturnTab,
            bookManagementTab,
            reportsTab
        );
        
        return contentTabPane;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.getStyleClass().add("status-bar");
        statusBar.setPadding(new Insets(5, 20, 5, 20));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLabel = new Label("Sẵn sàng");
        statusLabel.getStyleClass().add("status-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label timeLabel = new Label();
        timeLabel.getStyleClass().add("time-label");
        updateTimeLabel(timeLabel);
        
        // Update time every second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateTimeLabel(timeLabel)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        
        statusBar.getChildren().addAll(statusLabel, spacer, timeLabel);
        return statusBar;
    }
    
    private void updateTimeLabel(Label timeLabel) {
        timeLabel.setText(java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }
    
    private void switchToTab(int tabIndex) {
        if (tabIndex >= 0 && tabIndex < contentTabPane.getTabs().size()) {
            contentTabPane.getSelectionModel().select(tabIndex);
        }
    }
    
    private void refreshCurrentView() {
        int selectedIndex = contentTabPane.getSelectionModel().getSelectedIndex();
        
        switch (selectedIndex) {
            case 0:
                bookListView.refresh();
                break;
            case 1:
                userManagementView.refresh();
                break;
            case 2:
                borrowReturnView.refresh();
                break;
            case 3:
                bookManagementView.refresh();
                break;
            case 4:
                reportsView.refresh();
                break;
        }
        
        showStatusMessage("Đã làm mới dữ liệu");
    }
    
    private void showAboutDialog() {
        Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
        aboutAlert.setTitle("Về chúng tôi");
        aboutAlert.setHeaderText("Hệ thống Quản lý Thư viện v1.0");
        aboutAlert.setContentText(
            "Phát triển bởi: Nhóm phát triển\n" +
            "Năm: 2025\n" +
            "Công nghệ: JavaFX, SQLite, Google Books API\n\n" +
            "Hệ thống quản lý thư viện hiện đại với các tính năng:\n" +
            "- Quản lý sách và tài liệu\n" +
            "- Quản lý thành viên\n" +
            "- Mượn/trả sách\n" +
            "- Tích hợp Google Books API\n" +
            "- Đánh giá và nhận xét"
        );
        aboutAlert.getDialogPane().setPrefWidth(400);
        aboutAlert.showAndWait();
    }
    
    private void showHelp() {
        Alert helpAlert = new Alert(Alert.AlertType.INFORMATION);
        helpAlert.setTitle("Trợ giúp");
        helpAlert.setHeaderText("Hướng dẫn sử dụng");
        helpAlert.setContentText(
            "HƯỚNG DẪN SỬ DỤNG:\n\n" +
            "1. Danh sách sách: Xem và tìm kiếm sách trong thư viện\n" +
            "2. Quản lý thành viên: Thêm, sửa, xóa thành viên\n" +
            "3. Mượn/Trả sách: Xử lý các giao dịch mượn trả\n" +
            "4. Quản lý sách: Thêm, sửa, xóa sách\n" +
            "5. Báo cáo: Xem thống kê và báo cáo\n\n" +
            "Sử dụng thanh tìm kiếm để tìm sách hoặc thành viên nhanh chóng.\n" +
            "Nhấp đúp vào một mục để xem chi tiết."
        );
        helpAlert.getDialogPane().setPrefWidth(400);
        helpAlert.showAndWait();
    }
    
    private void loadInitialData() {
        // Load sample data if database is empty
        controller.initializeSampleData();
    }
    
    private void showStatusMessage(String message) {
        // Update status bar with message
        HBox statusBar = (HBox) mainLayout.getBottom();
        Label statusLabel = (Label) statusBar.getChildren().get(0);
        statusLabel.setText(message);
        
        // Reset to default after 3 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), 
            e -> statusLabel.setText("Sẵn sàng")));
        timeline.play();
    }
}
