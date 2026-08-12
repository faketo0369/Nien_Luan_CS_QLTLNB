# Hướng Dẫn Vận Hành và Phát Triển Dự Án QLTLNB

Tài liệu này hướng dẫn chi tiết các bước để cài đặt, chạy thử nghiệm hệ thống và cách sử dụng Git để đồng bộ/đẩy (push) mã nguồn lên repository.

---

## 🛠️ Yêu cầu hệ thống (Prerequisites)
Trước khi bắt đầu, hãy đảm bảo máy tính của bạn đã cài đặt các công cụ sau:
- **Git**
- **Docker & Docker Compose** (Khuyên dùng để triển khai nhanh)
- **Node.js** (Phiên bản >= 18) & **npm** (để chạy Frontend bằng tay)
- **Java JDK 21** & **Maven** (để chạy Backend bằng tay)
- **Python 3.9+** & **pip** (để chạy Crawler bằng tay)
- **MySQL Server 8.0** (nếu chạy DB bằng tay)

---

## 🚀 1. Hướng Dẫn Chạy Dự Án

### Cách 1: Chạy bằng Docker (Khuyên dùng - Nhanh & Tiện nhất)
Docker Compose sẽ tự động thiết lập và khởi chạy MySQL (có sẵn dữ liệu khởi tạo), Backend (Spring Boot), và Frontend (Nginx/Vue) trên các container độc lập.

1. **Khởi chạy toàn bộ hệ thống:**
   ```bash
   docker compose up -d
   ```
   *Hoặc nếu dùng phiên bản cũ hơn:*
   ```bash
   docker-compose up -d
   ```

2. **Kiểm tra trạng thái các container:**
   ```bash
   docker compose ps
   ```

3. **Xem logs hệ thống (để debug/theo dõi tiến trình):**
   ```bash
   docker compose logs -f
   ```

4. **Xây dựng lại (Rebuild) khi có thay đổi code:**
   ```bash
   docker compose up -d --build
   ```

5. **Dừng hệ thống:**
   ```bash
   docker compose down
   ```

