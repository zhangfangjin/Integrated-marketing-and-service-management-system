<template>
  <div class="module-management">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统功能模块树</span>
            </div>
          </template>
          <el-tree
            ref="treeRef"
            :data="moduleTree"
            :props="{ children: 'children', label: 'zhName' }"
            node-key="id"
            default-expand-all
            highlight-current
            :expand-on-click-node="false"
            @node-click="handleNodeClick"
            class="module-tree"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <span>{{ node.label }}</span>
                <span class="tree-actions">
                  <el-button
                    link
                    type="primary"
                    size="small"
                    @click.stop="handleAdd(data)"
                    icon="Plus"
                  >
                    增加
                  </el-button>
                </span>
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>模块信息</span>
            </div>
          </template>
          <el-form
            :model="form"
            :rules="rules"
            ref="formRef"
            label-width="100px"
            v-if="form.zhName !== undefined"
          >
            <el-form-item label="中文名称" prop="zhName">
              <el-input v-model="form.zhName" placeholder="请输入中文名称" />
            </el-form-item>
            <el-form-item label="英文名称" prop="enName">
              <el-input v-model="form.enName" placeholder="请输入英文名称" />
            </el-form-item>
            <el-form-item label="菜单级数" prop="level">
              <el-select v-model="form.level" placeholder="请选择菜单级数" style="width: 100%">
                <el-option label="1级" :value="1" />
                <el-option label="2级" :value="2" />
                <el-option label="3级" :value="3" />
              </el-select>
            </el-form-item>
            <el-form-item label="菜单序号" prop="orderNo">
              <el-input-number v-model="form.orderNo" :min="0" style="width: 100%" />
            </el-form-item>
            <el-form-item label="链接" prop="path">
              <el-input v-model="form.path" placeholder="请输入链接路径" />
            </el-form-item>
            <el-form-item label="图标" prop="icon">
              <el-input v-model="form.icon" placeholder="请输入图标名称" />
            </el-form-item>
            <el-form-item label="分组" prop="groupCode">
              <el-input v-model="form.groupCode" placeholder="请输入分组代码" />
            </el-form-item>
            <el-form-item label="权限" prop="permissionKey">
              <el-input v-model="form.permissionKey" placeholder="请输入权限标识" />
            </el-form-item>
            <el-form-item label="菜单可见" prop="isVisible">
              <el-switch v-model="form.isVisible" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave" :loading="loading">
                保存
              </el-button>
              <el-button
                v-if="form.id"
                type="danger"
                @click="handleDelete"
                :loading="deleting"
              >
                删除
              </el-button>
            </el-form-item>
          </el-form>
          <el-empty v-else description="请从左侧选择模块或点击'增加'按钮添加新模块" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/utils/api'

const treeRef = ref(null)
const formRef = ref(null)
const loading = ref(false)
const deleting = ref(false)
const moduleTree = ref([])

const form = reactive({
  id: null,
  zhName: '',
  enName: '',
  level: 1,
  orderNo: 0,
  path: '',
  icon: '',
  groupCode: '',
  permissionKey: '',
  isVisible: true,
  parentId: null
})

const rules = {
  zhName: [{ required: true, message: '请输入中文名称', trigger: 'blur' }],
  enName: [{ required: true, message: '请输入英文名称', trigger: 'blur' }],
  level: [{ required: true, message: '请选择菜单级数', trigger: 'change' }],
  orderNo: [{ required: true, message: '请输入菜单序号', trigger: 'blur' }]
}

const loadModuleTree = async () => {
  try {
    const modules = await api.get('/modules/tree')
    moduleTree.value = modules
  } catch (error) {
    ElMessage.error('加载模块树失败')
  }
}

const handleNodeClick = (data) => {
  Object.assign(form, {
    id: data.id,
    zhName: data.zhName,
    enName: data.enName,
    level: data.level,
    orderNo: data.orderNo || 0,
    path: data.path || '',
    icon: data.icon || '',
    groupCode: data.groupCode || '',
    permissionKey: data.permissionKey || '',
    isVisible: data.isVisible !== false,
    parentId: data.parentId
  })
}

const handleAdd = (parentData) => {
  Object.assign(form, {
    id: null,
    zhName: '',
    enName: '',
    level: (parentData?.level || 0) + 1,
    orderNo: 0,
    path: '',
    icon: '',
    groupCode: '',
    permissionKey: '',
    isVisible: true,
    parentId: parentData?.id || null
  })
}

const handleSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (form.id) {
          await api.put(`/modules/${form.id}`, form)
          ElMessage.success('更新成功')
        } else {
          await api.post('/modules', form)
          ElMessage.success('创建成功')
        }
        await loadModuleTree()
        // 重置表单
        Object.assign(form, {
          id: null,
          zhName: '',
          enName: '',
          level: 1,
          orderNo: 0,
          path: '',
          icon: '',
          groupCode: '',
          permissionKey: '',
          isVisible: true,
          parentId: null
        })
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除该模块吗？', '提示', {
      type: 'warning'
    })
    deleting.value = true
    await api.delete(`/modules/${form.id}`)
    ElMessage.success('删除成功')
    await loadModuleTree()
    // 重置表单
    Object.assign(form, {
      id: null,
      zhName: '',
      enName: '',
      level: 1,
      orderNo: 0,
      path: '',
      icon: '',
      groupCode: '',
      permissionKey: '',
      isVisible: true,
      parentId: null
    })
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  } finally {
    deleting.value = false
  }
}

onMounted(() => {
  loadModuleTree()
})
</script>

<style scoped>
.module-management {
  height: 100%;
}

.module-tree {
  min-height: 400px;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.tree-actions {
  margin-left: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

