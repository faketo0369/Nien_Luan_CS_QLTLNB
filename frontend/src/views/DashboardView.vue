<template>
  <div class="dashboard-box" style="padding: 25px; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
    
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <div>
        <h2 style="margin: 0; color: #333;">Xin chào, {{ authStore.user?.hoTen || 'Thành viên' }}</h2>
        <small style="color: #666;">Bộ phận: {{ authStore.user?.boPhan || 'Chưa rõ' }}</small>
      </div>
      <span style="padding: 6px 14px; background: #e2e3e5; color: #383d41; border-radius: 20px; font-weight: bold; font-size: 13px;">
        Quyền: {{ authStore.user?.vaiTro }}
      </span>
    </div>

    <hr style="border: 0; border-top: 1px solid #ddd; margin-bottom: 20px;" />

    <div style="margin-bottom: 15px; display: flex; gap: 10px;">
      <input 
        v-model="searchKeyword" 
        type="text" 
        placeholder="Nhập tên tài liệu cần tìm..." 
        style="padding: 8px 12px; width: 320px; border: 1px solid #ccc; border-radius: 4px;"
        @keyup.enter="doSearch"
      />
      <button @click="doSearch" style="padding: 8px 18px; background: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">
        Tìm kiếm
      </button>
    </div>

    <div v-if="loading" style="padding: 30px; text-align: center; color: #666; font-size: 16px;">
      🔄 Đang tải danh sách tài liệu thực tế từ MySQL...
    </div>
    
    <div v-else-if="errorMsg" style="padding: 15px; background: #f8d7da; color: #721c24; border-radius: 4px; margin-bottom: 15px;">
      ⚠️ Lỗi: {{ errorMsg }}
    </div>

    <div v-else>
      <table style="width: 100%; border-collapse: collapse; margin-bottom: 15px;">
        <thead>
          <tr style="background: #f4f6f9; text-align: left; border-bottom: 2px solid #dee2e6;">
            <th style="padding: 12px; border: 1px solid #dee2e6;">Tên tài liệu</th>
            <th style="padding: 12px; border: 1px solid #dee2e6;">Số hiệu</th>
            <th style="padding: 12px; border: 1px solid #dee2e6;">Danh mục</th>
            <th style="padding: 12px; border: 1px solid #dee2e6;">Loại tài liệu</th>
            <th style="padding: 12px; border: 1px solid #dee2e6;">Ngày tạo</th>
            <th style="padding: 12px; border: 1px solid #dee2e6;">Trạng thái</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in docs" :key="item.id" style="border-bottom: 1px solid #dee2e6;">
            <td style="padding: 12px; border: 1px solid #dee2e6; font-weight: 500; color: #0056b3;">{{ item.ten }}</td>
            <td style="padding: 12px; border: 1px solid #dee2e6;">{{ item.soHieu || '---' }}</td>
            <td style="padding: 12px; border: 1px solid #dee2e6;">{{ item.danhMuc }}</td>
            <td style="padding: 12px; border: 1px solid #dee2e6;">{{ item.loaiTaiLieu }}</td>
            <td style="padding: 12px; border: 1px solid #dee2e6;">{{ item.ngayTao ? item.ngayTao.substring(0,10) : '---' }}</td>
            <td style="padding: 12px; border: 1px solid #dee2e6;">
              <span :style="getBadgeStyle(item.trangThai)">
                {{ getStatusLabel(item.trangThai) }}
              </span>
            </td>
          </tr>
          <tr v-if="docs.length === 0">
            <td colspan="6" style="padding: 30px; text-align: center; color: #999;">Không có dữ liệu tài liệu nào thỏa mãn!</td>
          </tr>
        </tbody>
      </table>

      <div style="display: flex; justify-content: space-between; align-items: center;">
        <span style="font-size: 14px; color: #555;">
          Trang {{ pageIndex + 1 }} / {{ totalPages }} (Tổng cộng {{ totalElements }} bản ghi)
        </span>
        <div style="display: flex; gap: 10px;">
          <button :disabled="pageIndex === 0" @click="goToPage(pageIndex - 1)" style="padding: 6px 12px; cursor: pointer;">
            Trước
          </button>
          <button :disabled="pageIndex >= totalPages - 1" @click="goToPage(pageIndex + 1)" style="padding: 6px 12px; cursor: pointer;">
            Sau
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useAuthStore } from '../stores/auth';
import { danhSachTaiLieu } from '../api/auth';

const authStore = useAuthStore();
const docs = ref([]);
const loading = ref(false);
const errorMsg = ref('');

// Phân trang & Tìm kiếm
const pageIndex = ref(0);
const totalPages = ref(1);
const totalElements = ref(0);
const searchKeyword = ref('');

const loadData = async () => {
  loading.value = true;
  errorMsg.value = '';
  try {
    const data = await danhSachTaiLieu({
      page: pageIndex.value,
      size: 10,
      tuKhoa: searchKeyword.value
    });
    docs.value = data.content;
    totalPages.value = data.totalPages;
    totalElements.value = data.totalElements;
  } catch (err) {
    errorMsg.value = 'Không thể kết nối đến API lấy danh sách tài liệu. Hãy chắc chắn Spring Boot port 8080 vẫn đang chạy!';
  } finally {
    loading.value = false;
  }
};

const doSearch = () => {
  pageIndex.value = 0; // Tìm từ khóa mới thì reset về trang 1
  loadData();
};

const goToPage = (targetPage) => {
  if (targetPage >= 0 && targetPage < totalPages.value) {
    pageIndex.value = targetPage;
    loadData();
  }
};

const getStatusLabel = (st) => {
  if (st === 'da_duyet') return 'Đã duyệt';
  if (st === 'cho_duyet') return 'Chờ duyệt';
  if (st === 'tu_choi') return 'Từ chối';
  return 'Bản nháp';
};

const getBadgeStyle = (st) => {
  let bg = '#6c757d'; // nhap -> xám
  let color = '#fff';
  if (st === 'da_duyet') bg = '#28a745'; // xanh lá
  else if (st === 'cho_duyet') { bg = '#ffc107'; color = '#000'; } // vàng
  else if (st === 'tu_choi') bg = '#dc3545'; // đỏ
  return `padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold; background: ${bg}; color: ${color};`;
};

onMounted(() => {
  loadData();
});
</script>
