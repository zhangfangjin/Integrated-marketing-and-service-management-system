<template>
  <div class="approve-config">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>审批节点配置页面</span>
        </div>
      </template>

      <div class="config-content">
        <!-- 片区负责人 -->
        <div class="config-section">
          <div class="section-title">片区负责人</div>
          <div v-for="(item, index) in config.areaManagers" :key="'area-' + index" class="approver-row">
            <span class="label">审核人</span>
            <el-select v-model="item.approverId" placeholder="请选择审核人" @change="(val) => handleApproverChange(val, item)" filterable>
              <el-option v-for="user in users" :key="user.id" :label="user.name" :value="user.id" />
            </el-select>
            <el-button type="primary" link @click="addApprover('areaManagers')">添加</el-button>
            <el-button type="danger" link @click="removeApprover('areaManagers', index)" v-if="config.areaManagers.length > 1">删除</el-button>
          </div>
        </div>

        <!-- 部门负责人 -->
        <div class="config-section">
          <div class="section-title">部门负责人</div>
          <div v-for="(item, index) in config.departmentHeads" :key="'dept-' + index" class="approver-row">
            <span class="label">审核人</span>
            <el-select v-model="item.approverId" placeholder="请选择审核人" @change="(val) => handleApproverChange(val, item)" filterable>
              <el-option v-for="user in users" :key="user.id" :label="user.name" :value="user.id" />
            </el-select>
            <el-button type="primary" link @click="addApprover('departmentHeads')">添加</el-button>
            <el-button type="danger" link @click="removeApprover('departmentHeads', index)" v-if="config.departmentHeads.length > 1">删除</el-button>
          </div>
        </div>

        <!-- 公司领导 -->
        <div class="config-section">
          <div class="section-title">公司领导</div>
          <div v-for="(item, index) in config.companyLeaders" :key="'leader-' + index" class="approver-row">
            <span class="label">审核人</span>
            <el-select v-model="item.approverId" placeholder="请选择审核人" @change="(val) => handleApproverChange(val, item)" filterable>
              <el-option v-for="user in users" :key="user.id" :label="user.name" :value="user.id" />
            </el-select>
            <el-button type="primary" link @click="addApprover('companyLeaders')">添加</el-button>
            <el-button type="danger" link @click="removeApprover('companyLeaders', index)" v-if="config.companyLeaders.length > 1">删除</el-button>
          </div>
        </div>

        <!-- 财务总监 -->
        <div class="config-section">
          <div class="section-title">财务总监</div>
          <div v-for="(item, index) in config.financialDirectors" :key="'finance-' + index" class="approver-row">
            <span class="label">审核人</span>
            <el-select v-model="item.approverId" placeholder="请选择审核人" @change="(val) => handleApproverChange(val, item)" filterable>
              <el-option v-for="user in users" :key="user.id" :label="user.name" :value="user.id" />
            </el-select>
            <el-button type="primary" link @click="addApprover('financialDirectors')">添加</el-button>
            <el-button type="danger" link @click="removeApprover('financialDirectors', index)" v-if="config.financialDirectors.length > 1">删除</el-button>
          </div>
        </div>

        <div class="footer-buttons">
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 审批节点配置页面
 */
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { submitContractApproval } from '@/api/contract'
import api from '@/utils/api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const contractId = route.params.id

const users = ref([])
const submitting = ref(false)

const config = reactive({
  contractId: contractId,
  areaManagers: [{ approverId: '', approverName: '' }],
  departmentHeads: [{ approverId: '', approverName: '' }],
  companyLeaders: [{ approverId: '', approverName: '' }],
  financialDirectors: [{ approverId: '', approverName: '' }]
})

/**
 * 加载用户列表
 */
const loadUsers = async () => {
  try {
    const res = await api.get('/accounts')
    users.value = res.filter(acc => acc.status !== 'PENDING')
  } catch (error) {
    console.error('加载用户失败:', error)
  }
}

/**
 * 审批人选中变更
 */
const handleApproverChange = (val, item) => {
  const user = users.value.find(u => u.id === val)
  if (user) {
    item.approverName = user.name
  }
}

/**
 * 添加审核人行
 */
const addApprover = (type) => {
  config[type].push({ approverId: '', approverName: '' })
}

/**
 * 删除审核人行
 */
const removeApprover = (type, index) => {
  config[type].splice(index, 1)
}

/**
 * 提交配置并启动审批
 */
const handleSubmit = async () => {
  // 检查是否每个节点都至少有一个人
  const nodes = ['areaManagers', 'departmentHeads', 'companyLeaders', 'financialDirectors']
  for (const node of nodes) {
    if (config[node].some(a => !a.approverId)) {
      ElMessage.warning('请确保所有添加的节点都选择了审核人')
      return
    }
  }

  submitting.value = true
  try {
    await submitContractApproval(contractId, config)
    ElMessage.success('配置成功，已进入审批流程')
    router.push('/contracts')
  } catch (error) {
    console.error('提交配置失败:', error)
  } finally {
    submitting.value = false
  }
}

/**
 * 返回
 */
const handleBack = () => {
  router.back()
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.config-content {
  max-width: 800px;
  margin: 0 auto;
}

.config-section {
  border: 1px solid #ebeef5;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 4px;
}

.section-title {
  font-weight: bold;
  margin-bottom: 20px;
  color: #303133;
}

.approver-row {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.approver-row .label {
  width: 60px;
  text-align: right;
}

.footer-buttons {
  text-align: center;
  margin-top: 30px;
}
</style>










