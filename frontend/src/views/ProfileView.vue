<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-4xl mx-auto">
    <div class="border-b border-gray-100 pb-4 mb-6">
      <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
        <span>⚙️</span> Hồ sơ cá nhân
      </h1>
      <p class="text-gray-500 text-sm mt-1">Quản lý và cập nhật thông tin cá nhân hoặc thay đổi mật khẩu tài khoản cán bộ công ty luật.</p>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <!-- Cột trái: Avatar & Vai trò -->
      <div class="md:col-span-1 flex flex-col items-center p-6 bg-gray-50 rounded-lg border border-gray-200">
        <div class="w-24 h-24 bg-blue-600 text-white flex items-center justify-center rounded-full text-3xl font-bold shadow-md">
          {{ userLetter }}
        </div>
        <h2 class="text-lg font-bold text-gray-800 mt-4">{{ authStore.user?.hoTen || 'Thành viên' }}</h2>
        <span class="inline-flex items-center gap-1 rounded-full bg-blue-50 px-3 py-1 text-xs font-semibold text-blue-600 mt-1 uppercase tracking-wider">
          {{ authStore.user?.vaiTro }}
        </span>
        <p class="text-gray-500 text-xs mt-3">Bộ phận: {{ authStore.user?.boPhan || 'Quản trị hệ thống' }}</p>
      </div>

      <!-- Cột phải: Form thông tin chi tiết -->
      <div class="md:col-span-2 space-y-6">
        <div>
          <h3 class="font-bold text-gray-800 text-base mb-4 border-b pb-1">Chi tiết tài khoản</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div>
              <label class="block text-gray-500 font-medium">Tên đăng nhập (Tài khoản):</label>
              <input type="text" readonly :value="authStore.user?.taiKhoan" class="w-full bg-gray-100 border border-gray-200 rounded px-3 py-2 mt-1 focus:outline-none" />
            </div>
            <div>
              <label class="block text-gray-500 font-medium">Email:</label>
              <input type="text" readonly :value="authStore.user?.email" class="w-full bg-gray-100 border border-gray-200 rounded px-3 py-2 mt-1 focus:outline-none" />
            </div>
          </div>
        </div>

        <div>
          <h3 class="font-bold text-gray-800 text-base mb-4 border-b pb-1">Thay đổi mật khẩu</h3>
          <form class="space-y-4 text-sm">
            <div>
              <label class="block text-gray-700 font-medium">Mật khẩu hiện tại</label>
              <input type="password" placeholder="••••••••" class="w-full border border-gray-200 rounded px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500 focus:outline-none" />
            </div>
            <div>
              <label class="block text-gray-700 font-medium">Mật khẩu mới</label>
              <input type="password" placeholder="Mật khẩu mới ít nhất 6 ký tự" class="w-full border border-gray-200 rounded px-3 py-2 mt-1 focus:ring-2 focus:ring-blue-500 focus:outline-none" />
            </div>
            <button type="submit" class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded transition text-sm">
              Cập nhật mật khẩu
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const userLetter = computed(() => {
  return authStore.user?.hoTen ? authStore.user.hoTen.charAt(0).toUpperCase() : 'U';
});
</script>
