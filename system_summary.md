# BẢN TÓM TẮT HỆ THỐNG HIỆN TẠI (DỰ ÁN QLTLNB)

Tài liệu này tổng hợp toàn bộ thông tin chi tiết về cấu trúc thư mục, router, API, cơ sở dữ liệu và các cập nhật mới nhất của dự án **Quản lý Tài liệu Nội bộ và Văn bản Pháp luật (QLTLNB)** để phục vụ công tác vẽ sơ đồ (UML, DFD) và viết báo cáo Niên luận.

---

## 1. Cấu Trúc Danh Mục File & Route Frontend (Vue 3)

Ứng dụng Frontend được xây dựng bằng **Vue 3** (SFC, Composition API, `<script setup>`), sử dụng **Vite** làm công cụ đóng gói, **Pinia** để quản lý trạng thái, và **Axios** để giao tiếp với Backend.

### 1.1 Danh sách file View & Component chính
*   **Thư mục Layouts (`src/layouts/`)**:
    *   [MainLayout.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/layouts/MainLayout.vue): Layout chính của hệ thống sau khi đăng nhập, bao gồm Sidebar (thanh điều hướng) và Topbar (thanh công cụ trên chứa tên người dùng, nút đăng xuất, và biểu tượng thông báo).
*   **Thư mục Views (`src/views/`)**:
    *   [LoginView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/LoginView.vue): Trang đăng nhập hệ thống.
    *   [DashboardView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/DashboardView.vue): Bảng điều khiển tổng quan (hiển thị biểu đồ thống kê tài liệu, công việc, và nhật ký hoạt động mới nhất).
    *   [ApprovalView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/ApprovalView.vue): Trang phê duyệt tài liệu nội bộ (dành cho Trưởng phòng/Admin).
    *   [ActivityLogView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/ActivityLogView.vue): Trang xem lịch sử nhật ký hoạt động của hệ thống (dành cho Trưởng phòng/Admin).
    *   [NotificationView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/NotificationView.vue): Trang hiển thị tất cả thông báo của người dùng hiện tại.
    *   [ProfileView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/ProfileView.vue): Trang xem thông tin hồ sơ tài khoản cá nhân.
    *   **Thư mục `views/documents/`**:
        *   [DocumentListView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/documents/DocumentListView.vue): Trang quản lý và tìm kiếm văn bản pháp luật, tài liệu nội bộ.
        *   [DocumentDetailView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/documents/DocumentDetailView.vue): Trang xem chi tiết tài liệu, quản lý lịch sử phiên bản, phân quyền và duyệt tài liệu.
    *   **Thư mục `views/cases/`**:
        *   [CaseListView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/cases/CaseListView.vue): Quản lý danh sách hồ sơ vụ việc pháp lý.
        *   [CaseDetailView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/cases/CaseDetailView.vue): Xem chi tiết vụ việc và danh sách tài liệu liên quan đến vụ việc đó.
    *   **Thư mục `views/clients/`**:
        *   [ClientListView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/clients/ClientListView.vue): Quản lý thông tin khách hàng (Cá nhân/Tổ chức).
        *   [ClientDetailView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/clients/ClientDetailView.vue): Xem chi tiết khách hàng và lịch sử vụ việc của khách hàng.
    *   **Thư mục `views/admin/`**:
        *   [UserListView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/admin/UserListView.vue): Quản lý tài khoản cán bộ/nhân viên (chỉ dành cho Admin).
        *   [DepartmentListView.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/views/admin/DepartmentListView.vue): Quản lý danh mục phòng ban (chỉ dành cho Admin).
*   **Thư mục Components (`src/components/`)**:
    *   `documents/DocumentModal.vue`: Popup thêm mới hoặc sửa thông tin siêu dữ liệu tài liệu.
    *   `documents/DocumentPreviewModal.vue`: Popup tích hợp khung xem trước tài liệu trực tuyến (PDF, hình ảnh, hoặc file Word).
    *   `common/ConfirmModal.vue`: Popup dùng chung để xác nhận hành động nguy hiểm (xóa, phê duyệt, từ chối).
    *   `common/Pagination.vue`: Component phân trang dữ liệu.
    *   `common/StatusBadge.vue`: Huy hiệu màu sắc hiển thị trạng thái tài liệu (`nhap`, `cho_duyet`, `da_duyet`, `tu_choi`).
    *   `common/LoadingSpinner.vue` & `ApiErrorMessage.vue`: Xử lý trạng thái tải dữ liệu và thông báo lỗi tập trung.

### 1.2 Cấu hình định tuyến Router (`src/router/index.js`)
Các route được phân nhóm theo quyền truy cập (`requiresGuest`, `requiresAuth`) và vai trò (`allowedRoles`):

