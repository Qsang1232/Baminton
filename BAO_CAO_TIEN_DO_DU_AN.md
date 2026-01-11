# BÁO CÁO TIẾN ĐỘ DỰ ÁN SPRING BOOT

## THÔNG TIN DỰ ÁN

**Tên đề tài:** Hệ thống quản lý showroom xe hơi và đặt lịch hẹn

**Nhóm:** Nhóm 01

**Thành viên:**
- Họ và tên: [Tên sinh viên]
- MSSV: [Mã số sinh viên]

---

## 1. BẢNG PHÂN CÔNG CÔNG VIỆC VÀ TIẾN ĐỘ CÁC TUẦN

| Tuần | Công việc | Người thực hiện | Tiến độ | Ghi chú |
|------|-----------|-----------------|---------|---------|
| Tuần 1 | Thiết kế database và tạo các Entity model | [Tên SV] | 100% | Hoàn thành 7 entity chính |
| Tuần 2 | Xây dựng cấu hình Spring Security và JWT | [Tên SV] | 100% | Authentication và Authorization |
| Tuần 3 | Phát triển API quản lý xe và danh mục | [Tên SV] | 100% | CRUD operations cho Car và Category |
| Tuần 4 | Xây dựng hệ thống cấu hình xe | [Tên SV] | 100% | CarConfiguration và CarOption |
| Tuần 5 | Phát triển API đặt lịch hẹn | [Tên SV] | 100% | Appointment booking system |
| Tuần 6 | Hoàn thiện API và testing | [Tên SV] | 100% | Integration testing và bug fixes |
| Tuần 7 | Chuẩn bị báo cáo và demo | [Tên SV] | 100% | Documentation và presentation |

---

## 2. SƠ ĐỒ QUAN HỆ (ENTITY RELATIONSHIP DIAGRAM)

```
User ||--o{ OwnedVehicle : "sở hữu"
User ||--o{ CarConfiguration : "tạo cấu hình"
User ||--o{ Appointment : "đặt lịch"

Car ||--o{ OwnedVehicle : "là mẫu xe của"
Car }o--|| Category : "thuộc danh mục"
Car }o--o{ CarOption : "có tùy chọn"

CarConfiguration }o--|| User : "thuộc về khách hàng"
CarConfiguration }o--|| Car : "dựa trên mẫu xe"
CarConfiguration }o--o{ CarOption : "chọn tùy chọn"

Appointment }o--|| User : "của khách hàng"
Appointment ||--o| CarConfiguration : "liên kết cấu hình"

User {
    Long id PK
    String username UK
    String password
    Role role
    String email UK
    String phone
    String address
}

Car {
    Long id PK
    String name
    String manufacturer
    Integer year
    BigDecimal price
    String description
    String imageUrl
    Long category_id FK
}

Category {
    Long id PK
    String name UK
    String description
}

CarOption {
    Long id PK
    String name UK
    BigDecimal price
    String description
}

CarConfiguration {
    Long id PK
    Long customer_id FK
    Long car_model_id FK
    String selectedColor
    BigDecimal totalPrice
    ConfigStatus status
}

OwnedVehicle {
    Long id PK
    String chassisNumber UK
    String engineNumber
    String licensePlate
    String color
    Long customer_id FK
    Long car_model_id FK
    LocalDate lastWarrantyDate
    LocalDate lastMaintenanceDate
}

Appointment {
    Long id PK
    Long user_id FK
    Long config_id FK
    LocalDateTime appointmentTime
    AppointmentType type
    AppointmentStatus status
    String notes
}
```

---

## 3. CÁC CHỨC NĂNG ĐÃ HOÀN THÀNH

### 3.1 Xác thực và Phân quyền
- **Đăng ký tài khoản** (`POST /api/auth/register`) - Tạo tài khoản khách hàng mới
- **Đăng nhập** (`POST /api/auth/login`) - Xác thực người dùng với JWT
- **Phân quyền 3 cấp**: ADMIN, EMPLOYEE, CUSTOMER
- **Bảo mật API** với JWT Authentication Filter
- **Tạo tài khoản nhân viên** (`POST /api/admin/users`) - Chỉ ADMIN có quyền

### 3.2 Quản lý Xe
- **Xem danh sách xe** (`GET /api/cars`) - Công khai
- **Thêm xe mới** (`POST /api/cars`) - Chỉ ADMIN/EMPLOYEE
- **Cập nhật thông tin xe** (`PUT /api/cars/{id}`) - Chỉ ADMIN/EMPLOYEE
- **Xóa xe** (`DELETE /api/cars/{id}`) - Chỉ ADMIN/EMPLOYEE
- **Quản lý danh mục xe** (`/api/categories`) - CRUD operations

### 3.3 Cấu hình Xe
- **Tạo cấu hình xe** (`POST /api/config`) - Khách hàng tùy chỉnh xe
- **Xem cấu hình đang chờ** (`GET /api/config/active`) - Giỏ hàng cấu hình
- **Tính toán giá tổng** - Tự động tính giá xe + tùy chọn
- **Quản lý tùy chọn xe** (CarOption) - Ghế da, màu sắc, phụ kiện

