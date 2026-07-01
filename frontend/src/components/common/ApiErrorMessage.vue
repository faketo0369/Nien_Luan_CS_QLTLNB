<template>
  <div v-if="error" class="p-4 bg-rose-50 border border-rose-200 text-rose-700 rounded-xl flex items-start gap-3 my-3 shadow-sm animate-pulse">
    <span class="text-xl">⚠️</span>
    <div class="flex-1">
      <h5 class="font-bold text-sm">Lỗi xử lý nghiệp vụ hệ thống</h5>
      <p class="text-xs mt-1 text-rose-600 font-medium">{{ parsedMessage }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  error: { type: [Object, String, null], default: null }
});

const parsedMessage = computed(() => {
  if (!props.error) return '';
  if (typeof props.error === 'string') return props.error;
  return props.error.response?.data?.message || props.error.message || 'Hệ thống Backend từ chối phản hồi.';
});
</script>
