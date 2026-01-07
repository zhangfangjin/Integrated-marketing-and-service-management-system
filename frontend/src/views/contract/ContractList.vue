<template>
  <div class="contract-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>合同列表</span>
          <div class="header-operations">
            <el-input
              v-model="keyword"
              placeholder="搜索合同编号、名称、客户"
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
              <el-icon><Plus /></el-icon>新增
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0">
              <el-icon><Delete /></el-icon>删除
            </el-button>
            <el-button type="warning" @click="handleExport">
              <el-icon><Download /></el-icon>导出
            </el-button>
            <el-button type="info" @click="handleViewWorkflow" :disabled="selectedRows.length !== 1">
              查看流程审批状态
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="tableData"
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="contractNumber" label="合同编号" width="150">
          <template #default="{ row }">
            <el-input v-model="row.contractNumber" placeholder="合同编号" />
          </template>
        </el-table-column>
        <el-table-column prop="contractName" label="合同名称" min-width="180">
          <template #default="{ row }">
            <el-input v-model="row.contractName" placeholder="合同名称" />
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户名称" width="180">
          <template #default="{ row }">
            <el-select 
              v-model="row.customerName" 
              filterable 
              placeholder="请选择客户"
              style="width: 100%"
            >
              <el-option
                v-for="item in customerList"
                :key="item.id"
                :label="item.customerName"
                :value="item.customerName"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="signingDate" label="签订时间" width="160">
          <template #default="{ row }">
            <el-date-picker 
              v-model="row.signingDate" 
              type="date" 
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
              style="width: 100%"
            />
          </template>
        </el-table-column>
        <el-table-column prop="deliveryDate" label="交货期" width="160">
          <template #default="{ row }">
            <el-date-picker 
              v-model="row.deliveryDate" 
              type="date" 
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
              style="width: 100%"
            />
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="总价" width="150">
          <template #default="{ row }">
            <el-input-number 
              v-model="row.totalPrice" 
              :min="0" 
              :precision="2" 
              :step="100" 
              style="width: 100%" 
              controls-position="right"
            />
          </template>
        </el-table-column>
        <el-table-column prop="contractStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.contractStatus)">{{ row.contractStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contractRemark" label="备注" min-width="150">
          <template #default="{ row }">
            <el-input v-model="row.contractRemark" placeholder="备注" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleRowSave(row)">保存</el-button>
            <el-button link type="primary" @click="handleEdit(row)">详情</el-button>
            <el-button link type="danger" @click="handleRowDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 合同列表页面
 */
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getContractList, deleteContract, updateContract } from '@/api/contract'
import api from '@/utils/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Delete, Download } from '@element-plus/icons-vue'

const router = useRouter()
const keyword = ref('')
const tableData = ref([])
const loading = ref(false)
const selectedRows = ref([])
const customerList = ref([])

/**
 * 获取客户列表
 */
const loadCustomers = async () => {
  try {
    const res = await api.get('/basicinfo/customers')
    customerList.value = res
  } catch (error) {
    console.error('获取客户列表失败:', error)
  }
}

/**
 * 获取列表数据
 */
const fetchData = async () => {
  loading.value = true
  try {
    const res = await getContractList({ keyword: keyword.value })
    tableData.value = res
  } catch (error) {
    console.error('获取合同列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索处理
 */
const handleSearch = () => {
  fetchData()
}

/**
 * 多选处理
 */
const handleSelectionChange = (val) => {
  selectedRows.value = val
}

/**
 * 新增合同
 */
const handleCreate = () => {
  router.push('/contracts/new')
}

/**
 * 详情/修改合同
 */
const handleEdit = (row) => {
  router.push(`/contracts/${row.id}/edit`)
}

/**
 * 行保存
 */
const handleRowSave = async (row) => {
  try {
    await updateContract(row.id, row)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存合同失败:', error)
    ElMessage.error('保存失败')
  }
}

/**
 * 行删除
 */
const handleRowDelete = (row) => {
  ElMessageBox.confirm('确定要删除该合同吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteContract(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除合同失败:', error)
    }
  })
}

/**
 * 批量删除
 */
const handleBatchDelete = () => {
  if (selectedRows.value.length === 0) return
  ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个合同吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      // 简单实现：并行删除
      await Promise.all(selectedRows.value.map(row => deleteContract(row.id)))
      ElMessage.success('批量删除成功')
      fetchData()
    } catch (error) {
      console.error('批量删除合同失败:', error)
    }
  })
}

/**
 * 导出处理
 */
const handleExport = () => {
  ElMessage.info('导出功能开发中...')
}

/**
 * 查看审批状态
 */
const handleViewWorkflow = () => {
  if (selectedRows.value.length === 1) {
    router.push(`/contracts/${selectedRows.value[0].id}/status`)
  }
}

/**
 * 获取状态标签类型
 */
const getStatusType = (status) => {
  const map = {
    '待提交': 'info',
    '审批中': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger'
  }
  return map[status] || 'info'
}

onMounted(() => {
  fetchData()
  loadCustomers()
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
</style>







