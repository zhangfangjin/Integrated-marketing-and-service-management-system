<template>
  <div class="customer-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>客户管理</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索客户名称、联系人、联系电话"
              style="width: 300px; margin-right: 10px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增
            </el-button>
            <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
            <el-button type="success" @click="handleExport">
              <el-icon><Download /></el-icon>
              导出
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="customerList"
        border
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="id" label="客户编号" width="120" />
        <el-table-column prop="customerName" label="单位" min-width="150" />
        <el-table-column label="所属区域" width="120">
          <template #default="{ row }">
            {{ row.customerType?.title || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="行业" width="120">
          <template #default="{ row }">
            {{ row.industry?.title || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地区" width="150" />
        <el-table-column label="客户属性" width="120">
          <template #default="{ row }">
            {{ row.customerType?.title || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="信用评级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.creditCode" type="success">{{ row.creditCode }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
            <el-button link type="info" @click="handleView(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 客户编辑对话框 -->
    <CustomerForm
      v-model="formVisible"
      :customer-id="currentCustomerId"
      @success="handleFormSuccess"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete, Download } from '@element-plus/icons-vue'
import api from '@/utils/api'
import CustomerForm from './CustomerForm.vue'

const customerList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const formVisible = ref(false)
const currentCustomerId = ref(null)
const selectedRows = ref([])

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadCustomers = async () => {
  loading.value = true
  try {
    const params = {
      keyword: searchKeyword.value || undefined
    }
    const data = await api.get('/basicinfo/customers', { params })
    customerList.value = data
    pagination.total = data.length
  } catch (error) {
    ElMessage.error('加载客户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  loadCustomers()
}

const handleAdd = () => {
  currentCustomerId.value = null
  formVisible.value = true
}

const handleEdit = (row) => {
  currentCustomerId.value = row.id
  formVisible.value = true
}

const handleView = (row) => {
  currentCustomerId.value = row.id
  formVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该客户吗？', '提示', {
      type: 'warning'
    })
    await api.delete(`/basicinfo/customers/${row.id}`)
    ElMessage.success('删除成功')
    await loadCustomers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个客户吗？`, '提示', {
      type: 'warning'
    })
    const deletePromises = selectedRows.value.map(row => api.delete(`/basicinfo/customers/${row.id}`))
    await Promise.all(deletePromises)
    ElMessage.success('批量删除成功')
    await loadCustomers()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleSizeChange = () => {
  loadCustomers()
}

const handlePageChange = () => {
  loadCustomers()
}

const handleFormSuccess = (savedCustomerId) => {
  // 如果是新增客户，更新 customerId 以便可以继续添加关键人物等信息
  if (savedCustomerId && !currentCustomerId.value) {
    currentCustomerId.value = savedCustomerId
    // 不关闭对话框，让用户可以继续添加关键人物等信息
  } else {
    formVisible.value = false
  }
  loadCustomers()
}

onMounted(() => {
  loadCustomers()
})
</script>

<style scoped>
.customer-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

