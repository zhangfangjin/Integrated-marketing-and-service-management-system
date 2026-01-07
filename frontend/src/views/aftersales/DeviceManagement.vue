<template>
  <div class="device-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备信息管理</span>
          <div class="header-operations">
            <el-input
              v-model="keyword"
              placeholder="搜索设备编号、名称"
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
              <el-icon><Plus /></el-icon>新增设备
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="contractNumber" label="合同号" width="120" />
        <el-table-column prop="deviceName" label="设备名称" min-width="150" />
        <el-table-column prop="deviceModel" label="型号" width="120" />
        <el-table-column prop="deviceNumber" label="设备编号" width="150" />
        <el-table-column prop="productionDate" label="出厂时间" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="primary" @click="handleViewRecords(row)">维护记录</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 设备编辑弹窗 -->
    <el-dialog v-model="formVisible" :title="isEdit ? '修改设备信息' : '新增设备信息'" width="600px">
      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备名称" prop="deviceName">
              <el-input v-model="form.deviceName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="型号" prop="deviceModel">
              <el-input v-model="form.deviceModel" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备编号" prop="deviceNumber">
              <el-input v-model="form.deviceNumber" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出厂时间">
              <el-date-picker v-model="form.productionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="合同号">
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
        <el-form-item label="运行参数">
          <el-input v-model="form.operatingParameters" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="所用零件">
          <el-input v-model="form.partsInfo" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">保存</el-button>
      </template>
    </el-dialog>

    <!-- 维护记录对话框 -->
    <el-dialog v-model="recordsVisible" :title="'设备维护记录 - ' + currentDevice?.deviceNumber" width="900px">
      <div class="records-header">
        <el-button type="primary" size="small" @click="handleCreateRecord">新增维护记录</el-button>
      </div>
      <el-table :data="recordList" border style="width: 100%; margin-top: 10px">
        <el-table-column prop="maintenanceDate" label="维护时间" width="120" />
        <el-table-column prop="maintenancePersonName" label="维护人" width="100" />
        <el-table-column prop="faultReason" label="故障原因" show-overflow-tooltip />
        <el-table-column prop="solution" label="解决方案" show-overflow-tooltip />
        <el-table-column prop="partsReplacement" label="更换零件" show-overflow-tooltip />
        <el-table-column label="外供信息" width="150">
          <template #default="{ row }">
            <span v-if="row.isExternalPartsIssue">{{ row.externalSupplier }} ({{ row.externalDeviceModel }})</span>
            <span v-else>否</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEditRecord(row)">修改</el-button>
            <el-button link type="danger" @click="handleDeleteRecord(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 维护记录编辑弹窗 -->
    <el-dialog v-model="recordFormVisible" :title="isEditRecord ? '修改维护记录' : '新增维护记录'" width="600px">
      <el-form :model="recordForm" label-width="100px" ref="recordFormRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维护时间">
              <el-date-picker v-model="recordForm.maintenanceDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维护人员">
              <el-select v-model="recordForm.maintenancePersonId" filterable style="width: 100%" @change="handlePersonChange">
                <el-option v-for="u in userOptions" :key="u.id" :label="u.name" :value="u.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="故障原因">
          <el-input v-model="recordForm.faultReason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="解决方案">
          <el-input v-model="recordForm.solution" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="更换零件">
          <el-input v-model="recordForm.partsReplacement" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="外供零件问题">
          <el-switch v-model="recordForm.isExternalPartsIssue" />
        </el-form-item>
        <el-row :gutter="20" v-if="recordForm.isExternalPartsIssue">
          <el-col :span="12">
            <el-form-item label="外供厂家">
              <el-input v-model="recordForm.externalSupplier" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="外供型号">
              <el-input v-model="recordForm.externalDeviceModel" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="recordForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="recordFormVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRecord">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

const keyword = ref('')
const tableData = ref([])
const loading = ref(false)

// 设备表单
const formRef = ref(null)
const formVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: '',
  deviceName: '',
  deviceModel: '',
  deviceNumber: '',
  productionDate: '',
  contractId: '',
  contractNumber: '',
  operatingParameters: '',
  partsInfo: '',
  remark: ''
})

const rules = {
  deviceName: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  deviceNumber: [{ required: true, message: '请输入编号', trigger: 'blur' }]
}

const contractOptions = ref([])
const userOptions = ref([])

// 维护记录
const recordsVisible = ref(false)
const currentDevice = ref(null)
const recordList = ref([])
const recordFormVisible = ref(false)
const isEditRecord = ref(false)
const recordForm = reactive({
  id: '',
  deviceId: '',
  deviceNumber: '',
  maintenanceDate: '',
  maintenancePersonId: '',
  maintenancePersonName: '',
  faultReason: '',
  solution: '',
  partsReplacement: '',
  isExternalPartsIssue: false,
  externalSupplier: '',
  externalDeviceModel: '',
  remark: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await api.get('/devices', { params: { keyword: keyword.value } })
    tableData.value = res
  } finally {
    loading.value = false
  }
}

const handleSearch = () => fetchData()

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
  if (contract) form.contractId = contract.id
}

const saveForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (isEdit.value) await api.put(`/devices/${form.id}`, form)
      else await api.post('/devices', form)
      ElMessage.success('保存成功')
      formVisible.value = false
      fetchData()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该设备吗？', '提示', { type: 'warning' }).then(async () => {
    await api.delete(`/devices/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
  })
}

const handleViewRecords = async (row) => {
  currentDevice.value = row
  const res = await api.get(`/device-maintenance-records/device/${row.id}`)
  recordList.value = res
  recordsVisible.value = true
}

const handleCreateRecord = async () => {
  isEditRecord.value = false
  Object.keys(recordForm).forEach(key => {
    if (typeof recordForm[key] === 'boolean') recordForm[key] = false
    else recordForm[key] = ''
  })
  recordForm.deviceId = currentDevice.value.id
  recordForm.deviceNumber = currentDevice.value.deviceNumber
  recordForm.maintenanceDate = new Date().toISOString().split('T')[0]
  if (userOptions.value.length === 0) userOptions.value = await api.get('/accounts')
  recordFormVisible.value = true
}

const handleEditRecord = async (row) => {
  isEditRecord.value = true
  Object.assign(recordForm, row)
  if (userOptions.value.length === 0) userOptions.value = await api.get('/accounts')
  recordFormVisible.value = true
}

const handlePersonChange = (val) => {
  const user = userOptions.value.find(u => u.id === val)
  if (user) recordForm.maintenancePersonName = user.name
}

const saveRecord = async () => {
  if (isEditRecord.value) await api.put(`/device-maintenance-records/${recordForm.id}`, recordForm)
  else await api.post('/device-maintenance-records', recordForm)
  ElMessage.success('记录保存成功')
  recordFormVisible.value = false
  handleViewRecords(currentDevice.value)
}

const handleDeleteRecord = (row) => {
  ElMessageBox.confirm('确定删除该维护记录吗？', '提示', { type: 'warning' }).then(async () => {
    await api.delete(`/device-maintenance-records/${row.id}`)
    ElMessage.success('删除成功')
    handleViewRecords(currentDevice.value)
  })
}

onMounted(() => fetchData())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.records-header {
  display: flex;
  justify-content: flex-end;
}
</style>