| Đường dẫn (Path) | Tên Route (Name) | Component giao diện tương ứng | Phân quyền bảo mật (Meta) |
| :--- | :--- | :--- | :--- |
| `/login` | `login` | `LoginView.vue` | `requiresGuest: true` (chưa đăng nhập) |
| `/` | *N/A (Layout)* | `MainLayout.vue` | `requiresAuth: true` (yêu cầu đăng nhập) |
| `├─` (không dẫn) | `dashboard` | `DashboardView.vue` | Đăng nhập bất kỳ vai trò nào |
| `├─ documents` | `documents` | `documents/DocumentListView.vue` | Đăng nhập bất kỳ vai trò nào |
| `├─ documents/:id` | `document-detail` | `documents/DocumentDetailView.vue` | Đăng nhập bất kỳ vai trò nào |
| `├─ cases` | `cases` | `cases/CaseListView.vue` | Đăng nhập bất kỳ vai trò nào |
| `├─ cases/:id` | `case-detail` | `cases/CaseDetailView.vue` | Đăng nhập bất kỳ vai trò nào |
| `├─ clients` | `clients` | `clients/ClientListView.vue` | Đăng nhập bất kỳ vai trò nào |
| `├─ clients/:id` | `client-detail` | `clients/ClientDetailView.vue` | Đăng nhập bất kỳ vai trò nào |
| `├─ approval` | `approval` | `ApprovalView.vue` | `allowedRoles: ['TRUONG_PHONG', 'ADMIN']` |
| `├─ notifications` | `notifications` | `NotificationView.vue` | Đăng nhập bất kỳ vai trò nào |
| `├─ activity-logs` | `activity-logs` | `ActivityLogView.vue` | `allowedRoles: ['TRUONG_PHONG', 'ADMIN']` |
| `├─ profile` | `profile` | `ProfileView.vue` | Đăng nhập bất kỳ vai trò nào |
| `├─ admin/users` | `admin-users` | `admin/UserListView.vue` | `allowedRoles: ['ADMIN']` |
| `└─ admin/departments`| `admin-departments`| `admin/DepartmentListView.vue` | `allowedRoles: ['ADMIN']` |
| `*` | *N/A* | Chuyển hướng (redirect) về `/` | Bất kỳ |

### 1.3 Store Pinia (`src/stores/`)
*   `auth.js`: Quản lý thông tin đăng nhập, thông tin người dùng hiện tại (`user`) và lưu trữ JWT token (`token`) trong `localStorage` để duy trì phiên làm việc.
*   `notifications.js`: Quản lý danh sách thông báo và số lượng thông báo chưa đọc của tài khoản hiện tại.

### 1.4 API Axios (`src/api/`)
*   `auth.js`: Thiết lập Axios Instance chính với cấu hình `baseURL: 'http://localhost:8080/api'`. Tự động dùng **Interceptor** đính kèm Header `Authorization: Bearer <Token>` vào tất cả request gửi lên Backend và tự động xử lý khi Token hết hạn (nếu gặp lỗi `401 Unauthorized` thì xóa token và chuyển hướng về màn hình đăng nhập). Khai báo các API cơ bản: `dangNhap` (đăng nhập) và `danhSachTaiLieu` (lấy nhanh danh sách).
*   `admin.js`: CRUD người dùng (`/users`), CRUD phòng ban (`/departments`), CRUD danh mục (`/categories`), và CRUD loại tài liệu (`/doc-types`).
*   `activityLogs.js`: Lấy danh sách nhật ký hoạt động (`/activity-logs`).
*   `cases.js`: CRUD vụ việc pháp lý (`/cases`).
*   `clients.js`: CRUD khách hàng (`/clients`).
*   `documents.js`: CRUD tài liệu, download file, xem trước file (`/documents/{id}/preview`), và quy trình submit lên duyệt.
*   `notifications.js`: Các API xử lý thông báo của người dùng (`/notifications`).
*   `permissions.js`: Cấp/thu hồi quyền xem, sửa, tải tài liệu nội bộ (`/documents/{id}/permissions`).
*   `search.js`: Các API tìm kiếm nâng cao theo bộ lọc đa tiêu chí và tìm kiếm toàn cục (đa bảng) `/api/search/global`.
*   `versions.js`: Xem lịch sử các phiên bản tệp tin cũ của tài liệu và tải xuống.

---

## 2. Cấu Trúc Backend (Spring Boot 3)

Backend được lập trình trên **Java 21 + Spring Boot 3.2.5** theo mô hình phân lớp chuẩn: `Controller` -> `Service` -> `Repository` -> `Entity`.

