<template>
  <div class="execution-progress">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>合同执行动态列表</span>
        </div>
      </template>

      <el-table :data="progressList" border style="width: 100%" v-loading="loading">
        <el-table-column prop="contractNumber" label="合同编号" width="150" />
        <el-table-column prop="contractName" label="合同名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="designProgress" label="设计进度" width="120">
          <template #default="{ row }">
            <el-tag :type="getProgressType(row.designProgress)">{{ row.designProgress || '未开始' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="productionProgress" label="生产进度" width="120">
          <template #default="{ row }">
            <el-tag :type="getProgressType(row.productionProgress)">{{ row.productionProgress || '未开始' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="procurementProgress" label="采购进度" width="120">
          <template #default="{ row }">
            <el-tag :type="getProgressType(row.procurementProgress)">{{ row.procurementProgress || '未开始' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="manufacturingProgress" label="制造进度" width="120">
          <template #default="{ row }">
            <el-tag :type="getProgressType(row.manufacturingProgress)">{{ row.manufacturingProgress || '未开始' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assemblyProgress" label="装配进度" width="120">
          <template #default="{ row }">
            <el-tag :type="getProgressType(row.assemblyProgress)">{{ row.assemblyProgress || '未开始' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 合同执行动态列表页面
 */
import { ref, onMounted } from 'vue'
import { getAllExecutionProgress } from '@/api/contract'

const progressList = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAllExecutionProgress()
    progressList.value = res
  } catch (error) {
    console.error('获取执行进度失败:', error)
  } finally {
    loading.value = false
  }
}

const getProgressType = (progress) => {
  if (progress === '已完成') return 'success'
  if (progress === '进行中') return 'warning'
  if (progress === '未完成' || !progress) return 'info'
  return 'info'
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










