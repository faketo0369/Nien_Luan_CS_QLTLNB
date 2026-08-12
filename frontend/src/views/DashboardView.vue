<template>
  <div class="p-6 bg-white rounded-lg shadow-sm border border-gray-100 max-w-7xl mx-auto space-y-6">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <div>
        <h2 style="margin: 0; color: #333;" class="text-2xl font-bold">Xin chào, {{ authStore.user?.hoTen || 'Thành viên' }}</h2>
        <small style="color: #666;" class="text-sm">Bộ phận: {{ authStore.user?.boPhan || 'Chưa rõ' }}</small>
      </div>
      <span style="padding: 6px 14px; background: #e2e3e5; color: #383d41; border-radius: 20px; font-weight: bold; font-size: 13px;">
        Quyền: {{ authStore.user?.vaiTro }}
      </span>
    </div>

    <!-- Phân nhánh Dashboard theo role -->
    <div v-if="isAdminOrManager" class="space-y-6">
      <h1 class="text-xl font-bold text-gray-800">Thống kê tổng quan hệ thống</h1>

      <!-- Card Stats Grid -->
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div class="bg-white p-5 rounded-lg border shadow-sm flex flex-col justify-between">
          <div class="flex justify-between items-start">
            <div>
              <p class="text-xs text-gray-500 font-medium">TỔNG TÀI LIỆU</p>
              <h3 class="text-2xl font-bold text-gray-800 mt-1">{{ stats.totalDocs }}</h3>
            </div>
          </div>
          <p class="text-xs text-amber-600 mt-3 font-medium">{{ stats.pendingDocs }} chờ duyệt</p>
        </div>

        <div class="bg-white p-5 rounded-lg border shadow-sm flex flex-col justify-between">
          <div class="flex justify-between items-start">
            <div>
              <p class="text-xs text-gray-500 font-medium">TÀI LIỆU ĐÃ DUYỆT</p>
              <h3 class="text-2xl font-bold text-gray-800 mt-1">{{ stats.approvedDocs }}</h3>
            </div>
          </div>
          <p class="text-xs text-gray-400 mt-3">Tháng này: {{ stats.approvedDocs }}</p>
        </div>

        <div class="bg-white p-5 rounded-lg border shadow-sm flex flex-col justify-between">
          <div class="flex justify-between items-start">
            <div>
              <p class="text-xs text-gray-500 font-medium">TỔNG VỤ VIỆC</p>
              <h3 class="text-2xl font-bold text-gray-800 mt-1">{{ stats.totalCases }}</h3>
            </div>
          </div>
          <p class="text-xs text-indigo-600 mt-3 font-medium">{{ stats.activeCases }} đang xử lý</p>
        </div>

        <div class="bg-white p-5 rounded-lg border shadow-sm flex flex-col justify-between">
          <div class="flex justify-between items-start">
            <div>
              <p class="text-xs text-gray-500 font-medium">TỔNG KHÁCH HÀNG</p>
              <h3 class="text-2xl font-bold text-gray-800 mt-1">{{ stats.totalClients }}</h3>
            </div>
          </div>
          <p class="text-xs text-gray-500 mt-3">{{ stats.orgClients }} tổ chức / {{ stats.indClients }} cá nhân</p>
        </div>
      </div>

      <!-- Charts & Type Distributions -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="bg-white p-5 rounded-lg border shadow-sm">
          <h3 class="text-sm font-bold text-gray-700 mb-4">Tài liệu tạo mới 6 tháng gần nhất</h3>
          <div class="h-44 flex items-stretch justify-between gap-2 pt-6 px-2 border-b border-l">
            <div v-for="(m, i) in chartData.monthly" :key="i" class="flex-1 h-full flex flex-col justify-end items-center">
              <span class="text-xs text-gray-500 mb-1">{{ m.count }}</span>
              <div class="w-full bg-blue-500 rounded-t transition-all" :style="{ height: ((m.count / maxMonthlyCount) * 100) + 'px' }"></div>
              <span class="text-xs text-gray-400 mt-2">{{ m.month }}</span>
            </div>
          </div>
        </div>

        <div class="bg-white p-5 rounded-lg border shadow-sm">
          <h3 class="text-sm font-bold text-gray-700 mb-4">Phân loại vụ việc</h3>
          <div class="space-y-3 pt-2">
            <div v-for="(type, idx) in chartData.caseTypes" :key="idx">
              <div class="flex justify-between text-xs mb-1 font-medium">
                <span>{{ type.label }}</span>
                <span>{{ type.count }} vụ</span>
              </div>
              <div class="w-full bg-gray-100 h-2 rounded-full overflow-hidden">
                <div class="h-full bg-indigo-600 rounded-full" :style="{ width: type.percent + '%' }"></div>
              </div>
            </div>
            <div v-if="chartData.caseTypes.length === 0" class="text-center py-8 text-gray-400 text-sm">
              Không có dữ liệu vụ việc
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Pending Documents -->
      <div class="bg-white rounded-lg border shadow-sm p-5">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-base font-bold text-gray-800">Tài liệu chờ duyệt gần nhất</h3>
          <router-link to="/approval" class="text-sm text-blue-600 hover:underline font-medium">Xem tất cả →</router-link>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead>
              <tr class="bg-gray-50 text-left text-xs font-semibold text-gray-500 uppercase">
                <th class="px-4 py-3">Tên tài liệu</th>
                <th class="px-4 py-3">Số hiệu</th>
                <th class="px-4 py-3">Ngày gửi</th>
                <th class="px-4 py-3 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 text-sm">
              <tr v-for="doc in pendingList.slice(0, 5)" :key="doc.id" class="hover:bg-gray-50">
                <td class="px-4 py-3 font-medium text-gray-900">{{ doc.ten }}</td>
                <td class="px-4 py-3 text-gray-600">{{ doc.soHieu || '---' }}</td>
                <td class="px-4 py-3 text-gray-500">{{ formatDate(doc.ngayTao) }}</td>
                <td class="px-4 py-3 text-right">
                  <router-link to="/approval" class="px-2.5 py-1 bg-blue-50 text-blue-600 rounded text-xs font-medium">Xem nhanh</router-link>
                </td>
              </tr>
              <tr v-if="pendingList.length === 0">
                <td colspan="4" class="text-center py-6 text-gray-400">Không có tài liệu nào chờ duyệt</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <div v-else class="space-y-6">
      <h1 class="text-xl font-bold text-gray-800">Công việc của tôi</h1>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="bg-white p-5 rounded-lg border shadow-sm">
          <p class="text-xs text-gray-500 font-medium">TÀI LIỆU CỦA TÔI</p>
          <h3 class="text-2xl font-bold text-gray-800 mt-1">{{ myStats.totalDocs }}</h3>
          <p class="text-xs text-gray-400 mt-2">{{ myStats.draftDocs }} bản nháp</p>
        </div>

        <div class="bg-white p-5 rounded-lg border shadow-sm">
          <p class="text-xs text-gray-500 font-medium">ĐANG CHỜ DUYỆT</p>
          <h3 class="text-2xl font-bold text-amber-600 mt-1">{{ myStats.pendingDocs }}</h3>
          <p class="text-xs text-amber-600 mt-2">Đang chờ quản lý duyệt</p>
        </div>

        <div class="bg-white p-5 rounded-lg border shadow-sm">
          <p class="text-xs text-gray-500 font-medium">VỤ VIỆC PHỤ TRÁCH</p>
          <h3 class="text-2xl font-bold text-indigo-600 mt-1">{{ myStats.myCases }}</h3>
          <p class="text-xs text-indigo-600 mt-2">Đang xử lý</p>
        </div>
      </div>

      <!-- Employee Documents List -->
      <div class="bg-white rounded-lg border shadow-sm p-5">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-base font-bold text-gray-800">Tài liệu của tôi gần đây</h3>
          <router-link to="/documents" class="text-sm text-blue-600 hover:underline font-medium">Xem tất cả tài liệu →</router-link>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead>
              <tr class="bg-gray-50 text-left text-xs font-semibold text-gray-500 uppercase">
                <th class="px-4 py-3">Tên</th>
                <th class="px-4 py-3">Trạng thái</th>
                <th class="px-4 py-3">Ngày tạo</th>
                <th class="px-4 py-3 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 text-sm">
              <tr v-for="doc in myDocs.slice(0, 5)" :key="doc.id">
                <td class="px-4 py-3 font-medium text-gray-800">{{ doc.ten }}</td>
                <td class="px-4 py-3">
                  <span class="px-2 py-0.5 text-xs rounded-full font-medium" :class="getStatusBadgeClass(doc.trangThai)">
                    {{ getStatusText(doc.trangThai) }}
                  </span>
                </td>
                <td class="px-4 py-3 text-gray-500">{{ formatDate(doc.ngayTao) }}</td>
                <td class="px-4 py-3 text-right">
                  <router-link :to="`/documents/${doc.id}`" class="text-blue-600 text-xs font-medium">Xem</router-link>
                </td>
              </tr>
              <tr v-if="myDocs.length === 0">
                <td colspan="4" class="text-center py-6 text-gray-400">Bạn chưa tạo tài liệu nào</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Employee Cases List -->
      <div class="bg-white rounded-lg border shadow-sm p-5">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-base font-bold text-gray-800">Vụ việc tôi phụ trách</h3>
          <router-link to="/cases" class="text-sm text-blue-600 hover:underline font-medium">Xem tất cả vụ việc →</router-link>
        </div>
        <div class="overflow-x-auto">
          <table class="min-w-full divide-y divide-gray-200">
            <thead>
              <tr class="bg-gray-50 text-left text-xs font-semibold text-gray-500 uppercase">
                <th class="px-4 py-3">Tên vụ việc</th>
                <th class="px-4 py-3">Khách hàng</th>
                <th class="px-4 py-3">Trạng thái</th>
                <th class="px-4 py-3">Ngày mở</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-100 text-sm">
              <tr v-for="c in myCasesList.slice(0, 5)" :key="c.id">
                <td class="px-4 py-3 font-medium text-gray-800">{{ c.ten }}</td>
                <td class="px-4 py-3 text-gray-600">{{ c.khachHang?.ten || 'Chưa rõ' }}</td>
                <td class="px-4 py-3">
                  <span class="px-2 py-0.5 bg-blue-50 text-blue-600 text-xs rounded font-medium">
                    {{ getCaseStatusLabel(c.trangThai) }}
                  </span>
                </td>
                <td class="px-4 py-3 text-gray-500">{{ c.ngayMo }}</td>
              </tr>
              <tr v-if="myCasesList.length === 0">
                <td colspan="4" class="text-center py-6 text-gray-400">Bạn chưa được phân công phụ trách vụ việc nào</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import { documentApi } from '../api/documents';
