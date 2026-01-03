<template>
  <div class="under-development">
    <el-card class="development-card">
      <div class="content">
        <el-icon :size="80" class="icon">
          <Tools />
        </el-icon>
        <h2>系统开发中</h2>
        <p class="description">该功能正在开发中，敬请期待...</p>
        <p class="module-name" v-if="moduleName">{{ moduleName }}</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Tools } from '@element-plus/icons-vue'

defineProps({
  moduleName: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.under-development {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  padding: 40px;
}

.development-card {
  max-width: 500px;
  width: 100%;
}

.content {
  text-align: center;
  padding: 40px 20px;
}

.icon {
  color: #409eff;
  margin-bottom: 20px;
}

h2 {
  font-size: 24px;
  color: #303133;
  margin: 20px 0;
  font-weight: 500;
}

.description {
  font-size: 16px;
  color: #909399;
  margin: 10px 0;
}

.module-name {
  font-size: 14px;
  color: #606266;
  margin-top: 20px;
  font-weight: 500;
}
</style>

