<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-7xl mx-auto">
    <div class="flex justify-between items-center mb-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
          <span>💼</span> Hồ sơ Vụ việc
        </h1>
        <p class="text-gray-500 text-sm mt-1">Quản lý các vụ việc, hồ sơ tranh chấp đất đai, dân sự, lao động.</p>
      </div>
      <button 
        @click="openCreateModal"
        class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2.5 px-4 rounded-lg shadow-sm transition-all duration-200 text-sm flex items-center gap-2"
      >
        <span>+</span> Tạo vụ việc
      </button>
    </div>

    <!-- Bộ lọc -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
      <div class="relative">
        <input 
          v-model="searchKeyword"
          type="text" 
          placeholder="Tìm kiếm vụ việc..." 
          @keyup.enter="loadData"
          class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
        />
        <span class="absolute left-3 top-2.5 text-gray-400">🔍</span>
      </div>
      <select 
        v-model="filterStatus"
        @change="loadData"
        class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        <option value="">Tất cả trạng thái</option>
        <option value="MOI_TIEP_NHAN">Mới tiếp nhận</option>
        <option value="DANG_XU_LY">Đang xử lý</option>
        <option value="DA_DONG">Đã đóng</option>
      </select>
      <select 
        v-model="filterType"
        @change="loadData"
        class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        <option value="">Lĩnh vực</option>
        <option value="Dan su">Dân sự</option>
        <option value="Dat dai">Đất đai</option>
        <option value="Lao dong">Lao động</option>
      </select>
      <button 
        @click="loadData"
        class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-lg shadow-sm transition-all duration-200 text-sm"
      >
        Lọc dữ liệu
      </button>
    </div>

    <!-- Loading & Error States -->
    <div v-if="loading" class="text-center py-8 text-gray-500">Đang tải dữ liệu vụ việc...</div>
    <div v-else-if="errorMsg" class="p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg mb-6 text-sm">
      ⚠️ {{ errorMsg }}
    </div>

    <div v-else-if="filteredItems.length === 0" class="text-center py-12 bg-gray-50 rounded border border-dashed border-gray-300">
      <p class="text-gray-500 font-medium">Không có vụ việc nào</p>
    </div>

    <!-- Bảng danh sách vụ việc -->
    <div v-else class="overflow-x-auto rounded-lg border border-gray-100 shadow-sm bg-white">
      <table class="w-full border-collapse text-left text-sm text-gray-500">
        <thead class="bg-gray-50 text-xs font-semibold uppercase text-gray-700 border-b border-gray-200">
          <tr>
            <th class="px-6 py-4">Tên vụ việc</th>
            <th class="px-6 py-4">Loại hình</th>
            <th class="px-6 py-4">Khách hàng</th>
            <th class="px-6 py-4">Người phụ trách</th>
            <th class="px-6 py-4">Trạng thái</th>
            <th class="px-6 py-4">Ngày mở</th>
            <th class="px-6 py-4 text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100 border-t border-gray-100">
          <tr v-for="item in filteredItems" :key="item.id" class="hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4 font-semibold text-gray-900">{{ item.ten }}</td>
            <td class="px-6 py-4">{{ getLoaiLabel(item.loai) }}</td>
            <td class="px-6 py-4">
              <router-link v-if="item.khachHang" :to="`/clients/${item.khachHang.id}`" class="text-blue-600 hover:underline">
                {{ item.khachHang.ten }}
              </router-link>
              <span v-else>---</span>
            </td>
            <td class="px-6 py-4">{{ item.nguoiPhuTrach?.hoTen || '---' }}</td>
            <td class="px-6 py-4">
              <span :class="getStatusBadgeClass(item.trangThai)">
                {{ getStatusLabel(item.trangThai) }}
              </span>
            </td>
            <td class="px-6 py-4">{{ item.ngayMo || '---' }}</td>
            <td class="px-6 py-4 text-right">
              <div class="flex justify-end gap-2">
                <button @click="goToDetail(item.id)" class="text-blue-600 hover:text-blue-900 font-semibold">Chi tiết</button>
                <span class="text-gray-300">|</span>
                <button @click="handleDelete(item.id, item.ten)" class="text-red-600 hover:text-red-900 font-semibold">Xóa</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal Tạo Vụ Việc Mới -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-lg shadow-lg border border-gray-100">
        <h2 class="text-lg font-bold mb-4 text-gray-800">Tạo vụ việc mới</h2>
        <form @submit.prevent="handleCreateCase" class="space-y-4 text-sm">
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Tên vụ việc (*)</label>
            <input v-model="form.tenVuViec" required class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Loại vụ việc (*)</label>
            <select v-model="form.loaiVuViec" required class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none">
              <option value="Hon nhan">Hôn nhân & Gia đình</option>
              <option value="Dat dai">Đất đai & BĐS</option>
              <option value="Lao dong">Lao động</option>
              <option value="Dan su">Hợp đồng dân sự</option>
              <option value="Dan su">Dân sự khác</option>
            </select>
          </div>
          <div class="relative">
            <label class="block text-sm font-medium mb-1 text-gray-700">Khách hàng (*)</label>
            <div class="relative">
              <input
                type="text"
                v-model="clientSearchInput"
                @input="onClientInput"
                placeholder="Gõ ít nhất 2 ký tự để tìm..."
                class="w-full border p-2 rounded text-sm pr-8 focus:ring-2 focus:ring-blue-500 focus:outline-none"
                required
              />
              <span v-if="isSearchingClient" class="absolute right-2 top-2.5 text-xs text-gray-400">⏳</span>
            </div>

            <div
              v-if="showClientSuggestions"
              class="absolute left-0 right-0 top-full mt-1 bg-white border rounded-md shadow-lg z-50 max-h-48 overflow-y-auto"
            >
              <div
                v-for="c in clientSuggestions"
                :key="c.id"
                @click="selectClient(c)"
                class="p-2 hover:bg-blue-50 cursor-pointer text-xs border-b flex justify-between"
              >
                <span class="font-bold text-gray-800">{{ c.ten }}</span>
                <span class="text-gray-400">[{{ c.loai === 'TO_CHUC' ? 'Tổ chức' : 'Cá nhân' }}]</span>
              </div>

              <div
                v-if="clientSuggestions.length === 0 && clientSearchInput.length >= 2"
                @click="openInlineCreateClient"
                class="p-2 bg-amber-50 hover:bg-amber-100 text-amber-700 text-xs font-semibold cursor-pointer text-center"
              >
                + Tạo khách hàng mới: "{{ clientSearchInput }}"
              </div>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Luật sư phụ trách (*)</label>
            <select v-model="form.luatSuId" required class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none">
              <option value="" disabled>-- Chọn luật sư --</option>
              <option v-for="u in lawyersList" :key="u.id" :value="u.id">{{ u.hoTen }} ({{ u.vaiTro }})</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Ngày mở</label>
            <input v-model="form.ngayMo" type="date" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Ghi chú</label>
            <textarea v-model="form.ghiChu" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none"></textarea>
          </div>

          <div class="flex justify-end space-x-2 pt-4 border-t border-gray-100">
            <button type="button" @click="showModal = false" class="px-4 py-2 border rounded hover:bg-gray-50 transition text-sm">Hủy</button>
            <button type="submit" :disabled="submitting" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded transition text-sm font-semibold">
              {{ submitting ? 'Đang tạo...' : 'Tạo vụ việc' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal Thêm nhanh khách hàng -->
    <div v-if="showInlineClientModal" class="fixed inset-0 bg-black/60 flex items-center justify-center z-[60]">
      <div class="bg-white rounded-lg p-5 w-full max-w-sm space-y-3 shadow-lg">
        <h3 class="font-bold text-base text-gray-800">Thêm nhanh khách hàng</h3>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">Tên khách hàng (*)</label>
          <input v-model="inlineClientForm.ten" placeholder="Tên khách hàng" class="w-full border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none" />
        </div>
        <div>
          <label class="block text-xs font-medium text-gray-500 mb-1">Loại khách hàng (*)</label>
          <select v-model="inlineClientForm.loai" class="w-full border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none">
            <option value="CA_NHAN">Cá nhân</option>
            <option value="TO_CHUC">Tổ chức</option>
          </select>
        </div>
        <div class="flex justify-end space-x-2 pt-2 border-t">
          <button type="button" @click="showInlineClientModal = false" class="px-3 py-1.5 border text-xs rounded hover:bg-gray-50 transition">Hủy</button>
          <button type="button" @click="handleSaveInlineClient" class="px-3 py-1.5 bg-blue-600 hover:bg-blue-700 text-white text-xs rounded transition font-semibold">Lưu & Chọn</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { caseApi } from '../../api/cases';
import { clientApi } from '../../api/clients';
import { adminApi } from '../../api/admin';
import { useRouter } from 'vue-router';

const router = useRouter();
const items = ref([]);
const clientsList = ref([]);
const lawyersList = ref([]);
const loading = ref(false);
const errorMsg = ref('');
const showModal = ref(false);
const submitting = ref(false);

const searchKeyword = ref('');
const filterStatus = ref('');
const filterType = ref('');

const clientSearchInput = ref('');
const clientSuggestions = ref([]);
const showClientSuggestions = ref(false);
const isSearchingClient = ref(false);
const showInlineClientModal = ref(false);
const inlineClientForm = ref({ ten: '', loai: 'CA_NHAN' });

let debounceTimer = null;

const onClientInput = () => {
  form.value.khachHangId = ''; // Reset ID khi user sửa text
  clearTimeout(debounceTimer);
  
  if (clientSearchInput.value.length < 2) {
    clientSuggestions.value = [];
    showClientSuggestions.value = false;
    return;
  }

  isSearchingClient.value = true;
  debounceTimer = setTimeout(async () => {
    try {
      const res = await clientApi.getAll({ ten: clientSearchInput.value });
      const allClients = res.data?.data || [];
      clientSuggestions.value = allClients.slice(0, 5); // Lấy tối đa 5 kết quả gợi ý
      showClientSuggestions.value = true;
    } catch (e) {
      console.error(e);
    } finally {
      isSearchingClient.value = false;
    }
  }, 300);
};

const selectClient = (client) => {
  form.value.khachHangId = client.id;
  clientSearchInput.value = client.ten;
  showClientSuggestions.value = false;
};

const openInlineCreateClient = () => {
  inlineClientForm.value.ten = clientSearchInput.value;
  inlineClientForm.value.loai = 'CA_NHAN';
  showClientSuggestions.value = false;
  showInlineClientModal.value = true;
};

const handleSaveInlineClient = async () => {
  if (!inlineClientForm.value.ten.trim()) {
    alert('Vui lòng nhập tên khách hàng!');
    return;
  }
  try {
    const res = await clientApi.create(inlineClientForm.value);
    const newClient = res.data?.data || res.data;
    selectClient(newClient);
    showInlineClientModal.value = false;
  } catch (err) {
    alert('Lỗi tạo khách hàng: ' + err.message);
  }
};

const form = ref({
  tenVuViec: '',
  loaiVuViec: 'Dan su',
  khachHangId: '',
  luatSuId: '',
  ngayMo: new Date().toISOString().substring(0, 10),
  trangThai: 'DANG_XU_LY',
  ghiChu: ''
});

const loadData = async () => {
  loading.value = true;
  errorMsg.value = '';
  try {
    const params = {};
    if (filterStatus.value) {
      params.trangThai = filterStatus.value;
    }
    if (filterType.value) {
      params.loai = filterType.value;
    }
    const res = await caseApi.getAll(params);
    // Bóc tách đúng lớp dữ liệu: res.data.data.content hoặc res.data.data trực tiếp
    const content = res.data?.data?.content || res.data?.data || res.data?.content || res.data || [];
    items.value = Array.isArray(content) ? content : [];
  } catch (err) {
    console.error('Lỗi tải danh sách vụ việc:', err);
    errorMsg.value = 'Không thể kết nối đến API lấy danh sách vụ việc. Hãy chắc chắn Spring Boot port 8080 đang chạy!';
  } finally {
    loading.value = false;
  }
};

const openCreateModal = async () => {
  showModal.value = true;
  try {
    const [resClients, resUsers] = await Promise.all([
      clientApi.getAll(),
      adminApi.users.getAll()
    ]);
    clientsList.value = resClients.data?.data || [];
    
    const allUsers = resUsers.data?.data || [];
    lawyersList.value = allUsers.filter(u => u.vaiTro === 'NHAN_VIEN' || u.vaiTro === 'TRUONG_PHONG');
  } catch (err) {
    console.error('Lỗi nạp danh mục modal:', err);
  }
};

const handleCreateCase = async () => {
  submitting.value = true;
  try {
    // Map DTO keys to match backend Spring Boot CaseRequest
    const payload = {
      ten: form.value.tenVuViec,
      loai: form.value.loaiVuViec,
      khachHangId: form.value.khachHangId,
      nguoiPhuTrachId: form.value.luatSuId,
      ngayMo: form.value.ngayMo,
      trangThai: form.value.trangThai,
      ghiChu: form.value.ghiChu
    };
    await caseApi.create(payload);
    alert('Tạo vụ việc thành công');
    showModal.value = false;
    // Reset form
    clientSearchInput.value = '';
    form.value = {
      tenVuViec: '',
      loaiVuViec: 'Dan su',
      khachHangId: '',
      luatSuId: '',
      ngayMo: new Date().toISOString().substring(0, 10),
      trangThai: 'DANG_XU_LY',
      ghiChu: ''
    };
    await loadData(); // Tải lại danh sách
  } catch (err) {
    alert('Lỗi: ' + (err.response?.data?.message || err.message));
  } finally {
    submitting.value = false;
  }
};

// Lọc local theo từ khóa tìm kiếm
const filteredItems = computed(() => {
  if (!searchKeyword.value) return items.value;
  const kw = searchKeyword.value.toLowerCase().trim();
  return items.value.filter(item => 
    item.ten.toLowerCase().includes(kw) || 
    (item.khachHang?.ten && item.khachHang.ten.toLowerCase().includes(kw))
  );
});

const goToDetail = (id) => {
  router.push(`/cases/${id}`);
};

const handleDelete = async (id, name) => {
  if (confirm(`Bạn có chắc chắn muốn xóa hồ sơ vụ việc: "${name}"?`)) {
    try {
      await caseApi.delete(id);
      alert('Xóa hồ sơ vụ việc thành công!');
      loadData();
    } catch (err) {
      alert('Lỗi khi xóa vụ việc: ' + (err.response?.data?.message || err.message));
    }
  }
};

const getLoaiLabel = (loai) => {
  if (!loai) return '---';
  if (loai === 'Hon nhan') return 'Hôn nhân & Gia đình';
  if (loai === 'Dat dai') return 'Đất đai & BĐS';
  if (loai === 'Lao dong') return 'Lao động';
  if (loai === 'Dan su') return 'Dân sự';
  return loai;
};

const getStatusLabel = (trangThai) => {
  if (!trangThai) return 'Chưa rõ';
  const st = String(trangThai).toUpperCase();
  if (st === 'MOI_TIEP_NHAN') return 'Mới tiếp nhận';
  if (st === 'DANG_XU_LY') return 'Đang xử lý';
  if (st === 'DA_DONG') return 'Đã đóng';
  return trangThai;
};

const getStatusBadgeClass = (trangThai) => {
  if (!trangThai) return 'inline-flex items-center rounded bg-gray-50 px-2 py-0.5 text-xs font-medium text-gray-600';
  const st = String(trangThai).toUpperCase();
  if (st === 'MOI_TIEP_NHAN') return 'inline-flex items-center rounded bg-yellow-50 px-2 py-0.5 text-xs font-semibold text-yellow-600';
  if (st === 'DANG_XU_LY') return 'inline-flex items-center rounded bg-blue-50 px-2 py-0.5 text-xs font-semibold text-blue-600';
  if (st === 'DA_DONG') return 'inline-flex items-center rounded bg-green-50 px-2 py-0.5 text-xs font-semibold text-green-600';
  return 'inline-flex items-center rounded bg-gray-50 px-2 py-0.5 text-xs font-medium text-gray-600';
};

onMounted(() => {
  loadData();
});
</script>
