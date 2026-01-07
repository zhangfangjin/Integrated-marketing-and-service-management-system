<template>
  <div class="maintenance-progress">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>维修进度查询</span>
          <div class="header-operations">
            <el-input
              v-model="keyword"
              placeholder="搜索合同号、名称"
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
              <el-icon><Plus /></el-icon>录入进度
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="contractNumber" label="合同编号" width="120" />
        <el-table-column prop="contractName" label="合同名称" min-width="150" />
        <el-table-column label="换件清单" width="100" align="center">
          <template #default="{ row }">
            <el-link v-if="row.replacementPartsListAttachment" type="primary" :href="row.replacementPartsListAttachment" target="_blank">
              <el-icon><Document /></el-icon>
            </el-link>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="计划" width="100" align="center">
          <template #default="{ row }">
            <el-link v-if="row.planAttachment" type="success" :href="row.planAttachment" target="_blank">
              <el-icon><Calendar /></el-icon>
            </el-link>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="deductionTime" label="扣套时间" width="120" />
        <el-table-column prop="estimatedDeliveryTime" label="发货时间" width="120" />
        <el-table-column prop="contractType" label="合同类型" width="120" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="formVisible" :title="isEdit ? '修改维修进度' : '录入维修进度'" width="600px">
      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
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
        <el-form-item label="合同名称">
          <el-input v-model="form.contractName" disabled />
        </el-form-item>
        <el-form-item label="合同类型">
          <el-select v-model="form.contractType" placeholder="请选择" style="width: 100%">
            <el-option label="有偿维修" value="有偿维修" />
            <el-option label="无偿三包" value="无偿三包" />
          </el-select>
        </el-form-item>
        <el-form-item label="换件清单附件">
          <el-input v-model="form.replacementPartsListAttachment" placeholder="附件路径" />
        </el-form-item>
        <el-form-item label="拆解报告附件">
          <el-input v-model="form.disassemblyReportAttachment" placeholder="附件路径" />
        </el-form-item>
        <el-form-item label="运行部计划附件">
          <el-input v-model="form.planAttachment" placeholder="附件路径" />
        </el-form-item>
        <el-form-item label="装配车间扣套时间">
          <el-date-picker v-model="form.deductionTime" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预计发货时间">
          <el-date-picker v-model="form.estimatedDeliveryTime" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
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
import { Search, Plus, Document, Calendar } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

const keyword = ref('')
const tableData = ref([])
const loading = ref(false)

const formRef = ref(null)
const formVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: '',
  contractId: '',
  contractNumber: '',
  contractName: '',
  contractType: '',
  replacementPartsListAttachment: '',
  disassemblyReportAttachment: '',
  planAttachment: '',
  deductionTime: '',
  estimatedDeliveryTime: '',
  remark: ''
})

const rules = {
  contractNumber: [{ required: true, message: '请选择合同', trigger: 'change' }]
}

const contractOptions = ref([])

const fetchData = async () => {
  loading.value = true
  try {
    const res = await api.get('/maintenance-progress', { params: { keyword: keyword.value } })
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
  if (contract) {
    form.contractId = contract.id
    form.contractName = contract.contractName
  }
}

const saveForm = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      await api.post('/maintenance-progress', form)
      ElMessage.success('保存成功')
      formVisible.value = false
      fetchData()
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条进度信息吗？', '提示', { type: 'warning' }).then(async () => {
    await api.delete(`/maintenance-progress/${row.id}`)
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





