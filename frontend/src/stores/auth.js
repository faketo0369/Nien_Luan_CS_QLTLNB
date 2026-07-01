import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    // Khi F5 làm mới trang, tự động load lại user cũ từ bộ nhớ trình duyệt
    user: JSON.parse(localStorage.getItem('user')) || null
  }),
  
  getters: {
    isLoggedIn: (state) => state.user !== null,
    token: () => localStorage.getItem('token')
  },
  
  actions: {
    saveUserSession(userData) {
      this.user = {
        id: userData.id,
        hoTen: userData.hoTen,
        taiKhoan: userData.taiKhoan,
        email: userData.email,
        vaiTro: userData.vaiTro,
        boPhan: userData.boPhan
      };
      // Lưu thông tin user vào trình duyệt để giữ trạng thái đăng nhập khi F5
      localStorage.setItem('user', JSON.stringify(this.user));
      // Token đã được lưu tự động bởi hàm dangNhap() trong api/auth.js
    },
    
    logout() {
      this.user = null;
      // Xóa sạch cả user lẫn token khỏi trình duyệt khi đăng xuất
      localStorage.removeItem('user');
      localStorage.removeItem('token');
    }
  }
});
