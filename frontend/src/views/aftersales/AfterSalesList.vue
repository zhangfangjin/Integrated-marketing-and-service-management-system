<template>
  <div class="after-sales-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>售后服务列表</span>
          <div class="header-operations">
            <el-input
              v-model="keyword"
              placeholder="搜索单号、合同号、客户"
              style="width: 250px; margin-right: 10px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-button type="primary" @click="handleCreate">
              <el-icon><Plus /></el-icon>新建售后服务
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="serviceOrderNumber" label="服务单号" width="150" />
        <el-table-column prop="contractNumber" label="合同编号" width="120" />
        <el-table-column prop="contractName" label="合同名称" min-width="150" />
        <el-table-column prop="serviceDate" label="售后日期" width="120" />
        <el-table-column prop="serviceStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.serviceStatus)">{{ row.serviceStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="region" label="地区" width="100" />
        <el-table-column prop="serviceType" label="合同类型" width="120" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="primary" @click="handleAssign(row)" v-if="row.serviceStatus === '待分配'">指派</el-button>
            <el-button link type="primary" @click="handleTrack(row)">过程记录</el-button>
            <el-button link type="success" @click="handleComplete(row)" v-if="row.serviceStatus === '处理中'">完成</el-button>
            <el-button link type="warning" @click="handleEvaluate(row)" v-if="row.serviceStatus === '已完成' && !row.evaluation">评价</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 售后申请/修改弹窗 -->
    <el-dialog
      v-model="formVisible"
      :title="isEdit ? '修改售后服务' : '新建售后服务'"
      width="700px"
    >
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="售后类型" prop="serviceType">
              <el-select v-model="form.serviceType" placeholder="请选择" style="width: 100%">
                <el-option label="故障处理" value="TROUBLESHOOTING" />
                <el-option label="安装调试" value="INSTALLATION_DEBUGGING" />
                <el-option label="有偿售后" value="PAID_SERVICE" />
                <el-option label="无偿三包" value="FREE_WARRANTY" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同编号" prop="contractNumber">
              <el-select
                v-model="form.contractNumber"
                filterable
                remote
                placeholder="搜索合同"
                :remote-method="searchContracts"
                @change="handleContractChange"
                style="width: 100%"
              >
                <el-option
                  v-for="item in contractOptions"
                  :key="item.id"
                  :label="item.contractNumber"
                  :value="item.contractNumber"
                >
                  <span>{{ item.contractNumber }} ({{ item.contractName }})</span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同名称">
              <el-input v-model="form.contractName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主单位">
              <el-input v-model="form.customerUnit" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="售后时间" prop="serviceDate">
              <el-date-picker
                v-model="form.serviceDate"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地区" prop="region">
              <el-input v-model="form.region" placeholder="地区" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="付款计划">
          <el-input v-model="form.paymentPlan" type="textarea" :rows="2" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维修方案">
              <el-input v-model="form.maintenancePlanAttachment" placeholder="附件路径" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="换件清单">
              <el-input v-model="form.replacementPartsListAttachment" placeholder="附件路径" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 指派受理人员弹窗 -->
    <el-dialog v-model="assignVisible" title="指派受理人员" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="受理人员">
          <el-select v-model="assignForm.handlerId" placeholder="选择人员" filterable style="width: 100%">
            <el-option
              v-for="item in userOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- 跟踪过程弹窗 -->
    <el-dialog v-model="trackVisible" title="过程记录" width="600px">
      <div class="track-history">
        <el-timeline>
          <el-timeline-item
            v-for="(activity, index) in trackHistory"
            :key="index"
            :timestamp="activity.activityDate"
          >
            <h4>{{ activity.activityType }}</h4>
            <p>{{ activity.description }}</p>
            <p><small>操作人: {{ activity.operatorName }}</small></p>
          </el-timeline-item>
        </el-timeline>
      </div>
      <el-divider>添加记录</el-divider>
      <el-form :model="trackForm" label-width="80px">
        <el-form-item label="类型">
          <el-input v-model="trackForm.activityType" placeholder="如：电话跟踪、现场查勘" />
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker v-model="trackForm.activityDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="trackForm.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="trackVisible = false">关闭</el-button>
        <el-button type="primary" @click="saveActivity">添加</el-button>
      </template>
    </el-dialog>

    <!-- 完成售后弹窗 -->
    <el-dialog v-model="completeVisible" title="售后完成" width="500px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="设备编号">
          <el-select
            v-model="completeForm.deviceNumber"
            placeholder="选择具体设备"
            style="width: 100%"
            @change="handleDeviceChange"
          >
            <el-option
              v-for="item in deviceOptions"
              :key="item.id"
              :label="item.deviceNumber"
              :value="item.deviceNumber"
            >
              <span>{{ item.deviceNumber }} ({{ item.deviceName }})</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设备名称">
          <el-input v-model="completeForm.deviceName" disabled />
        </el-form-item>
        <el-form-item label="售后总结">
          <el-input v-model="completeForm.serviceSummary" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmComplete">保存并提交</el-button>
      </template>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="evaluateVisible" title="售后评价" width="500px">
      <el-form :model="evaluateForm" label-width="100px">
        <el-form-item label="售后评价">
          <el-rate
            v-model="evaluateRate"
            :texts="['差', '中', '良', '优', '极好']"
            show-text
          />
        </el-form-item>
        <el-form-item label="评价等级">
          <el-select v-model="evaluateForm.evaluation" placeholder="选择等级" style="width: 100%">
            <el-option label="优" value="优" />
            <el-option label="良" value="良" />
            <el-option label="中" value="中" />
            <el-option label="差" value="差" />
          </el-select>
        </el-form-item>
        <el-form-item label="回访备注">
          <el-input v-model="evaluateForm.remark" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="evaluateVisible = false">返回</el-button>
        <el-button type="primary" @click="confirmEvaluate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const keyword = ref('')
const tableData = ref([])
const loading = ref(false)

// 表单相关
const formRef = ref(null)
const formVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: '',
  serviceType: '',
  contractId: '',
  contractNumber: '',
  contractName: '',
  customerUnit: '',
  serviceDate: '',
  region: '',
  paymentPlan: '',
  maintenancePlanAttachment: '',
  replacementPartsListAttachment: '',
  remark: ''
})

