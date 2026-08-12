<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-7xl mx-auto">
    <div class="flex items-center justify-between mb-6">
      <router-link to="/clients" class="text-blue-600 hover:text-blue-800 text-sm font-semibold flex items-center gap-1">
        <span>←</span> Quay lại danh sách
      </router-link>
      <button 
        v-if="client"
        @click="openEditModal"
        class="bg-indigo-600 hover:bg-indigo-700 text-white font-semibold py-2 px-4 rounded-lg shadow-sm transition text-sm flex items-center gap-2"
      >
        <span>✏️</span> Sửa thông tin
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="p-12 text-center text-gray-500 text-base">
      🔄 Đang tải thông tin khách hàng từ MySQL...
    </div>

    <!-- Error State -->
    <div v-else-if="errorMsg" class="p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg mb-6 text-sm">
      ⚠️ {{ errorMsg }}
    </div>

    <!-- Content -->
    <div v-else-if="client">
      <!-- Alert thông báo thiếu thông tin quan trọng -->
      <div 
        v-if="!client.cccdMst || !client.sdt || !client.email || !client.diaChi" 
        class="p-4 bg-amber-50 border border-amber-200 text-amber-800 rounded-lg mb-6 flex items-start gap-3"
      >
        <span class="text-lg">⚠️</span>
        <div>
          <h4 class="font-bold text-sm">Hồ sơ khách hàng chưa hoàn thiện!</h4>
          <p class="text-xs mt-1">
            Khách hàng này đang thiếu các thông tin quan trọng: 
            <span class="font-semibold text-amber-900">
              {{ [
                !client.cccdMst && 'CCCD/Mã số thuế',
                !client.sdt && 'Số điện thoại',
                !client.email && 'Email',
                !client.diaChi && 'Địa chỉ'
              ].filter(Boolean).join(', ') }}
            </span>. Vui lòng bổ sung đầy đủ thông tin để phục vụ nghiệp vụ pháp lý.
          </p>
        </div>
      </div>

      <div class="border-b border-gray-100 pb-6 mb-6">
        <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
          <span>👤</span> Chi tiết Khách hàng: {{ client.ten }}
        </h1>
        <p class="text-gray-500 text-sm mt-1">Thông tin chi tiết hồ sơ cá nhân/tổ chức và danh sách vụ việc liên quan.</p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Cột thông tin chi tiết -->
        <div class="lg:col-span-1 bg-gray-50 rounded-lg p-5 border border-gray-200">
          <h2 class="text-lg font-bold text-gray-800 mb-4 border-b pb-2">Thông tin cơ bản</h2>
          <div class="space-y-3 text-sm text-gray-600">
            <div>
              <span class="block font-semibold text-gray-500">Tên khách hàng:</span>
              <span class="text-gray-900 font-semibold">{{ client.ten }}</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Loại khách hàng:</span>
              <span class="text-gray-900">{{ client.loai === 'TO_CHUC' ? 'Tổ chức' : 'Cá nhân' }}</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Số CCCD / Mã số thuế:</span>
              <span class="text-gray-900 font-medium">{{ client.cccdMst || '---' }}</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Số điện thoại:</span>
              <span class="text-gray-900">{{ client.sdt || '---' }}</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Email:</span>
              <span class="text-gray-900">{{ client.email || '---' }}</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Địa chỉ:</span>
              <span class="text-gray-900">{{ client.diaChi || '---' }}</span>
            </div>
          </div>
        </div>

        <!-- Cột vụ việc liên quan -->
        <div class="lg:col-span-2">
          <h2 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
            <span>💼</span> Các vụ việc tham gia ({{ client.dsVuViec?.length || 0 }})
          </h2>
          <div class="overflow-x-auto rounded-lg border border-gray-200 shadow-sm bg-white">
            <table class="w-full border-collapse text-left text-sm text-gray-500">
              <thead class="bg-gray-50 text-xs font-semibold uppercase text-gray-700 border-b border-gray-200">
                <tr>
                  <th class="px-6 py-4">Tên vụ việc</th>
                  <th class="px-6 py-4">Trạng thái</th>
                  <th class="px-6 py-4 text-right">Hành động</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 border-t border-gray-100">
                <tr v-for="c in client.dsVuViec" :key="c.id" class="hover:bg-gray-50 transition-colors">
                  <td class="px-6 py-4 font-medium text-blue-600 hover:underline cursor-pointer" @click="goToCaseDetail(c.id)">
                    {{ c.ten }}
                  </td>
                  <td class="px-6 py-4">
                    <span :class="getStatusBadgeClass(c.trangThai)">
                      {{ getStatusLabel(c.trangThai) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-right">
                    <button @click="goToCaseDetail(c.id)" class="text-blue-600 hover:text-blue-900 font-semibold">Chi tiết</button>
                  </td>
                </tr>
                <tr v-if="!client.dsVuViec || client.dsVuViec.length === 0">
                  <td colspan="3" class="px-6 py-8 text-center text-gray-400">
                    Chưa có vụ việc pháp lý nào được lập cho khách hàng này.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal Sửa Khách Hàng -->
    <div v-if="showEditModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md shadow-lg border border-gray-100 text-sm">
        <h2 class="text-lg font-bold mb-4 text-gray-800">Sửa thông tin khách hàng</h2>
        <form @submit.prevent="handleUpdateClient" class="space-y-4">
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Tên khách hàng (*)</label>
            <input v-model="editForm.ten" required class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Loại khách hàng (*)</label>
            <select v-model="editForm.loai" required class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none">
              <option value="CA_NHAN">Cá nhân</option>
              <option value="TO_CHUC">Tổ chức</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">CCCD / Mã số thuế (*)</label>
            <input v-model="editForm.cccdMst" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" placeholder="Nhập để hoàn thiện hồ sơ" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Số điện thoại (*)</label>
            <input v-model="editForm.sdt" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" placeholder="Nhập để hoàn thiện hồ sơ" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Email (*)</label>
            <input v-model="editForm.email" type="email" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" placeholder="Nhập để hoàn thiện hồ sơ" />
          </div>
          <div>
            <label class="block text-sm font-medium mb-1 text-gray-700">Địa chỉ (*)</label>
            <input v-model="editForm.diaChi" class="w-full border p-2 rounded focus:ring-2 focus:ring-blue-500 focus:outline-none" placeholder="Nhập để hoàn thiện hồ sơ" />
          </div>

          <div class="flex justify-end space-x-2 pt-4 border-t border-gray-100">
            <button type="button" @click="showEditModal = false" class="px-4 py-2 border rounded hover:bg-gray-50 transition text-sm">Hủy</button>
            <button type="submit" :disabled="submitting" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded transition text-sm font-semibold">
              {{ submitting ? 'Đang lưu...' : 'Lưu thay đổi' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { clientApi } from '../../api/clients';

const route = useRoute();
const router = useRouter();
const client = ref(null);
const loading = ref(false);
const errorMsg = ref('');

// Edit Modal State
const showEditModal = ref(false);
const submitting = ref(false);
const editForm = ref({
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
    const id = route.params.id;
    const res = await clientApi.getById(id);
    client.value = res.data.data;
  } catch (err) {
    console.error('Lỗi khi tải chi tiết khách hàng:', err);
    errorMsg.value = 'Không thể tải thông tin chi tiết của khách hàng.';
  } finally {
    loading.value = false;
  }
};

const openEditModal = () => {
  if (!client.value) return;
  editForm.value = {
    ten: client.value.ten || '',
    loai: client.value.loai || 'CA_NHAN',
    cccdMst: client.value.cccdMst || '',
    sdt: client.value.sdt || '',
    email: client.value.email || '',
    diaChi: client.value.diaChi || ''
  };
  showEditModal.value = true;
};

const handleUpdateClient = async () => {
  submitting.value = true;
  try {
    const id = route.params.id;
    await clientApi.update(id, editForm.value);
    alert('Cập nhật thông tin khách hàng thành công');
    showEditModal.value = false;
    await loadData(); // Tải lại thông tin mới
  } catch (err) {
    alert('Lỗi cập nhật: ' + (err.response?.data?.message || err.message));
  } finally {
    submitting.value = false;
  }
};

const goToCaseDetail = (id) => {
  router.push(`/cases/${id}`);
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
  if (!trangThai) return 'inline-flex items-center rounded-full bg-gray-50 px-2 py-1 text-xs font-medium text-gray-600';
  const st = String(trangThai).toUpperCase();
  if (st === 'MOI_TIEP_NHAN') return 'inline-flex items-center rounded-full bg-yellow-50 px-2 py-1 text-xs font-semibold text-yellow-600';
  if (st === 'DANG_XU_LY') return 'inline-flex items-center rounded-full bg-blue-50 px-2 py-1 text-xs font-semibold text-blue-600';
  if (st === 'DA_DONG') return 'inline-flex items-center rounded-full bg-green-50 px-2 py-1 text-xs font-semibold text-green-600';
  return 'inline-flex items-center rounded-full bg-gray-50 px-2 py-1 text-xs font-medium text-gray-600';
};

onMounted(() => {
  loadData();
});
</script>
