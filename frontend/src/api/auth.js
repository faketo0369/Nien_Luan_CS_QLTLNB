import axios from 'axios';

// Cấu hình cổng kết nối thẳng đến Spring Boot Backend (port 8080)
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// API Đăng nhập hệ thống (Bypass bảo mật JWT để phục vụ test nhanh)
export const dangNhap = async (taiKhoan, matKhau) => {
  const response = await api.post('/auth/login', { taiKhoan, matKhau });
  return response.data; 
};

// API Lấy danh sách tài liệu tích hợp phân trang, từ khóa tìm kiếm
export const danhSachTaiLieu = async (params) => {
  // params truyền lên sẽ gồm: page, size, tuKhoa
  const response = await api.get('/documents/tai-lieu', { params });
  return response.data;
};

export default api;
