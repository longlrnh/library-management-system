# Hệ thống Quản lý Thư viện

## Mô tả

Hệ thống Quản lý Thư viện là một ứng dụng JavaFX hiện đại được thiết kế để quản lý các hoạt động của thư viện một cách hiệu quả. Hệ thống cung cấp giao diện người dùng thân thiện và các tính năng mạnh mẽ để quản lý sách, thành viên, và các giao dịch mượn trả.

## Tính năng chính

### 🚀 Tính năng cơ bản
- **Quản lý Sách**: Thêm, sửa, xóa và tìm kiếm sách
- **Quản lý Thành viên**: Quản lý sinh viên và nhân viên
- **Mượn/Trả sách**: Xử lý các giao dịch mượn và trả sách
- **Tìm kiếm nâng cao**: Tìm kiếm theo tiêu đề, tác giả, thể loại
- **Báo cáo thống kê**: Thống kê về sách, thành viên và giao dịch

### 🔥 Tính năng nâng cao
- **Tích hợp Google Books API**: Tự động lấy thông tin sách từ ISBN
- **Tạo mã QR**: Tạo mã QR cho mỗi cuốn sách để quản lý dễ dàng
- **Hệ thống gợi ý**: Gợi ý sách dựa trên lịch sử mượn và sở thích
- **Đánh giá và nhận xét**: Thành viên có thể đánh giá và nhận xét sách
- **Đa luồng**: Xử lý không đồng bộ để tránh treo giao diện
- **Giao diện thân thiện**: Thiết kế hiện đại với CSS styling

## Công nghệ sử dụng

- **Java 11+**: Ngôn ngữ lập trình chính
- **JavaFX 17**: Framework giao diện người dùng
- **SQLite**: Cơ sở dữ liệu nhúng
- **Maven**: Quản lý dependencies và build
- **Google Books API**: Tích hợp thông tin sách
- **ZXing**: Thư viện tạo và đọc mã QR
- **OkHttp**: HTTP client cho API calls
- **Gson**: Xử lý JSON

## Cấu trúc dự án

```
src/
├── main/
│   ├── java/
│   │   ├── com/library/
│   │   │   ├── LibraryApplication.java          # Main application class
│   │   │   ├── controller/
│   │   │   │   └── LibraryController.java       # Main controller
│   │   │   ├── database/
│   │   │   │   ├── Database.java                # Database connection
│   │   │   │   ├── BookDAO.java                 # Book data access
│   │   │   │   └── UserDAO.java                 # User data access
│   │   │   ├── model/
│   │   │   │   ├── Book.java                    # Book model
│   │   │   │   ├── User.java                    # User base class
│   │   │   │   ├── Student.java                 # Student model
│   │   │   │   ├── Staff.java                   # Staff model
│   │   │   │   ├── BorrowRecord.java            # Borrow record model
│   │   │   │   └── BookReview.java              # Review model
│   │   │   ├── service/
│   │   │   │   ├── BorrowService.java           # Borrow/return logic
│   │   │   │   ├── GoogleBooksService.java      # Google Books API
│   │   │   │   └── RecommendationService.java   # Recommendation system
│   │   │   ├── utils/
│   │   │   │   ├── QRCodeGenerator.java         # QR code utilities
│   │   │   │   └── ValidationUtils.java         # Input validation
│   │   │   └── view/
│   │   │       ├── MainView.java                # Main application view
│   │   │       ├── BookListView.java            # Book listing view
│   │   │       ├── BookDetailView.java          # Book detail view
│   │   │       ├── UserManagementView.java      # User management
│   │   │       ├── BorrowReturnView.java        # Borrow/return interface
│   │   │       ├── BookManagementView.java      # Book management
│   │   │       └── ReportsView.java             # Reports and statistics
│   │   └── module-info.java                     # Module definition
│   └── resources/
│       ├── css/
│       │   └── main-style.css                   # Application styles
│       ├── fxml/                                # FXML files (if used)
│       └── images/                              # Application icons and images
└── pom.xml                                      # Maven configuration
```

## Cài đặt và Chạy

### Yêu cầu hệ thống
- Java Development Kit (JDK) 11 hoặc cao hơn
- Apache Maven 3.6 hoặc cao hơn
- Kết nối Internet (cho Google Books API)

### Hướng dẫn cài đặt

1. **Clone repository**
   ```bash
   git clone <repository-url>
   cd library-management-system
   ```

2. **Cài đặt dependencies**
   ```bash
   mvn clean install
   ```

3. **Chạy ứng dụng**
   ```bash
   mvn javafx:run
   ```

   Hoặc chạy từ IDE:
   ```bash
   java -cp "target/classes:target/lib/*" com.library.LibraryApplication
   ```

### Build JAR file

