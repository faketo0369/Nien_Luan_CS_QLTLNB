<template>
  <div v-if="modelValue" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4 overflow-y-auto">
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-2xl border border-gray-100 overflow-hidden transform transition-all my-8">
      <div class="px-6 py-4 bg-slate-900 text-white flex items-center justify-between">
        <h3 class="font-bold text-base">✏️ {{ documentId ? 'Cập nhật tài liệu văn bản' : 'Khai báo tạo mới tài liệu' }}</h3>
        <button type="button" @click="closeModal" class="text-slate-400 hover:text-white text-xl bg-transparent border-0 cursor-pointer">✕</button>
      </div>

      <form @submit.prevent="handleSubmit" class="p-6 space-y-4 max-h-[calc(100vh-200px)] overflow-y-auto">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="form-label">Tên văn bản tài liệu <span class="text-red-500">*</span></label>
            <input v-model="form.ten" type="text" class="form-input" placeholder="Nhập tên chính thức của văn bản..." required />
          </div>
          <div>
            <label class="form-label">Số hiệu văn bản</label>
            <input v-model="form.soHieu" type="text" class="form-input" placeholder="Ví dụ: 05/2026/NQ-HĐTP..." />
          </div>
        </div>

        <div>
          <label class="form-label">Mô tả tóm tắt nội dung chính</label>
          <textarea v-model="form.moTa" rows="2" class="form-input" placeholder="Ghi chú tóm tắt nội dung văn bản pháp lý..."></textarea>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label class="form-label">Danh mục nghiệp vụ</label>
            <select v-model="form.danhMucId" class="form-input">
              <option value="">-- Chọn danh mục --</option>
              <option v-for="item in options.categories" :key="item.id" :value="item.id">{{ item.ten }}</option>
            </select>
          </div>
          <div>
            <label class="form-label">Phân loại tệp luật</label>
            <select v-model="form.loaiTaiLieuId" class="form-input">
              <option value="">-- Chọn loại --</option>
              <option v-for="item in options.docTypes" :key="item.id" :value="item.id">{{ item.ten }}</option>
            </select>
          </div>
          <div>
            <label class="form-label">Mức độ bảo mật</label>
            <select v-model="form.baoMat" class="form-input">
              <option :value="false">Công khai hệ thống</option>
              <option :value="true">Mật / Lưu trữ nội bộ</option>
            </select>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label class="form-label">Bộ phận ban hành</label>
            <select v-model="form.boPhanId" class="form-input">
              <option value="">-- Chọn bộ phận --</option>
              <option v-for="item in options.departments" :key="item.id" :value="item.id">{{ item.ten }}</option>
            </select>
          </div>
          <div>
            <label class="form-label">Liên kết hồ sơ vụ việc</label>
            <select v-model="form.vuViecId" class="form-input">
              <option value="">-- Không liên kết (Hành chính chung) --</option>
              <option v-for="item in options.cases" :key="item.id" :value="item.id">{{ item.ten }}</option>
            </select>
          </div>
        </div>

        <div>
          <label class="form-label">Tệp đính kèm văn bản vật lý <span class="text-red-500" v-if="!documentId">*</span></label>
          <div @dragover.prevent="isDragOver = true" @dragleave="isDragOver = false" @drop.prevent="handleFileDrop" :class="['border-2 border-dashed rounded-2xl p-6 text-center transition cursor-pointer flex flex-col items-center justify-center', isDragOver ? 'border-blue-500 bg-blue-50/40' : 'border-gray-300 hover:border-blue-400 bg-gray-50']" @click="$refs.fileInput.click()">
            <span class="text-3xl mb-1">📁</span>
            <p class="text-sm font-semibold text-gray-700">Kéo thả tệp văn bản vào đây hoặc nhấp chuột để chọn</p>
            <p class="text-xs text-gray-400 mt-1">Chấp nhận mọi định dạng tệp pháp lý hành chính (Tối đa 50MB)</p>
            <input type="file" ref="fileInput" @change="handleFileSelect" class="hidden" :required="!documentId" />
          </div>
          
          <div v-if="selectedFile" class="mt-3 p-3 bg-blue-50 rounded-xl border border-blue-100 flex items-center justify-between text-xs animate-fade-in">
            <div class="flex items-center gap-2">
              <span class="text-base">📄</span>
              <div>
                <p class="font-bold text-blue-900 truncate max-w-[400px]">{{ selectedFile.name }}</p>
                <p class="text-blue-500 font-mono mt-0.5">Dung lượng: {{ formatSize(selectedFile.size) }}</p>
              </div>
            </div>
            <button type="button" @click.stop="selectedFile = null" class="text-gray-400 hover:text-red-500 font-bold text-sm px-2 border-0 bg-transparent cursor-pointer">✕</button>
          </div>
        </div>

        <div class="pt-4 border-t border-gray-100 flex justify-end gap-3">
          <button type="button" @click="closeModal" class="px-4 py-2 text-sm font-semibold text-gray-600 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 transition cursor-pointer">Hủy bỏ</button>
          <button type="submit" :disabled="submitting" class="px-5 py-2 text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 rounded-xl shadow-md transition disabled:opacity-50 border-0 cursor-pointer">
            {{ submitting ? 'Đang lưu trữ...' : 'Lưu văn bản' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue';
import { documentApi } from '../../api/documents';
import { adminApi } from '../../api/admin';
import { caseApi } from '../../api/cases';

const props = defineProps({
  modelValue: { type: Boolean, required: true },
  documentId: { type: [Number, null], default: null }
});

const emit = defineEmits(['update:modelValue', 'saved']);

const submitting = ref(false);
const isDragOver = ref(false);
const selectedFile = ref(null);
const fileInput = ref(null);

const form = reactive({ ten: '', soHieu: '', moTa: '', danhMucId: '', loaiTaiLieuId: '', boPhanId: '', vuViecId: '', baoMat: false });
const options = reactive({ categories: [], docTypes: [], departments: [], cases: [] });

const fetchAllOptions = async () => {
  try {
    const [cat, doc, dept, csv] = await Promise.all([
      adminApi.categories.getAll(),
      adminApi.docTypes.getAll(),
      adminApi.departments.getAll(),
      caseApi.getAll({ size: 100 })
    ]);
    options.categories = cat.data?.data || [];
    options.docTypes = doc.data?.data || [];
    options.departments = dept.data?.data || [];
    options.cases = csv.data?.data || [];
  } catch (err) {
    console.error('Lỗi tải cấu trúc danh mục nền:', err);
  }
};

const fetchDocumentDetails = async (id) => {
  try {
    const res = await documentApi.getById(id);
    if (res.data?.success) {
      const doc = res.data.data;
      form.ten = doc.ten || '';
      form.soHieu = doc.soHieu || '';
      form.moTa = doc.moTa || '';
      form.danhMucId = doc.danhMuc?.id || '';
      form.loaiTaiLieuId = doc.loaiTaiLieu?.id || '';
      form.boPhanId = doc.boPhan?.id || '';
      form.vuViecId = doc.vuViec?.id || '';
      form.baoMat = !!doc.baoMat;
    }
  } catch (err) {
    console.error('Lỗi tải chi tiết tài liệu cần cập nhật:', err);
  }
};

const handleFileSelect = (e) => {
  if (e.target.files.length > 0) selectedFile.value = e.target.files[0];
};

const handleFileDrop = (e) => {
  isDragOver.value = false;
  if (e.dataTransfer.files.length > 0) selectedFile.value = e.dataTransfer.files[0];
};

const closeModal = () => emit('update:modelValue', false);

const handleSubmit = async () => {
  submitting.value = true;
  const formData = new FormData();
  if (selectedFile.value) formData.append('file', selectedFile.value);
  formData.append('ten', form.ten);
  formData.append('soHieu', form.soHieu);
  formData.append('moTa', form.moTa);
  formData.append('danhMucId', form.danhMucId);
  formData.append('loaiTaiLieuId', form.loaiTaiLieuId);
  formData.append('boPhanId', form.boPhanId);
  if (form.vuViecId) formData.append('vuViecId', form.vuViecId);
  formData.append('baoMat', form.baoMat);

  try {
    let res;
    if (props.documentId) {
      res = await documentApi.update(props.documentId, formData);
    } else {
      res = await documentApi.create(formData);
    }
    if (res.data?.success) {
      emit('saved');
      closeModal();
    }
  } catch (err) {
    alert(err.response?.data?.message || 'Thao tác lưu trữ tệp tin thất bại.');
  } finally {
    submitting.value = false;
  }
};

const formatSize = (bytes) => {
  if (!bytes) return '0 B';
  const k = 1024;
  return parseFloat((bytes / k).toFixed(2)) + ' KB';
};

watch(() => props.modelValue, (isOpen) => {
  if (isOpen) {
    fetchAllOptions();
    selectedFile.value = null;
    if (props.documentId) {
      fetchDocumentDetails(props.documentId);
    } else {
      form.ten = ''; form.soHieu = ''; form.moTa = ''; form.danhMucId = ''; form.loaiTaiLieuId = ''; form.boPhanId = ''; form.vuViecId = ''; form.baoMat = false;
    }
  }
});
</script>

<style scoped>
.form-label {
  display: block;
  font-size: 0.75rem;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 0.375rem;
}
.form-input {
  width: 100%;
  font-size: 0.875rem;
  padding: 0.625rem 0.875rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  outline: none;
  transition: all 0.2s;
}
.form-input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 131, 246, 0.15);
}
</style>
