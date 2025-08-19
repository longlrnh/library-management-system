package com.library.view;

import com.library.controller.LibraryController;
import com.library.model.BorrowRecord;
import com.library.model.Staff;
import com.library.model.Student;
import com.library.model.User;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class UserManagementView {
    private final LibraryController controller;

    private TableView<User> table;
    private ObservableList<User> users = FXCollections.observableArrayList();

    private TextField searchField;
    private Button addBtn;
    private Button editBtn;
    private Button deleteBtn;
    private Label totalUsersLabel;
    private Label activeUsersLabel;


    public UserManagementView(LibraryController controller) {
        this.controller = controller;
    }

    public VBox createView() {
        VBox container = new VBox(12);
        container.setPadding(new Insets(12));


        // Header
        Label title = new Label("Quản lý thành viên");
        title.getStyleClass().add("section-title");

        // Toolbar (search + actions)
        HBox toolbar = new HBox(8);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Tìm theo tên, mã thành viên…");
        searchField.setPrefWidth(320);

        Button searchBtn = new Button("Tìm");
        searchBtn.setOnAction(e -> doSearch());

        Button clearBtn = new Button("Xóa tìm");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            reloadUsers();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addBtn = new Button("Thêm thành viên");
        addBtn.setOnAction(e -> openAddDialog());

        editBtn = new Button("Chỉnh sửa");
        editBtn.setDisable(true);
        editBtn.setOnAction(e -> openEditDialog(getSelectedUser()));

        deleteBtn = new Button("Xóa");
        deleteBtn.setDisable(true);
        deleteBtn.setOnAction(e -> handleDelete(getSelectedUser()));

        toolbar.getChildren().addAll(searchField, searchBtn, clearBtn, spacer, addBtn, editBtn, deleteBtn);

        // Table
        table = createTable();
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            boolean hasSelection = newV != null;
            editBtn.setDisable(!hasSelection);
            deleteBtn.setDisable(!hasSelection);
        });

        // Stats
        HBox stats = new HBox(18);
        stats.setAlignment(Pos.CENTER_LEFT);
        totalUsersLabel = new Label("Tổng thành viên: 0");
        activeUsersLabel = new Label("Đang hoạt động: 0");
        stats.getChildren().addAll(totalUsersLabel, activeUsersLabel);
        

        container.getChildren().addAll(title, toolbar, table, stats);

        // Initial load
        reloadUsers();
        return container;
    }

    public void refresh() {
        // gọi khi chuyển tab hoặc bấm làm mới từ MainView
        reloadUsers();
    }

    // ===== Table definition =====
    private TableView<User> createTable() {
        TableView<User> tv = new TableView<>();
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        TableColumn<User, String> idCol = new TableColumn<>("Mã");
        idCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getId()));
        idCol.setMinWidth(100);

        TableColumn<User, String> nameCol = new TableColumn<>("Họ tên");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        nameCol.setMinWidth(180);

        TableColumn<User, String> typeCol = new TableColumn<>("Loại");
        typeCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole()));

        TableColumn<User, String> deptCol = new TableColumn<>("Khoa/Phòng ban");
        deptCol.setCellValueFactory(c -> {
            User u = c.getValue();
            if (u instanceof Student s) return new SimpleStringProperty(s.getMajor());
            if (u instanceof Staff s) return new SimpleStringProperty(s.getDepartment());
            return new SimpleStringProperty("");
        });

        TableColumn<User, LocalDate> dateCol = new TableColumn<>("Ngày nhập/hire");
        dateCol.setCellValueFactory(c -> {
            User u = c.getValue();
            if (u instanceof Student s) return new SimpleObjectProperty<>(s.getEnrollmentDate());
            if (u instanceof Staff s) return new SimpleObjectProperty<>(s.getHireDate());
            return new SimpleObjectProperty<>(null);
        });

        TableColumn<User, String> activeCol = new TableColumn<>("Trạng thái");
        activeCol.setCellValueFactory(c -> {
            boolean active = false;
            User u = c.getValue();
            if (u instanceof Student s) active = s.isActive();
            if (u instanceof Staff s) active = s.isActive();
            return new SimpleStringProperty(active ? "Đang hoạt động" : "Vô hiệu");
        });

        tv.getColumns().addAll(idCol, nameCol, typeCol, deptCol, dateCol, activeCol);

        // Double-click to edit
        tv.setRowFactory(rows -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    openEditDialog(row.getItem());
                }
            });
            return row;
        });

        tv.setItems(users);
        return tv;
    }

    // ===== Data operations =====
    private void reloadUsers() {
        users.setAll(controller.getAllUsers());
        updateStats();
    }

    private void doSearch() {
        String q = searchField.getText();
        if (q == null || q.isBlank()) {
            reloadUsers();
            return;
        }
        users.setAll(controller.searchUsers(q.trim()));
        updateStats();
    }

    private void updateStats() {
        totalUsersLabel.setText("Tổng thành viên: " + users.size());
        long active = users.stream().filter(u -> {
            if (u instanceof Student s) return s.isActive();
            if (u instanceof Staff s) return s.isActive();
            return false;
        }).count();
        activeUsersLabel.setText("Đang hoạt động: " + active);
    }

    private User getSelectedUser() {
        return table.getSelectionModel().getSelectedItem();
    }

    // ===== Add / Edit / Delete =====
    private void openAddDialog() {
        UserForm form = new UserForm(null);
        Optional<User> result = form.showAndWait();
        result.ifPresent(newUser -> {
            // kiểm tra trùng mã
            if (controller.getUserById(newUser.getId()) != null) {
                showError("Mã thành viên đã tồn tại. Vui lòng chọn mã khác.");
                return;
            }
            boolean ok = controller.addUser(newUser);
            if (ok) {
                showInfo("Thêm thành viên thành công.");
                reloadUsers();
            } else {
                showError("Không thể thêm thành viên. Vui lòng kiểm tra dữ liệu hoặc nhật ký lỗi.");
            }
        });
    }

    private void openEditDialog(User user) {
        if (user == null) return;
        UserForm form = new UserForm(user);
        Optional<User> result = form.showAndWait();
        result.ifPresent(updated -> {
            boolean ok = controller.updateUser(updated);
            if (ok) {
                showInfo("Cập nhật thông tin thành công.");
                reloadUsers();
            } else {
                showError("Không thể cập nhật thành viên.");
            }
        });
    }

    private void handleDelete(User user) {
        if (user == null) return;

        // Không cho xóa nếu đang mượn sách
        List<BorrowRecord> current = controller.getUserCurrentBorrows(user.getId());
        if (current != null && !current.isEmpty()) {
            showError("Không thể xóa. Thành viên đang có sách đang mượn.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Xóa thành viên");
        confirm.setHeaderText("Bạn có chắc muốn xóa?");
        confirm.setContentText("Mã: " + user.getId() + " - " + user.getName());
        Optional<ButtonType> resp = confirm.showAndWait();
        if (resp.isPresent() && resp.get() == ButtonType.OK) {
            boolean ok = controller.deleteUser(user.getId());
            if (ok) {
                showInfo("Đã xóa thành viên.");
                reloadUsers();
            } else {
                showError("Xóa thất bại.");
            }
        }
    }

    // ===== Dialog form =====
    private final class UserForm extends Dialog<User> {
        private final boolean editing;
        private final TextField idField = new TextField();
        private final TextField nameField = new TextField();
        private final ComboBox<String> typeBox = new ComboBox<>();
        private final TextField majorDeptField = new TextField();
        private final DatePicker datePicker = new DatePicker();
        private final CheckBox activeBox = new CheckBox("Đang hoạt động");

        UserForm(User existing) {
            this.editing = existing != null;
            setTitle(editing ? "Chỉnh sửa thành viên" : "Thêm thành viên");
            setHeaderText(editing ? "Cập nhật thông tin thành viên" : "Nhập thông tin thành viên mới");

            // Buttons
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Content grid
            GridPane grid = new GridPane();
            grid.setVgap(8);
            grid.setHgap(10);
            grid.setPadding(new Insets(10));

            idField.setPromptText("VD: STU001 hoặc STF001");
            nameField.setPromptText("Họ và tên");
            majorDeptField.setPromptText("Khoa (Sinh viên) / Phòng ban (Nhân viên)");

            typeBox.setItems(FXCollections.observableArrayList("Sinh viên", "Nhân viên"));
            typeBox.setConverter(new StringConverter<>() {
                @Override public String toString(String s) { return s; }
                @Override public String fromString(String s) { return s; }
            });

            int row = 0;
            grid.add(new Label("Mã:"), 0, row);         grid.add(idField, 1, row++);
            grid.add(new Label("Họ tên:"), 0, row);     grid.add(nameField, 1, row++);
            grid.add(new Label("Loại:"), 0, row);       grid.add(typeBox, 1, row++);
            grid.add(new Label("Khoa/Phòng ban:"), 0, row); grid.add(majorDeptField, 1, row++);
            grid.add(new Label("Ngày nhập/hire:"), 0, row); grid.add(datePicker, 1, row++);
            grid.add(activeBox, 1, row++);

            getDialogPane().setContent(grid);

            // Prefill if editing
            if (editing) {
                idField.setText(existing.getId());
                idField.setDisable(true); // không cho đổi mã khi sửa

                nameField.setText(existing.getName());
                if (existing instanceof Student s) {
                    typeBox.getSelectionModel().select("Sinh viên");
                    majorDeptField.setText(s.getMajor());
                    datePicker.setValue(s.getEnrollmentDate());
                    activeBox.setSelected(s.isActive());
                } else if (existing instanceof Staff s) {
                    typeBox.getSelectionModel().select("Nhân viên");
                    majorDeptField.setText(s.getDepartment());
                    datePicker.setValue(s.getHireDate());
                    activeBox.setSelected(s.isActive());
                }
            } else {
                typeBox.getSelectionModel().selectFirst();
                datePicker.setValue(LocalDate.now());
                activeBox.setSelected(true);
            }

            // Result converter
            setResultConverter(btn -> {
                if (btn != ButtonType.OK) return null;

                // Validate
                String id = idField.getText() != null ? idField.getText().trim() : "";
                String name = nameField.getText() != null ? nameField.getText().trim() : "";
                String type = typeBox.getValue();
                String md = majorDeptField.getText() != null ? majorDeptField.getText().trim() : "";
                LocalDate date = datePicker.getValue();

                if (id.isBlank() || name.isBlank() || type == null || type.isBlank() || md.isBlank() || date == null) {
                    showError("Vui lòng nhập đầy đủ thông tin.");
                    return null;
                }

                boolean active = activeBox.isSelected();

                if (editing) {
                    // Cập nhật đối tượng hiện có, giữ nguyên subclass
                    if (existing instanceof Student s) {
                        s.setName(name);
                        s.setMajor(md);
                        s.setEnrollmentDate(date);
                        s.setActive(active);
                        return s;
                    } else if (existing instanceof Staff s) {
                        s.setName(name);
                        s.setDepartment(md);
                        s.setHireDate(date);
                        s.setActive(active);
                        return s;
                    }
                    return null;
                } else {
                    // Tạo mới theo loại
                    if ("Sinh viên".equals(type)) {
                        Student s = new Student(name, id, md, date);
                        s.setActive(active);
                        return s;
                    } else {
                        Staff s = new Staff(name, id, md, date);
                        s.setActive(active);
                        return s;
                    }
                }
            });
        }
    }

    // ===== Helpers =====
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