import { caseApi } from '../api/cases';
import { clientApi } from '../api/clients';

const authStore = useAuthStore();

// Kiểm tra Role
const isAdminOrManager = computed(() => {
  const role = authStore.user?.vaiTro || '';
  return role === 'ADMIN' || role === 'TRUONG_PHONG';
});

// Data Admin Stats
const stats = ref({ totalDocs: 0, pendingDocs: 0, approvedDocs: 0, approvedThisMonth: 0, totalCases: 0, activeCases: 0, totalClients: 0, orgClients: 0, indClients: 0 });
const pendingList = ref([]);

// Data Employee Stats
const myStats = ref({ totalDocs: 0, draftDocs: 0, pendingDocs: 0, myCases: 0 });
const myDocs = ref([]);
const myCasesList = ref([]);

// Hàm tạo cấu trúc rỗng cho biểu đồ 6 tháng gần nhất
const generateEmptyMonthlyData = () => {
  const months = [];
  const now = new Date();
  for (let i = 5; i >= 0; i--) {
    const d = new Date(now.getFullYear(), now.getMonth() - i, 1);
    months.push({
      year: d.getFullYear(),
      monthNum: d.getMonth(),
      month: `Tháng ${d.getMonth() + 1}`,
      count: 0
    });
  }
  return months;
};

