<template>
  <div class="home">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>欢迎使用权限管理平台</span>
        </div>
      </template>
      <div class="welcome-content">
        <h2>系统概览</h2>
        <el-row :gutter="20" style="margin-top: 20px">
          <el-col :span="6">
            <el-statistic title="角色数量" :value="stats.roleCount" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="模块数量" :value="stats.moduleCount" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="待审核注册" :value="stats.pendingCount" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="账号总数" :value="stats.accountCount" />
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '@/utils/api'

const stats = ref({
  roleCount: 0,
  moduleCount: 0,
  pendingCount: 0,
  accountCount: 0
})

const loadStats = async () => {
  try {
    const [roles, modules, pending, accounts] = await Promise.all([
      api.get('/roles'),
      api.get('/modules'),
      api.get('/register/pending'),
      api.get('/accounts')
    ])
    stats.value = {
      roleCount: roles.length,
      moduleCount: modules.length,
      pendingCount: pending.length,
      accountCount: accounts.length
    }
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.home {
  height: 100%;
}

.welcome-content {
  padding: 20px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}
</style>

