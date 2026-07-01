import api from './auth';

export const versionApi = {
  getByDocumentId: (documentId) => api.get(`/documents/${documentId}/versions`),
  downloadVersion: (documentId, versionId) => api.get(`/documents/${documentId}/versions/${versionId}/download`, {
    responseType: 'blob'
  })
};