### 2.1 REST Controllers & API Endpoints

Mọi API của hệ thống đều có tiền tố `/api` và trả về định dạng chung thông qua class [ApiResponse](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/dto/ApiResponse.java) (`success`, `message`, `data`, `errors`, `timestamp`).

| REST Controller Class | HTTP Method | API Path | Chức Năng Nghiệp Vụ |
| :--- | :--- | :--- | :--- |
| **`AuthController`** | `POST` | `/api/auth/login` | Đăng nhập tài khoản, trả về JWT Token |
| **`DocumentController`** | `GET` | `/api/documents` | Lấy danh sách tài liệu phân trang & bộ lọc |
| | `GET` | `/api/documents/{id}` | Lấy chi tiết siêu dữ liệu tài liệu |
| | `POST` | `/api/documents` | Tạo mới tài liệu (Upload kèm file qua Multipart Form) |
| | `PUT` | `/api/documents/{id}` | Cập nhật siêu dữ liệu tài liệu |
| | `DELETE` | `/api/documents/{id}` | Xóa mềm tài liệu |
| | `POST` | `/api/documents/{id}/upload`| Upload thay thế file cũ (Tạo một phiên bản mới) |
| | `GET` | `/api/documents/{id}/download`| Tải tệp tin vật lý đính kèm của tài liệu |
| | `GET` | `/api/documents/{id}/preview`| Xem trực tuyến tệp tin vật lý (PDF, Image, Word...) |
| | `POST` | `/api/documents/{id}/submit` | Chuyển trạng thái tài liệu thành chờ duyệt |
| **`VersionController`** | `GET` | `/api/documents/{documentId}/versions` | Lấy lịch sử phiên bản của tài liệu |
| | `GET` | `/api/documents/{documentId}/versions/{versionId}` | Chi tiết một phiên bản tài liệu |
| | `GET` | `/api/documents/{documentId}/versions/{versionId}/download` | Tải về tệp tin của phiên bản cũ đó |
| **`ApprovalController`** | `POST` | `/api/documents/{documentId}/approval/submit` | Gửi yêu cầu duyệt tài liệu |
| | `POST` | `/api/documents/{documentId}/approval/approve` | Phê duyệt tài liệu -> Trạng thái đổi thành `DA_DUYET` |
| | `POST` | `/api/documents/{documentId}/approval/reject` | Từ chối tài liệu -> Trạng thái đổi thành `TU_CHOI` |
| | `GET` | `/api/documents/{documentId}/approval/history` | Xem lịch sử toàn bộ các bước duyệt tài liệu |
| **`PermissionController`**| `POST` | `/api/documents/{documentId}/permissions` | Cấp quyền cho người dùng/bộ phận khác |
| | `GET` | `/api/documents/{documentId}/permissions` | Danh sách các quyền đã được cấp của tài liệu |
| | `DELETE`| `/api/documents/{documentId}/permissions/{id}` | Thu hồi quyền truy cập |
| **`CaseController`** | `GET` | `/api/cases` | Danh sách vụ việc (lọc theo trang thái, loại) |
| | `GET` | `/api/cases/{id}` | Lấy chi tiết vụ việc |
| | `POST` | `/api/cases` | Thêm mới vụ việc pháp lý |
| | `PUT` | `/api/cases/{id}` | Cập nhật hồ sơ vụ việc |
| | `DELETE`| `/api/cases/{id}` | Xóa mềm hồ sơ vụ việc |
| | `GET` | `/api/cases/{id}/documents` | Danh sách tài liệu thuộc vụ việc này |
| **`ClientController`** | `GET` | `/api/clients` | Lọc & Lấy danh sách khách hàng |
| | `GET` | `/api/clients/{id}` | Lấy thông tin chi tiết khách hàng |
| | `POST` | `/api/clients` | Thêm khách hàng mới |
| | `PUT` | `/api/clients/{id}` | Cập nhật thông tin khách hàng |
| | `DELETE`| `/api/clients/{id}` | Xóa mềm thông tin khách hàng |
| **`LookupController`** | `GET` | `/api/categories` | Trả về danh sách Danh mục (cho dropdown) |
| | `GET` | `/api/doc-types` | Trả về các Loại tài liệu pháp lý |
| | `GET` | `/api/departments` | Trả về danh sách Bộ phận/Phòng ban |
| | `GET` | `/api/users` | Trả về danh sách Tài khoản người dùng |
| **`NotificationController`**| `GET` | `/api/notifications` | Lấy tất cả thông báo của user đang đăng nhập |
| | `PUT` | `/api/notifications/{id}/read`| Đánh dấu một thông báo đã đọc |
| | `PUT` | `/api/notifications/read-all`| Đánh dấu tất cả thông báo đã đọc |
| | `GET` | `/api/notifications/unread-count`| Đếm số thông báo chưa đọc của user |
| **`SearchController`** | `GET` | `/api/search/documents` | Tìm kiếm nâng cao tài liệu (nhiều thuộc tính) |
| | `GET` | `/api/search/clients` | Tìm kiếm khách hàng theo từ khóa và loại |
| | `GET` | `/api/search/cases` | Tìm kiếm vụ việc nâng cao |
| | `GET` | `/api/search/global` | Tìm kiếm toàn cục đa đối tượng (Multi-table search) |
| **`ActivityLogController`**| `GET` | `/api/activity-logs` | Lọc và xem lịch sử nhật ký hệ thống |

