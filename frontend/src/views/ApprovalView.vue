<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-7xl mx-auto space-y-6">
    <div class="flex justify-between items-center mb-4">
      <h1 class="text-2xl font-bold text-gray-800">Quản lý Phê duyệt Văn bản</h1>
      <button 
        @click="fetchAllData" 
        :disabled="loading"
        class="px-4 py-2 bg-gray-100 hover:bg-gray-200 text-gray-700 rounded-md text-sm font-medium flex items-center space-x-1 transition"
      >
        <span>Làm mới</span>
      </button>
    </div>

    <!-- 4 Stats/Filtering Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div
        @click="activeFilter = 'ALL'"
        class="p-4 rounded-lg border cursor-pointer transition-all bg-white shadow-sm hover:border-blue-300"
        :class="activeFilter === 'ALL' ? 'border-blue-500 ring-2 ring-blue-100 bg-blue-50/30' : 'border-gray-200'"
      >
        <p class="text-xs text-gray-500 font-semibold">TẤT CẢ VĂN BẢN</p>
        <h3 class="text-2xl font-bold text-gray-800 mt-1">{{ counts.pending + counts.approved + counts.rejected }}</h3>
      </div>

      <div
        @click="activeFilter = 'CHO_DUYET'"
        class="p-4 rounded-lg border cursor-pointer transition-all bg-white shadow-sm hover:border-amber-300"
        :class="activeFilter === 'CHO_DUYET' ? 'border-amber-500 ring-2 ring-amber-100 bg-amber-50/30' : 'border-gray-200'"
      >
        <p class="text-xs text-amber-600 font-semibold">CHỜ DUYỆT</p>
        <h3 class="text-2xl font-bold text-amber-600 mt-1">{{ counts.pending }}</h3>
      </div>

      <div
        @click="activeFilter = 'DA_DUYET'"
        class="p-4 rounded-lg border cursor-pointer transition-all bg-white shadow-sm hover:border-green-300"
        :class="activeFilter === 'DA_DUYET' ? 'border-green-500 ring-2 ring-green-100 bg-green-50/30' : 'border-gray-200'"
      >
        <p class="text-xs text-green-600 font-semibold">ĐÃ DUYỆT</p>
        <h3 class="text-2xl font-bold text-green-600 mt-1">{{ counts.approved }}</h3>
      </div>

      <div
        @click="activeFilter = 'TU_CHOI'"
        class="p-4 rounded-lg border cursor-pointer transition-all bg-white shadow-sm hover:border-red-300"
        :class="activeFilter === 'TU_CHOI' ? 'border-red-500 ring-2 ring-red-100 bg-red-50/30' : 'border-gray-200'"
      >
        <p class="text-xs text-red-600 font-semibold">TỪ CHỐI</p>
        <h3 class="text-2xl font-bold text-red-600 mt-1">{{ counts.rejected }}</h3>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="text-center py-8 text-gray-500">
      Đang tải danh sách tài liệu...
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredDocuments.length === 0" class="text-center py-12 bg-gray-50 rounded-lg border border-dashed border-gray-300">
      <p class="text-gray-500 font-medium">Không có tài liệu nào thuộc bộ lọc này</p>
    </div>

    <!-- Table -->
    <div v-else class="bg-white rounded-lg border border-gray-200 shadow-sm overflow-hidden">
      <table class="min-w-full divide-y divide-gray-200 text-sm">
        <thead class="bg-gray-50">
          <tr class="text-left text-xs font-semibold text-gray-500 uppercase">
            <th class="px-4 py-3">Tên tài liệu</th>
            <th class="px-4 py-3">Bộ phận đề xuất</th>
            <th class="px-4 py-3">Ngày gửi</th>
            <th class="px-4 py-3">Trạng thái</th>
            <th class="px-4 py-3 text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100 text-sm">
          <tr v-for="doc in filteredDocuments" :key="doc.id" class="hover:bg-gray-50">
            <td class="px-4 py-3 font-medium text-gray-900">
              <div>{{ doc.ten }}</div>
              <div class="text-xs text-gray-400 font-mono mt-0.5">Số hiệu: {{ doc.soHieu || '---' }}</div>
            </td>
            <td class="px-4 py-3 text-gray-600">{{ doc.boPhan?.ten || 'Cán bộ hệ thống' }}</td>
            <td class="px-4 py-3 text-gray-500">{{ formatDate(doc.ngayTao) }}</td>
            <td class="px-4 py-3">
              <span class="px-2 py-0.5 text-xs rounded-full font-medium" :class="getBadgeClass(doc.trangThai)">
                {{ getBadgeText(doc.trangThai) }}
              </span>
            </td>
            <td class="px-4 py-3 text-right space-x-2">
              <template v-if="isStatus(doc.trangThai, 'CHO_DUYET')">
                <button @click="handleApprove(doc)" class="px-2.5 py-1 bg-green-600 hover:bg-green-700 text-white rounded text-xs font-semibold transition shadow-sm">Duyệt</button>
                <button @click="openRejectModal(doc)" class="px-2.5 py-1 bg-red-600 hover:bg-red-700 text-white rounded text-xs font-semibold transition shadow-sm">Từ chối</button>
              </template>

              <template v-else-if="isStatus(doc.trangThai, 'DA_DUYET')">
                <router-link :to="`/documents/${doc.id}`" class="px-2.5 py-1 border text-gray-600 hover:bg-gray-50 rounded text-xs font-semibold transition">Xem</router-link>
              </template>

              <template v-else-if="isStatus(doc.trangThai, 'TU_CHOI')">
                <router-link :to="`/documents/${doc.id}`" class="px-2.5 py-1 border text-gray-600 hover:bg-gray-50 rounded text-xs font-semibold transition">Xem</router-link>
                <button @click="handleResubmit(doc)" class="px-2.5 py-1 bg-amber-500 hover:bg-amber-600 text-white rounded text-xs font-semibold transition shadow-sm">Gửi lại</button>
              </template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Reject Modal -->
    <div v-if="showRejectModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-5 w-full max-w-md space-y-4 shadow-lg border">
        <h3 class="font-bold text-lg text-gray-800">Lý do từ chối phê duyệt</h3>
        <textarea
          v-model="rejectReason"
          placeholder="Nhập lý do chi tiết từ chối..."
          class="w-full border p-2 rounded text-sm h-28 focus:ring-2 focus:ring-red-500 focus:outline-none"
          :class="{ 'border-red-500': rejectError }"
        ></textarea>
        <p v-if="rejectError" class="text-xs text-red-500 font-medium">Vui lòng nhập lý do trước khi từ chối!</p>

        <div class="flex justify-end space-x-2 pt-2">
          <button @click="showRejectModal = false" class="px-4 py-2 border rounded text-sm hover:bg-gray-50 transition">Hủy</button>
          <button @click="confirmReject" class="px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded text-sm font-semibold transition">Xác nhận từ chối</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { documentApi } from '../api/documents';

