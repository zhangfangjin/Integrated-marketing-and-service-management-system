<template>
  <div class="maintenance-plans">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="设备大类维护计划" name="device_type">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>按设备型号制定计划</span>
              <el-button type="primary" size="small" @click="handleCreatePlan('DEVICE_TYPE')">新增大类计划</el-button>
            </div>
          </template>
          <el-table :data="typePlans" border style="width: 100%">
            <el-table-column prop="deviceType" label="设备大类" width="150" />
            <el-table-column prop="maintenanceCycle" label="维护周期" width="120" />
            <el-table-column prop="maintenanceItems" label="维护项目" show-overflow-tooltip />
            <el-table-column prop="precautions" label="注意事项" show-overflow-tooltip />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleEditPlan(row)">修改</el-button>
                <el-button link type="danger" @click="handleDeletePlan(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="具体设备维护计划" name="specific_device">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>按具体设备制定计划</span>
              <el-button type="primary" size="small" @click="handleCreatePlan('SPECIFIC_DEVICE')">新增具体计划</el-button>
            </div>
          </template>
          <el-table :data="specificPlans" border style="width: 100%">
            <el-table-column prop="deviceNumber" label="设备编号" width="150" />
            <el-table-column prop="deviceName" label="设备名称" width="150" />
            <el-table-column prop="maintenanceCycle" label="维护周期" width="120" />
            <el-table-column prop="maintenanceItems" label="维护项目" show-overflow-tooltip />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleEditPlan(row)">修改</el-button>
                <el-button link type="danger" @click="handleDeletePlan(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 计划编辑弹窗 -->
    <el-dialog v-model="formVisible" :title="isEdit ? '修改维护计划' : '新增维护计划'" width="800px">
      <el-form :model="form" label-width="100px" ref="formRef" :rules="rules">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划级别">
              <el-tag>{{ form.planType === 'DEVICE_TYPE' ? '设备大类' : '具体设备' }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.planType === 'DEVICE_TYPE'" label="设备大类" prop="deviceType">
              <el-input v-model="form.deviceType" placeholder="如：ZD, SD" />
            </el-form-item>
            <el-form-item v-else label="选择设备" prop="deviceNumber">
              <el-select
                v-model="form.deviceNumber"
                filterable
                remote
                placeholder="搜索设备编号"
                :remote-method="searchDevices"
                @change="handleDeviceChange"
                style="width: 100%"
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
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维护周期" prop="maintenanceCycle">
              <el-input v-model="form.maintenanceCycle" placeholder="如：6个月" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否启用">
              <el-switch v-model="form.enabled" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="维护项目">
          <el-input v-model="form.maintenanceItems" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="注意事项">
          <el-input v-model="form.precautions" type="textarea" :rows="2" />
        </el-form-item>

        <el-divider>基础换件清单</el-divider>
        <div class="part-items">
          <div v-for="(part, index) in form.replacementParts" :key="index" class="part-item-row">
            <el-input v-model="part.partDrawingNumber" placeholder="零件图号" style="width: 150px" />
            <el-input v-model="part.partName" placeholder="名称" style="width: 150px" />
            <el-input v-model="part.material" placeholder="材料" style="width: 120px" />
            <el-input-number v-model="part.quantity" :min="1" placeholder="数量" style="width: 100px" />
            <el-button type="danger" circle @click="removePart(index)"><el-icon><Delete /></el-icon></el-button>
          </div>
          <el-button type="dashed" style="width: 100%; margin-top: 10px" @click="addPart">
            <el-icon><Plus /></el-icon>添加零件
          </el-button>
        </div>

        <el-form-item label="备注" style="margin-top: 20px">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="saveForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

const activeTab = ref('device_type')
const typePlans = ref([])
const specificPlans = ref([])
const loading = ref(false)

const formRef = ref(null)
const formVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: '',
  planType: '',
  deviceType: '',
  deviceId: '',
  deviceNumber: '',
  deviceName: '',
  maintenanceCycle: '',
  maintenanceItems: '',
  precautions: '',
  remark: '',
  enabled: true,
  replacementParts: []
})

const rules = {
  deviceType: [{ required: true, message: '请输入大类', trigger: 'blur' }],
  deviceNumber: [{ required: true, message: '请选择设备', trigger: 'change' }],
  maintenanceCycle: [{ required: true, message: '请输入周期', trigger: 'blur' }]
}

const deviceOptions = ref([])

const fetchPlans = async () => {
  loading.value = true
  try {
    const resType = await api.get('/maintenance-plans', { params: { planType: 'DEVICE_TYPE' } })
    typePlans.value = resType
    const resSpecific = await api.get('/maintenance-plans', { params: { planType: 'SPECIFIC_DEVICE' } })
    specificPlans.value = resSpecific
  } finally {
    loading.value = false
  }
}

const handleCreatePlan = (type) => {
  isEdit.value = false
  Object.keys(form).forEach(key => {
    if (key === 'enabled') form[key] = true
    else if (key === 'replacementParts') form[key] = []
    else form[key] = ''
  })
  form.planType = type
  formVisible.value = true
}

const handleEditPlan = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  // 这里可能需要单独加载零件列表，如果row里没有带
  formVisible.value = true
}

const searchDevices = async (query) => {
  if (query) {
    const res = await api.get('/devices', { params: { keyword: query } })
    deviceOptions.value = res
  }
}

const handleDeviceChange = (val) => {
  const device = deviceOptions.value.find(d => d.deviceNumber === val)
  if (device) {
    form.deviceId = device.id
    form.deviceName = device.deviceName
  }
}

const addPart = () => {
  form.replacementParts.push({ partDrawingNumber: '', partName: '', material: '', quantity: 1 })
}

const removePart = (index) => {
  form.replacementParts.splice(index, 1)
}

const saveForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (isEdit.value) await api.put(`/maintenance-plans/${form.id}`, form)
      else await api.post('/maintenance-plans', form)
      ElMessage.success('计划保存成功')
      formVisible.value = false
      fetchPlans()
    }
  })
}

const handleDeletePlan = (row) => {
  ElMessageBox.confirm('确定删除该维护计划吗？', '提示', { type: 'warning' }).then(async () => {
    await api.delete(`/maintenance-plans/${row.id}`)
    ElMessage.success('删除成功')
    fetchPlans()
  })
}

onMounted(() => fetchPlans())
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.part-item-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  align-items: center;
}
.part-items {
  border: 1px solid #ebeef5;
  padding: 15px;
  border-radius: 4px;
}
</style>