### 2.2 Các JPA Entities & Mối quan hệ chính
Hệ thống có 17 class Entity tương ứng với các bảng cơ sở dữ liệu:
*   [VaiTro](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/VaiTro.java): Mapped bảng `VAI_TRO`. Vai trò người dùng (`ADMIN`, `TRUONG_PHONG`, `NHAN_VIEN`).
*   [BoPhan](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/BoPhan.java): Mapped bảng `BO_PHAN`. Phòng ban nghiệp vụ (ví dụ: Phòng Dân sự, Phòng Đất đai...).
*   [NguoiDung](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/NguoiDung.java): Mapped bảng `TAI_KHOAN_NGUOI_DUNG`. Tài khoản cán bộ. Có quan hệ `ManyToOne` với `BoPhan` và `VaiTro`.
*   [KhachHang](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/KhachHang.java): Mapped bảng `KHACH_HANG`. Thông tin khách hàng, lưu `KH_loai` dưới dạng Enum `LoaiKhachHang` (`CA_NHAN` / `TO_CHUC`).
*   [VuViec](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/VuViec.java): Mapped bảng `VU_VIEC`. Vụ việc pháp lý thụ lý, quan hệ `ManyToOne` với `KhachHang` và `NguoiDung` (cán bộ phụ trách).
*   [DanhMuc](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/DanhMuc.java): Mapped bảng `DANH_MUC`. Phân nhóm tài liệu.
*   [LoaiTaiLieuPhapLy](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/LoaiTaiLieuPhapLy.java): Mapped bảng `LOAI_TAI_LIEU_PHAP_LY`. Thể loại văn bản (Luật, Nghị định, Quyết định...).
*   [TaiLieu](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/TaiLieu.java): Mapped bảng `TAI_LIEU`. Thực thể cốt lõi, lưu trữ văn bản pháp luật và hồ sơ nội bộ. Có quan hệ `ManyToOne` với `DanhMuc`, `LoaiTaiLieuPhapLy`, `VuViec` (nếu thuộc vụ việc nào đó), và `NguoiDung` (người tải lên).
*   [PhienBanTaiLieu](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/PhienBanTaiLieu.java): Mapped bảng `PHIEN_BAN_TAI_LIEU`. Quản lý lịch sử thay đổi file của từng tài liệu. Có quan hệ `ManyToOne` với `TaiLieu` (`TL_id`).
*   [DocumentPermission](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/DocumentPermission.java): Mapped bảng `tai_lieu_quyen_moi`. Quản lý phân quyền tài liệu động theo người dùng (`NguoiDung`) hoặc bộ phận (`BoPhan`).
*   [DocumentApproval](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/DocumentApproval.java): Mapped bảng `duyet_tai_lieu_moi`. Ghi nhận lịch sử và kết quả phê duyệt tài liệu nội bộ.
*   [ThongBao](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/ThongBao.java): Mapped bảng `thong_bao_moi`. Quản lý thông báo gửi cho từng cá nhân.
*   [LichSuHoatDong](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/entity/LichSuHoatDong.java): Mapped bảng `lich_su_hoat_dong_moi`. Lưu vết hoạt động người dùng trên hệ thống.
*   *Lưu ý*: Các entity `DuyetTaiLieu`, `QuyenTruyCap` là các entity ánh xạ tới bảng cũ (legacy) trong thiết kế trước đó của dự án. Hệ thống hiện thời ưu tiên sử dụng các thực thể và bảng kết thúc bằng hậu tố `_moi` để tương thích tốt với API Vue 3.