```bash
mvn clean package
java -jar target/library-management-system-1.0.0.jar
```

## Sử dụng

### Khởi chạy lần đầu
1. Khởi động ứng dụng
2. Hệ thống sẽ tự động tạo cơ sở dữ liệu SQLite
3. Dữ liệu mẫu sẽ được tạo tự động

### Các chức năng chính

#### 📚 Quản lý Sách
- **Xem danh sách**: Tab "Danh sách sách"
- **Tìm kiếm**: Sử dụng thanh tìm kiếm ở đầu trang
- **Xem chi tiết**: Double-click vào sách trong danh sách
- **Tạo mã QR**: Chọn sách và nhấn "Tạo mã QR"

#### 👥 Quản lý Thành viên
- **Thêm thành viên**: Tab "Quản lý thành viên" > "Thêm mới"
- **Chỉnh sửa**: Double-click vào thành viên để chỉnh sửa
- **Tìm kiếm**: Tìm theo tên hoặc mã thành viên

#### 📖 Mượn/Trả sách
- **Mượn sách**: Tab "Mượn/Trả sách" > Chọn thành viên và sách
- **Trả sách**: Quét mã QR hoặc nhập thông tin thủ công
- **Xem lịch sử**: Theo dõi các giao dịch mượn trả

## API Reference

### Google Books API
Ứng dụng tích hợp với Google Books API để:
- Tự động lấy thông tin sách từ ISBN
- Tìm kiếm sách theo tiêu đề và tác giả
- Cập nhật thông tin sách (ảnh bìa, mô tả, v.v.)

### Database Schema
```sql
-- Books table
CREATE TABLE books (
    isbn TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    author TEXT NOT NULL,
    publisher TEXT,
    publish_date TEXT,
    page_count INTEGER,
    genre TEXT,
    language TEXT,
    rating REAL DEFAULT 0.0,
    rating_count INTEGER DEFAULT 0,
    description TEXT,
    is_borrowed BOOLEAN DEFAULT FALSE
);

-- Users table
CREATE TABLE users (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    user_type TEXT NOT NULL CHECK (user_type IN ('student', 'staff')),
    major_department TEXT,
    enrollment_hire_date TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

-- Borrow records table
CREATE TABLE borrow_records (
    record_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id TEXT NOT NULL,
    book_isbn TEXT NOT NULL,
    borrow_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP NOT NULL,
    return_date TIMESTAMP,
    is_returned BOOLEAN DEFAULT FALSE,
    fine_amount REAL DEFAULT 0.0,
    notes TEXT
);

-- Book reviews table
CREATE TABLE book_reviews (
    review_id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_isbn TEXT NOT NULL,
    user_id TEXT NOT NULL,
    rating REAL NOT NULL CHECK (rating >= 1.0 AND rating <= 5.0),
    comment TEXT,
    review_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_approved BOOLEAN DEFAULT TRUE
);
```

## Phát triển

### Thêm tính năng mới
1. Tạo model trong `com.library.model`
2. Thêm DAO trong `com.library.database`
3. Cập nhật controller trong `com.library.controller`
4. Tạo view trong `com.library.view`
5. Cập nhật CSS nếu cần

### Testing
```bash
mvn test
```

### Coding Standards
- Sử dụng Java naming conventions
- Comment code bằng tiếng Việt cho business logic
- Comment technical bằng tiếng Anh
- Tuân thủ Clean Code principles

## Đóng góp

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add some amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Tạo Pull Request

## Giấy phép

Dự án này được phát hành dưới giấy phép MIT. Xem file `LICENSE` để biết thêm chi tiết.

## Liên hệ

- **Email**: developer@example.com
- **GitHub**: [repository-url]
- **Documentation**: [docs-url]

## Changelog

### Version 1.0.0 (2025-08-17)
- ✅ Quản lý sách cơ bản
- ✅ Quản lý thành viên
- ✅ Hệ thống mượn/trả
- ✅ Tích hợp Google Books API
- ✅ Tạo mã QR
- ✅ Hệ thống gợi ý
- ✅ Giao diện người dùng hiện đại

### Kế hoạch phát triển
- 🔄 Hệ thống đánh giá và nhận xét
- 🔄 Báo cáo thống kê nâng cao
- 🔄 Xuất dữ liệu Excel/PDF
- 🔄 Hệ thống thông báo
- 🔄 Multi-language support
- 🔄 Dark mode
- 🔄 Mobile responsive (JavaFX Mobile)

## Screenshots

[Thêm screenshots của ứng dụng ở đây]

---

**Lưu ý**: Đây là phiên bản 1.0 của hệ thống. Một số tính năng có thể chưa hoàn thiện hoàn toàn và sẽ được cập nhật trong các phiên bản tiếp theo.
