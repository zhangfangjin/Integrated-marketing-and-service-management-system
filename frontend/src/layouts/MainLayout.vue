<template>
  <el-container class="main-layout">
    <el-aside width="250px" class="sidebar">
      <div class="logo">权限管理平台</div>
      <el-tree
        ref="treeRef"
        :data="moduleTree"
        :props="{ children: 'children', label: 'zhName' }"
        node-key="id"
        default-expand-all
        highlight-current
        :expand-on-click-node="false"
        @node-click="handleNodeClick"
        class="module-tree"
      >
        <template #default="{ node, data }">
          <span>{{ node.label }}</span>
        </template>
      </el-tree>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <span class="title">{{ currentModuleName || '首页' }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><UserFilled /></el-icon>
              {{ authStore.user?.name || '用户' }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const authStore = useAuthStore()
const treeRef = ref(null)
const moduleTree = ref([])
const currentModuleName = ref('')

const loadModuleTree = () => {
  // 使用登录时获取的模块数据，这些数据已经根据用户角色权限过滤
  moduleTree.value = authStore.modules || []
}

const handleNodeClick = (data) => {
  currentModuleName.value = data.zhName
  if (data.path) {
    router.push(data.path)
  }
}

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }
}

onMounted(() => {
  loadModuleTree()
  // 如果没有模块数据但有认证信息，说明数据丢失或不完整，清除认证信息并跳转到登录页
  if (moduleTree.value.length === 0 && authStore.isAuthenticated) {
    ElMessage.warning('模块数据丢失，请重新登录')
    authStore.logout()
    router.push('/login')
  }
})
</script>

<style scoped>
.main-layout {
  height: 100vh;
}

.sidebar {
  background: #304156;
  color: white;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.module-tree {
  padding: 10px;
  background: transparent;
  color: white;
}

.module-tree :deep(.el-tree-node__content) {
  color: white;
  height: 36px;
}

.module-tree :deep(.el-tree-node__content:hover) {
  background: rgba(255, 255, 255, 0.1);
}

.module-tree :deep(.el-tree-node.is-current > .el-tree-node__content) {
  background: #409eff;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.header-left .title {
  font-size: 18px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.main-content {
  background: #f0f2f5;
  padding: 20px;
}
</style>

