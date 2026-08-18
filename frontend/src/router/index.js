import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { requiresGuest: true }
    },
    {
      path: '/',
      component: () => import('../layouts/MainLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('../views/DashboardView.vue')
        },
        {
          path: 'documents',
          name: 'documents',
          component: () => import('../views/documents/DocumentListView.vue')
        },
        {
          path: 'documents/:id',
          name: 'document-detail',
          component: () => import('../views/documents/DocumentDetailView.vue')
        },
        {
          path: 'cases',
          name: 'cases',
          component: () => import('../views/cases/CaseListView.vue')
        },
        {
          path: 'cases/:id',
          name: 'case-detail',
          component: () => import('../views/cases/CaseDetailView.vue')
        },
        {
          path: 'clients',
          name: 'clients',
          component: () => import('../views/clients/ClientListView.vue')
        },
        {
          path: 'clients/:id',
          name: 'client-detail',
          component: () => import('../views/clients/ClientDetailView.vue')
        },
        {
          path: 'approval',
          name: 'approval',
          component: () => import('../views/ApprovalView.vue'),
          meta: { allowedRoles: ['TRUONG_PHONG', 'ADMIN'] }
        },
        {
          path: 'notifications',
          name: 'notifications',
          component: () => import('../views/NotificationView.vue')
        },
        {
          path: 'activity-logs',
          name: 'activity-logs',
          component: () => import('../views/ActivityLogView.vue'),
          meta: { allowedRoles: ['TRUONG_PHONG', 'ADMIN'] }
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('../views/ProfileView.vue')
        },
        // Admin Module Routes
        {
          path: 'admin/users',
          name: 'admin-users',
          component: () => import('../views/admin/UserListView.vue'),
          meta: { allowedRoles: ['ADMIN', 'TRUONG_PHONG'] }
        },
        {
          path: 'admin/departments',
          name: 'admin-departments',
          component: () => import('../views/admin/DepartmentListView.vue'),
          meta: { allowedRoles: ['ADMIN'] }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ]
});

// Navigation Guards
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = !!authStore.token || !!authStore.user;
  const userRole = authStore.user?.vaiTro;

  // Nếu đã đăng nhập mà vào trang login -> chuyển về dashboard
  if (to.meta.requiresGuest && isAuthenticated) {
    return next({ name: 'dashboard' });
  }

  // Nếu chưa đăng nhập mà vào trang yêu cầu auth -> chuyển về login
  if (to.meta.requiresAuth && !isAuthenticated) {
    return next({ name: 'login' });
  }

  // Kiểm tra phân quyền vai trò
  if (to.meta.allowedRoles && !to.meta.allowedRoles.includes(userRole)) {
    return next({ name: 'dashboard' });
  }

  next();
});

export default router;
