<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-7xl mx-auto">
    <div class="flex items-center gap-4 mb-6">
      <router-link to="/cases" class="text-blue-600 hover:text-blue-800 text-sm font-semibold flex items-center gap-1">
        <span>←</span> Quay lại danh sách
      </router-link>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="p-12 text-center text-gray-500 text-base">
      🔄 Đang tải thông tin chi tiết vụ việc...
    </div>

    <!-- Error State -->
    <div v-else-if="errorMsg" class="p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg mb-6 text-sm">
      ⚠️ {{ errorMsg }}
    </div>

    <!-- Content -->
    <div v-else-if="caseDetail">
      <div class="border-b border-gray-100 pb-6 mb-6">
        <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
          <span>💼</span> Chi tiết Vụ việc: {{ caseDetail.ten }}
        </h1>
        <p class="text-gray-500 text-sm mt-1">Thông tin chi tiết về hồ sơ vụ việc pháp lý, nhật ký hoạt động và tài liệu đính kèm.</p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Cột thông tin chi tiết vụ việc -->
        <div class="lg:col-span-1 bg-gray-50 rounded-lg p-5 border border-gray-200">
          <h2 class="text-lg font-bold text-gray-800 mb-4 border-b pb-2">Thông tin vụ việc</h2>
          <div class="space-y-3 text-sm text-gray-600">
            <div>
              <span class="block font-semibold text-gray-500">Tên vụ việc:</span>
              <span class="text-gray-900 font-semibold">{{ caseDetail.ten }}</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Loại hình:</span>
              <span class="text-gray-900">{{ caseDetail.loai || 'Chưa phân loại' }}</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Khách hàng liên quan:</span>
              <router-link v-if="caseDetail.khachHang" :to="`/clients/${caseDetail.khachHang.id}`" class="text-blue-600 font-medium hover:underline">
                {{ caseDetail.khachHang.ten }}
              </router-link>
              <span v-else>---</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Luật sư phụ trách:</span>
              <span class="text-gray-900 font-medium">{{ caseDetail.nguoiPhuTrach?.hoTen || 'Chưa phân công' }}</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Ngày mở hồ sơ:</span>
              <span class="text-gray-900">{{ caseDetail.ngayMo || '---' }}</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Ngày đóng hồ sơ:</span>
              <span class="text-gray-900">{{ caseDetail.ngayDong || 'Đang mở' }}</span>
            </div>
            <div>
              <span class="block font-semibold text-gray-500">Ghi chú vụ việc:</span>
              <span class="text-gray-900 block bg-white p-2 rounded border mt-1">{{ caseDetail.ghiChu || 'Không có ghi chú' }}</span>
            </div>
          </div>
        </div>

        <!-- Cột tài liệu thuộc vụ việc -->
        <div class="lg:col-span-2">
          <h2 class="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
            <span>📄</span> Tài liệu liên quan đến vụ việc ({{ documents.length }})
          </h2>
          <div class="overflow-x-auto rounded-lg border border-gray-200 shadow-sm bg-white">
            <table class="w-full border-collapse text-left text-sm text-gray-500">
              <thead class="bg-gray-50 text-xs font-semibold uppercase text-gray-700 border-b border-gray-200">
                <tr>
                  <th class="px-6 py-4">Tên tài liệu</th>
                  <th class="px-6 py-4">Số hiệu</th>
                  <th class="px-6 py-4">Định dạng</th>
                  <th class="px-6 py-4 text-center">Trạng thái</th>
                  <th class="px-6 py-4 text-right">Thao tác</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-100 border-t border-gray-100">
                <tr v-for="doc in documents" :key="doc.id" class="hover:bg-gray-50 transition-colors">
                  <td class="px-6 py-4 font-semibold text-gray-900">
                    <router-link :to="`/documents/${doc.id}`" class="text-blue-600 hover:underline">
                      {{ doc.ten }}
                    </router-link>
                  </td>
                  <td class="px-6 py-4">{{ doc.soHieu || '---' }}</td>
                  <td class="px-6 py-4">
                    <span class="uppercase font-medium text-gray-400">.{{ doc.dinhDang }}</span>
                  </td>
                  <td class="px-6 py-4 text-center">
                    <span :class="getDocStatusBadgeClass(doc.trangThai)">
                      {{ getDocStatusLabel(doc.trangThai) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 text-right">
                    <router-link :to="`/documents/${doc.id}`" class="text-blue-600 hover:text-blue-900 font-semibold">Xem</router-link>
                  </td>
                </tr>
                <tr v-if="documents.length === 0">
                  <td colspan="5" class="px-6 py-8 text-center text-gray-400">
                    Không có tài liệu nào thuộc vụ việc này được đăng tải.
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { caseApi } from '../../api/cases';

const route = useRoute();
const caseDetail = ref(null);
const documents = ref([]);
const loading = ref(false);
const errorMsg = ref('');

const loadData = async () => {
  loading.value = true;
  errorMsg.value = '';
  try {
    const id = route.params.id;
    // Tải chi tiết vụ việc
    const resDetail = await caseApi.getById(id);
    caseDetail.value = resDetail.data.data;
    
    // Tải danh sách tài liệu
    const resDocs = await caseApi.getDocuments(id);
    documents.value = resDocs.data.data;
  } catch (err) {
    console.error('Lỗi khi tải chi tiết vụ việc:', err);
    errorMsg.value = 'Không thể tải thông tin chi tiết vụ việc và danh sách tài liệu từ máy chủ.';
  } finally {
    loading.value = false;
  }
};

const getDocStatusLabel = (st) => {
  if (!st) return 'Chưa rõ';
  const s = String(st).toLowerCase();
  if (s === 'da_duyet') return 'Đã duyệt';
  if (s === 'cho_duyet') return 'Chờ duyệt';
  if (s === 'tu_choi') return 'Từ chối';
  return 'Bản nháp';
};

const getDocStatusBadgeClass = (st) => {
  if (!st) return 'inline-flex items-center gap-1 rounded bg-gray-50 px-2 py-0.5 text-xs font-semibold text-gray-600';
  const s = String(st).toLowerCase();
  if (s === 'da_duyet') return 'inline-flex items-center gap-1 rounded bg-green-50 px-2 py-0.5 text-xs font-semibold text-green-600';
  if (s === 'cho_duyet') return 'inline-flex items-center gap-1 rounded bg-yellow-50 px-2 py-0.5 text-xs font-semibold text-yellow-600';
  if (s === 'tu_choi') return 'inline-flex items-center gap-1 rounded bg-red-50 px-2 py-0.5 text-xs font-semibold text-red-600';
  return 'inline-flex items-center gap-1 rounded bg-gray-50 px-2 py-0.5 text-xs font-semibold text-gray-600';
};

onMounted(() => {
  loadData();
});
</script>
