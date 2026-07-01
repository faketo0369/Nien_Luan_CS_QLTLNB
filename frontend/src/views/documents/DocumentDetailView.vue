<template>
  <div class="space-y-6">
    <button @click="$router.push('/documents')" class="text-sm font-bold text-gray-500 hover:text-blue-600 transition flex items-center gap-1.5 bg-transparent border-0 cursor-pointer">
      ◀ Trở lại kho tài liệu
    </button>

    <div v-if="loading"><LoadingSpinner /></div>
    <ApiErrorMessage :error="error" />

    <div v-if="document" class="grid grid-cols-1 lg:grid-cols-3 gap-6 items-start">
      <div class="bg-white p-6 rounded-2xl border border-gray-200 shadow-sm space-y-5 lg:col-span-1">
        <div class="flex items-start justify-between border-b border-gray-100 pb-4">
          <div>
            <h4 class="text-lg font-bold text-gray-900">{{ document.ten }}</h4>
            <p class="text-xs font-mono font-bold text-gray-400 mt-1 uppercase">Số hiệu: {{ document.soHieu || '---' }}</p>
          </div>
          <StatusBadge :status="document.trangThai" />
        </div>

        <div class="space-y-3 text-sm">
          <div class="flex justify-between"><span class="text-gray-400">Định dạng file:</span> <span class="font-mono font-bold uppercase text-blue-600">{{ document.dinhDang }}</span></div>
          <div class="flex justify-between"><span class="text-gray-400">Dung lượng:</span> <span class="font-medium text-gray-800">{{ formatSize(document.dungLuong) }}</span></div>
          <div class="flex justify-between"><span class="text-gray-400">Mức bảo mật:</span> <span :class="document.baoMat ? 'text-red-600 font-bold' : 'text-emerald-600 font-bold'">{{ document.baoMat ? 'Mật / Nội bộ' : 'Công khai' }}</span></div>
          <div class="flex justify-between"><span class="text-gray-400">Danh mục nền:</span> <span class="font-semibold text-gray-700">{{ document.danhMuc?.ten }}</span></div>
          <div class="flex justify-between"><span class="text-gray-400">Loại văn bản:</span> <span class="font-semibold text-gray-700">{{ document.loaiTaiLieu?.ten }}</span></div>
          <div class="flex justify-between"><span class="text-gray-400">Hồ sơ vụ việc:</span> <span class="font-semibold text-blue-600 truncate max-w-[150px]">{{ document.vuViec?.ten || 'Không đính kèm' }}</span></div>
          <div class="pt-2 border-t border-gray-50 text-xs text-gray-400 space-y-1">
            <p>Ngày khởi tạo: {{ document.ngayTao }}</p>
            <p>Cập nhật cuối: {{ document.ngayCapNhat || '---' }}</p>
          </div>
        </div>

        <div class="pt-4 border-t border-gray-100 space-y-2">
          <button @click="downloadCurrent" class="w-full py-2.5 bg-emerald-600 hover:bg-emerald-700 text-white text-sm font-bold rounded-xl transition shadow-sm border-0 cursor-pointer">
            📥 Tải tệp bản mới nhất
          </button>
          <button v-if="document.trangThai === 'NHAP'" @click="submitApproval" class="w-full py-2.5 bg-amber-500 hover:bg-amber-600 text-white text-sm font-bold rounded-xl transition shadow-sm border-0 cursor-pointer">
            🚀 Trình Hội đồng phê duyệt
          </button>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden lg:col-span-2 flex flex-col min-h-[450px]">
        <div class="flex border-b border-gray-200 bg-gray-50/50">
          <button @click="activeTab = 'versions'" :class="['px-6 py-4 text-sm font-bold border-b-2 transition border-0 cursor-pointer', activeTab === 'versions' ? 'border-blue-600 text-blue-600 bg-white' : 'border-transparent text-gray-500 hover:text-gray-800']">
            🔄 Nhật ký Phiên bản (Versions)
          </button>
          <button @click="activeTab = 'history'" :class="['px-6 py-4 text-sm font-bold border-b-2 transition border-0 cursor-pointer', activeTab === 'history' ? 'border-blue-600 text-blue-600 bg-white' : 'border-transparent text-gray-500 hover:text-gray-800']">
            📝 Lịch sử Phê duyệt (Approvals)
          </button>
        </div>

        <div v-if="activeTab === 'versions'" class="p-6 flex-1 flex flex-col justify-between">
          <div class="space-y-4">
            <div class="flex items-center justify-between">
              <h5 class="text-sm font-bold text-gray-400 uppercase tracking-wider">Cấu trúc các tệp tin đính kèm lịch sử</h5>
              <label class="cursor-pointer px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-700 font-bold text-xs rounded-lg border border-slate-300 transition">
                🗂️ Tải lên phiên bản mới
                <input type="file" @change="uploadNewVersion" class="hidden" />
              </label>
            </div>
            
            <div class="divide-y divide-gray-100">
              <div v-for="ver in versions" :key="ver.id" class="py-3.5 flex items-center justify-between text-sm">
                <div class="flex items-center gap-3">
                  <span class="px-2 py-0.5 font-mono font-bold bg-blue-50 text-blue-600 text-xs rounded border border-blue-200">V{{ ver.phienBan }}</span>
                  <div>
                    <p class="font-semibold text-gray-800">{{ ver.tenFileGoc }}</p>
                    <p class="text-xs text-gray-400 mt-0.5">Dung lượng: {{ formatSize(ver.dungLuong) }} | Cập nhật lúc: {{ ver.ngayTao }}</p>
                  </div>
                </div>
                <button @click="downloadVersion(ver)" class="px-3 py-1 text-xs font-bold text-emerald-600 hover:bg-emerald-50 rounded border border-emerald-200 transition cursor-pointer">Tải bản này</button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'history'" class="p-6 flex-1">
          <h5 class="text-sm font-bold text-gray-400 uppercase tracking-wider mb-4">Nhật ký xử lý luân chuyển văn bản</h5>
          <div class="relative pl-6 border-l-2 border-gray-100 space-y-6">
            <div v-for="log in approvalHistory" :key="log.id" class="relative">
              <span class="absolute -left-[31px] top-0 w-4 h-4 rounded-full border-4 border-white bg-blue-500 shadow-sm"></span>
              <div class="text-sm">
                <div class="flex items-center gap-2">
                  <span class="font-bold text-gray-800">Cán bộ xử lý ID: {{ log.nguoiDungId }}</span>
                  <span :class="['px-2 py-0.5 text-[10px] font-bold rounded font-mono', log.hanhDong === 'PHE_DUYET' ? 'bg-emerald-100 text-emerald-800' : 'bg-rose-100 text-rose-800']">
                    {{ log.hanhDong }}
                  </span>
                </div>
                <p class="text-xs text-gray-400 mt-0.5 font-mono">{{ log.timeLog }}</p>
                <p class="mt-1.5 p-3 bg-gray-50 rounded-xl text-xs text-gray-600 font-medium border border-gray-100" v-if="log.ghiChu">
                  💬 Nội dung ý kiến: {{ log.ghiChu }}
                </p>
              </div>
            </div>
            <div v-if="approvalHistory.length === 0" class="text-center py-8 text-gray-400 font-medium">Tài liệu này chưa phát sinh lịch sử phê duyệt.</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { documentApi } from '../../api/documents';
