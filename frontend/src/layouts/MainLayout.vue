<template>
  <div class="layout-container">
    <aside class="sidebar">
      <div class="sidebar-content">
        <div class="sidebar-header">
          <h1 class="sidebar-title">VeenY</h1>
        </div>

        <nav class="sidebar-nav">
          <router-link to="/" class="nav-item">Dashboard</router-link>
          <router-link to="/documents" class="nav-item">Tài liệu</router-link>
          <router-link to="/cases" class="nav-item">Vụ việc</router-link>
          <router-link to="/clients" class="nav-item">Khách hàng</router-link>
          
          <router-link to="/notifications" class="nav-item nav-item-badge">
            <span>Thông báo</span>
            <span v-if="notificationStore.unreadCount > 0" class="badge-count">
              {{ notificationStore.unreadCount }}
            </span>
          </router-link>

          <div v-if="['TRUONG_PHONG', 'ADMIN'].includes(authStore.user?.vaiTro)" class="nav-section">
            <router-link to="/approval" class="nav-item">Duyệt tài liệu</router-link>
            <router-link to="/activity-logs" class="nav-item">Lịch sử hoạt động</router-link>
          </div>

          <div v-if="['ADMIN', 'TRUONG_PHONG'].includes(authStore.user?.vaiTro)" class="nav-section">
            <router-link to="/admin/users" class="nav-item">Người dùng</router-link>
            <router-link v-if="authStore.user?.vaiTro === 'ADMIN'" to="/admin/departments" class="nav-item">Bộ phận</router-link>
          </div>
        </nav>
      </div>

      <div class="sidebar-footer">
        <div class="user-info">
          <div class="user-avatar">
            {{ userAvatarLetter }}
          </div>
          <div class="user-details">
            <p class="user-name">{{ authStore.user?.hoTen }}</p>
            <p class="user-role">{{ authStore.user?.vaiTro }}</p>
          </div>
        </div>
        <div class="footer-actions">
          <button @click="router.push('/profile')" class="footer-btn profile-btn">
            Hồ sơ
          </button>
          <button @click="handleLogout" class="footer-btn logout-btn">
            Đăng xuất
          </button>
        </div>
      </div>
    </aside>

    <div class="main-area">
      <header class="main-header">
        <h2 class="header-title">Document management system VeenY</h2>
        <div class="header-date">{{ currentDate }}</div>
      </header>
      <main class="main-content">
        <router-view />
        <footer class="main-footer">
          <p>© 2026 Bản quyền thuộc về B2308384</p>
        </footer>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import { useNotificationStore } from '../stores/notifications';

const authStore = useAuthStore();
const router = useRouter();
const notificationStore = useNotificationStore();
let intervalId = null;

const userAvatarLetter = computed(() => {
  return authStore.user?.hoTen ? authStore.user.hoTen.charAt(0).toUpperCase() : 'U';
});

const currentDate = computed(() => {
  return new Date().toLocaleDateString('vi-VN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' });
});

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};

onMounted(() => {
  notificationStore.fetchUnreadCount();
  // Thiết lập cơ chế Polling tự động chạy mỗi 30 giây
  intervalId = setInterval(() => {
    notificationStore.fetchUnreadCount();
  }, 30000);
});

onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  background-color: #f9fafb;
  color: #1f2937;
}

/* ===== Sidebar ===== */
.sidebar {
  width: 16rem;
  background-color: #fdf2f8;
  color: #0f172a;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: 1px 0 0 rgba(15, 23, 42, 0.08);
  flex-shrink: 0;
  position: relative;
  z-index: 20;
}

.sidebar-content {
  overflow-y: auto;
  flex: 1;
  background-color: transparent;
}

.sidebar-header {
  height: 4rem;
  display: flex;
  align-items: center;
  padding: 0 1.5rem;
  background-color: transparent;
  gap: 0.75rem;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
}

.sidebar-logo {
  font-size: 1.5rem;
  color: #0f172a;
}

.sidebar-title {
  font-size: 1.25rem;
  font-weight: 800;
  color: #0f172a;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0;
}

/* ===== Navigation ===== */
.sidebar-nav {
  padding: 1rem;
}

.nav-section {
  padding-top: 1rem;
}

.nav-section-title {
  padding: 0.5rem 0.75rem;
  font-size: 0.75rem;
  font-weight: 700;
  color: rgba(15, 23, 42, 0.65);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-weight: 600;
  transition: all 0.2s;
  color: #0f172a;
  text-decoration: none;
  margin-bottom: 0.125rem;
}

.nav-item:hover {
  background-color: rgba(255, 255, 255, 0.45);
  color: #0f172a;
}

.nav-item-badge {
  justify-content: space-between;
}

.badge-count {
  background-color: #ef4444;
  color: #ffffff;
  font-size: 0.75rem;
  padding: 0.125rem 0.5rem;
  border-radius: 9999px;
  font-weight: 700;
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.router-link-active:not([href="/"]),
.router-link-exact-active {
  background-color: #0f172a !important;
  color: #ffffff !important;
  font-weight: 700;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

/* ===== Sidebar Footer ===== */
.sidebar-footer {
  padding: 1rem;
  background-color: transparent;
  border-top: 1px solid rgba(15, 23, 42, 0.08);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}

.user-avatar {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 9999px;
  background-color: #0f172a;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1.125rem;
  box-shadow: inset 0 2px 4px 0 rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.user-details {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 0.875rem;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-role {
  font-size: 0.75rem;
  color: #be185d;
  font-family: monospace;
  font-weight: 700;
  letter-spacing: 0.05em;
  text-transform: uppercase;
  margin: 0;
}

.footer-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.5rem;
}

.footer-btn {
  padding: 0.5rem;
  font-size: 0.75rem;
  font-weight: 600;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: all 0.2s;
}

.profile-btn {
  background-color: rgba(15, 23, 42, 0.08);
  color: #0f172a;
}

.profile-btn:hover {
  background-color: rgba(15, 23, 42, 0.16);
}

.logout-btn {
  background-color: rgba(190, 24, 74, 0.12);
  color: #be123c;
}

.logout-btn:hover {
  background-color: rgba(190, 24, 74, 0.22);
}

/* ===== Main Area ===== */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
  position: relative;
  z-index: 10;
}

.main-header {
  height: 4rem;
  background-color: #fdf2f8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 2rem;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
  flex-shrink: 0;
}

.header-title {
  font-weight: 800;
  color: #0f172a;
  font-size: 1.125rem;
  margin: 0;
  text-shadow: 0 1px 1px rgba(255, 255, 255, 0.4);
}

.header-date {
  font-size: 0.875rem;
  color: #0f172a;
  font-weight: 700;
  font-family: monospace;
  background-color: rgba(255, 255, 255, 0.55);
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  backdrop-filter: blur(4px);
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding: 2rem;
}

.main-footer {
  margin-top: 3rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e5e7eb;
  text-align: center;
  font-size: 0.875rem;
  color: #6b7280;
  font-weight: 500;
}

/* Custom scrollbar for sidebar */
.sidebar-content::-webkit-scrollbar {
  width: 4px;
}
.sidebar-content::-webkit-scrollbar-track {
  background: transparent;
}
.sidebar-content::-webkit-scrollbar-thumb {
  background: #334155;
  border-radius: 2px;
}
.sidebar-content::-webkit-scrollbar-thumb:hover {
  background: #475569;
}

/* Custom scrollbar for main content */
.main-content::-webkit-scrollbar {
  width: 6px;
}
.main-content::-webkit-scrollbar-track {
  background: transparent;
}
.main-content::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}
.main-content::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>
