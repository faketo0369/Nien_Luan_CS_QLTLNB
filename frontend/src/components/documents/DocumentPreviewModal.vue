<template>
  <div v-if="show" class="fixed inset-0 bg-black/75 flex items-center justify-center z-50 p-4" @click.self="close">
    <div class="bg-white rounded-xl shadow-2xl w-full max-w-5xl h-[90vh] flex flex-col overflow-hidden">
      
      <!-- Header -->
      <div class="flex justify-between items-center px-6 py-4 border-b bg-gray-50">
        <div class="flex items-center space-x-2">
          <h3 class="font-bold text-gray-800 text-lg truncate max-w-xl">Xem trước: {{ documentTitle }}</h3>
        </div>
        <div class="flex items-center space-x-3">
          <button 
            @click="handleDownload" 
            class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold rounded-lg shadow transition"
          >
            Tải về
          </button>
          <button 
            @click="close" 
            class="text-gray-400 hover:text-gray-700 text-2xl font-bold px-2 transition"
          >
            &times;
          </button>
        </div>
      </div>

      <!-- Preview Body -->
      <div class="flex-1 bg-gray-100 p-4 overflow-auto flex items-center justify-center relative">
        <div v-if="loading" class="text-center text-gray-500">
          <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-blue-600 border-t-transparent mb-2"></div>
          <p class="text-sm font-medium">Đang tải bản xem trước...</p>
        </div>

        <div v-else-if="errorMsg" class="text-center py-12 bg-white rounded-lg p-8 shadow max-w-md border border-gray-200">
          <p class="text-gray-800 font-bold text-base mb-2">Không thể xem trước tệp tin</p>
          <p class="text-gray-500 text-sm mb-6">{{ errorMsg }}</p>
          <button @click="handleDownload" class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold rounded-lg shadow transition">
            Thử tải về máy
          </button>
        </div>

        <iframe 
          v-else-if="fileType === 'pdf' || fileType === 'iframe'" 
          :src="fileBlobUrl" 
          class="w-full h-full rounded border bg-white shadow-sm"
        ></iframe>

        <img 
          v-else-if="fileType === 'image'" 
          :src="fileBlobUrl" 
          class="max-w-full max-h-full object-contain rounded shadow bg-white"
        />

        <div 
          v-else-if="fileType === 'docx'" 
          ref="docxContainer" 
          class="w-full h-full bg-white p-8 overflow-auto rounded border shadow text-gray-800"
        ></div>

        <div v-else-if="!loading" class="text-center py-12 bg-white rounded-lg p-8 shadow max-w-md border border-gray-200">
          <p class="text-gray-700 font-semibold mb-2">Định dạng tệp tin chưa hỗ trợ xem trực tiếp</p>
          <p class="text-gray-500 text-sm mb-4">Định dạng: {{ fileExtension || 'không xác định' }}</p>
          <button @click="handleDownload" class="px-4 py-2 bg-blue-600 text-white text-sm font-semibold rounded-lg shadow">Tải về máy để xem</button>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue';
import { renderAsync } from 'docx-preview';
import { documentApi } from '../../api/documents';

const props = defineProps({
  show: Boolean,
  documentId: [Number, String],
  documentTitle: String,
  fileExtension: String
});

const emit = defineEmits(['close', 'download']);

const loading = ref(false);
const errorMsg = ref('');
const fileBlobUrl = ref('');
const fileType = ref('');
const docxContainer = ref(null);

const close = () => {
  if (fileBlobUrl.value) {
    URL.revokeObjectURL(fileBlobUrl.value);
  }
  fileBlobUrl.value = '';
  errorMsg.value = '';
  fileType.value = '';
  emit('close');
};

const loadPreview = async () => {
  if (!props.documentId) return;
  loading.value = true;
  errorMsg.value = '';
  fileBlobUrl.value = '';
  fileType.value = '';

  try {
    const response = await documentApi.preview(props.documentId);
    const contentType = (response.headers['content-type'] || '').toLowerCase();
    const ext = (props.fileExtension || '').toLowerCase();

    const blob = new Blob([response.data], { type: contentType || 'application/octet-stream' });
    
    if (blob.size === 0) {
      errorMsg.value = 'Tài liệu này chưa có nội dung tệp tin vật lý đính kèm.';
      return;
    }

    fileBlobUrl.value = URL.createObjectURL(blob);

    if (contentType.includes('pdf') || ext === 'pdf') {
      fileType.value = 'pdf';
    } else if (contentType.includes('image') || ['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext)) {
      fileType.value = 'image';
    } else if (contentType.includes('word') || contentType.includes('officedocument') || ext === 'docx' || ext === 'doc') {
      fileType.value = 'docx';
      await nextTick();
      if (docxContainer.value) {
        docxContainer.value.innerHTML = '';
        await renderAsync(blob, docxContainer.value);
      }
    } else {
      fileType.value = 'iframe';
    }
  } catch (err) {
    console.error('Lỗi nạp tệp tin preview:', err);
    if (err.response?.data instanceof Blob) {
      try {
        const text = await err.response.data.text();
        const json = JSON.parse(text);
        errorMsg.value = json.message || 'Không thể tải bản xem trước tệp tin.';
      } catch (e) {
        errorMsg.value = 'Tài liệu này chưa có tệp tin đính kèm hoặc tệp tin vật lý không tồn tại trên máy chủ.';
      }
    } else {
      errorMsg.value = err.response?.data?.message || 'Tài liệu này chưa có tệp tin đính kèm hoặc tệp tin vật lý không tồn tại trên máy chủ.';
    }
  } finally {
    loading.value = false;
  }
};

const handleDownload = () => {
  emit('download', props.documentId);
};

watch(() => props.show, (newVal) => {
  if (newVal) {
    loadPreview();
  }
});
</script>
