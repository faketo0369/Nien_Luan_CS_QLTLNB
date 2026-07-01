<template>
  <div v-if="modelValue" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/40 backdrop-blur-sm">
    <div class="bg-white rounded-2xl max-w-md w-full shadow-2xl border border-gray-100 overflow-hidden transform transition-all animate-fade-in">
      <div class="p-6">
        <div class="flex items-center gap-3 text-slate-800 mb-3">
          <span class="text-2xl">{{ typeIcon }}</span>
          <h3 class="text-base font-bold">{{ title }}</h3>
        </div>
        <p class="text-sm text-gray-500 pl-9 font-medium">{{ message }}</p>
        
        <div v-if="requireNote" class="mt-4 pl-9">
          <label class="block text-xs font-bold text-gray-400 uppercase tracking-wider mb-1.5">Lý do xử lý bắt buộc</label>
          <textarea v-model="note" rows="3" class="w-full text-sm p-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:outline-none transition" placeholder="Nhập lý do chi tiết tại đây..."></textarea>
        </div>
      </div>
      <div class="bg-gray-50 px-6 py-4 flex justify-end gap-3 border-t border-gray-100">
        <button @click="closeModal" class="px-4 py-2 text-sm font-semibold text-gray-600 bg-white border border-gray-200 rounded-xl hover:bg-gray-50 transition">
          Hủy bỏ
        </button>
        <button @click="confirmAction" :disabled="requireNote && !note.trim()" :class="['px-4 py-2 text-sm font-semibold text-white rounded-xl shadow-md transition disabled:opacity-50', confirmBtnClass]">
          Xác nhận
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';

const props = defineProps({
  modelValue: { type: Boolean, required: true },
  title: { type: String, default: 'Xác nhận thao tác' },
  message: { type: String, default: 'Bạn có chắc chắn muốn thực hiện hành động kiểm soát này không?' },
  type: { type: String, default: 'info' }, // danger, success, info
  requireNote: { type: Boolean, default: false }
});

const emit = defineEmits(['update:modelValue', 'confirm']);
const note = ref('');

const typeIcon = computed(() => {
  if (props.type === 'danger') return '🚨';
  if (props.type === 'success') return '✅';
  return '💡';
});

const confirmBtnClass = computed(() => {
  if (props.type === 'danger') return 'bg-rose-600 hover:bg-rose-700 shadow-rose-200';
  if (props.type === 'success') return 'bg-emerald-600 hover:bg-emerald-700 shadow-emerald-200';
  return 'bg-blue-600 hover:bg-blue-700 shadow-blue-200';
});

const closeModal = () => emit('update:modelValue', false);

const confirmAction = () => {
  emit('confirm', props.requireNote ? note.value : true);
  closeModal();
};

watch(() => props.modelValue, (val) => { if (val) note.value = ''; });
</script>