import { versionApi } from '../../api/versions';
import StatusBadge from '../../components/common/StatusBadge.vue';
import LoadingSpinner from '../../components/common/LoadingSpinner.vue';
import ApiErrorMessage from '../../components/common/ApiErrorMessage.vue';

const route = useRoute();
const documentId = route.params.id;

const loading = ref(false);
const error = ref(null);
const document = ref(null);
const versions = ref([]);
const approvalHistory = ref([]);
const activeTab = ref('versions');

const fetchDetailData = async () => {
  loading.value = true;
  error.value = null;
  try {
    const [docRes, verRes, hisRes] = await Promise.all([
      documentApi.getById(documentId),
      versionApi.getByDocumentId(documentId),
      documentApi.getApprovalHistory(documentId)
    ]);
    if (docRes.data?.success) document.value = docRes.data.data;
    versions.value = verRes.data?.data || [];
    approvalHistory.value = hisRes.data?.data || [];
  } catch (err) {
    error.value = err;
  } finally {
    loading.value = false;
  }
};

const submitApproval = async () => {
  if (!confirm('Xác nhận trình Hội đồng ban giám đốc thẩm định văn bản?')) return;
  try {
    const res = await documentApi.submit(documentId);
    if (res.data?.success) {
      alert('Đã gửi phê duyệt thành công.');
      fetchDetailData();
    }
  } catch (err) {
    alert(err.response?.data?.message || 'Lỗi gửi phê duyệt.');
  }
};

const uploadNewVersion = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append('file', file);
  formData.append('ten', document.value.ten);
  formData.append('danhMucId', document.value.danhMuc?.id || '');
  formData.append('loaiTaiLieuId', document.value.loaiTaiLieu?.id || '');
  formData.append('boPhanId', document.value.boPhan?.id || '');

  try {
    loading.value = true;
    const res = await documentApi.changeFile(documentId, file);
    if (res.data?.success) {
      alert('Đã cập nhật và gia tăng số phiên bản đính kèm thành công!');
      fetchDetailData();
    }
  } catch (err) {
    alert(err.response?.data?.message || 'Tải lên phiên bản mới thất bại.');
  } finally {
    loading.value = false;
  }
};

const downloadCurrent = async () => {
  try {
    const res = await documentApi.download(documentId);
    triggerDownload(res.data, document.value.ten + '.' + document.value.dinhDang);
  } catch (err) {
    alert('Không thể tải tệp tin vật lý.');
  }
};

const downloadVersion = async (ver) => {
  try {
    const res = await versionApi.downloadVersion(documentId, ver.id);
    triggerDownload(res.data, ver.tenFileGoc);
  } catch (err) {
    alert('Tải phiên bản cũ thất bại.');
  }
};

const triggerDownload = (data, filename) => {
  const blob = new Blob([data]);
  const link = document.createElement('a');
  link.href = window.URL.createObjectURL(blob);
  link.download = filename;
  link.click();
};

const formatSize = (bytes) => {
  if (!bytes) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

onMounted(fetchDetailData);
</script>
