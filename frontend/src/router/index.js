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
      },
      // 基本信息管理相关页面
      {
        path: 'basicinfo/customers',
        name: 'CustomerManagement',
        component: () => import('@/views/basicinfo/CustomerManagement.vue')
      },
      // 价格本管理
      {
        path: 'pricebook',
        name: 'PriceBookManagement',
        component: () => import('@/views/pricebook/PriceBookManagement.vue')
      },
      // 应收账管理相关页面
      {
        path: 'receivable/plan',
        name: 'ReceivablePlan',
        component: () => import('@/views/receivable/ReceivablePlan.vue')
      },
      {
        path: 'receivable/entry',
        name: 'ReceivableEntry',
        component: () => import('@/views/receivable/ReceivableEntry.vue')
      },
      {
        path: 'receivable/query',
        name: 'ReceivableQuery',
        component: () => import('@/views/receivable/ReceivableQuery.vue')
      },
      // 合同管理相关页面
      {
        path: 'contracts',
        name: 'ContractList',
        component: () => import('@/views/contract/ContractList.vue')
      },
      {
        path: 'contracts/new',
        name: 'ContractCreate',
        component: () => import('@/views/contract/ContractForm.vue')
      },
      {
        path: 'contracts/:id/edit',
        name: 'ContractEdit',
        component: () => import('@/views/contract/ContractForm.vue')
      },
      {
        path: 'contracts/:id/approve',
        name: 'ApproveConfig',
        component: () => import('@/views/contract/ApproveConfig.vue')
      },
      {
        path: 'contracts/:id/status',
        name: 'FlowStatus',
        component: () => import('@/views/contract/FlowStatus.vue')
      },
      {
        path: 'contracts/execution-progress',
        name: 'ExecutionProgress',
        component: () => import('@/views/contract/ExecutionProgress.vue')
      },
      // 售后服务管理相关页面
      {
        path: 'aftersales/orders',
        name: 'AfterSalesList',
        component: () => import('@/views/aftersales/AfterSalesList.vue')
      },
      {
        path: 'aftersales/returned-pumps',
        name: 'ReturnedPumps',
        component: () => import('@/views/aftersales/ReturnedPumps.vue')
      },
      {
        path: 'aftersales/maintenance-progress',
        name: 'MaintenanceProgress',
        component: () => import('@/views/aftersales/MaintenanceProgress.vue')
      },
      {
        path: 'aftersales/devices',
        name: 'DeviceManagement',
        component: () => import('@/views/aftersales/DeviceManagement.vue')
      },
      {
        path: 'aftersales/maintenance-plans',
        name: 'MaintenancePlans',
        component: () => import('@/views/aftersales/MaintenancePlans.vue')
      },
      {
        path: 'aftersales/experience',
        name: 'AfterSalesExperience',
        component: () => import('@/views/aftersales/AfterSalesExperience.vue')
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