const activeFilter = ref('CHO_DUYET'); // Mặc định khi vào là Chờ duyệt
const documents = ref([]);
const loading = ref(false);
const counts = ref({ pending: 0, approved: 0, rejected: 0 });

const showRejectModal = ref(false);
const rejectReason = ref('');
const rejectError = ref(false);
const selectedDoc = ref(null);

const fetchAllData = async () => {
  loading.value = true;
  try {
    const res = await documentApi.getAll({ size: 100 });
    const list = res.data?.data?.content || res.data?.data || [];
    documents.value = list;

    // Cập nhật số liệu 3 Card
    counts.value.pending = list.filter(d => isStatus(d.trangThai, 'CHO_DUYET')).length;
    counts.value.approved = list.filter(d => isStatus(d.trangThai, 'DA_DUYET')).length;
    counts.value.rejected = list.filter(d => isStatus(d.trangThai, 'TU_CHOI')).length;
  } catch (err) {
    console.error('Lỗi lấy danh sách phê duyệt:', err);
  } finally {
    loading.value = false;
  }
};

const filteredDocuments = computed(() => {
  if (activeFilter.value === 'ALL') return documents.value;
  return documents.value.filter(d => isStatus(d.trangThai, activeFilter.value));
});

const isStatus = (status, check) => {
  if (!status) return false;
  const s = String(status).toUpperCase();
  if (check === 'CHO_DUYET') {
    return s === 'CHO_DUYET' || s === 'CHO_PHE_DUYET';
  }
  return s === check;
};

const getBadgeClass = (s) => {
  if (isStatus(s, 'DA_DUYET')) return 'bg-green-100 text-green-700';
  if (isStatus(s, 'CHO_DUYET')) return 'bg-amber-100 text-amber-700';
  if (isStatus(s, 'TU_CHOI')) return 'bg-red-100 text-red-700';
  return 'bg-gray-100 text-gray-700';
};

const getBadgeText = (s) => {
  if (isStatus(s, 'DA_DUYET')) return 'Đã duyệt';
  if (isStatus(s, 'CHO_DUYET')) return 'Chờ duyệt';
  if (isStatus(s, 'TU_CHOI')) return 'Từ chối';
  return 'Bản nháp';
};

const handleApprove = async (doc) => {
  if (confirm(`Bạn có chắc chắn muốn duyệt tài liệu "${doc.ten}"?`)) {
    try {
      await documentApi.approve(doc.id);
      alert('Đã phê duyệt tài liệu thành công!');
      await fetchAllData();
    } catch (err) {
      alert('Phê duyệt lỗi: ' + (err.response?.data?.message || err.message));
    }
  }
};

const openRejectModal = (doc) => {
  selectedDoc.value = doc;
  rejectReason.value = '';
  rejectError.value = false;
  showRejectModal.value = true;
};

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    rejectError.value = true;
    return;
  }
  if (selectedDoc.value) {
    try {
      await documentApi.reject(selectedDoc.value.id, { ghiChu: rejectReason.value });
      showRejectModal.value = false;
      alert('Đã từ chối tài liệu!');
      await fetchAllData();
    } catch (err) {
      alert('Lỗi từ chối tài liệu: ' + (err.response?.data?.message || err.message));
    }
  }
};

const handleResubmit = async (doc) => {
  if (confirm(`Bạn muốn gửi duyệt lại tài liệu "${doc.ten}"?`)) {
    try {
      await documentApi.submit(doc.id);
      alert('Đã gửi lại yêu cầu duyệt!');
      await fetchAllData();
    } catch (err) {
      alert('Lỗi gửi duyệt lại: ' + (err.response?.data?.message || err.message));
    }
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return '---';
  return dateStr.replace('T', ' ').substring(0, 16);
};

onMounted(() => {
  fetchAllData();
});
</script>
