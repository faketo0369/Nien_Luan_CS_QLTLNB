<template>
  <div class="app-container">
    <div v-if="authStore.isLoggedIn" class="dashboard-wrapper">
      <header class="app-header">
        <div class="header-left">
          <span class="app-logo">📂</span>
          <h1>QLTNB System</h1>
        </div>
        <button class="logout-btn" @click="handleLogout">
          Đăng xuất
        </button>
      </header>
      <main class="app-main">
        <DashboardView />
      </main>
    </div>
    <div v-else class="login-wrapper">
      <div class="login-card">
        <div class="login-header">
          <span class="login-logo">📂</span>
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
            <button class="quick-btn manager-color" @click="fillAndSubmit('truongphong.dansu', 'manager')">Trưởng phòng</button>
            <button class="quick-btn staff-color" @click="fillAndSubmit('nhanvien.dansu', 'staff')">Nhân viên</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from './stores/auth'
import { dangNhap } from './api/auth'
import DashboardView from './views/DashboardView.vue'

const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref('')

const handleLogin = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await dangNhap(username.value, password.value)
    authStore.saveUserSession(data)
  } catch (err) {
    if (err.response && err.response.data && err.response.data.message) {
      errorMsg.value = err.response.data.message
    } else {
      errorMsg.value = 'Lỗi kết nối đến server. Hãy chắc chắn backend port 8080 đang chạy!'
    }
  } finally {
    loading.value = false
  }
}

const handleLogout = () => {
  authStore.logout()
}

const fillAndSubmit = (user, pass) => {
  username.value = user
  password.value = pass
  handleLogin()
}
</script>

<style>
/* CSS Reset and Variables */
:root {
  --primary-color: #4f46e5;
  --primary-hover: #4338ca;
  --bg-gradient-start: #0f172a;
  --bg-gradient-end: #1e1b4b;
  --card-bg: rgba(255, 255, 255, 0.95);
  --text-main: #1f2937;
  --text-muted: #6b7280;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'Outfit', 'Inter', -apple-system, sans-serif;
  background-color: #f3f4f6;
  color: var(--text-main);
}

.app-container {
  min-height: 100vh;
}

/* Header styling */
.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.app-logo {
  font-size: 28px;
}

.header-left h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(to right, #4f46e5, #9333ea);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.logout-btn {
  padding: 8px 16px;
  background-color: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s ease;
}

.logout-btn:hover {
  background-color: #e5e7eb;
  color: #111827;
}

/* Main content container */
.app-main {
  max-width: 1200px;
  margin: 30px auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06);
}

/* Login Page Styling */
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--bg-gradient-start), var(--bg-gradient-end));
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--card-bg);
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
  color: var(--text-muted);
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
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
}

.submit-btn {
  margin-top: 10px;
  padding: 14px;
  background-color: var(--primary-color);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.2s ease, transform 0.1s ease;
}

.submit-btn:hover:not(:disabled) {
  background-color: var(--primary-hover);
}

.submit-btn:active:not(:disabled) {
  transform: scale(0.98);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* Quick Login Styles */
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