const rules = {
  serviceType: [{ required: true, message: '请选择售后类型', trigger: 'change' }],
  contractNumber: [{ required: true, message: '请选择合同', trigger: 'change' }],
  serviceDate: [{ required: true, message: '请选择日期', trigger: 'change' }],
  region: [{ required: true, message: '请输入地区', trigger: 'blur' }]
}

// 选项
const contractOptions = ref([])
const userOptions = ref([])
const deviceOptions = ref([])

// 指派
const assignVisible = ref(false)
const assignForm = reactive({
  id: '',
  handlerId: '',
  handlerName: ''
})

// 跟踪
const trackVisible = ref(false)
const trackHistory = ref([])
const currentOrderId = ref('')
const trackForm = reactive({
  afterSalesOrderId: '',
  activityType: '',
  activityDate: '',
  description: '',
  operatorId: authStore.user?.id,
  operatorName: authStore.user?.name
})

// 完成
const completeVisible = ref(false)
const completeForm = reactive({
  id: '',
  deviceNumber: '',
  deviceName: '',
  serviceSummary: '',
  completionDate: ''
})

// 评价
const evaluateVisible = ref(false)
const evaluateRate = ref(0)
const evaluateForm = reactive({
  id: '',
  evaluation: '',
  remark: '',
  evaluatorId: authStore.user?.id,
  evaluatorName: authStore.user?.name
})

