<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-7xl mx-auto">
    <div class="flex justify-between items-center mb-6 border-b border-gray-100 pb-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">
          Quản lý Người dùng hệ thống
        </h1>
        <p class="text-gray-500 text-sm mt-1">Quản lý danh sách cán bộ, nhân viên, phân vai trò quyền hạn và trạng thái kích hoạt tài khoản.</p>
      </div>
      <button @click="openCreateModal" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded text-sm font-semibold transition shadow-sm">
        + Thêm người dùng
      </button>
    </div>

    <!-- Loading & Error -->
    <div v-if="loading" class="p-12 text-center text-gray-500 text-base">
      Đang tải người dùng từ máy chủ...
    </div>
    <div v-else-if="errorMsg" class="p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg mb-6 text-sm">
      {{ errorMsg }}
    </div>

    <!-- Bảng người dùng -->
    <div v-else class="overflow-x-auto rounded-lg border border-gray-100 shadow-sm bg-white">
      <table class="w-full border-collapse text-left text-sm text-gray-500">
        <thead class="bg-gray-50 text-xs font-semibold uppercase text-gray-700 border-b border-gray-200">
          <tr>
            <th class="px-6 py-4">Họ và tên</th>
            <th class="px-6 py-4">Tên tài khoản</th>
            <th class="px-6 py-4">Email</th>
            <th class="px-6 py-4">Vai trò</th>
            <th class="px-6 py-4">Bộ phận</th>
            <th class="px-6 py-4 text-center">Trạng thái</th>
            <th class="px-6 py-4 text-right">Hành động</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100 border-t border-gray-100">
          <tr v-for="item in items" :key="item.id" class="hover:bg-gray-50 transition-colors">
            <td class="px-6 py-4 font-semibold text-gray-900">{{ item.hoTen }}</td>
            <td class="px-6 py-4 font-medium text-gray-700">{{ item.taiKhoan }}</td>
            <td class="px-6 py-4">{{ item.email }}</td>
            <td class="px-6 py-4">
              <span :class="getRoleBadgeClass(item.vaiTro)">
                {{ item.vaiTro }}
              </span>
            </td>
            <td class="px-6 py-4">{{ item.boPhan || 'Chưa rõ' }}</td>
            <td class="px-6 py-4 text-center">
              <span 
                :class="[
                  'inline-flex items-center gap-1 rounded-full px-2.5 py-1 text-xs font-semibold',
                  item.trangThai === 'KHOA' ? 'bg-red-50 text-red-600' : 'bg-green-50 text-green-600'
                ]"
              >
                {{ item.trangThai === 'KHOA' ? 'Đã khóa' : 'Hoạt động' }}
              </span>
            </td>
            <td class="px-6 py-4 text-right">
              <div class="flex justify-end items-center gap-2">
                <button @click="openEditModal(item)" class="text-blue-600 hover:text-blue-900 font-semibold">Sửa</button>
                <span class="text-gray-300">|</span>
                <button @click="handleDelete(item.id, item.hoTen)" class="text-red-600 hover:text-red-900 font-semibold">Xóa</button>
              </div>
            </td>
          </tr>
          <tr v-if="items.length === 0">
            <td colspan="7" class="px-6 py-8 text-center text-gray-400">
              Không có tài khoản người dùng nào được tạo.
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal Thêm/Sửa Thông Tin Người Dùng -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md space-y-4 shadow-lg border border-gray-100 max-h-[90vh] overflow-y-auto">
        <h3 class="font-bold text-lg border-b pb-2 text-gray-800">
          {{ isEditMode ? 'Sửa thông tin người dùng' : 'Thêm người dùng mới' }}
        </h3>
        
        <div>
          <label class="block text-xs font-semibold mb-1 text-gray-600">Họ và tên (*)</label>
          <input v-model="form.hoTen" class="w-full border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none" :class="{ 'border-red-500': errors.hoTen }" />
          <span v-if="errors.hoTen" class="text-xs text-red-500">Họ tên không được để trống</span>
        </div>

        <div>
          <label class="block text-xs font-semibold mb-1 text-gray-600">Tên tài khoản (*)</label>
          <input v-model="form.taiKhoan" :disabled="isEditMode" class="w-full border p-2 rounded text-sm bg-gray-50 disabled:bg-gray-100 disabled:text-gray-400 focus:ring-2 focus:ring-blue-500 focus:outline-none" :class="{ 'border-red-500': errors.taiKhoan }" />
          <span v-if="errors.taiKhoan" class="text-xs text-red-500">Tên tài khoản không được để trống</span>
        </div>

        <div>
          <label class="block text-xs font-semibold mb-1 text-gray-600">
            {{ isEditMode ? 'Mật khẩu mới (để trống nếu giữ nguyên)' : 'Mật khẩu (mặc định "123456" nếu để trống)' }}
          </label>
          <input v-model="form.matKhau" type="password" class="w-full border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none" />
        </div>

        <div>
          <label class="block text-xs font-semibold mb-1 text-gray-600">Email (*)</label>
          <input v-model="form.email" type="email" class="w-full border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none" :class="{ 'border-red-500': errors.email }" />
          <span v-if="errors.email" class="text-xs text-red-500">Email không được để trống</span>
        </div>

        <div>
          <label class="block text-xs font-semibold mb-1 text-gray-600">Bộ phận</label>
          <select v-model="form.boPhanId" :disabled="authStore.user?.vaiTro === 'TRUONG_PHONG'" class="w-full border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none bg-white disabled:bg-gray-100 disabled:text-gray-500">
            <option :value="null">-- Chọn bộ phận --</option>
            <option v-for="d in departments" :key="d.id" :value="d.id">{{ d.ten }}</option>
          </select>
        </div>

        <div>
          <label class="block text-xs font-semibold mb-1 text-gray-600">Vai trò</label>
          <select v-model="form.role" :disabled="authStore.user?.vaiTro === 'TRUONG_PHONG'" class="w-full border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none bg-white disabled:bg-gray-100 disabled:text-gray-500">
            <option value="ADMIN">ADMIN</option>
            <option value="TRUONG_PHONG">TRUONG_PHONG</option>
            <option value="NHAN_VIEN">NHAN_VIEN</option>
          </select>
        </div>

        <div>
          <label class="block text-xs font-semibold mb-1 text-gray-600">Chuyên môn</label>
          <input v-model="form.chuyenMon" placeholder="Ví dụ: Luật đất đai" class="w-full border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none" />
        </div>

        <div>
          <label class="block text-xs font-semibold mb-1 text-gray-600">Số chứng chỉ luật sư</label>
          <input v-model="form.soChungChi" class="w-full border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none" />
        </div>

        <div class="flex justify-end space-x-2 pt-4 border-t border-gray-100">
          <button @click="showModal = false" class="px-4 py-2 border rounded text-xs hover:bg-gray-50 transition">Hủy</button>
          <button @click="handleSubmit" :disabled="submitting" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded text-xs font-semibold transition">
            {{ submitting ? 'Đang lưu...' : (isEditMode ? 'Lưu thay đổi' : 'Tạo tài khoản') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue';
import { adminApi } from '../../api/admin';
import { useAuthStore } from '../../stores/auth';

const authStore = useAuthStore();
const items = ref([]);
const loading = ref(false);
const errorMsg = ref('');

// Modal states
const showModal = ref(false);
const isEditMode = ref(false);
const submitting = ref(false);
const selectedUserId = ref(null);
const departments = ref([]);
const errors = ref({});

const form = ref({
  hoTen: '',
  taiKhoan: '',
  matKhau: '',
  email: '',
  boPhanId: null,
  role: 'NHAN_VIEN',
  chuyenMon: '',
  soChungChi: ''
});

const loadData = async () => {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res = await adminApi.users.getAll();
    items.value = res.data.data;
  } catch (err) {
    console.error('Lỗi khi tải danh sách tài khoản:', err);
    errorMsg.value = 'Không thể kết nối đến API lấy danh sách tài khoản. Hãy chắc chắn Spring Boot port 8080 đang chạy!';
  } finally {
    loading.value = false;
  }
};

const fetchDepartments = async () => {
  try {
    const res = await adminApi.departments.getAll();
    departments.value = res.data?.data || [];
  } catch (err) {
    console.error('Lỗi tải danh sách bộ phận:', err);
  }
};

const openCreateModal = () => {
  isEditMode.value = false;
  selectedUserId.value = null;
  
  let deptId = null;
  if (authStore.user?.vaiTro === 'TRUONG_PHONG') {
    const matchedDept = departments.value.find(d => d.ten === authStore.user.boPhan);
    deptId = matchedDept ? matchedDept.id : null;
  }
  
  form.value = {
    hoTen: '',
    taiKhoan: '',
    matKhau: '',
    email: '',
    boPhanId: deptId,
    role: 'NHAN_VIEN',
    chuyenMon: '',
    soChungChi: ''
  };
  errors.value = {};
  showModal.value = true;
};

const openEditModal = (user) => {
  isEditMode.value = true;
  selectedUserId.value = user.id;
  const matchedDept = departments.value.find(d => d.ten === user.boPhan);
  form.value = {
    hoTen: user.hoTen || '',
    taiKhoan: user.taiKhoan || '',
    matKhau: '',
    email: user.email || '',
    boPhanId: authStore.user?.vaiTro === 'TRUONG_PHONG' ? (departments.value.find(d => d.ten === authStore.user.boPhan)?.id || null) : (matchedDept ? matchedDept.id : null),
    role: authStore.user?.vaiTro === 'TRUONG_PHONG' ? 'NHAN_VIEN' : (user.vaiTro || 'NHAN_VIEN'),
    chuyenMon: user.chuyenMon || '',
    soChungChi: user.soChungChi || ''
  };
  errors.value = {};
  showModal.value = true;
};

const handleSubmit = async () => {
  errors.value = {};
  if (!form.value.hoTen.trim()) errors.value.hoTen = true;
  if (!form.value.email.trim()) errors.value.email = true;
  if (!isEditMode.value && !form.value.taiKhoan.trim()) errors.value.taiKhoan = true;
  if (Object.keys(errors.value).length > 0) return;

  submitting.value = true;
  try {
    const payload = {
      hoTen: form.value.hoTen,
      taiKhoan: form.value.taiKhoan,
      email: form.value.email,
      vaiTro: form.value.role,
      boPhanId: form.value.boPhanId,
      chuyenMon: form.value.chuyenMon,
      soChungChi: form.value.soChungChi
    };

    if (form.value.matKhau.trim()) {
      payload.matKhau = form.value.matKhau;
    }

    if (isEditMode.value) {
      await adminApi.users.update(selectedUserId.value, payload);
      alert('Cập nhật thông tin người dùng thành công');
    } else {
      await adminApi.users.create(payload);
      alert('Tạo người dùng mới thành công');
    }
    showModal.value = false;
    await loadData();
  } catch (err) {
    alert('Lỗi thao tác: ' + (err.response?.data?.message || err.message));
  } finally {
    submitting.value = false;
  }
};

const handleDelete = async (id, name) => {
  if (confirm(`Bạn có chắc chắn muốn xóa tài khoản: "${name}"?`)) {
    try {
      await adminApi.users.delete(id);
      alert('Xóa tài khoản thành công!');
      loadData();
    } catch (err) {
      alert('Lỗi khi xóa tài khoản: ' + (err.response?.data?.message || err.message));
    }
  }
};

const getRoleBadgeClass = (role) => {
  if (!role) return 'inline-flex items-center gap-1 rounded bg-gray-50 px-2 py-0.5 text-xs font-semibold text-gray-600';
  const r = String(role).toUpperCase();
  if (r === 'ADMIN') return 'inline-flex items-center gap-1 rounded bg-red-50 px-2 py-0.5 text-xs font-semibold text-red-600';
  if (r === 'TRUONG_PHONG') return 'inline-flex items-center gap-1 rounded bg-yellow-50 px-2 py-0.5 text-xs font-semibold text-yellow-600';
  return 'inline-flex items-center gap-1 rounded bg-blue-50 px-2 py-0.5 text-xs font-semibold text-blue-600';
};

onMounted(() => {
  loadData();
  fetchDepartments();
});
</script>
