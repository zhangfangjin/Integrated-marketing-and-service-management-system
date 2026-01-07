<template>
  <div class="flow-status">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>流程过程状态</span>
          <el-button @click="$router.back()">返回</el-button>
        </div>
      </template>

      <el-table :data="statusList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="nodeName" label="节点" width="180" />
        <el-table-column prop="operatorName" label="操作者" width="150" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operateTime" label="操作时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 流程审批状态查询页面
 */
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getWorkflowStatus } from '@/api/contract'

const route = useRoute()
const contractId = route.params.id
const statusList = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getWorkflowStatus(contractId)
    statusList.value = res
  } catch (error) {
    console.error('获取流程状态失败:', error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const map = {
    '已提交': 'primary',
    '待审核': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger',
    '未查看': 'info'
  }
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
</style>










