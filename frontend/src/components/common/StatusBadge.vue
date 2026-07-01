<template>
  <span :class="['px-2.5 py-1 text-xs font-bold font-mono tracking-wider uppercase rounded border shadow-inner', badgeConfig.class]">
    {{ badgeConfig.text }}
  </span>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  status: { type: String, required: true }
});

const badgeConfig = computed(() => {
  switch (props.status?.toLowerCase()) {
    case 'nhap':
      return { text: 'Bản nháp', class: 'bg-gray-100 border-gray-300 text-gray-600' };
    case 'cho_duyet':
      return { text: 'Chờ duyệt', class: 'bg-amber-50 border-amber-300 text-amber-700' };
    case 'da_duyet':
      return { text: 'Đã duyệt', class: 'bg-emerald-50 border-emerald-300 text-emerald-700' };
    case 'tu_choi':
      return { text: 'Từ chối', class: 'bg-rose-50 border-rose-300 text-rose-700' };
    default:
      return { text: props.status, class: 'bg-slate-100 border-slate-300 text-slate-600' };
  }
});
</script>