// Dữ liệu Biểu đồ khởi tạo trống
const chartData = ref({
  monthly: generateEmptyMonthlyData(),
  caseTypes: []
});

const maxMonthlyCount = computed(() => {
  const counts = chartData.value.monthly.map(m => m.count);
  return counts.length > 0 ? Math.max(...counts, 10) : 10;
});

const fetchDashboardData = async () => {
  try {
    const [resDocs, resCases, resClients] = await Promise.all([
      documentApi.getAll({ size: 100 }),
      caseApi.getAll(),
      clientApi.getAll()
    ]);

    const docs = resDocs.data?.data?.content || resDocs.data?.data || [];
    const cases = resCases.data?.data?.content || resCases.data?.data || [];
    const clients = resClients.data?.data || [];

    if (isAdminOrManager.value) {
      stats.value.totalDocs = docs.length;
      stats.value.pendingDocs = docs.filter(d => {
        const s = (d.trangThai || '').toUpperCase();
        return s === 'CHO_DUYET' || s === 'CHO_PHE_DUYET';
      }).length;
      stats.value.approvedDocs = docs.filter(d => (d.trangThai || '').toUpperCase() === 'DA_DUYET').length;
      stats.value.totalCases = cases.length;
      stats.value.activeCases = cases.filter(c => (c.trangThai || '').toUpperCase() !== 'DA_DONG').length;
      stats.value.totalClients = clients.length;
      stats.value.orgClients = clients.filter(c => c.loai === 'TO_CHUC').length;
      stats.value.indClients = clients.filter(c => c.loai !== 'TO_CHUC').length;
      pendingList.value = docs.filter(d => {
        const s = (d.trangThai || '').toUpperCase();
        return s === 'CHO_DUYET' || s === 'CHO_PHE_DUYET';
      });

      // THỐNG KÊ ĐỘNG CHO BIỂU ĐỒ TÀI LIỆU
      const monthlyData = generateEmptyMonthlyData();
      docs.forEach(doc => {
        if (doc.ngayTao) {
          const date = new Date(doc.ngayTao);
          if (!isNaN(date.getTime())) {
            const docYear = date.getFullYear();
            const docMonth = date.getMonth();
            const matchedMonth = monthlyData.find(m => m.year === docYear && m.monthNum === docMonth);
            if (matchedMonth) {
              matchedMonth.count++;
            }
          }
        }
      });
      chartData.value.monthly = monthlyData;

      // THỐNG KÊ ĐỘNG CHO BIỂU ĐỒ PHÂN LOẠI VỤ VIỆC
      const caseTypeCounts = {};
      cases.forEach(c => {
        const type = c.loai || 'Khác';
        caseTypeCounts[type] = (caseTypeCounts[type] || 0) + 1;
      });

      const totalCasesCount = cases.length;
      chartData.value.caseTypes = Object.keys(caseTypeCounts).map(type => {
        const count = caseTypeCounts[type];
        const percent = totalCasesCount > 0 ? Math.round((count / totalCasesCount) * 100) : 0;
        return {
          label: type,
          count: count,
          percent: percent
        };
      });
    } else {
      // Đối với nhân viên, chỉ lấy tài liệu/vụ việc thuộc về chính họ phụ trách
      const myId = authStore.user?.id;
      
      myDocs.value = docs.filter(d => d.nguoiTaoId === myId || d.boPhan?.id === authStore.user?.boPhan?.id);
      myCasesList.value = cases.filter(c => c.nguoiPhuTrach?.id === myId);

      myStats.value.totalDocs = myDocs.value.length;
      myStats.value.draftDocs = myDocs.value.filter(d => {
        const s = (d.trangThai || '').toUpperCase();
        return s === 'NHAP' || s === 'DRAFT';
      }).length;
      myStats.value.pendingDocs = myDocs.value.filter(d => {
        const s = (d.trangThai || '').toUpperCase();
        return s === 'CHO_DUYET' || s === 'CHO_PHE_DUYET';
      }).length;
      myStats.value.myCases = myCasesList.value.length;
    }
  } catch (err) {
    console.error('Lỗi tải dữ liệu Dashboard:', err);
  }
};

