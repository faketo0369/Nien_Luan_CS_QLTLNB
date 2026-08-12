import api from './auth';

export const adminApi = {
  users: {
    getAll: (params) => api.get('/users', { params }),
    getById: (id) => api.get(`/users/${id}`),
    create: (data) => api.post('/users', data),
    update: (id, data) => api.put(`/users/${id}`, data),
    delete: (id) => api.delete(`/users/${id}`)
  },
  departments: {
    getAll: () => api.get('/departments'),
    create: (data) => api.post('/departments', data),
    update: (id, data) => api.put(`/departments/${id}`, data),
    delete: (id) => api.delete(`/departments/${id}`)
  },
  categories: {
    getAll: () => api.get('/categories'),
    create: (data) => api.post('/categories', data),
    delete: (id) => api.delete(`/categories/${id}`)
  },
  docTypes: {
    getAll: () => api.get('/doc-types'),
    create: (data) => api.post('/doc-types', data),
    delete: (id) => api.delete(`/doc-types/${id}`)
  }
};
