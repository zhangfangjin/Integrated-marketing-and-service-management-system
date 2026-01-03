<template>
  <div class="ad-settings">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>广告设置</span>
          <el-button type="primary" @click="handleAdd" icon="Plus">新增广告</el-button>
        </div>
      </template>
      <el-table :data="ads" border style="width: 100%">
        <el-table-column prop="title" label="广告标题" width="200" />
        <el-table-column prop="imageUrl" label="图片地址" />
        <el-table-column prop="linkUrl" label="跳转链接" />
        <el-table-column prop="position" label="位置" width="120" />
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'info'">{{ row.status }}</el-tag>
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
      :title="form.id ? '编辑广告' : '新增广告'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="广告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入广告标题" />
        </el-form-item>
        <el-form-item label="图片地址" prop="imageUrl">
          <el-input v-model="form.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="跳转链接" prop="linkUrl">
          <el-input v-model="form.linkUrl" placeholder="请输入跳转链接" />
        </el-form-item>
        <el-form-item label="位置" prop="position">
          <el-select v-model="form.position" placeholder="请选择位置" style="width: 100%">
            <el-option label="首页轮播" value="首页轮播" />
            <el-option label="侧边栏" value="侧边栏" />
            <el-option label="底部横幅" value="底部横幅" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="启用">启用</el-radio>
            <el-radio label="禁用">禁用</el-radio>
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

const ads = ref([])
const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  title: '',
  imageUrl: '',
  linkUrl: '',
  position: '',
  sortOrder: 0,
  status: '启用'
})

const rules = {
  title: [{ required: true, message: '请输入广告标题', trigger: 'blur' }],
  imageUrl: [{ required: true, message: '请输入图片地址', trigger: 'blur' }],
  position: [{ required: true, message: '请选择位置', trigger: 'change' }]
}

const loadAds = async () => {
  try {
    const data = await api.get('/mall/ad-settings')
    ads.value = data
  } catch (error) {
    // 如果是权限错误，清空列表
    if (error.response?.status === 403) {
      ads.value = []
      ElMessage.error('无权限查看广告设置')
    } else {
      // 其他错误，使用空列表
      ads.value = []
      console.error('加载广告设置失败', error)
    }
  }
}

const handleAdd = () => {
  Object.assign(form, {
    id: null,
    title: '',
    imageUrl: '',
    linkUrl: '',
    position: '',
    sortOrder: 0,
    status: '启用'
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
          await api.put(`/mall/ad-settings/${form.id}`, form)
          ElMessage.success('更新成功')
        } else {
          await api.post('/mall/ad-settings', form)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await loadAds()
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
    await ElMessageBox.confirm('确定要删除该广告吗？', '提示', {
      type: 'warning'
    })
    await api.delete(`/mall/ad-settings/${row.id}`)
    ElMessage.success('删除成功')
    await loadAds()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadAds()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

