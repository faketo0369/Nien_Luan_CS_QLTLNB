# Mô tả chi tiết kiểu dữ liệu thuộc tính từ sơ đồ ERD

Tài liệu này cung cấp chi tiết kiểu dữ liệu của toàn bộ thuộc tính thuộc các bảng trong sơ đồ ERD, được đối chiếu giữa **Mô hình logic (ERD)** và **Thiết kế vật lý (MySQL)**.

---

## 1. BO_PHAN (Bộ phận)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **BP_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính (Primary Key) |
| **BP_ten** | Text | `VARCHAR(150)` | Tên bộ phận (Unique) |
| **BP_moTa** | Text | `TEXT` | Mô tả bộ phận |

---

## 2. TAI_KHOAN_NGUOI_DUNG (Tài khoản người dùng)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **ND_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| *BP_id* | Integer | `INT` | Khóa ngoại liên kết với `BO_PHAN` |
| *VT_id* | Integer | `INT` | Khóa ngoại liên kết với `VAI_TRO` |
| **ND_hoTen** | Text | `TEXT` | Họ và tên |
| **ND_taiKhoan** | Text | `VARCHAR(100)` | Tên đăng nhập (Unique) |
| **ND_matKhau** | Text | `TEXT` | Mật khẩu (được mã hóa) |
| **ND_email** | Text | `VARCHAR(150)` | Địa chỉ email (Unique) |
| **ND_soLanSai** | Integer | `INT` | Số lần đăng nhập sai (Default: 0) |
| **ND_trangThaiTK** | Boolean | `BOOLEAN` | Trạng thái hoạt động (Default: TRUE) |
| **ND_chuyenMon** | Text | `TEXT` | Lĩnh vực chuyên môn |
| **ND_chungChi** | Text | `TEXT` | Chứng chỉ hành nghề |

---

## 3. VAI_TRO (Vai trò)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **VT_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| **VT_ten** | Text | `VARCHAR(100)` | Tên vai trò (Unique) |
| **VT_moTa** | Text | `TEXT` | Mô tả vai trò |

---

## 4. VU_VIEC (Vụ việc)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **VV_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| *KH_id* | Integer | `INT` | Khóa ngoại liên kết với `KHACH_HANG` |
| *ND_phuTrach_id* | Integer | `INT` | Khóa ngoại liên kết với `TAI_KHOAN_NGUOI_DUNG` |
| **VV_ten** | Text | `VARCHAR(200)` | Tên vụ việc (Unique) |
| **VV_loai** | Text | `TEXT` | Loại vụ việc |
| **VV_trangThai** | Text | `TEXT` | Trạng thái vụ việc |
| **VV_ngayMo** | Date & Time | `DATETIME` | Ngày mở hồ sơ vụ việc |
| **VV_ngayDong** | Date & Time | `DATETIME` | Ngày đóng hồ sơ vụ việc |
| **VV_ghiChu** | Text | `TEXT` | Ghi chú thêm |

---

## 5. KHACH_HANG (Khách hàng)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **KH_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| **KH_ten** | Text | `TEXT` | Tên khách hàng |
| **KH_loai** | Text | `TEXT` | Phân loại (Cá nhân / Tổ chức) |
| **KH_CCCD_MST** | Text | `VARCHAR(50)` | Số CCCD hoặc Mã số thuế (Unique) |
| **KH_sdt** | Text | `TEXT` | Số điện thoại liên hệ |
| **KH_diaChi** | Text | `TEXT` | Địa chỉ cư trú/trụ sở |
| **KH_email** | Text | `TEXT` | Địa chỉ email |
| **KH_ngayTao** | Date & Time | `DATETIME` | Thời gian tạo thông tin (Default: NOW) |

---

## 6. PHIEN_BAN_TAI_LIEU (Phiên bản tài liệu)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **PBTL_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| *TL_id* | Integer | `INT` | Khóa ngoại liên kết với `TAI_LIEU` |
| *ND_update_id* | Integer | `INT` | Khóa ngoại người cập nhật |
| **PBTL_maPhienBan** | Text | `TEXT` | Mã số phiên bản (ví dụ: v1.0, v2.0) |
| **PBTL_nguoiUpdate**| Text | `TEXT` | Tên người thực hiện cập nhật |
| **PBTL_timeUpdate** | Date & Time | `DATETIME` | Thời gian cập nhật phiên bản |
| **PBTL_ghiChu** | Text | `TEXT` | Nhật ký thay đổi (changelog) |
| **PBTL_duongDan** | Text | `TEXT` | Đường dẫn lưu trữ tệp phiên bản |
| **PBTL_kichCo** | Number | `BIGINT` | Dung lượng tệp phiên bản (byte) |
| **PBTL_dinhDang** | Text | `VARCHAR(50)` | Định dạng tệp (docx, pdf, xlsx...) |

---

