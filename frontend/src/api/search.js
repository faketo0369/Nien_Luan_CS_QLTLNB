import api from './auth';

export const searchApi = {
  documents: (params) => api.get('/search/documents', { params }),
  clients: (params) => api.get('/search/clients', { params }),
  cases: (params) => api.get('/search/cases', { params }),
  global: (q) => api.get('/search/global', { params: { q } })
};
