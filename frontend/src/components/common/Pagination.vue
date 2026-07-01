<template>
  <div v-if="totalPages > 1" class="flex items-center justify-between bg-white px-6 py-4 border border-gray-200 rounded-2xl shadow-sm mt-4">
    <div class="flex flex-1 justify-between sm:hidden">
      <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 0" class="pagination-btn">Trước</button>
      <button @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages - 1" class="pagination-btn ml-3">Sau</button>
    </div>
    <div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
      <p class="text-sm text-gray-500 font-medium">
        Trang <span class="font-bold text-blue-600">{{ currentPage + 1 }}</span> / <span class="font-bold text-gray-800">{{ totalPages }}</span> (Tổng số <span class="font-bold text-gray-800">{{ totalElements }}</span> mục dữ liệu)
      </p>
      <nav class="inline-flex -space-x-px rounded-xl shadow-sm bg-white border border-gray-200 overflow-hidden" aria-label="Pagination">
        <button @click="goToPage(currentPage - 1)" :disabled="currentPage === 0" class="px-3 py-2 text-gray-400 hover:bg-gray-50 disabled:opacity-40">◀</button>
        
        <button v-for="page in totalPages" :key="page" @click="goToPage(page - 1)" :class="['px-4 py-2 text-sm font-bold border-r border-gray-100 last:border-0 transition', page - 1 === currentPage ? 'bg-blue-600 text-white' : 'text-gray-700 hover:bg-gray-50']">
          {{ page }}
        </button>

        <button @click="goToPage(currentPage + 1)" :disabled="currentPage === totalPages - 1" class="px-3 py-2 text-gray-400 hover:bg-gray-50 disabled:opacity-40">▶</button>
      </nav>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  currentPage: { type: Number, required: true },
  totalPages: { type: Number, required: true },
  totalElements: { type: Number, default: 0 }
});

const emit = defineEmits(['page-changed']);

const goToPage = (page) => {
  if (page >= 0 && page < props.totalPages) {
    emit('page-changed', page);
  }
};
</script>

<style scoped>
.pagination-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 0.75rem;
  background-color: white;
  font-size: 0.875rem;
  font-weight: 600;
  color: #334155;
}
.pagination-btn:disabled {
  opacity: 0.5;
}
</style>