### 2.3 Lớp Services chính
*   `AuthService`: Logic xác thực tài khoản, so khớp BCrypt mật khẩu, cấp JWT Token.
*   `DocumentService`: Logic chính về CRUD tài liệu, quản lý tệp tin, kiểm tra quyền hạn của người dùng trước khi cho xem/sửa/tải tài liệu.
*   `VersionService`: Tạo, lưu trữ và nâng cấp các phiên bản tệp tin tài liệu.
*   `FileStorageService`: Quản lý lưu trữ vật lý tệp tin trên ổ đĩa Server (đường dẫn `/app/documents`).
*   `CaseService` & `ClientService`: Logic xử lý nghiệp vụ vụ việc và đối tác khách hàng.
*   `SearchService`: Xây dựng các truy vấn tìm kiếm phức tạp.
*   `PermissionService`: Thiết lập phân quyền cho nhân viên.
*   `NotificationService`: Tạo và gửi thông báo hệ thống tự động.
*   `ActivityLogService`: Ghi log kiểm toán hoạt động của cán bộ.
*   `DocumentImportService` & `DocumentScanService`: Tự động quét và import metadata từ crawler vào CSDL.

### 2.4 Cấu trúc Security, JWT & Specification
*   **Security Config (`config/SecurityConfig.java`)**:
    *   Sử dụng **Spring Security + JWT**. Không dùng Session (Stateless Session Policy).
    *   Cho phép truy cập tự do (Anonymous) đối với API đăng nhập `/api/auth/login` và API xem trước tài liệu công khai.
    *   Chặn mọi HTTP request khác, yêu cầu cấu hình xác thực JWT hợp lệ. Phân quyền chi tiết dựa trên Authority vai trò của người dùng (ví dụ: route `/api/activity-logs` chỉ cho `ADMIN` hoặc `TRUONG_PHONG`).
    *   Đăng ký bộ lọc `JwtAuthenticationFilter` chạy trước bộ lọc của Spring Security.
*   **JWT Classes (`security/`)**:
    *   `JwtTokenProvider`: Chịu trách nhiệm sinh token JWT (chứa thông tin đăng nhập, vai trò, thời gian sống) và giải mã token để xác thực request.
    *   `JwtAuthenticationFilter`: Đọc token từ header `Authorization: Bearer ...`, gọi `CustomUserDetailsService` để load thông tin người dùng, sau đó đóng gói đối tượng `UsernamePasswordAuthenticationToken` nạp vào `SecurityContextHolder`.
*   **Specification (Truy vấn động)**:
    *   Thay vì viết các truy vấn tĩnh phức tạp hoặc dùng nhiều câu lệnh `if-else` nối chuỗi SQL, dự án áp dụng **JPA Specification** sử dụng Criteria API.
    *   Các interface Repository kế thừa `JpaSpecificationExecutor<T>`.
    *   Các Service (`SearchService`, `DocumentService`, `ClientService`, `CaseService`, `ActivityLogService`) tự động build đối tượng `Specification<T>` thông qua biểu thức Lambda động dựa trên tham số filter truyền lên từ Frontend.

---

## 3. Danh Sách Bảng Cơ Sở Dữ Liệu (MySQL Schema)

Cơ sở dữ liệu của dự án có tên `qltl_luat_dan_su`, bao gồm **17 bảng**. Dưới đây là chi tiết các bảng, khóa chính (PK), khóa ngoại (FK), các cột dữ liệu chính và trạng thái Enum.

### 3.1 Bảng VAI_TRO
*   **Tên bảng**: `VAI_TRO`
*   **Khóa chính (PK)**: `VT_id` (INT, Auto Increment)
*   **Các cột chính**: `VT_ten` (VARCHAR(100), Unique - ví dụ: `ADMIN`, `TRUONG_PHONG`, `NHAN_VIEN`), `VT_moTa` (TEXT)

### 3.2 Bảng BO_PHAN
*   **Tên bảng**: `BO_PHAN`
*   **Khóa chính (PK)**: `BP_id` (INT, Auto Increment)
*   **Các cột chính**: `BP_ten` (VARCHAR(150), Unique - ví dụ: `Phong Dan su`, `Phong Dat dai`), `BP_moTa` (TEXT)

