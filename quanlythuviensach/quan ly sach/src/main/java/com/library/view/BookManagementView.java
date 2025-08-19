package com.library.view;

import com.library.controller.LibraryController;
import com.library.model.Book;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

/**
 * Placeholder view for Book Management
 */
public class BookManagementView {
    private LibraryController controller;

    private TableView<Book> bookTable;
    private TextField txtMaSach, txtTenSach, txtTacGia, txtNhaXuatBan, txtNamXuatBan, txtTheLoai, txtSoLuong;
    private Button btnThem, btnSua, btnXoa, btnLamMoi;

    public BookManagementView(LibraryController controller) {
        this.controller = controller;
    }

    public BorderPane createView() {
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        VBox topContainer = new VBox(10);
        topContainer.getChildren().addAll(createFormPane(), createButtonBox());
        layout.setTop(topContainer);
        layout.setCenter(createTableView());
        setupEventHandlers();
        refresh();
        return layout;
    }

    private GridPane createFormPane() {
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(10));

        formPane.add(new Label("Mã sách (ISBN):"), 0, 0);
        txtMaSach = new TextField();
        formPane.add(txtMaSach, 1, 0);

        formPane.add(new Label("Tên sách:"), 2, 0);
        txtTenSach = new TextField();
        GridPane.setColumnSpan(txtTenSach, 3);
        formPane.add(txtTenSach, 3, 0);

        formPane.add(new Label("Tác giả:"), 0, 1);
        txtTacGia = new TextField();
        formPane.add(txtTacGia, 1, 1);

        formPane.add(new Label("Nhà XB:"), 2, 1);
        txtNhaXuatBan = new TextField();
        formPane.add(txtNhaXuatBan, 3, 1);

        formPane.add(new Label("Năm XB:"), 0, 2);
        txtNamXuatBan = new TextField();
        formPane.add(txtNamXuatBan, 1, 2);

        formPane.add(new Label("Thể loại:"), 2, 2);
        txtTheLoai = new TextField();
        formPane.add(txtTheLoai, 3, 2);

        formPane.add(new Label("Số lượng:"), 4, 2);
        txtSoLuong = new TextField();
        formPane.add(txtSoLuong, 5, 2);

        return formPane;
    }

    private HBox createButtonBox() {
        HBox buttonBox = new HBox(10);
        buttonBox.setPadding(new Insets(10, 0, 10, 0));
        btnThem = new Button("Thêm");
        btnSua = new Button("Sửa");
        btnXoa = new Button("Xóa");
        btnLamMoi = new Button("Làm mới");
        buttonBox.getChildren().addAll(btnThem, btnSua, btnXoa, btnLamMoi);
        return buttonBox;
    }

    private TableView<Book> createTableView() {
        bookTable = new TableView<>();

        TableColumn<Book, String> maSachCol = new TableColumn<>("Mã sách (ISBN)");
        maSachCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        TableColumn<Book, String> tenSachCol = new TableColumn<>("Tên sách");
        tenSachCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        tenSachCol.setPrefWidth(250);

        TableColumn<Book, String> tacGiaCol = new TableColumn<>("Tác giả");
        tacGiaCol.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> nhaXBCol = new TableColumn<>("Nhà XB");
        nhaXBCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));

        TableColumn<Book, Integer> namXBCol = new TableColumn<>("Năm XB");
        namXBCol.setCellValueFactory(new PropertyValueFactory<>("publishYear"));

        TableColumn<Book, Integer> soLuongCol = new TableColumn<>("Số lượng");
        soLuongCol.setCellValueFactory(new PropertyValueFactory<>("soLuong"));

        bookTable.getColumns().addAll(maSachCol, tenSachCol, tacGiaCol, nhaXBCol, namXBCol, soLuongCol);
        return bookTable;
    }

    private void setupEventHandlers() {
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtMaSach.setText(newSelection.getIsbn());
                txtTenSach.setText(newSelection.getTitle());
                txtTacGia.setText(newSelection.getAuthor());
                txtNhaXuatBan.setText(newSelection.getPublisher());
                txtNamXuatBan.setText(String.valueOf(newSelection.getPublishYear()));
                txtTheLoai.setText(newSelection.getGenre());
                txtSoLuong.setText(String.valueOf(newSelection.getSoLuong()));
                txtMaSach.setEditable(false);
            }
        });

        btnLamMoi.setOnAction(e -> clearForm());
        btnThem.setOnAction(e -> addBook());
        btnSua.setOnAction(e -> updateBook());
        btnXoa.setOnAction(e -> deleteBook());
    }

    public void refresh() {
        bookTable.setItems(FXCollections.observableArrayList(controller.getAllBooks()));
        bookTable.refresh();
        clearForm();
    }

    private void clearForm() {
        txtMaSach.clear();
        txtTenSach.clear();
        txtTacGia.clear();
        txtNhaXuatBan.clear();
        txtNamXuatBan.clear();
        txtTheLoai.clear();
        txtSoLuong.clear();
        txtMaSach.setEditable(true);
        bookTable.getSelectionModel().clearSelection();
    }

    private Book getBookFromForm() {
        try {
            return new Book(
                    txtMaSach.getText().trim(),
                    txtTenSach.getText().trim(),
                    txtTacGia.getText().trim(),
                    txtNhaXuatBan.getText().trim(),
                    Integer.parseInt(txtNamXuatBan.getText().trim()),
                    txtTheLoai.getText().trim(),
                    Integer.parseInt(txtSoLuong.getText().trim())
            );
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Lỗi Nhập Liệu", "Năm xuất bản và Số lượng phải là số.");
            return null;
        }
    }

    private void addBook() {
        Book book = getBookFromForm();
        if (book != null) {
            if (controller.addBook(book)) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thêm sách mới thành công!");
                refresh();
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Thêm sách mới thất bại.");
            }
        }
    }

    private void updateBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn sách", "Vui lòng chọn một cuốn sách để sửa.");
            return;
        }

        Book updatedBook = getBookFromForm();
        if (updatedBook != null) {
            boolean success = controller.updateBook(updatedBook);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật sách thành công!");
                refresh();
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Cập nhật sách thất bại.");
            }
        }
    }

    private void deleteBook() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Chưa chọn sách", "Vui lòng chọn một cuốn sách để xóa.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Xác nhận xóa");
        confirmation.setHeaderText("Bạn có chắc chắn muốn xóa sách: " + selectedBook.getTitle() + "?");
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = controller.deleteBook(selectedBook.getIsbn());
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Xóa sách thành công!");
                refresh();
            } else {
                showAlert(Alert.AlertType.ERROR, "Thất bại", "Xóa sách thất bại.");
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}