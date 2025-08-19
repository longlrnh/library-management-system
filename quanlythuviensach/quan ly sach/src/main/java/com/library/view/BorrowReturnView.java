package com.library.view;

import com.library.controller.LibraryController;
import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BorrowReturnView {
    private final LibraryController controller;

    // Borrow controls
    private TextField borrowUserIdField;
    private TextField borrowIsbnField;
    private Button borrowBtn;

    // Return controls
    private TextField returnUserIdField;
    private TextField returnIsbnField;
    private Button loadUserBorrowsBtn;
    private Button returnSelectedBtn;

    // Tables
    private TableView<BorrowRecord> userBorrowTable;
    private TableView<BorrowRecord> activeBorrowTable;

    private final ObservableList<BorrowRecord> userBorrowData = FXCollections.observableArrayList();
    private final ObservableList<BorrowRecord> activeBorrowData = FXCollections.observableArrayList();

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public BorrowReturnView(LibraryController controller) {
        this.controller = controller;
    }

    public VBox createView() {
        VBox root = new VBox(12);
        root.setPadding(new Insets(12));

        // --- Borrow panel ---
        TitledPane borrowPane = new TitledPane();
        borrowPane.setText("Mượn sách");
        borrowPane.setCollapsible(false);
        borrowPane.setContent(buildBorrowBox());

        // --- Return panel ---
        TitledPane returnPane = new TitledPane();
        returnPane.setText("Trả sách");
        returnPane.setCollapsible(false);
        returnPane.setContent(buildReturnBox());

        // --- Active borrows table (all users) ---
        Label activeLbl = new Label("Các lượt mượn đang hoạt động");
        activeBorrowTable = buildRecordTable();
        HBox activeBar = new HBox(8);
        Button refreshActiveBtn = new Button("Làm mới");
        refreshActiveBtn.setOnAction(e -> reloadActiveBorrows());
        activeBar.getChildren().addAll(refreshActiveBtn);
        activeBar.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(borrowPane, returnPane, activeLbl, activeBorrowTable, activeBar);

        // initial loads
        reloadActiveBorrows();

        return root;
    }

    public void refresh() {
        // gọi khi chuyển tab hoặc bấm làm mới từ MainView
        reloadActiveBorrows();
        if (returnUserIdField != null && !returnUserIdField.getText().isBlank()) {
            loadUserBorrows();
        }
    }

    // ===== UI builders =====
    private Pane buildBorrowBox() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        borrowUserIdField = new TextField();
        borrowUserIdField.setPromptText("Nhập mã thành viên (VD: STU001)");
        borrowIsbnField = new TextField();
        borrowIsbnField.setPromptText("Nhập ISBN sách");

        borrowBtn = new Button("Mượn");
        borrowBtn.setOnAction(e -> onBorrow());

        int r = 0;
        grid.add(new Label("Mã thành viên:"), 0, r);
        grid.add(borrowUserIdField, 1, r++);
        grid.add(new Label("ISBN sách:"), 0, r);
        grid.add(borrowIsbnField, 1, r++);
        grid.add(borrowBtn, 1, r);

        return grid;
    }

    private Pane buildReturnBox() {
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);

        returnUserIdField = new TextField();
        returnUserIdField.setPromptText("Nhập mã thành viên để tải các sách đang mượn");
        returnIsbnField = new TextField();
        returnIsbnField.setPromptText("Nhập ISBN để trả nhanh (tùy chọn)");

        loadUserBorrowsBtn = new Button("Tải sách đang mượn");
        loadUserBorrowsBtn.setOnAction(e -> loadUserBorrows());

        Button quickReturnBtn = new Button("Trả (nhập ISBN)");
        quickReturnBtn.setOnAction(e -> onReturnByIsbn());

        int r = 0;
        form.add(new Label("Mã thành viên:"), 0, r);
        form.add(returnUserIdField, 1, r++);
        form.add(new Label("ISBN trả nhanh:"), 0, r);
        form.add(returnIsbnField, 1, r++);
        HBox actions = new HBox(8, loadUserBorrowsBtn, quickReturnBtn);
        form.add(actions, 1, r);

        userBorrowTable = buildRecordTable();
        returnSelectedBtn = new Button("Trả sách đã chọn");
        returnSelectedBtn.setOnAction(e -> onReturnSelected());

        box.getChildren().addAll(form, new Label("Sách đang mượn của thành viên"), userBorrowTable, returnSelectedBtn);
        return box;
    }

    private TableView<BorrowRecord> buildRecordTable() {
        TableView<BorrowRecord> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<BorrowRecord, Integer> idCol = new TableColumn<>("Mã giao dịch");
        idCol.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        idCol.setMinWidth(120);

        TableColumn<BorrowRecord, String> userCol = new TableColumn<>("Mã thành viên");
        userCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userCol.setMinWidth(120);

        TableColumn<BorrowRecord, String> isbnCol = new TableColumn<>("ISBN");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("bookIsbn"));
        isbnCol.setMinWidth(140);

        TableColumn<BorrowRecord, LocalDateTime> borrowCol = new TableColumn<>("Ngày mượn");
        borrowCol.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        borrowCol.setCellFactory(timeCell());

        TableColumn<BorrowRecord, LocalDateTime> dueCol = new TableColumn<>("Hạn trả");
        dueCol.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        dueCol.setCellFactory(timeCell());

        TableColumn<BorrowRecord, Boolean> returnedCol = new TableColumn<>("Đã trả");
        returnedCol.setCellValueFactory(new PropertyValueFactory<>("returned"));

        TableColumn<BorrowRecord, Double> fineCol = new TableColumn<>("Tiền phạt");
        fineCol.setCellValueFactory(new PropertyValueFactory<>("fineAmount"));

        tv.getColumns().addAll(idCol, userCol, isbnCol, borrowCol, dueCol, returnedCol, fineCol);
        return tv;
    }

    private Callback<TableColumn<BorrowRecord, LocalDateTime>, TableCell<BorrowRecord, LocalDateTime>> timeCell() {
        return col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dtf.format(item));
                }
            }
        };
    }

    // ===== Actions =====
    private void onBorrow() {
        String userId = safeText(borrowUserIdField);
        String isbn = safeText(borrowIsbnField);

        if (userId.isBlank() || isbn.isBlank()) {
            showError("Vui lòng nhập đầy đủ mã thành viên và ISBN.");
            return;
        }

        // Kiểm tra tồn tại
        if (!controller.userExists(userId)) {
            showError("Không tìm thấy thành viên: " + userId);
            return;
        }
        Book book = controller.getBookByIsbn(isbn);
        if (book == null) {
            showError("Không tìm thấy sách với ISBN: " + isbn);
            return;
        }
        if (book.isBorrowed()) {
            showError("Sách hiện đang được mượn.");
            return;
        }

        boolean ok = controller.borrowBook(userId, isbn);
        if (ok) {
            showInfo("Mượn sách thành công.");
            clearBorrowInputs();
            reloadActiveBorrows();
            // nếu đang xem danh sách mượn của user đó thì refresh luôn
            if (userId.equals(safeText(returnUserIdField))) {
                loadUserBorrows();
            }
        } else {
            showError("Không thể mượn sách. Vui lòng kiểm tra điều kiện mượn hoặc nhật ký lỗi.");
        }
    }

    private void onReturnByIsbn() {
        String userId = safeText(returnUserIdField);
        String isbn = safeText(returnIsbnField);

        if (userId.isBlank() || isbn.isBlank()) {
            showError("Vui lòng nhập mã thành viên và ISBN cần trả.");
            return;
        }

        if (!controller.userExists(userId)) {
            showError("Không tìm thấy thành viên: " + userId);
            return;
        }
        Book book = controller.getBookByIsbn(isbn);
        if (book == null) {
            showError("Không tìm thấy sách với ISBN: " + isbn);
            return;
        }

        boolean ok = controller.returnBook(userId, isbn);
        if (ok) {
            showInfo("Trả sách thành công.");
            returnIsbnField.clear();
            reloadActiveBorrows();
            loadUserBorrows();
        } else {
            showError("Không thể trả sách. Kiểm tra lại dữ liệu hoặc nhật ký lỗi.");
        }
    }

    private void onReturnSelected() {
        BorrowRecord rec = userBorrowTable.getSelectionModel().getSelectedItem();
        if (rec == null) {
            showError("Hãy chọn một dòng trong bảng sách đang mượn.");
            return;
        }
        if (rec.isReturned()) {
            showInfo("Bản ghi đã được trả trước đó.");
            return;
        }
        boolean ok = controller.returnBook(rec.getUserId(), rec.getBookIsbn());
        if (ok) {
            showInfo("Trả sách thành công.");
            reloadActiveBorrows();
            loadUserBorrows();
        } else {
            showError("Không thể trả sách. Kiểm tra lại dữ liệu hoặc nhật ký lỗi.");
        }
    }

    private void loadUserBorrows() {
        String userId = safeText(returnUserIdField);
        if (userId.isBlank()) {
            showError("Vui lòng nhập mã thành viên.");
            return;
        }
        if (!controller.userExists(userId)) {
            showError("Không tìm thấy thành viên: " + userId);
            return;
        }
        List<BorrowRecord> list = controller.getUserCurrentBorrows(userId);
        userBorrowData.setAll(list);
        userBorrowTable.setItems(userBorrowData);
    }

    private void reloadActiveBorrows() {
        List<BorrowRecord> list = controller.getAllActiveBorrows();
        activeBorrowData.setAll(list);
        activeBorrowTable.setItems(activeBorrowData);
    }

    // ===== helpers =====
    private String safeText(TextField tf) {
        return tf == null || tf.getText() == null ? "" : tf.getText().trim();
    }

    private void clearBorrowInputs() {
        borrowIsbnField.clear();
        // giữ lại userId để có thể mượn tiếp cho cùng user nếu muốn
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Lỗi");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Thông báo");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
