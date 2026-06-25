import axios from 'axios';

// Cấu hình cổng kết nối thẳng đến Spring Boot Backend (port 8080)
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// ===== INTERCEPTOR: Tự động đính kèm JWT Token vào Header mọi request =====
// Mỗi khi Axios gửi request, interceptor sẽ kiểm tra localStorage,
// nếu có token thì tự động thêm vào header Authorization.
// Đây là cách làm chuẩn Production, không cần truyền token thủ công từng lần.
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// ===== INTERCEPTOR RESPONSE: Xử lý tự động khi token hết hạn =====
// Nếu server trả về 401 (Unauthorized), nghĩa là token đã hết hạn hoặc không hợp lệ.
// Tự động xóa session cũ và chuyển về trang đăng nhập.
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token hết hạn hoặc không hợp lệ -> buộc đăng xuất
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // Tải lại trang để quay về màn hình đăng nhập
      window.location.reload();
    }
    return Promise.reject(error);
  }
);

// API Đăng nhập hệ thống (JWT Authentication)
export const dangNhap = async (taiKhoan, matKhau) => {
  const response = await api.post('/auth/login', { taiKhoan, matKhau });
  // Lưu JWT token vào localStorage ngay khi đăng nhập thành công
  if (response.data.token) {
    localStorage.setItem('token', response.data.token);
  }
  return response.data; 
};

// API Lấy danh sách tài liệu tích hợp phân trang, từ khóa tìm kiếm
// Token sẽ được interceptor tự động đính kèm, không cần truyền thủ công
export const danhSachTaiLieu = async (params) => {
  // params truyền lên sẽ gồm: page, size, tuKhoa
  const response = await api.get('/documents', { params });
  return response.data.data;
};

export default api;
