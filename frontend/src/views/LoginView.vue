<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="login-header">
        <span class="login-logo">⚖️</span>
        <h2>HỆ THỐNG QUẢN LÝ</h2>
        <p>Tài liệu nội bộ & Văn bản pháp lý</p>
      </div>
      <form @submit.prevent="handleLogin" class="login-form">
        <div v-if="errorMsg" class="error-banner">
          {{ errorMsg }}
        </div>
        <div class="input-group">
          <label for="username">Tài khoản</label>
          <input 
            v-model="username" 
            type="text" 
            id="username" 
            required 
            placeholder="Nhập tài khoản (vd: admin)"
          />
        </div>
        <div class="input-group">
          <label for="password">Mật khẩu</label>
          <input 
            v-model="password" 
            type="password" 
            id="password" 
            required 
            placeholder="Nhập mật khẩu (vd: demo-admin)"
          />
        </div>
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? 'Đang xác thực...' : 'Đăng nhập' }}
        </button>
      </form>

      <!-- Khối Đăng nhập nhanh phục vụ test prototype -->
      <div class="quick-login-panel">
        <p class="quick-login-title">⚡ Đăng nhập nhanh (Test):</p>
        <div class="quick-login-buttons">
          <button class="quick-btn admin-color" @click="fillAndSubmit('admin', 'admin')">Admin</button>
          <button class="quick-btn manager-color" @click="fillAndSubmit('truongphong.dansu', 'truongphong.dansu')">Trưởng phòng</button>
          <button class="quick-btn staff-color" @click="fillAndSubmit('nhanvien.dansu', 'nhanvien.dansu')">Nhân viên</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import { dangNhap } from '../api/auth';

const authStore = useAuthStore();
const router = useRouter();

const username = ref('');
const password = ref('');
const loading = ref(false);
const errorMsg = ref('');

const handleLogin = async () => {
  loading.value = true;
  errorMsg.value = '';
  try {
    const data = await dangNhap(username.value, password.value);
    authStore.saveUserSession(data);
    router.push({ name: 'dashboard' });
  } catch (err) {
    if (err.response && err.response.data && err.response.data.message) {
      errorMsg.value = err.response.data.message;
    } else {
      errorMsg.value = 'Lỗi kết nối đến server. Hãy chắc chắn backend port 8080 đang chạy!';
    }
  } finally {
    loading.value = false;
  }
};

const fillAndSubmit = (user, pass) => {
  username.value = user;
  password.value = pass;
  handleLogin();
};
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #0f172a, #1e1b4b);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.3), 0 10px 10px -5px rgba(0, 0, 0, 0.2);
  padding: 40px 30px;
  box-sizing: border-box;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-logo {
  font-size: 48px;
  display: inline-block;
  margin-bottom: 15px;
}

.login-header h2 {
  margin: 0 0 5px;
  font-size: 22px;
  font-weight: 800;
  color: #111827;
  letter-spacing: 0.5px;
}

.login-header p {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.error-banner {
  background-color: #fef2f2;
  border: 1px solid #fca5a5;
  color: #b91c1c;
  padding: 10px 15px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  text-align: center;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-group label {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.input-group input {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.input-group input:focus {
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
}

.submit-btn {
  margin-top: 10px;
  padding: 14px;
  background-color: #4f46e5;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease;
}

.submit-btn:hover:not(:disabled) {
  background-color: #4338ca;
}

.submit-btn:active:not(:disabled) {
  transform: scale(0.98);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.quick-login-panel {
  margin-top: 25px;
  border-top: 1px dashed #d1d5db;
  padding-top: 15px;
  text-align: center;
}

.quick-login-title {
  margin: 0 0 10px;
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
}

.quick-login-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.quick-btn {
  flex: 1;
  padding: 8px 4px;
  font-size: 12px;
  font-weight: 700;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  color: white;
  transition: opacity 0.2s ease, transform 0.1s ease;
}

.quick-btn:hover {
  opacity: 0.9;
}

.quick-btn:active {
  transform: scale(0.95);
}

.admin-color {
  background-color: #dc2626;
}

.manager-color {
  background-color: #d97706;
}

.staff-color {
  background-color: #059669;
}
</style>
