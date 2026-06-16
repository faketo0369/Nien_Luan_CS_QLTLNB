import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    // Khi F5 làm mới trang, tự động load lại user cũ từ bộ nhớ trình duyệt
    user: JSON.parse(localStorage.getItem('user')) || null
  }),
  
  getters: {
    isLoggedIn: (state) => state.user !== null
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
      // Lưu vĩnh viễn vào trình duyệt để giữ trạng thái đăng nhập
      localStorage.setItem('user', JSON.stringify(this.user));
    },
    
    logout() {
      this.user = null;
      localStorage.removeItem('user');
    }
  }
});
