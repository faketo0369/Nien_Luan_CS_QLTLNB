import api from './auth';

export const documentApi = {
  getAll: (params) => api.get('/documents', { params }),
  getById: (id) => api.get(`/documents/${id}`),
  create: (formData) => api.post('/documents', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  update: (id, formData) => api.put(`/documents/${id}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  delete: (id) => api.delete(`/documents/${id}`),
  download: (id) => api.get(`/documents/${id}/download`, { responseType: 'blob' }),

  // Quy trình phê duyệt tài liệu
  submit: (id) => api.post(`/documents/${id}/approval/submit`),
  approve: (id) => api.post(`/documents/${id}/approval/approve`),
  reject: (id, data) => api.post(`/documents/${id}/approval/reject`, data), // data: { ghiChu: '...' }
  getApprovalHistory: (id) => api.get(`/documents/${id}/approval/history`),
  changeFile: (id, file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(`/documents/${id}/upload`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });
  }
};
