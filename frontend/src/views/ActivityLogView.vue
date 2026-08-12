<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-7xl mx-auto">
    <div class="flex justify-between items-center mb-6 border-b border-gray-100 pb-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">Nhật ký Hoạt động (Audit Logs)</h1>
        <p class="text-gray-500 text-sm mt-1">Lịch sử ghi lại toàn bộ các thao tác thêm, sửa, xóa, duyệt và phân quyền tài liệu trong hệ thống.</p>
      </div>
    </div>

    <!-- Bộ lọc hành động -->
    <div class="flex gap-4 mb-6">
      <select 
        v-model="filterAction"
        @change="loadData"
        class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        <option value="">Tất cả hành động</option>
        <option value="TAO_MOI">Tạo mới</option>
        <option value="CAP_NHAT">Cập nhật</option>
        <option value="XOA">Xóa</option>
        <option value="XEM_CHI_TIET">Xem chi tiết</option>
        <option value="PHE_DUYET">Phê duyệt</option>
        <option value="TU_CHOI">Từ chối</option>
      </select>
      <button 
        @click="loadData"
        class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-lg shadow-sm transition-all duration-200 text-sm"
      >
        Lọc nhật ký
      </button>
    </div>

    <!-- Loading & Error -->
    <div v-if="loading" class="p-12 text-center text-gray-500 text-base">
      Đang tải nhật ký từ máy chủ...
    </div>
    <div v-else-if="errorMsg" class="p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg mb-6 text-sm">
      {{ errorMsg }}
    </div>

    <!-- Bảng logs -->
    <div v-else class="overflow-x-auto rounded-lg border border-gray-100 shadow-sm bg-white">
      <table class="w-full border-collapse text-left text-sm text-gray-500">
        <thead class="bg-gray-50 text-xs font-semibold uppercase text-gray-700 border-b border-gray-200">
          <tr>
            <th class="px-6 py-4">Mã Log</th>
            <th class="px-6 py-4">Người dùng</th>
            <th class="px-6 py-4">Hành động</th>
            <th class="px-6 py-4">Chi tiết hoạt động</th>
            <th class="px-6 py-4">Thời gian</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100 border-t border-gray-100">
          <tr v-for="item in items" :key="item.id" class="hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4 font-semibold text-gray-900">#LOG-{{ item.id }}</td>
            <td class="px-6 py-4 font-medium text-gray-800">{{ item.tenNguoiDung || 'Hệ thống' }}</td>
            <td class="px-6 py-4">
              <span :class="getActionBadgeClass(item.loaiHanhDong)">
                {{ item.loaiHanhDong }}
              </span>
            </td>
            <td class="px-6 py-4">
              <div>{{ item.moTa }}</div>
              <div v-if="item.tenTaiLieu" class="text-xs text-gray-400 mt-0.5">Tài liệu: {{ item.tenTaiLieu }}</div>
            </td>
            <td class="px-6 py-4">{{ formatDateTime(item.timeLog) }}</td>
          </tr>
          <tr v-if="items.length === 0">
            <td colspan="5" class="px-6 py-8 text-center text-gray-400">
              Chưa có nhật ký hoạt động nào được ghi nhận.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
    <div v-if="!loading && !errorMsg && totalPages > 1" class="flex justify-between items-center mt-6">
      <span class="text-sm text-gray-500">
        Trang {{ pageIndex + 1 }} / {{ totalPages }} (Tổng cộng {{ totalElements }} bản ghi)
      </span>
      <div class="flex gap-2">
        <button 
          :disabled="pageIndex === 0" 
          @click="goToPage(pageIndex - 1)" 
          class="px-3 py-1 border rounded disabled:opacity-50 text-sm font-semibold hover:bg-gray-50"
        >
          Trước
        </button>
        <button 
          :disabled="pageIndex >= totalPages - 1" 
          @click="goToPage(pageIndex + 1)" 
          class="px-3 py-1 border rounded disabled:opacity-50 text-sm font-semibold hover:bg-gray-50"
        >
          Sau
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { activityLogApi } from '../api/activityLogs';

const items = ref([]);
const loading = ref(false);
const errorMsg = ref('');
const filterAction = ref('');

// Phân trang
const pageIndex = ref(0);
const totalPages = ref(1);
const totalElements = ref(0);

const loadData = async () => {
  loading.value = true;
  errorMsg.value = '';
  try {
    const params = {
      page: pageIndex.value,
      size: 10
    };
    if (filterAction.value) {
      params.loaiHanhDong = filterAction.value;
    }
    const res = await activityLogApi.getAll(params);
    items.value = res.data.data.content;
    totalPages.value = res.data.data.totalPages;
    totalElements.value = res.data.data.totalElements;
  } catch (err) {
    console.error('Lỗi khi tải nhật ký hoạt động:', err);
    errorMsg.value = 'Không thể tải lịch sử nhật ký hoạt động từ máy chủ.';
  } finally {
    loading.value = false;
  }
};

const goToPage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    pageIndex.value = page;
    loadData();
  }
};

const formatDateTime = (val) => {
  if (!val) return '---';
  return val.replace('T', ' ').substring(0, 19);
};

const getActionBadgeClass = (action) => {
  if (!action) return 'inline-flex items-center rounded bg-gray-50 px-2 py-0.5 text-xs font-semibold text-gray-600';
  const a = String(action).toUpperCase();
  if (a.includes('TAO')) return 'inline-flex items-center rounded bg-green-50 px-2 py-0.5 text-xs font-semibold text-green-600';
  if (a.includes('CAP_NHAT') || a.includes('SUA')) return 'inline-flex items-center rounded bg-blue-50 px-2 py-0.5 text-xs font-semibold text-blue-600';
  if (a.includes('XOA')) return 'inline-flex items-center rounded bg-red-50 px-2 py-0.5 text-xs font-semibold text-red-600';
  if (a.includes('PHE_DUYET') || a.includes('DUYET')) return 'inline-flex items-center rounded bg-yellow-50 px-2 py-0.5 text-xs font-semibold text-yellow-600';
  return 'inline-flex items-center rounded bg-gray-50 px-2 py-0.5 text-xs font-semibold text-gray-600';
};

onMounted(() => {
  loadData();
});
</script>