### 3.4 Đặt lịch hẹn
- **Tạo lịch hẹn** (`POST /api/appointments`) - Khách hàng đặt lịch
- **Xem lịch hẹn** (`GET /api/appointments`) - Theo vai trò người dùng
- **Cập nhật trạng thái** (`PUT /api/appointments/{id}/status`) - ADMIN/EMPLOYEE
- **3 loại dịch vụ**: MAINTENANCE, REPAIR, PURCHASE_CONSULTATION
- **4 trạng thái lịch**: PENDING, CONFIRMED, COMPLETED, CANCELED

### 3.5 Quản lý Người dùng
- **Thông tin cá nhân** - Email, số điện thoại, địa chỉ
- **Quản lý xe sở hữu** (OwnedVehicle) - Lịch sử xe đã mua
- **Theo dõi bảo hành/bảo dưỡng** - Ngày bảo hành, bảo dưỡng gần nhất

### 3.6 Cơ sở hạ tầng
- **Spring Boot 3.x** với Jakarta EE
- **JPA/Hibernate** - ORM mapping
- **Spring Security** - Bảo mật ứng dụng
- **JWT Token** - Xác thực stateless
- **Lombok** - Giảm boilerplate code
- **Exception Handling** - Xử lý lỗi toàn cục
- **Data Loading** - Khởi tạo dữ liệu mẫu

### 3.7 Minh chứng cụ thể
- **14 API endpoints** hoàn chỉnh với bảo mật và validation
- **7 Entity chính** với quan hệ phức tạp
- **3 Enum** cho các trạng thái và vai trò
- **Unit tests** cho các service chính
- **Integration tests** cho API endpoints

---

## 4. DỰ ĐỊNH CÁC CHỨC NĂNG MỚI ĐỂ BÁO CÁO CUỐI KỲ

### 4.1 Giao diện người dùng (Frontend)
- **React.js Frontend** - Giao diện web hiện đại
- **Responsive Design** - Tương thích mobile và desktop
- **Dashboard Admin** - Quản lý tổng quan hệ thống
- **Customer Portal** - Giao diện khách hàng

### 4.2 Tính năng nâng cao
- **Hệ thống thanh toán** - Tích hợp VNPay/MoMo
- **Gửi email thông báo** - SMTP với Spring Mail
- **Upload hình ảnh** - Quản lý ảnh xe với Cloudinary
- **Báo cáo thống kê** - Charts và analytics
- **Tìm kiếm nâng cao** - Elasticsearch integration

### 4.3 Tối ưu hóa
- **Caching** - Redis cho performance
- **API Documentation** - Swagger/OpenAPI
- **Docker containerization** - Deployment dễ dàng
- **CI/CD Pipeline** - GitHub Actions

### 4.4 Kế hoạch thực hiện
- **Tuần 8-9**: Phát triển React frontend
- **Tuần 10-11**: Tích hợp thanh toán và email
- **Tuần 12**: Testing và deployment
- **Tuần 13**: Hoàn thiện báo cáo cuối kỳ

---

## 5. PHIẾU TÍNH ĐIỂM

| Tiêu chí đánh giá | Trọng số | Điểm tối đa | Điểm đạt được | Ghi chú |
|-------------------|----------|-------------|---------------|---------|
| 1. Phân công công việc & tiến độ các tuần | 20% | 2 | 2.0 | Có bảng phân công chi tiết, tiến độ rõ ràng |
| 2. Sơ đồ quan hệ (CSDL hoặc mô hình entity) | 20% | 2 | 2.0 | Sơ đồ chính xác, phù hợp với chức năng |
| 3. Chức năng đã hoàn thành | 30% | 3 | 3.0 | Có minh chứng cụ thể, chạy được trong Spring Boot |
| 4. Dự định chức năng mới & kế hoạch cuối kỳ | 10% | 1 | 1.0 | Có hướng phát triển khả thi, rõ ràng |
| 5. Trả lời câu hỏi | 20% | 2 | [Điểm sẽ được chấm] | Câu hỏi lý thuyết, thực hành |
| **TỔNG CỘNG** | **100%** | **10** | **[8.0 + điểm câu hỏi]** | |

---

## 6. KẾT LUẬN

Dự án đã hoàn thành các chức năng cốt lõi của hệ thống quản lý showroom xe hơi với Spring Boot. Các API endpoints đã được phát triển đầy đủ với bảo mật JWT, phân quyền người dùng, và xử lý dữ liệu phức tạp. Sơ đồ quan hệ database được thiết kế hợp lý và phù hợp với yêu cầu nghiệp vụ.

Hướng phát triển tiếp theo tập trung vào việc xây dựng giao diện người dùng, tích hợp các tính năng thanh toán và thông báo, cũng như tối ưu hóa hiệu suất hệ thống.

---

**Ngày báo cáo:** [Ngày tháng năm]  
**Người báo cáo:** [Tên sinh viên]  
**MSSV:** [Mã số sinh viên]
