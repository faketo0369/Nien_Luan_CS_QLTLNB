import api from './auth';

export const activityLogApi = {
  getAll: (params) => api.get('/activity-logs', { params })
};
