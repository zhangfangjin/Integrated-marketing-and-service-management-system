<template>
  <div class="account-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>账号管理</span>
          <el-tabs v-model="activeTab" @tab-change="handleTabChange">
            <el-tab-pane label="待审核" name="pending" />
            <el-tab-pane label="已审核" name="approved" />
          </el-tabs>
        </div>
      </template>
      <el-table :data="accounts" border style="width: 100%">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="120">
          <template #default="{ row }">
            {{ row.role?.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'PENDING'">
              <el-button link type="success" @click="handleApprove(row)">审核通过</el-button>
              <el-button link type="danger" @click="handleReject(row)">审核拒绝</el-button>
            </template>
            <template v-else>
              <el-button link type="primary" @click="handleResetPassword(row)">重置密码</el-button>
              <el-button link type="primary" @click="handleAssignRole(row)">分配角色</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog v-model="reviewDialogVisible" title="审核注册" width="500px">
      <el-form :model="reviewForm" :rules="reviewRules" ref="reviewFormRef" label-width="100px">
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="reviewForm.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roles"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmApprove" :loading="loading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog v-model="roleDialogVisible" title="分配角色" width="500px">
      <el-form :model="roleForm" :rules="roleRules" ref="roleFormRef" label-width="100px">
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="roleForm.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="role in roles"
              :key="role.id"
              :label="role.name"
              :value="role.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAssignRole" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

const accounts = ref([])
const roles = ref([])
const activeTab = ref('pending')
const loading = ref(false)
const reviewDialogVisible = ref(false)
const roleDialogVisible = ref(false)
const reviewFormRef = ref(null)
const roleFormRef = ref(null)
const currentAccountId = ref(null)

const reviewForm = reactive({
  roleId: null
})

const roleForm = reactive({
  roleId: null
})

const reviewRules = {
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const roleRules = {
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const loadAccounts = async () => {
  try {
    if (activeTab.value === 'pending') {
      const data = await api.get('/register/pending')
      accounts.value = data
    } else {
      const data = await api.get('/accounts')
      accounts.value = data.filter(acc => acc.status !== 'PENDING')
    }
  } catch (error) {
    ElMessage.error('加载账号列表失败')
  }
}

const loadRoles = async () => {
  try {
    const data = await api.get('/roles')
    roles.value = data
  } catch (error) {
    ElMessage.error('加载角色列表失败')
  }
}

const handleTabChange = () => {
  loadAccounts()
}

const getStatusType = (status) => {
  const map = {
    PENDING: 'warning',
    APPROVED: 'success',
    REJECTED: 'danger',
    ENABLED: 'success',
    DISABLED: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    PENDING: '待审核',
    APPROVED: '已审核',
    REJECTED: '已拒绝',
    ENABLED: '已启用',
    DISABLED: '已禁用'
  }
  return map[status] || status
}

const handleApprove = (row) => {
  currentAccountId.value = row.id
  reviewForm.roleId = null
  reviewDialogVisible.value = true
}

const handleConfirmApprove = async () => {
  await reviewFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await api.put(`/register/${currentAccountId.value}/approve`, {
          roleId: reviewForm.roleId
        })
        ElMessage.success('审核通过')
        reviewDialogVisible.value = false
        await loadAccounts()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReject = async (row) => {
  try {
    await ElMessageBox.confirm('确定要拒绝该注册申请吗？', '提示', {
      type: 'warning'
    })
    await api.put(`/register/${row.id}/reject`)
    ElMessage.success('已拒绝')
    await loadAccounts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleResetPassword = async (row) => {
  try {
    await ElMessageBox.confirm('确定要重置该用户的密码为 123456 吗？', '提示', {
      type: 'warning'
    })
    await api.put(`/accounts/${row.id}/reset-password`, {
      newPassword: '123456'
    })
    ElMessage.success('密码已重置为 123456')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleAssignRole = (row) => {
  currentAccountId.value = row.id
  roleForm.roleId = row.role?.id || null
  roleDialogVisible.value = true
}

const handleConfirmAssignRole = async () => {
  await roleFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await api.put(`/accounts/${currentAccountId.value}/role`, {
          roleId: roleForm.roleId
        })
        ElMessage.success('角色分配成功')
        roleDialogVisible.value = false
        await loadAccounts()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadAccounts()
  loadRoles()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

