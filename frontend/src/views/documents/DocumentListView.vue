<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <div>
        <h3 class="text-2xl font-bold text-gray-900">Kho Tài Liệu Nội Bộ</h3>
        <p class="text-sm text-gray-500 mt-1">Quản lý, phân quyền và lưu trữ văn bản pháp lý hành chính.</p>
      </div>
      <button @click="openCreateModal" class="px-4 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl shadow-md transition flex items-center gap-2 text-sm">
        ➕ Tạo tài liệu mới
      </button>
    </div>

    <div class="bg-white p-5 rounded-2xl border border-gray-200 shadow-sm grid grid-cols-1 md:grid-cols-5 gap-4">
      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-1.5">Từ khóa tìm kiếm</label>
        <input v-model="filters.q" @input="debounceSearch" type="text" class="filter-input" placeholder="Tên tệp, số hiệu..." />
      </div>
      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-1.5">Danh mục</label>
        <select v-model="filters.danhMucId" @change="fetchDocuments(0)" class="filter-input">
          <option value="">-- Tất cả danh mục --</option>
          <option v-for="item in selectOptions.categories" :key="item.id" :value="item.id">{{ item.ten }}</option>
        </select>
      </div>
      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-1.5">Loại văn bản</label>
        <select v-model="filters.loaiTaiLieuId" @change="fetchDocuments(0)" class="filter-input">
          <option value="">-- Tất cả loại --</option>
          <option v-for="item in selectOptions.docTypes" :key="item.id" :value="item.id">{{ item.ten }}</option>
        </select>
      </div>
      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-1.5">Hồ sơ vụ việc</label>
        <select v-model="filters.vuViecId" @change="fetchDocuments(0)" class="filter-input">
          <option value="">-- Tất cả vụ việc --</option>
          <option v-for="item in selectOptions.cases" :key="item.id" :value="item.id">{{ item.ten }}</option>
        </select>
      </div>
      <div>
        <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-1.5">Trạng thái phê duyệt</label>
        <select v-model="filters.trangThai" @change="fetchDocuments(0)" class="filter-input">
          <option value="">-- Tất cả trạng thái --</option>
          <option value="NHAP">Bản nháp</option>
          <option value="CHO_DUYET">Chờ duyệt</option>
          <option value="DA_DUYET">Đã duyệt</option>
          <option value="TU_CHOI">Từ chối</option>
        </select>
      </div>
    </div>

    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
      <ApiErrorMessage :error="error" />
      
      <LoadingSpinner v-if="loading" />

      <div v-else class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-gray-50 border-b border-gray-200 text-xs font-bold text-gray-400 uppercase tracking-wider">
              <th class="px-6 py-4">Tên tài liệu / Số hiệu</th>
              <th class="px-6 py-4">Danh mục</th>
              <th class="px-6 py-4">Loại văn bản</th>
              <th class="px-6 py-4">Ngày tạo</th>
              <th class="px-6 py-4 text-center">Trạng thái</th>
              <th class="px-6 py-4 text-right">Thao tác</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100 text-sm">
            <tr v-for="doc in documents" :key="doc.id" class="hover:bg-gray-50/80 transition">
              <td class="px-6 py-4 max-w-xs">
                <p @click="viewDetail(doc.id)" class="font-semibold text-blue-600 hover:underline cursor-pointer truncate">{{ doc.ten }}</p>
                <p class="text-xs text-gray-400 font-mono mt-0.5">{{ doc.soHieu || 'Không có số hiệu' }}</p>
              </td>
              <td class="px-6 py-4 text-gray-500 font-medium">{{ doc.danhMuc?.ten }}</td>
              <td class="px-6 py-4 text-gray-500 font-medium">{{ doc.loaiTaiLieu?.ten }}</td>
              <td class="px-6 py-4 text-gray-400 font-mono">{{ formatDate(doc.ngayTao) }}</td>
              <td class="px-6 py-4 text-center">
                <StatusBadge :status="doc.trangThai" />
              </td>
              <td class="px-6 py-4 text-right space-x-2">
                <button @click="viewDetail(doc.id)" class="text-xs font-bold text-gray-600 hover:text-blue-600 transition">Xem</button>
                <button @click="downloadFile(doc)" class="text-xs font-bold text-slate-600 hover:text-emerald-600 transition">Tải về</button>
                <button v-if="doc.trangThai === 'NHAP'" @click="triggerSubmitApproval(doc.id)" class="text-xs font-bold text-amber-600 hover:text-amber-700 transition">Gửi duyệt</button>
              </td>
            </tr>
            <tr v-if="documents.length === 0">
              <td colspan="6" class="text-center py-12 text-gray-400 font-medium">Không tìm thấy tài liệu phù hợp với bộ lọc.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <Pagination :currentPage="pageInfo.current" :totalPages="pageInfo.total" :totalElements="pageInfo.elements" @page-changed="fetchDocuments" />
    </div>

    <DocumentModal v-model="modalState.show" :documentId="modalState.selectedId" @saved="fetchDocuments(0)" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { documentApi } from '../../api/documents';
