import { defineStore } from 'pinia';
import { notificationApi } from '../api/notifications';

export const useNotificationStore = defineStore('notification', {
  state: () => ({
    unreadCount: 0
  }),
  actions: {
    async fetchUnreadCount() {
      try {
        const res = await notificationApi.getUnreadCount();
        if (res.data?.success) {
          this.unreadCount = res.data.data;
        }
      } catch (error) {
        console.error('Lỗi lấy số thông báo chưa đọc:', error);
      }
    }
  }
});
