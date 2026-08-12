<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-7xl mx-auto">
    <div class="flex justify-between items-center mb-6">
      <div>
        <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
          <span>👥</span> Quản lý Khách hàng
        </h1>
        <p class="text-gray-500 text-sm mt-1">Danh sách đối tác, khách hàng cá nhân và tổ chức của công ty luật.</p>
      </div>
      <button 
        @click="openCreateModal"
        class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2.5 px-4 rounded-lg shadow-sm transition-all duration-200 text-sm flex items-center gap-2"
      >
        <span>+</span> Thêm khách hàng
      </button>
    </div>

    <!-- Thanh tìm kiếm & Bộ lọc -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div class="relative">
        <input 
          v-model="searchKeyword"
          type="text" 
          placeholder="Tìm kiếm tên..." 
          @keyup.enter="loadData"
          class="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent text-sm"
        />
        <span class="absolute left-3 top-2.5 text-gray-400">🔍</span>
      </div>
      <select 
        v-model="filterType"
        @change="loadData"
        class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        <option value="">Tất cả phân loại</option>
        <option value="CA_NHAN">Cá nhân</option>
        <option value="TO_CHUC">Tổ chức</option>
      </select>
      <button 
        @click="loadData"
        class="bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 px-4 rounded-lg shadow-sm transition-all duration-200 text-sm"
      >
        Tìm kiếm
      </button>
    </div>

    <!-- Loading & Error States -->
    <div v-if="loading" class="p-12 text-center text-gray-500 text-base">
      🔄 Đang tải dữ liệu thực tế từ MySQL...
    </div>
    <div v-else-if="errorMsg" class="p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg mb-6 text-sm">
      ⚠️ {{ errorMsg }}
    </div>

    <!-- Bảng danh sách khách hàng -->
    <div v-else class="overflow-x-auto rounded-lg border border-gray-100 shadow-sm bg-white">
      <table class="w-full border-collapse bg-white text-left text-sm text-gray-500">
        <thead class="bg-gray-50 text-xs font-semibold uppercase text-gray-700 border-b border-gray-200">
          <tr>
            <th class="px-6 py-4">Tên khách hàng</th>
            <th class="px-6 py-4">Loại</th>
            <th class="px-6 py-4">CCCD / Mã số thuế</th>
            <th class="px-6 py-4">Số điện thoại</th>
            <th class="px-6 py-4">Địa chỉ</th>
            <th class="px-6 py-4">Số vụ việc</th>
            <th class="px-6 py-4 text-right">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100 border-t border-gray-100">
          <tr v-for="item in items" :key="item.id" class="hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4">
              <div class="flex items-center gap-2">
                <span class="font-medium text-gray-900">{{ item.ten }}</span>
                <span 
                  v-if="!item.cccdMst || !item.sdt || !item.email || !item.diaChi" 
                  title="Thiếu thông tin quan trọng (CCCD/MST, SĐT, Email hoặc Địa chỉ)" 
                  class="cursor-pointer inline-flex items-center gap-1 rounded bg-amber-50 px-1.5 py-0.5 text-[10px] font-semibold text-amber-700 border border-amber-200"
                >
                  ⚠️ Thiếu thông tin
                </span>
              </div>
            </td>
            <td class="px-6 py-4">
              <span 
                :class="[
                  'inline-flex items-center gap-1 rounded-full px-2 py-1 text-xs font-semibold',
                  item.loai === 'TO_CHUC' ? 'bg-blue-50 text-blue-600' : 'bg-green-50 text-green-600'
                ]"
              >
                {{ item.loai === 'TO_CHUC' ? 'Tổ chức' : 'Cá nhân' }}
              </span>
            </td>
            <td class="px-6 py-4">{{ item.cccdMst || '---' }}</td>
            <td class="px-6 py-4">{{ item.sdt || '---' }}</td>
            <td class="px-6 py-4">{{ item.diaChi || '---' }}</td>
            <td class="px-6 py-4 font-semibold text-gray-700">{{ item.soVuViec || 0 }}</td>
            <td class="px-6 py-4 text-right">
              <div class="flex justify-end gap-2">
                <button @click="goToDetail(item.id)" class="text-blue-600 hover:text-blue-900 font-medium">Chi tiết</button>
                <span class="text-gray-300">|</span>
                <button @click="openEditModal(item)" class="text-indigo-600 hover:text-indigo-900 font-medium">Sửa</button>
                <span class="text-gray-300">|</span>
                <button @click="handleDelete(item.id, item.ten)" class="text-red-600 hover:text-red-900 font-medium">Xóa</button>
              </div>
            </td>
          </tr>
          <tr v-if="items.length === 0">
            <td colspan="7" class="px-6 py-8 text-center text-gray-400">
              Không tìm thấy khách hàng nào thỏa mãn điều kiện lọc.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal Thêm/Sửa Khách Hàng -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md shadow-lg border border-gray-100">
        <h2 class="text-lg font-bold mb-4 text-gray-800">
          {{ isEditMode ? 'Sửa thông tin khách hàng' : 'Thêm khách hàng mới' }}
        </h2>
        <form @submit.prevent="handleSubmitClient" class="space-y-4 text-sm">
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Tên khách hàng (*)</label>
            <input v-model="form.ten" required class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Loại khách hàng (*)</label>
            <select v-model="form.loai" required class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none">
              <option value="CA_NHAN">Cá nhân</option>
              <option value="TO_CHUC">Tổ chức</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">CCCD / Mã số thuế (*)</label>
            <input v-model="form.cccdMst" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" placeholder="Nhập để hoàn thiện hồ sơ" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Số điện thoại (*)</label>
            <input v-model="form.sdt" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" placeholder="Nhập để hoàn thiện hồ sơ" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Email (*)</label>
            <input v-model="form.email" type="email" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" placeholder="Nhập để hoàn thiện hồ sơ" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Địa chỉ (*)</label>
            <input v-model="form.diaChi" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" placeholder="Nhập để hoàn thiện hồ sơ" />
          </div>

          <div class="flex justify-end space-x-2 pt-4 border-t border-gray-100">
            <button type="button" @click="showModal = false" class="px-4 py-2 border rounded hover:bg-gray-50 transition text-sm">Hủy</button>
            <button type="submit" :disabled="submitting" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded transition text-sm font-semibold">
              {{ submitting ? 'Đang lưu...' : (isEditMode ? 'Lưu thay đổi' : 'Lưu khách hàng') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { clientApi } from '../../api/clients';
import { useRouter } from 'vue-router';

const router = useRouter();
const items = ref([]);
const loading = ref(false);
const errorMsg = ref('');
const searchKeyword = ref('');
const filterType = ref('');

// Modal state
const showModal = ref(false);
const isEditMode = ref(false);
const selectedClientId = ref(null);
const submitting = ref(false);
const form = ref({
  ten: '',
  loai: 'CA_NHAN',
  cccdMst: '',
  sdt: '',
  email: '',
  diaChi: ''
});

const loadData = async () => {
  loading.value = true;
  errorMsg.value = '';
  try {
    const params = {};
    if (searchKeyword.value) {
      params.ten = searchKeyword.value;
    }
    if (filterType.value) {
      params.loai = filterType.value;
    }
    const res = await clientApi.getAll(params);
    items.value = res.data.data;
  } catch (err) {
    console.error('Lỗi khi tải dữ liệu từ API:', err);
    errorMsg.value = 'Không thể kết nối đến API lấy danh sách khách hàng. Hãy chắc chắn Spring Boot port 8080 đang chạy!';
  } finally {
    loading.value = false;
  }
};

const openCreateModal = () => {
  isEditMode.value = false;
  selectedClientId.value = null;
  form.value = {
    ten: '',
    loai: 'CA_NHAN',
    cccdMst: '',
    sdt: '',
    email: '',
    diaChi: ''
  };
  showModal.value = true;
};

const openEditModal = (client) => {
  isEditMode.value = true;
  selectedClientId.value = client.id;
  form.value = {
    ten: client.ten || '',
    loai: client.loai || 'CA_NHAN',
    cccdMst: client.cccdMst || '',
    sdt: client.sdt || '',
    email: client.email || '',
    diaChi: client.diaChi || ''
  };
  showModal.value = true;
};

const handleSubmitClient = async () => {
  submitting.value = true;
  try {
    if (isEditMode.value) {
      await clientApi.update(selectedClientId.value, form.value);
      alert('Cập nhật thông tin khách hàng thành công');
    } else {
      await clientApi.create(form.value);
      alert('Thêm khách hàng thành công');
    }
    showModal.value = false;
    await loadData();
  } catch (err) {
    alert('Lỗi thao tác khách hàng: ' + (err.response?.data?.message || err.message));
  } finally {
    submitting.value = false;
  }
};

const goToDetail = (id) => {
  router.push(`/clients/${id}`);
};

const handleDelete = async (id, name) => {
  if (confirm(`Bạn có chắc chắn muốn xóa khách hàng: "${name}"?`)) {
    try {
      await clientApi.delete(id);
      alert('Xóa khách hàng thành công!');
      loadData();
    } catch (err) {
      alert('Lỗi khi xóa khách hàng: ' + (err.response?.data?.message || err.message));
    }
  }
};

onMounted(() => {
  loadData();
});
</script>