### 3.3 Bảng TAI_KHOAN_NGUOI_DUNG
*   **Tên bảng**: `TAI_KHOAN_NGUOI_DUNG`
*   **Khóa chính (PK)**: `ND_id` (INT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `BP_id` tham chiếu đến `BO_PHAN(BP_id)`
    *   `VT_id` tham chiếu đến `VAI_TRO(VT_id)`
*   **Các cột chính**: `ND_hoTen` (TEXT), `ND_taiKhoan` (VARCHAR(100), Unique), `ND_matKhau` (TEXT - lưu Bcrypt Hash), `ND_email` (VARCHAR(150), Unique), `ND_soLanSai` (INT), `ND_chuyenMon` (TEXT), `ND_chungChi` (TEXT)
*   **Trạng thái**: `ND_trangThaiTK` (BOOLEAN - `TRUE`: Hoạt động, `FALSE`: Bị khóa)

### 3.4 Bảng KHACH_HANG
*   **Tên bảng**: `KHACH_HANG`
*   **Khóa chính (PK)**: `KH_id` (INT, Auto Increment)
*   **Các cột chính**: `KH_ten` (TEXT), `KH_CCCD_MST` (VARCHAR(50), Unique - số CCCD hoặc mã số thuế), `KH_sdt` (TEXT), `KH_diaChi` (TEXT), `KH_email` (TEXT), `KH_ngayTao` (DATETIME)
*   **Trạng thái Enum**: `KH_loai` (TEXT - `CA_NHAN` hoặc `TO_CHUC`)

### 3.5 Bảng VU_VIEC
*   **Tên bảng**: `VU_VIEC`
*   **Khóa chính (PK)**: `VV_id` (INT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `KH_id` tham chiếu đến `KHACH_HANG(KH_id)`
    *   `ND_phuTrach_id` tham chiếu đến `TAI_KHOAN_NGUOI_DUNG(ND_id)`
*   **Các cột chính**: `VV_ten` (VARCHAR(200), Unique), `VV_loai` (TEXT), `VV_ngayMo` (DATETIME), `VV_ngayDong` (DATETIME), `VV_ghiChu` (TEXT)
*   **Trạng thái Enum**: `VV_trangThai` (TEXT - `MOI_TIEP_NHAN` / `DANG_XU_LY` / `DA_DONG`)

### 3.6 Bảng DANH_MUC
*   **Tên bảng**: `DANH_MUC`
*   **Khóa chính (PK)**: `DM_id` (INT, Auto Increment)
*   **Các cột chính**: `DM_ten` (VARCHAR(150), Unique)

### 3.7 Bảng LOAI_TAI_LIEU_PHAP_LY
*   **Tên bảng**: `LOAI_TAI_LIEU_PHAP_LY`
*   **Khóa chính (PK)**: `LTLPL_id` (INT, Auto Increment)
*   **Các cột chính**: `LTLPL_ten` (VARCHAR(150), Unique), `LTLPL_moTa` (TEXT)

### 3.8 Bảng TAI_LIEU
*   **Tên bảng**: `TAI_LIEU`
*   **Khóa chính (PK)**: `TL_id` (INT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `DM_id` tham chiếu đến `DANH_MUC(DM_id)`
    *   `LTLPL_id` tham chiếu đến `LOAI_TAI_LIEU_PHAP_LY(LTLPL_id)`
    *   `VV_id` tham chiếu đến `VU_VIEC(VV_id)` (Có thể NULL)
    *   `ND_nguoiTao_id` tham chiếu đến `TAI_KHOAN_NGUOI_DUNG(ND_id)` (Có thể NULL)
*   **Các cột chính**: `TL_ten` (TEXT), `TL_soHieu` (VARCHAR(100)), `TL_duongDan` (TEXT - đường dẫn lưu trữ), `TL_dinhDang` (TEXT - `.docx`, `.pdf`...), `TL_dungLuong` (BIGINT), `TL_nguoiTao` (TEXT - lưu tên hoặc 'crawler'), `TL_ngayTao` (DATETIME), `TL_ngayBanHanh` (DATE), `TL_daXoa` (BOOLEAN), `TL_ngayHetHan` (DATETIME)
*   **Trạng thái Enum**: `TL_baoMat` (TEXT - `NOI_BO` / `CONG_KHAI`)
*   *Lưu ý*: Trạng thái phê duyệt của tài liệu không lưu trực tiếp tại bảng này mà được trích xuất từ trạng thái mới nhất của bảng `duyet_tai_lieu_moi` (hoặc `DUYET_TAI_LIEU`).

### 3.9 Bảng PHIEN_BAN_TAI_LIEU
*   **Tên bảng**: `PHIEN_BAN_TAI_LIEU`
*   **Khóa chính (PK)**: `PBTL_id` (INT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `TL_id` tham chiếu đến `TAI_LIEU(TL_id)`
    *   `ND_update_id` tham chiếu đến `TAI_KHOAN_NGUOI_DUNG(ND_id)`
*   **Các cột chính**: `PBTL_maPhienBan` (TEXT - ví dụ: "v1", "v2"), `PBTL_nguoiUpdate` (TEXT), `PBTL_timeUpdate` (DATETIME), `PBTL_ghiChu` (TEXT), `PBTL_duongDan` (TEXT), `PBTL_kichCo` (BIGINT), `PBTL_dinhDang` (VARCHAR(50))

### 3.10 Bảng LICH_SU_HOAT_DONG (Legacy)
*   **Tên bảng**: `LICH_SU_HOAT_DONG`
*   **Khóa chính (PK)**: `LS_id` (INT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `ND_id` tham chiếu đến `TAI_KHOAN_NGUOI_DUNG(ND_id)`
    *   `TL_id` tham chiếu đến `TAI_LIEU(TL_id)`
*   **Các cột chính**: `LS_hoatDong` (TEXT), `LS_thoiGianSua` (DATETIME), `LS_diaChiIP` (TEXT)

### 3.11 Bảng QUYEN_TRUY_CAP (Legacy)
*   **Tên bảng**: `QUYEN_TRUY_CAP`
*   **Khóa chính (PK)**: `QTC_id` (INT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `TL_id` tham chiếu đến `TAI_LIEU(TL_id)`
    *   `ND_id` tham chiếu đến `TAI_KHOAN_NGUOI_DUNG(ND_id)`
*   **Các cột chính**: `QTC_loaiQuyen` (TEXT), `QTC_thoiHan` (DATETIME)

### 3.12 Bảng DUYET_TAI_LIEU (Legacy)
*   **Tên bảng**: `DUYET_TAI_LIEU`
*   **Khóa chính (PK)**: `DTL_id` (INT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `TL_id` tham chiếu đến `TAI_LIEU(TL_id)`
    *   `ND_duyet_id` tham chiếu đến `TAI_KHOAN_NGUOI_DUNG(ND_id)`
*   **Các cột chính**: `DTL_ghiChu` (TEXT), `DTL_timeDuyet` (DATETIME)
*   **Trạng thái Enum**: `DTL_trangThai` (TEXT - `NHAP` / `CHO_DUYET` / `DA_DUYET` / `TU_CHOI`)

### 3.13 Bảng THONG_BAO (Legacy)
*   **Tên bảng**: `THONG_BAO`
*   **Khóa chính (PK)**: `TB_id` (INT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `ND_id` tham chiếu đến `TAI_KHOAN_NGUOI_DUNG(ND_id)`
    *   `TL_id` tham chiếu đến `TAI_LIEU(TL_id)`
*   **Các cột chính**: `TB_tieuDe` (TEXT), `TB_noiDung` (TEXT), `TB_trangThaiDoc` (BOOLEAN)

### 3.14 Bảng tai_lieu_quyen_moi (Bảng Quyền Hiện Tại)
*   **Tên bảng**: `tai_lieu_quyen_moi`
*   **Khóa chính (PK)**: `id` (BIGINT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `tai_lieu_id` tham chiếu đến `TAI_LIEU(TL_id)`
    *   `nguoi_dung_id` tham chiếu đến `TAI_KHOAN_NGUOI_DUNG(ND_id)` (Có thể NULL)
    *   `bo_phan_id` tham chiếu đến `BO_PHAN(BP_id)` (Có thể NULL - cấp quyền theo phòng ban)
*   **Các cột chính**: `ngay_het_han` (DATE)
*   **Trạng thái Enum**: `loai_quyen` (VARCHAR(50) - `XEM` / `TAI` / `SUA` / `XOA`)

### 3.15 Bảng duyet_tai_lieu_moi (Bảng Duyệt Hiện Tại)
*   **Tên bảng**: `duyet_tai_lieu_moi`
*   **Khóa chính (PK)**: `id` (BIGINT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `tai_lieu_id` tham chiếu đến `TAI_LIEU(TL_id)`
    *   `nguoi_duyet_id` tham chiếu đến `TAI_KHOAN_NGUOI_DUNG(ND_id)`
*   **Các cột chính**: `ghi_chu` (VARCHAR(500)), `time_approve` (DATETIME)
*   **Trạng thái/Hành động**: `hanhDong` (VARCHAR(100) - `GUI_DUYET` / `PHE_DUYET` / `TU_CHOI`)

### 3.16 Bảng thong_bao_moi (Bảng Thông Báo Hiện Tại)
*   **Tên bảng**: `thong_bao_moi`
*   **Khóa chính (PK)**: `id` (BIGINT, Auto Increment)
*   **Khóa ngoại (FK)**:
    *   `nguoi_nhan_id` tham chiếu đến `TAI_KHOAN_NGUOI_DUNG(ND_id)`
*   **Các cột chính**: `tieu_de` (VARCHAR(255)), `noi_dung` (VARCHAR(1000)), `ngay_tao` (DATETIME), `da_doc` (BOOLEAN)
*   **Trạng thái Enum**: `loai` (VARCHAR(50) - `YEU_CAU_DUYET`, `DA_DUYET`, `TU_CHOI`, `DUOC_CAP_QUYEN`, `HET_HAN_QUYEN`)

### 3.17 Bảng lich_su_hoat_dong_moi (Bảng Nhật Ký Hiện Tại)
*   **Tên bảng**: `lich_su_hoat_dong_moi`
*   **Khóa chính (PK)**: `id` (BIGINT, Auto Increment)
*   **Các cột chính**: `nguoi_dung_id` (BIGINT), `tai_lieu_id` (BIGINT), `mo_ta` (VARCHAR(1000)), `time_log` (DATETIME)
*   **Trạng thái/Hành động**: `loai_hanh_dong` (VARCHAR(100) - `TAO_MOI`, `CAP_NHAT`, `XOA`, `XEM`, `UPLOAD`, `DOWNLOAD`, `GUI_DUYET`, `PHE_DUYET`, `TU_CHOI`, `CAP_QUYEN`, `THU_HOI_QUYEN`)

---

## 4. Tổng Hợp Các Chỉnh Sửa / Cập Nhật Mới Nhất

Hệ thống vừa qua đã trải qua đợt cập nhật quan trọng để cải thiện trải nghiệm người dùng (UX) và chuẩn hóa dữ liệu:

### 4.1 Cập nhật phía Backend (API & Logic)
1.  **Thêm API Xem Trước Tài Liệu Trực Tuyến**:
    *   Bổ sung endpoint `GET /api/documents/{id}/preview` trong `DocumentController`.
    *   Tự động phát hiện định dạng tệp tin (`.pdf`, `.png`, `.jpg`, `.jpeg`, `.webp`, `.doc`, `.docx`) để trả về luồng byte dữ liệu đính kèm tiêu đề HTTP header `Content-Type` chuẩn xác (ví dụ: `application/pdf`, `image/jpeg`).
    *   Trả về tiêu đề `Content-Disposition: inline` giúp trình duyệt hiển thị tệp tin trực tiếp thay vì tự động tải xuống.
2.  **Sửa lỗi Lọc Tài Liệu (HTTP 500)**:
    *   Sửa lỗi logic đệ quy/nối bảng trong JPA Specification của `DocumentService` liên quan đến việc lọc tài liệu theo Phòng ban (`boPhan`) và Người tạo (`nguoiDungNguoiTao`), tránh lỗi xung đột liên kết khi truy vấn dữ liệu từ crawler và dữ liệu nội bộ.
3.  **Chuẩn hóa dữ liệu Lookup**:
    *   Hoàn thiện [LookupController](file:///d:/FIle_learn/Nien_Luan_CS/Project/backend/src/main/java/com/qltnb/controller/LookupController.java) để cung cấp toàn bộ API danh mục tĩnh (categories, doc-types, departments, users), phục vụ các dropdown động trên giao diện thay vì hardcode.

### 4.2 Cập nhật phía Frontend (Giao diện & Route)
1.  **Thiết kế Bố cục & Thanh điều hướng Hệ thống**:
    *   Hoàn thiện [MainLayout.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/layouts/MainLayout.vue) với thiết kế thanh bên (Sidebar) mượt mà có khả năng tự co giãn (Responsive Sidebar), hiển thị danh sách các chức năng theo vai trò của người dùng nhờ đồng bộ trạng thái từ Pinia Store.
2.  **Tích hợp Trình Xem Trước Văn Bản (Inline Preview)**:
    *   Tạo mới Component [DocumentPreviewModal.vue](file:///d:/FIle_learn/Nien_Luan_CS/Project/frontend/src/components/documents/DocumentPreviewModal.vue) sử dụng thẻ `<iframe\>` hoặc các thẻ Media HTML5 để nhúng file PDF/Hình ảnh trực tiếp trong ứng dụng Vue 3 khi nhấn nút "Xem trước" (Preview) ở trang danh sách và trang chi tiết tài liệu.
3.  **Hoàn thiện Module Vụ việc và Khách hàng**:
    *   Xây dựng đầy đủ giao diện quản lý vụ việc pháp lý (`CaseListView.vue`, `CaseDetailView.vue`) và khách hàng (`ClientListView.vue`, `ClientDetailView.vue`), tích hợp trực tiếp việc hiển thị các tài liệu đính kèm bên trong vụ việc cụ thể.
4.  **Bảo mật nâng cao tại Route (Navigation Guards)**:
    *   Cấu hình `router/index.js` chặn và kiểm soát vai trò (`meta.allowedRoles`) ở mức Router: Nếu nhân viên cố tình truy cập vào `/admin/users` hay `/activity-logs` của Trưởng phòng/Admin thì hệ thống Vue Router sẽ chặn lại và tự động chuyển hướng về trang Dashboard.