// 方法
const fetchData = async () => {
  loading.value = true
  try {
    const res = await api.get('/after-sales', { params: { keyword: keyword.value } })
    tableData.value = res
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchData()
}

const handleCreate = () => {
  isEdit.value = false
  Object.keys(form).forEach(key => form[key] = '')
  formVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  formVisible.value = true
}

const searchContracts = async (query) => {
  if (query) {
    const res = await api.get('/contracts', { params: { keyword: query } })
    contractOptions.value = res
  }
}

const handleContractChange = (val) => {
  const contract = contractOptions.value.find(c => c.contractNumber === val)
  if (contract) {
    form.contractId = contract.id
    form.contractName = contract.contractName
    form.customerUnit = contract.customerName || contract.orderingUnit
  }
}

const saveForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (isEdit.value) {
        await api.put(`/after-sales/${form.id}`, form)
        ElMessage.success('更新成功')
      } else {
        await api.post('/after-sales', form)
        ElMessage.success('创建成功')
      }
      formVisible.value = false
      fetchData()
    }
  })
}

const handleAssign = async (row) => {
  assignForm.id = row.id
  assignForm.handlerId = ''
  assignForm.handlerName = ''
  // 加载用户列表（实际中可能需要按角色过滤）
  const res = await api.get('/accounts')
  userOptions.value = res
  assignVisible.value = true
}

const confirmAssign = async () => {
  const user = userOptions.value.find(u => u.id === assignForm.handlerId)
  if (user) assignForm.handlerName = user.name
  await api.post(`/after-sales/${assignForm.id}/assign-handler`, assignForm)
  ElMessage.success('指派成功')
  assignVisible.value = false
  fetchData()
}

const handleTrack = async (row) => {
  currentOrderId.value = row.id
  trackForm.afterSalesOrderId = row.id
  trackForm.activityType = ''
  trackForm.activityDate = new Date().toISOString().split('T')[0]
  trackForm.description = ''
  const res = await api.get(`/after-sales/${row.id}/activities`)
  trackHistory.value = res
  trackVisible.value = true
}

const saveActivity = async () => {
  await api.post('/after-sales/activities', trackForm)
  ElMessage.success('记录添加成功')
  const res = await api.get(`/after-sales/${currentOrderId.value}/activities`)
  trackHistory.value = res
  trackForm.activityType = ''
  trackForm.description = ''
}

const handleComplete = async (row) => {
  completeForm.id = row.id
  completeForm.deviceNumber = ''
  completeForm.deviceName = ''
  completeForm.serviceSummary = ''
  completeForm.completionDate = new Date().toISOString().split('T')[0]
  // 加载合同下的设备
  if (row.contractId) {
    const res = await api.get(`/devices/contract/${row.contractId}`)
    deviceOptions.value = res
  }
  completeVisible.value = true
}

const handleDeviceChange = (val) => {
  const device = deviceOptions.value.find(d => d.deviceNumber === val)
  if (device) completeForm.deviceName = device.deviceName
}

const confirmComplete = async () => {
  await api.post(`/after-sales/${completeForm.id}/complete`, completeForm)
  ElMessage.success('售后已完结')
  completeVisible.value = false
  fetchData()
}

const handleEvaluate = (row) => {
  evaluateForm.id = row.id
  evaluateForm.evaluation = ''
  evaluateForm.remark = ''
  evaluateRate.value = 0
  evaluateVisible.value = true
}

const confirmEvaluate = async () => {
  await api.post(`/after-sales/${evaluateForm.id}/evaluate`, evaluateForm)
  ElMessage.success('评价已提交')
  evaluateVisible.value = false
  fetchData()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' }).then(async () => {
    await api.delete(`/after-sales/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  })
}

const getStatusType = (status) => {
  const map = { '待分配': 'info', '处理中': 'warning', '已完成': 'success', '已评价': 'primary' }
  return map[status] || 'info'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-operations {
  display: flex;
  align-items: center;
}
.track-history {
  max-height: 300px;
  overflow-y: auto;
  padding: 10px;
}
</style>





