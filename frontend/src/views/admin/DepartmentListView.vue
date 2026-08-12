<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-7xl mx-auto">
    <div class="flex justify-between items-center mb-6 border-b border-gray-100 pb-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-800">
          Quản lý Bộ phận (Phòng ban)
        </h1>
        <p class="text-gray-500 text-sm mt-1">Danh mục các phòng ban nghiệp vụ thuộc công ty luật dân sự.</p>
      </div>
    </div>

    <!-- Loading & Error -->
    <div v-if="loading" class="p-12 text-center text-gray-500 text-base">
      Đang tải phòng ban từ máy chủ...
    </div>
    <div v-else-if="errorMsg" class="p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg mb-6 text-sm">
      {{ errorMsg }}
    </div>

    <!-- Danh sách bộ phận -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div 
        v-for="item in items" 
        :key="item.id" 
        class="bg-gray-50 rounded-lg p-5 border border-gray-200 flex flex-col justify-between hover:shadow transition"
      >
        <div>
          <span class="text-xs font-semibold text-blue-600 uppercase tracking-wider">Mã bộ phận: #{{ item.id }}</span>
          <h3 class="text-lg font-bold text-gray-900 mt-1 mb-2">{{ item.ten }}</h3>
          <p class="text-gray-500 text-xs">Bộ phận nghiệp vụ phục vụ giải quyết vụ việc và tài liệu của công ty luật.</p>
        </div>
        <div class="flex justify-end gap-3 mt-4 border-t pt-2 text-xs font-semibold">
          <button @click="openEditDeptModal(item)" class="text-blue-600 hover:underline">Sửa bộ phận</button>
          <span class="text-gray-300">|</span>
          <button @click="handleDelete(item.id, item.ten)" class="text-red-500 hover:underline">Xóa bộ phận</button>
        </div>
      </div>
      
      <div v-if="items.length === 0" class="col-span-full p-8 text-center text-gray-400 bg-gray-50 border border-dashed rounded-lg">
        Không có bộ phận/phòng ban nào được tạo.
      </div>
    </div>

    <!-- Modal Sửa Thông Tin Phòng Ban -->
    <div v-if="showEditDeptModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg p-6 w-full max-w-md space-y-4 shadow-lg border border-gray-100">
        <h3 class="font-bold text-lg border-b pb-2 text-gray-800">Sửa thông tin phòng ban</h3>

        <div>
          <label class="block text-xs font-semibold mb-1 text-gray-600">Tên phòng ban (*)</label>
          <input v-model="editDeptForm.tenBoPhan" class="w-full border p-2 rounded text-sm focus:ring-2 focus:ring-blue-500 focus:outline-none" :class="{ 'border-red-500': deptError }" />
          <span v-if="deptError" class="text-xs text-red-500">Tên phòng ban không được để trống</span>
        </div>

        <div>
          <label class="block text-xs font-semibold mb-1 text-gray-600">Mô tả</label>
          <textarea v-model="editDeptForm.moTa" class="w-full border p-2 rounded text-sm h-24 focus:ring-2 focus:ring-blue-500 focus:outline-none"></textarea>
        </div>

        <div class="flex justify-end space-x-2 pt-4 border-t border-gray-100">
          <button @click="showEditDeptModal = false" class="px-4 py-2 border rounded text-xs hover:bg-gray-50 transition">Hủy</button>
          <button @click="handleUpdateDept" :disabled="submittingDept" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded text-xs font-semibold transition">
            {{ submittingDept ? 'Đang lưu...' : 'Lưu thay đổi' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { adminApi } from '../../api/admin';

const items = ref([]);
const loading = ref(false);
const errorMsg = ref('');

// Edit Modal states
const showEditDeptModal = ref(false);
const submittingDept = ref(false);
const selectedDeptId = ref(null);
const deptError = ref(false);
const editDeptForm = ref({ tenBoPhan: '', moTa: '' });

const loadData = async () => {
  loading.value = true;
  errorMsg.value = '';
  try {
    const res = await adminApi.departments.getAll();
    items.value = res.data.data;
  } catch (err) {
    console.error('Lỗi khi tải danh sách phòng ban:', err);
    errorMsg.value = 'Không thể tải danh sách phòng ban từ máy chủ. Hãy chắc chắn Spring Boot port 8080 đang chạy!';
  } finally {
    loading.value = false;
  }
};

const openEditDeptModal = (dept) => {
  selectedDeptId.value = dept.id;
  editDeptForm.value = { 
    tenBoPhan: dept.ten || dept.name || '', 
    moTa: dept.moTa || dept.description || '' 
  };
  deptError.value = false;
  showEditDeptModal.value = true;
};

const handleUpdateDept = async () => {
  if (!editDeptForm.value.tenBoPhan.trim()) {
    deptError.value = true;
    return;
  }

  submittingDept.value = true;
  try {
    // Gọi API cập nhật phòng ban
    await adminApi.departments.update(selectedDeptId.value, {
      ten: editDeptForm.value.tenBoPhan,
      moTa: editDeptForm.value.moTa
    });
    alert('Cập nhật phòng ban thành công');
    showEditDeptModal.value = false;
    await loadData();
  } catch (err) {
    alert('Lỗi cập nhật phòng ban: ' + (err.response?.data?.message || err.message));
  } finally {
    submittingDept.value = false;
  }
};

const handleDelete = async (id, name) => {
  if (confirm(`Bạn có chắc chắn muốn xóa bộ phận: "${name}"?`)) {
    try {
      await adminApi.departments.delete(id);
      alert('Xóa bộ phận thành công!');
      loadData();
    } catch (err) {
      alert('Lỗi khi xóa bộ phận: ' + (err.response?.data?.message || err.message));
    }
  }
};

onMounted(() => {
  loadData();
});
</script>