## 7. TAI_LIEU (Tài liệu)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **TL_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| *DM_id* | Integer | `INT` | Khóa ngoại liên kết với `DANH_MUC` |
| *LTLPL_id* | Integer | `INT` | Khóa ngoại liên kết `LOAI_TAI_LIEU_PHAP_LY` |
| *VV_id* | Integer | `INT` | Khóa ngoại liên kết với `VU_VIEC` |
| *ND_nguoiTao_id* | Integer | `INT` | Khóa ngoại người sở hữu/tạo tài liệu |
| **TL_ten** | Text | `TEXT` | Tên tài liệu |
| **TL_duongDan** | Text | `TEXT` | Đường dẫn lưu trữ file trên hệ thống |
| **TL_dinhDang** | Text | `TEXT` | Định dạng file (pdf, docx, ...) |
| **TL_dungLuong** | Long integer | `BIGINT` | Dung lượng tệp tin (byte) |
| **TL_nguoiTao** | Text | `TEXT` | Tên hiển thị người tạo |
| **TL_ngayTao** | Date & Time | `DATETIME` | Thời gian đăng tải tài liệu |
| **TL_ngayBanHanh**| Date | `DATE` | Ngày ban hành văn bản pháp luật (nếu có) |
| **TL_daXoa** | Boolean | `BOOLEAN` | Trạng thái xóa mềm (Default: FALSE) |
| **TL_baoMat** | Text | `TEXT` | Mức độ bảo mật (Nội bộ, Công khai...) |
| **TL_ngayHetHan** | Date & Time | `DATETIME` | Ngày tài liệu hết hiệu lực |
| **TL_soHieu** | Text | `VARCHAR(100)` | Số ký hiệu văn bản |

---

## 8. DANH_MUC (Danh mục)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **DM_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| **DM_ten** | Text | `VARCHAR(150)` | Tên danh mục tài liệu (Unique) |

---

## 9. LOAI_TAI_LIEU_PHAP_LY (Loại tài liệu pháp lý)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **LTLPL_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| **LTLPL_ten** | Text | `VARCHAR(150)` | Tên loại tài liệu (Luật, Nghị định...) |
| **LTLPL_moTa** | Text | `TEXT` | Mô tả về loại tài liệu |

---

## 10. LICH_SU_HOAT_DONG (Lịch sử hoạt động)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **LS_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| *ND_id* | Integer | `INT` | Khóa ngoại liên kết với `TAI_KHOAN_NGUOI_DUNG` |
| *TL_id* | Integer | `INT` | Khóa ngoại liên kết với `TAI_LIEU` |
| **LS_hoatDong** | Text | `TEXT` | Mô tả hành động (Xem, Tải, Sửa...) |
| **LS_thoiGianSua**| Date & Time | `DATETIME` | Thời gian thực hiện hành động |
| **LS_diaChiIP** | Text | `TEXT` | Địa chỉ IP của thiết bị thực hiện |
| **LS_loaiHanhDong**| Text | `VARCHAR(100)` | Loại hành động phân nhóm |

---

## 11. QUYEN_TRUY_CAP (Quyền truy cập)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **QTC_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| *TL_id* | Integer | `INT` | Khóa ngoại liên kết với `TAI_LIEU` |
| *ND_id* | Integer | `INT` | Khóa ngoại liên kết với `TAI_KHOAN_NGUOI_DUNG` |
| **QTC_loaiQuyen** | Text | `TEXT` | Loại quyền (Xem, Chỉnh sửa, Tải...) |
| **QTC_thoiHan** | Date & Time | `DATETIME` | Thời gian hết hiệu lực của quyền |

---

## 12. DUYET_TAI_LIEU (Duyệt tài liệu)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **DTL_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| *TL_id* | Integer | `INT` | Khóa ngoại liên kết với `TAI_LIEU` |
| *ND_duyet_id* | Integer | `INT` | Khóa ngoại liên kết người phê duyệt |
| **DTL_trangThai** | Text | `TEXT` | Trạng thái (Đã duyệt, Từ chối, ...) |
| **DTL_ghiChu** | Text | `TEXT` | Ý kiến/Ghi chú phản hồi của người duyệt |
| **DTL_timeDuyet** | Date & Time | `DATETIME` | Thời điểm phê duyệt |
| **DTL_hanhDong** | Text | `VARCHAR(100)` | Hành động phê duyệt cụ thể |

---

## 13. THONG_BAO (Thông báo)
| Thuộc tính | Kiểu dữ liệu (ERD) | Kiểu dữ liệu (SQL) | Mô tả / Ràng buộc |
| :--- | :--- | :--- | :--- |
| **TB_id** | Integer | `INT AUTO_INCREMENT` | Khóa chính |
| *ND_id* | Integer | `INT` | Khóa ngoại người nhận thông báo |
| *TL_id* | Integer | `INT` | Khóa ngoại liên kết tài liệu liên quan |
| **TB_tieuDe** | Text | `TEXT` | Tiêu đề thông báo |
| **TB_noiDung** | Text | `TEXT` | Nội dung thông báo chi tiết |
| **TB_trangThaiDoc**| Boolean | `BOOLEAN` | Trạng thái đã đọc (Default: FALSE) |
| **TB_loai** | Text | `VARCHAR(50)` | Phân loại thông báo |
| **TB_ngay** | Date | `DATETIME` | Ngày tạo thông báo |
