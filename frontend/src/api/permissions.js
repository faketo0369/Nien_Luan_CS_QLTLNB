import api from './auth';

export const permissionApi = {
  getByDocumentId: (documentId) => api.get(`/documents/${documentId}/permissions`),
  grant: (documentId, data) => api.post(`/documents/${documentId}/permissions`, data),
  revoke: (documentId, permissionId) => api.delete(`/documents/${documentId}/permissions/${permissionId}`)
};