const getStatusBadgeClass = (status) => {
  const s = (status || '').toUpperCase();
  if (s === 'DA_DUYET') return 'bg-green-100 text-green-700';
  if (s === 'CHO_DUYET' || s === 'CHO_PHE_DUYET') return 'bg-amber-100 text-amber-700';
  if (s === 'TU_CHOI') return 'bg-red-100 text-red-700';
  return 'bg-gray-100 text-gray-700';
};

const getStatusText = (status) => {
  const s = (status || '').toUpperCase();
  if (s === 'DA_DUYET') return 'Đã duyệt';
  if (s === 'CHO_DUYET' || s === 'CHO_PHE_DUYET') return 'Chờ duyệt';
  if (s === 'TU_CHOI') return 'Từ chối';
  return 'Bản nháp';
};

const getCaseStatusLabel = (st) => {
  if (!st) return 'Chưa rõ';
  const s = String(st).toUpperCase();
  if (s === 'MOI_TIEP_NHAN') return 'Mới tiếp nhận';
  if (s === 'DANG_XU_LY') return 'Đang xử lý';
  if (s === 'DA_DONG') return 'Đã đóng';
  return st;
};

const formatDate = (dateStr) => dateStr ? dateStr.substring(0, 10) : '---';

onMounted(() => {
  fetchDashboardData();
});
</script>
