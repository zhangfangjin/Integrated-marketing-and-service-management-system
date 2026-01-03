import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/home',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'modules',
        name: 'Modules',
        component: () => import('@/views/modules/ModuleManagement.vue')
      },
      {
        path: 'permissions',
        name: 'Permissions',
        component: () => import('@/views/permissions/PermissionConfig.vue')
      },
      {
        path: 'roles',
        name: 'Roles',
        component: () => import('@/views/roles/RoleManagement.vue')
      },
      {
        path: 'options',
        name: 'Options',
        component: () => import('@/views/options/OptionManagement.vue')
      },
      {
        path: 'accounts',
        name: 'Accounts',
        component: () => import('@/views/accounts/AccountManagement.vue')
      },
      {
        path: 'personnel/archive',
        name: 'PersonnelArchive',
        component: () => import('@/views/personnel/PersonnelArchive.vue')
      },
      // 组织架构相关页面
      {
        path: 'organization/group-company',
        name: 'GroupCompany',
        component: () => import('@/views/organization/GroupCompany.vue')
      },
      {
        path: 'organization/functional-center',
        name: 'FunctionalCenter',
        component: () => import('@/views/organization/FunctionalCenter.vue')
      },
      {
        path: 'organization/department',
        name: 'Department',
        component: () => import('@/views/organization/Department.vue')
      },
      {
        path: 'organization/project-department',
        name: 'ProjectDepartment',
        component: () => import('@/views/organization/ProjectDepartment.vue')
      },
      {
        path: 'organization/project',
        name: 'Project',
        component: () => import('@/views/organization/Project.vue')
      },
      // 商城管理相关页面
      {
        path: 'mall/ad-settings',
        name: 'AdSettings',
        component: () => import('@/views/mall/AdSettings.vue')
      },
      {
        path: 'mall/mall-type',
        name: 'MallType',
        component: () => import('@/views/mall/MallType.vue')
      },
      {
        path: 'mall/stores',
        name: 'StoreManagement',
        component: () => import('@/views/mall/StoreManagement.vue')
      },
      {
        path: 'mall/products',
        name: 'ProductManagement',
        component: () => import('@/views/mall/ProductManagement.vue')
      },
      {
        path: 'mall/orders',
        name: 'OrderManagement',
        component: () => import('@/views/mall/OrderManagement.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router

