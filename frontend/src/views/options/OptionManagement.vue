<template>
  <div class="option-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>选项管理</span>
          <div>
            <el-select v-model="filterGroup" placeholder="筛选分组" style="width: 150px; margin-right: 10px" clearable>
              <el-option label="全部" value="" />
              <el-option label="岗位" value="position" />
              <el-option label="片区" value="region" />
            </el-select>
            <el-button type="primary" @click="handleAdd" icon="Plus">新增选项</el-button>
          </div>
        </div>
      </template>
      <el-table :data="filteredOptions" border style="width: 100%">
        <el-table-column prop="group" label="分组" width="120" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="value" label="值" width="200" />
        <el-table-column prop="order" label="排序" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑选项' : '新增选项'"
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="分组" prop="group">
          <el-select v-model="form.group" placeholder="请选择分组" style="width: 100%">
            <el-option label="岗位" value="position" />
            <el-option label="片区" value="region" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="值" prop="value">
          <el-input v-model="form.value" placeholder="请输入值" />
        </el-form-item>
        <el-form-item label="排序" prop="order">
          <el-input-number v-model="form.order" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

const options = ref([])
const filterGroup = ref('')
const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref(null)

const filteredOptions = computed(() => {
  if (!filterGroup.value) return options.value
  return options.value.filter(opt => opt.group === filterGroup.value)
})

const form = reactive({
  id: null,
  group: '',
  title: '',
  value: '',
  order: 0
})

const rules = {
  group: [{ required: true, message: '请输入分组', trigger: 'blur' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  value: [{ required: true, message: '请输入值', trigger: 'blur' }]
}

const loadOptions = async () => {
  try {
    const data = await api.get('/options')
    options.value = data
  } catch (error) {
    ElMessage.error('加载选项列表失败')
  }
}

const handleAdd = () => {
  Object.assign(form, {
    id: null,
    group: '',
    title: '',
    value: '',
    order: 0
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    group: row.group,
    title: row.title,
    value: row.value,
    order: row.order
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (form.id) {
          await api.put(`/options/${form.id}`, form)
          ElMessage.success('更新成功')
        } else {
          await api.post('/options', form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await loadOptions()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该选项吗？', '提示', {
      type: 'warning'
    })
    await api.delete(`/options/${row.id}`)
    ElMessage.success('删除成功')
    await loadOptions()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadOptions()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

