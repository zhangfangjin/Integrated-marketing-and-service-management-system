<template>
  <div class="store-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商店管理</span>
          <el-button type="primary" @click="handleAdd" icon="Plus">新增商店</el-button>
        </div>
      </template>
      <el-table :data="stores" border style="width: 100%">
        <el-table-column prop="name" label="商店名称" width="200" />
        <el-table-column prop="type" label="类型" width="120" />
        <el-table-column prop="address" label="地址" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="manager" label="负责人" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '营业中' ? 'success' : 'info'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
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
      :title="form.id ? '编辑商店' : '新增商店'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商店名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商店名称" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="购物中心" value="购物中心" />
            <el-option label="百货商场" value="百货商场" />
            <el-option label="专卖店" value="专卖店" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="负责人" prop="manager">
          <el-input v-model="form.manager" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="营业中">营业中</el-radio>
            <el-radio label="暂停营业">暂停营业</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import api from '@/utils/api'

const stores = ref([])
const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  type: '',
  address: '',
  phone: '',
  manager: '',
  status: '营业中'
})

const rules = {
  name: [{ required: true, message: '请输入商店名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }]
}

const loadStores = async () => {
  try {
    const data = await api.get('/mall/stores')
    stores.value = data
  } catch (error) {
    if (error.response?.status === 403) {
      stores.value = []
      ElMessage.error('无权限查看商店管理')
    } else {
      stores.value = []
      console.error('加载商店列表失败', error)
    }
  }
}

const handleAdd = () => {
  Object.assign(form, {
    id: null,
    name: '',
    type: '',
    address: '',
    phone: '',
    manager: '',
    status: '营业中'
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (form.id) {
          await api.put(`/mall/stores/${form.id}`, form)
          ElMessage.success('更新成功')
        } else {
          await api.post('/mall/stores', form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await loadStores()
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
    await ElMessageBox.confirm('确定要删除该商店吗？', '提示', {
      type: 'warning'
    })
    await api.delete(`/mall/stores/${row.id}`)
    ElMessage.success('删除成功')
    await loadStores()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadStores()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