import { adminApi } from '../../api/admin';
import { caseApi } from '../../api/cases';
import StatusBadge from '../../components/common/StatusBadge.vue';
import Pagination from '../../components/common/Pagination.vue';
import ApiErrorMessage from '../../components/common/ApiErrorMessage.vue';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import DocumentModal from '../../components/documents/DocumentModal.vue';

const router = useRouter();
const loading = ref(false);
const error = ref(null);
const documents = ref([]);

const filters = reactive({ q: '', danhMucId: '', loaiTaiLieuId: '', vuViecId: '', trangThai: '' });
const pageInfo = reactive({ current: 0, total: 0, elements: 0 });
const modalState = reactive({ show: false, selectedId: null });
const selectOptions = reactive({ categories: [], docTypes: [], cases: [] });

let searchTimeout = null;

const fetchDropdowns = async () => {
  try {
    const [catRes, typeRes, caseRes] = await Promise.all([
      adminApi.categories.getAll(),
      adminApi.docTypes.getAll(),
      caseApi.getAll({ size: 100 })
    ]);
    selectOptions.categories = catRes.data?.data || [];
    selectOptions.docTypes = typeRes.data?.data || [];
    selectOptions.cases = caseRes.data?.data || [];
  } catch (err) {
    console.error('Lỗi nạp danh mục nền:', err);
  }
};

const fetchDocuments = async (page = 0) => {
  loading.value = true;
  error.value = null;
  try {
    const params = {
      page,
      size: 10,
      tuKhoa: filters.q || null,
      danhMucId: filters.danhMucId || null,
      loaiTaiLieuId: filters.loaiTaiLieuId || null,
      vuViecId: filters.vuViecId || null,
      trangThai: filters.trangThai || null
    };
    const res = await documentApi.getAll(params);
    if (res.data?.success) {
      documents.value = res.data.data.content;
      pageInfo.current = res.data.data.number;
      pageInfo.total = res.data.data.totalPages;
      pageInfo.elements = res.data.data.totalElements;
    }
  } catch (err) {
    error.value = err;
  } finally {
    loading.value = false;
  }
};

const debounceSearch = () => {
  clearTimeout(searchTimeout);
  searchTimeout = setTimeout(() => fetchDocuments(0), 400);
};

const triggerSubmitApproval = async (id) => {
  if (!confirm('Bạn có chắc chắn muốn trình duyệt tài liệu này lên Cấp quản lý?')) return;
  try {
    const res = await documentApi.submit(id);
    if (res.data?.success) {
      alert('Đã gửi yêu cầu phê duyệt thành công.');
      fetchDocuments(pageInfo.current);
    }
  } catch (err) {
    alert(err.response?.data?.message || 'Không thể gửi duyệt văn bản.');
  }
};

const downloadFile = async (doc) => {
  try {
    const res = await documentApi.download(doc.id);
    const blob = new Blob([res.data]);
    const link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = doc.ten + '.' + (doc.dinhDang || 'docx');
    link.click();
  } catch (err) {
    alert('Tải tệp tin thất bại hoặc bạn không có quyền tải văn bản này.');
  }
};

const openCreateModal = () => {
  modalState.selectedId = null;
  modalState.show = true;
};

const viewDetail = (id) => router.push(`/documents/${id}`);
const formatDate = (dateStr) => dateStr ? dateStr.substring(0, 10) : '---';

onMounted(() => {
  fetchDropdowns();
  fetchDocuments();
});
</script>

<style scoped>
.filter-input {
  width: 100%;
  font-size: 0.875rem;
  padding: 0.625rem 0.875rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  background-color: #f8fafc;
  outline: none;
  transition: all 0.2s;
}
.filter-input:focus {
  border-color: #3b82f6;
  background-color: #ffffff;
  box-shadow: 0 0 0 2px rgba(59, 131, 246, 0.15);
}
</style>
