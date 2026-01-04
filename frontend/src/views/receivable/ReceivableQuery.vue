<template>
  <div class="receivable-query">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>应收账查询</span>
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
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleUnpaid">查看未付清</el-button>
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
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import api from '@/utils/api'

const receivableList = ref([])
const loading = ref(false)
const searchKeyword = ref('')

const loadReceivables = async (unpaid = false) => {
  loading.value = true
  try {
    let data
    if (unpaid) {
      data = await api.get('/receivable/unpaid')
    } else {
      const keyword = searchKeyword.value?.trim()
      data = keyword
        ? await api.get('/receivable', { params: { keyword } })
        : await api.get('/receivable')
    }
    receivableList.value = data
  } catch (error) {
    ElMessage.error('加载应收账列表失败')
    receivableList.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadReceivables(false)
}

const handleUnpaid = () => {
  loadReceivables(true)
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
.receivable-query {
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

