<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-7xl mx-auto">
    <div class="flex justify-between items-center mb-6 border-b border-gray-100 pb-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Thông báo hệ thống</h1>
        <p class="text-gray-500 text-sm mt-1">Nơi nhận và xử lý các thông báo về trạng thái duyệt hồ sơ tài liệu hoặc phân công vụ việc pháp lý.</p>
      </div>
      <button 
        v-if="items.some(item => !item.daDoc)"
        @click="handleMarkAllAsRead" 
        class="text-sm font-semibold text-blue-600 hover:text-blue-800 transition"
      >
        Đánh dấu đã đọc tất cả
      </button>
    </div>

    <!-- Loading & Error -->
    <div v-if="loading" class="p-12 text-center text-gray-500 text-base">
      Đang tải thông báo từ máy chủ...
    </div>
    <div v-else-if="errorMsg" class="p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg mb-6 text-sm">
      {{ errorMsg }}
    </div>

    <!-- Danh sách thông báo -->
    <div v-else class="divide-y divide-gray-100 border border-gray-100 rounded-lg overflow-hidden shadow-sm bg-white">
      <div 
        v-for="item in items" 
        :key="item.id" 
        :class="['p-4 transition flex gap-4 items-start', !item.daDoc ? 'bg-blue-50/40 hover:bg-blue-50/60' : 'hover:bg-gray-50']"
      >
        <div class="flex-1">
          <div class="flex justify-between items-baseline">
            <h4 :class="['text-sm', !item.daDoc ? 'font-bold text-gray-900' : 'font-semibold text-gray-700']">
              {{ item.tieuDe }}
            </h4>
            <span class="text-xs text-gray-400">{{ formatDateTime(item.ngayTao) }}</span>
          </div>
          <p class="text-gray-600 text-xs mt-1">{{ item.noiDung }}</p>
          <div class="mt-2 flex gap-3 text-xs">
            <button 
              v-if="!item.daDoc"
              @click="handleMarkAsRead(item.id)"
              class="text-blue-600 hover:text-blue-800 font-semibold"
            >
              Đánh dấu đã đọc
            </button>
          </div>
        </div>
      </div>
      <div v-if="items.length === 0" class="p-8 text-center text-gray-400">
        Không có thông báo nào trong hệ thống.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { notificationApi } from '../api/notifications';
import { useNotificationStore } from '../stores/notifications';

const items = ref([]);
const loading = ref(false);
const errorMsg = ref('');
const notificationStore = useNotificationStore();

const loadData = async () => {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res = await notificationApi.getAll();
    items.value = res.data.data;
  } catch (err) {
    console.error('Lỗi khi tải danh sách thông báo:', err);
    errorMsg.value = 'Không thể tải danh sách thông báo từ máy chủ.';
  } finally {
    loading.value = false;
  }
};

const handleMarkAsRead = async (id) => {
  try {
    await notificationApi.markAsRead(id);
    // 1. Cập nhật UI local
    const found = items.value.find(item => item.id === id);
    if (found) found.daDoc = true;
    
    // 2. Cập nhật lại badge unreadCount ngay lập tức
    await notificationStore.fetchUnreadCount();
  } catch (err) {
    alert('Không thể cập nhật trạng thái thông báo: ' + err.message);
  }
};

const handleMarkAllAsRead = async () => {
  try {
    await notificationApi.markAllAsRead();
    // 1. Cập nhật UI local
    items.value.forEach(item => item.daDoc = true);
    
    // 2. Cập nhật lại badge unreadCount ngay lập tức
    await notificationStore.fetchUnreadCount();
  } catch (err) {
    alert('Không thể đánh dấu đã đọc tất cả: ' + err.message);
  }
};

const formatDateTime = (val) => {
  if (!val) return '---';
  return val.replace('T', ' ').substring(0, 16);
};

const getIconByLoai = (loai) => {
  if (!loai) return '🔔';
  const l = String(loai).toUpperCase();
  if (l.includes('DUYET') || l.includes('APPROVE')) return '📄';
  if (l.includes('VU_VIEC') || l.includes('CASE')) return '💼';
  return '🔔';
};

onMounted(() => {
  loadData();
});
</script>
