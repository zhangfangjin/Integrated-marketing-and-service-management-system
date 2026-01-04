<template>
  <div class="receivable-plan">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>应收账计划</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索合同编号、合同名称、客户名称"
              style="width: 300px; margin-right: 10px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button @click="handleSearch">查询</el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="receivableList"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="contractNumber" label="合同号" width="150" />
        <el-table-column prop="contractName" label="合同名称" min-width="150" />
        <el-table-column prop="customerName" label="客户名称" min-width="150" />
        <el-table-column prop="paymentStage" label="付款阶段" width="100" />
        <el-table-column prop="paymentStageName" label="付款阶段名称" width="150" />
        <el-table-column prop="amountPayable" label="应付金额" width="120">
          <template #default="{ row }">
            {{ formatPrice(row.amountPayable) }}
          </template>
        </el-table-column>
        <el-table-column prop="amountPaid" label="已付金额" width="120">
          <template #default="{ row }">
            {{ formatPrice(row.amountPaid) }}
          </template>
        </el-table-column>
        <el-table-column prop="unpaidAmount" label="未付金额" width="120">
          <template #default="{ row }">
            <span :style="{ color: row.unpaidAmount > 0 ? 'red' : 'green' }">
              {{ formatPrice(row.unpaidAmount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="应付时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.dueDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentDate" label="付款日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.paymentDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="responsiblePerson" label="责任人" width="100" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="编辑应收账计划"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="合同号">
          <el-input v-model="form.contractNumber" disabled />
        </el-form-item>
        <el-form-item label="付款阶段" prop="paymentStage">
          <el-input v-model="form.paymentStage" placeholder="请输入付款阶段" />
        </el-form-item>
        <el-form-item label="付款阶段名称" prop="paymentStageName">
          <el-input v-model="form.paymentStageName" placeholder="请输入付款阶段名称" />
        </el-form-item>
        <el-form-item label="应付金额" prop="amountPayable">
          <el-input-number
            v-model="form.amountPayable"
            :precision="2"
            :step="0.01"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="已付金额" prop="amountPaid">
          <el-input-number
            v-model="form.amountPaid"
            :precision="2"
            :step="0.01"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="应付时间" prop="dueDate">
          <el-date-picker
            v-model="form.dueDate"
            type="date"
            placeholder="选择应付时间"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="付款日期" prop="paymentDate">
          <el-date-picker
            v-model="form.paymentDate"
            type="date"
            placeholder="选择付款日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="责任人" prop="responsiblePerson">
          <el-input v-model="form.responsiblePerson" placeholder="请输入责任人" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import api from '@/utils/api'

const receivableList = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const searchKeyword = ref('')
const formRef = ref(null)

const form = reactive({
  id: null,
  contractNumber: '',
  paymentStage: '',
  paymentStageName: '',
  amountPayable: null,
  amountPaid: null,
  dueDate: null,
  paymentDate: null,
  responsiblePerson: '',
  remark: ''
})

const rules = {
  amountPayable: [{ required: true, message: '请输入应付金额', trigger: 'blur' }]
}

const loadReceivables = async () => {
  loading.value = true
  try {
    const keyword = searchKeyword.value?.trim()
    const data = keyword
      ? await api.get('/receivable/plan', { params: { keyword } })
      : await api.get('/receivable/plan')
    receivableList.value = data
  } catch (error) {
    ElMessage.error('加载应收账计划失败')
    receivableList.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadReceivables()
}

const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    contractNumber: row.contractNumber,
    paymentStage: row.paymentStage || '',
    paymentStageName: row.paymentStageName || '',
    amountPayable: row.amountPayable,
    amountPaid: row.amountPaid || 0,
    dueDate: row.dueDate,
    paymentDate: row.paymentDate,
    responsiblePerson: row.responsiblePerson || '',
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        const requestData = {
          paymentStage: form.paymentStage,
          paymentStageName: form.paymentStageName,
          amountPayable: form.amountPayable,
          amountPaid: form.amountPaid,
          dueDate: form.dueDate ? new Date(form.dueDate + 'T00:00:00').toISOString().split('T')[0] : null,
          paymentDate: form.paymentDate ? new Date(form.paymentDate + 'T00:00:00').toISOString().split('T')[0] : null,
          responsiblePerson: form.responsiblePerson,
          remark: form.remark
        }
        await api.put(`/receivable/${form.id}`, requestData)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        await loadReceivables()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        saving.value = false
      }
    }
  })
}

const formatPrice = (price) => {
  if (price == null) return '-'
  return `¥${parseFloat(price).toFixed(2)}`
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

onMounted(() => {
  loadReceivables()
})
</script>

<style scoped>
.receivable-plan {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}
</style>

