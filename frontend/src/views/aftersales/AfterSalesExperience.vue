<template>
  <div class="after-sales-experience">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>售后经验管理</span>
          <div class="header-operations">
            <el-input
              v-model="keyword"
              placeholder="搜索客户、型号、关键词"
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
              <el-icon><Plus /></el-icon>新增经验
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="deviceType" label="设备类型" width="120" />
        <el-table-column prop="caseRegistrationDate" label="案例登记时间" width="150" />
        <el-table-column prop="caseType" label="案例类型" width="120" />
        <el-table-column prop="experienceSummary" label="经验总结" show-overflow-tooltip />
        <el-table-column prop="caseRemark" label="案例备注" show-overflow-tooltip />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="formVisible" :title="isEdit ? '修改售后经验' : '售后经验登记'" width="600px">
      <el-form :model="form" label-width="100px" ref="formRef" :rules="rules">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <el-input v-model="form.customerName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备类型" prop="deviceType">
              <el-input v-model="form.deviceType" placeholder="如：ZD350" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="案例时间">
              <el-date-picker v-model="form.caseRegistrationDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="案例类型">
              <el-input v-model="form.caseType" placeholder="如：机械故障" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="经验总结" prop="experienceSummary">
          <el-input v-model="form.experienceSummary" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.caseRemark" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="附件">
          <el-input v-model="form.attachment" placeholder="附件路径" />
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
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const keyword = ref('')
const tableData = ref([])
const loading = ref(false)

const formRef = ref(null)
const formVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: '',
  customerName: '',
  deviceType: '',
  caseRegistrationDate: '',
  caseType: '',
  experienceSummary: '',
  caseRemark: '',
  attachment: '',
  registrantId: authStore.user?.id,
  registrantName: authStore.user?.name
})

const rules = {
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  experienceSummary: [{ required: true, message: '请输入经验总结', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await api.get('/after-sales-experiences', { params: { keyword: keyword.value } })
    tableData.value = res
  } finally {
    loading.value = false
  }
}

const handleSearch = () => fetchData()

const handleCreate = () => {
  isEdit.value = false
  Object.keys(form).forEach(key => form[key] = '')
  form.caseRegistrationDate = new Date().toISOString().split('T')[0]
  form.registrantId = authStore.user?.id
  form.registrantName = authStore.user?.name
  formVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  formVisible.value = true
}

const saveForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      if (isEdit.value) await api.put(`/after-sales-experiences/${form.id}`, form)
      else await api.post('/after-sales-experiences', form)
      ElMessage.success('经验保存成功')
      formVisible.value = false
      fetchData()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除这条经验记录吗？', '提示', { type: 'warning' }).then(async () => {
    await api.delete(`/after-sales-experiences/${row.id}`)
    ElMessage.success('删除成功')
    fetchData()
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
</style>