*Sau khi khởi động thành công:*
- **Frontend:** Truy cập tại [http://localhost](http://localhost) (Cổng 80)
- **Backend API:** Truy cập tại [http://localhost:8080](http://localhost:8080)
- **MySQL Database:** Chạy tại `localhost:3306`

---

### Cách 2: Khởi chạy thủ công từng phần (Dành cho việc lập trình/phát triển)

#### Bước 1: Khởi tạo Cơ sở dữ liệu (MySQL)
1. Tạo một database mới tên là `qltl_luat_dan_su` trong MySQL của bạn.
2. Thực thi lần lượt 3 file script SQL trong thư mục [database/](file:///d:/FIle_learn/Nien_Luan_CS/Project/database) để khởi tạo bảng và dữ liệu mẫu:
   - `01_create_schema.sql` (Khởi tạo cấu trúc các bảng)
   - `02_seed_fake_data.sql` (Thêm dữ liệu người dùng & hệ thống mẫu)
   - `03_insert_vanban.sql` (Thêm dữ liệu văn bản pháp luật đã crawl)

#### Bước 2: Khởi chạy Backend (Java Spring Boot)
1. Di chuyển vào thư mục backend:
   ```bash
   cd backend
   ```
2. Cấu hình lại thông tin kết nối MySQL (username, password) trong file `backend/src/main/resources/application.properties` nếu có thay đổi so với cấu hình mặc định (`dev_user`/`dev_password`).
3. Chạy ứng dụng Spring Boot:
   *   **Trên Windows (sử dụng Maven Wrapper):**
       ```cmd
       .\mvnw.cmd spring-boot:run
       ```
   *   **Trên Linux / macOS:**
       ```bash
       ./mvnw spring-boot:run
       ```
   *   **Hoặc nếu đã cài đặt sẵn Maven:**
       ```bash
       mvn spring-boot:run
       ```
   *Backend sẽ khởi chạy tại cổng **8080**.*

#### Bước 3: Khởi chạy Frontend (Vue 3 + Vite)
1. Di chuyển vào thư mục frontend:
   ```bash
   cd frontend
   ```
2. Cài đặt các thư viện cần thiết:
   ```bash
   npm install
   ```
3. Khởi chạy dev server:
   ```bash
   npm run dev
   ```
   *Frontend sẽ hoạt động tại địa chỉ: [http://localhost:5173](http://localhost:5173).*
4. Để đóng gói mã nguồn sang dạng file tĩnh cho Production:
   ```bash
   npm run build
   ```

#### Bước 4: Khởi chạy Module Crawler (Python)
*Lưu ý: Chỉ cần chạy khi bạn muốn cào thêm tài liệu mới.*
1. Di chuyển vào thư mục crawler:
   ```bash
   cd crawler
   ```
2. Tạo môi trường ảo Python (Virtual Environment):
   *   **Trên Windows:**
       ```bash
       python -m venv venv
       .\venv\Scripts\activate
       ```
   *   **Trên Linux / macOS:**
       ```bash
       python3 -m venv venv
       source venv/bin/activate
       ```
3. Cài đặt thư viện dependencies:
   ```bash
   pip install -r requirements.txt
   ```
4. Chạy script crawl văn bản pháp luật:
   ```bash
   python crawl_vbpl.py
   ```
5. Chạy script tạo mã SQL từ dữ liệu đã cào:
   ```bash
   python generate_insert_vanban.py
   ```

---

## 💾 2. Quy trình Commit và Push Code Lên Git

Để đưa những thay đổi mã nguồn của bạn lên repository chung một cách an toàn, hãy thực hiện theo các bước chuẩn sau:

### Bước 1: Kiểm tra trạng thái các file thay đổi
Xem các file nào đã bị chỉnh sửa, thêm mới hoặc xóa đi:
```bash
git status
```

### Bước 2: Thêm các file thay đổi vào Staging Area
- Để thêm **tất cả** các file thay đổi:
  ```bash
  git add .
  ```
- Hoặc thêm cụ thể từng file/thư mục:
  ```bash
  git add path/to/file_hoac_folder
  ```

### Bước 3: Lưu lại thay đổi với thông điệp (Commit)
Đặt tên commit ngắn gọn, rõ nghĩa về tính năng hoặc lỗi vừa sửa:
```bash
git commit -m "feat: mô_tả_ngắn_gọn_tính_năng_mới"
# Ví dụ: git commit -m "feat: bo sung tai lieu huong dan readme"
```

### Bước 4: Cập nhật code mới nhất từ server về (Rất quan trọng)
Trước khi push lên, luôn luôn lấy code mới nhất từ nhánh làm việc hiện tại về máy để tránh xung đột (conflict):
1. Kiểm tra nhánh hiện tại của bạn:
   ```bash
   git branch --show-current
   ```
2. Pull code mới về (Ví dụ nếu bạn đang ở nhánh `main` hoặc `master`):
   ```bash
   git pull origin <tên_nhánh>
   # Ví dụ: git pull origin main
   ```

### Bước 5: Đẩy code lên Server (Push)
Đẩy các commit của bạn lên nhánh tương ứng trên remote repository:
```bash
git push origin <tên_nhánh>
# Ví dụ: git push origin main
```

---
💡 **Một số lệnh Git hữu ích khác:**
- Xem danh sách các nhánh: `git branch`
- Chuyển sang nhánh khác: `git checkout <tên_nhánh>`
- Tạo và chuyển sang nhánh mới: `git checkout -b <tên_nhánh_mới>`
- Hủy bỏ các thay đổi chưa commit trên một file: `git checkout -- <tên_file>`
- Xem lịch sử commit: `git log --oneline -n 10`
