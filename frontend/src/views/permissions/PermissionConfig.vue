<template>
  <div class="permission-config">
    <el-row :gutter="20">
      <el-col :span="10">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>角色选择</span>
            </div>
          </template>
          <el-form :inline="true">
            <el-form-item label="角色名称:">
              <el-select
                v-model="selectedRoleId"
                placeholder="请选择角色"
                style="width: 200px"
                @change="handleRoleChange"
              >
                <el-option
                  v-for="role in roles"
                  :key="role.id"
                  :label="role.name"
                  :value="role.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-checkbox v-model="allRoles">所有角色</el-checkbox>
            </el-form-item>
          </el-form>
          <div class="tree-title">系统功能模块树</div>
          <el-tree
            ref="treeRef"
            :data="permissionTree"
            :props="{ children: 'children', label: 'zhName' }"
            node-key="id"
            default-expand-all
            show-checkbox
            :check-strictly="false"
            @check="handleTreeCheck"
            @node-click="handleNodeClick"
            highlight-current
            class="permission-tree"
          >
            <template #default="{ node, data }">
              <span 
                style="cursor: pointer; padding: 0 4px;"
                @click.stop="handleNodeTextClick(data)"
              >
                {{ node.label }}
              </span>
            </template>
          </el-tree>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>操作设置({{ selectedModuleName }})</span>
            </div>
          </template>
          <div v-if="selectedModuleId" class="permission-settings">
            <div class="grant-revoke">
              <span>授予:</span>
              <el-radio-group v-model="grantAll" @change="handleGrantAll">
                <el-radio :label="true">全部给予</el-radio>
                <el-radio :label="false">全部收回</el-radio>
              </el-radio-group>
            </div>
            <el-divider />
            <div class="permission-items">
              <div class="permission-item">
                <span>可浏览:</span>
                <el-switch v-model="permissions.canRead" />
              </div>
              <div class="permission-item">
                <span>可新增:</span>
                <el-switch v-model="permissions.canAdd" />
              </div>
              <div class="permission-item">
                <span>可修改:</span>
                <el-switch v-model="permissions.canUpdate" />
              </div>
              <div class="permission-item">
                <span>可查看:</span>
                <el-switch v-model="permissions.canSee" />
              </div>
            </div>
            <div class="save-button">
              <el-button type="primary" @click="handleSave" :loading="loading" size="large">
                保存
              </el-button>
            </div>
          </div>
          <el-empty v-else description="请从左侧选择模块" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const treeRef = ref(null)
const loading = ref(false)
const roles = ref([])
const selectedRoleId = ref(null)
const allRoles = ref(false)
const permissionTree = ref([])
const selectedModuleId = ref(null)
const selectedModuleName = ref('')
const grantAll = ref(false)
const modulePermissionsMap = ref(new Map())

const permissions = reactive({
  canRead: true,
  canAdd: false,
  canUpdate: false,
  canSee: true
})

const loadRoles = async () => {
  try {
    const data = await api.get('/roles')
    roles.value = data
    if (data.length > 0) {
      selectedRoleId.value = data[0].id
    }
  } catch (error) {
    ElMessage.error('加载角色列表失败')
  }
}

const loadPermissionTree = async () => {
  if (!selectedRoleId.value) return
  
  try {
    const data = await api.get(`/permissions/role/${selectedRoleId.value}/tree`)
    permissionTree.value = data
    
    // 设置已选中的节点（根据 selected 字段）
    const checkedNodes = []
    const traverse = (nodes) => {
      nodes.forEach(node => {
        if (node.selected) {
          checkedNodes.push(node.id)
        }
        if (node.children) {
          traverse(node.children)
        }
      })
    }
    traverse(data)
    treeRef.value?.setCheckedKeys(checkedNodes)
  } catch (error) {
    ElMessage.error('加载权限树失败')
  }
}

const handleRoleChange = () => {
  modulePermissionsMap.value.clear() // 切换角色时清空权限配置缓存
  loadPermissionTree()
  selectedModuleId.value = null
  selectedModuleName.value = ''
}

const handleNodeTextClick = (data) => {
  // 点击节点文本时，显示权限配置
  // 保存上一个选中模块的权限配置
  if (selectedModuleId.value && selectedModuleId.value !== data.id) {
    modulePermissionsMap.value.set(selectedModuleId.value, {
      canRead: permissions.canRead,
      canAdd: permissions.canAdd,
      canUpdate: permissions.canUpdate,
      canSee: permissions.canSee
    })
  }
  
  selectedModuleId.value = data.id
  selectedModuleName.value = data.zhName
  loadModulePermissions(data.id)
}

const handleNodeClick = (data) => {
  // Element Plus tree 的 node-click 事件（备用处理）
  // 主要使用 handleNodeTextClick 处理
}

const handleTreeCheck = (data, checked) => {
  // 当复选框状态改变时，如果节点被选中，显示权限配置
  if (checked.checkedNodes.includes(data.id)) {
    // 保存上一个选中模块的权限配置
    if (selectedModuleId.value && selectedModuleId.value !== data.id) {
      modulePermissionsMap.value.set(selectedModuleId.value, {
        canRead: permissions.canRead,
        canAdd: permissions.canAdd,
        canUpdate: permissions.canUpdate,
        canSee: permissions.canSee
      })
    }
    
    selectedModuleId.value = data.id
    selectedModuleName.value = data.zhName
    loadModulePermissions(data.id)
  } else {
    // 如果取消选中且是当前选中的模块，清空权限配置面板
    if (selectedModuleId.value === data.id) {
      selectedModuleId.value = null
      selectedModuleName.value = ''
    }
  }
}

const loadModulePermissions = async (moduleId) => {
  if (!selectedRoleId.value || !moduleId) return
  
  try {
    // 先从内存中查找是否有保存的权限配置
    const savedPerms = modulePermissionsMap.value.get(moduleId)
    if (savedPerms) {
      permissions.canRead = savedPerms.canRead
      permissions.canAdd = savedPerms.canAdd
      permissions.canUpdate = savedPerms.canUpdate
      permissions.canSee = savedPerms.canSee
      return
    }
    
    // 从树数据中查找该模块的权限信息
    const findNodeInTree = (nodes, id) => {
      for (const node of nodes) {
        if (node.id === id) {
          return node
        }
        if (node.children && node.children.length > 0) {
          const found = findNodeInTree(node.children, id)
          if (found) return found
        }
      }
      return null
    }
    
    const moduleNode = findNodeInTree(permissionTree.value, moduleId)
    if (moduleNode && moduleNode.selected) {
      // 如果模块已配置权限，使用配置的值
      permissions.canRead = moduleNode.canRead || false
      permissions.canAdd = moduleNode.canAdd || false
      permissions.canUpdate = moduleNode.canUpdate || false
      permissions.canSee = moduleNode.canSee !== undefined ? moduleNode.canSee : true
      
      // 保存到内存中
      modulePermissionsMap.value.set(moduleId, {
        canRead: permissions.canRead,
        canAdd: permissions.canAdd,
        canUpdate: permissions.canUpdate,
        canSee: permissions.canSee
      })
    } else {
      // 重置为默认值
      permissions.canRead = true
      permissions.canAdd = false
      permissions.canUpdate = false
      permissions.canSee = true
    }
  } catch (error) {
    console.error('加载模块权限失败', error)
    // 出错时使用默认值
    permissions.canRead = true
    permissions.canAdd = false
    permissions.canUpdate = false
    permissions.canSee = true
  }
}

const handleGrantAll = (value) => {
  if (value) {
    permissions.canRead = true
    permissions.canAdd = true
    permissions.canUpdate = true
    permissions.canSee = true
  } else {
    permissions.canRead = false
    permissions.canAdd = false
    permissions.canUpdate = false
    permissions.canSee = false
  }
}


const handleSave = async () => {
  if (!selectedRoleId.value) {
    ElMessage.warning('请选择角色')
    return
  }
  
  // 如果没有选中任何模块，提示用户
  const checkedKeys = treeRef.value.getCheckedKeys()
  if (checkedKeys.length === 0) {
    ElMessage.warning('请至少选择一个模块')
    return
  }
  
  // 如果有当前选中的模块，保存其权限配置
  if (selectedModuleId.value) {
    modulePermissionsMap.value.set(selectedModuleId.value, {
      canRead: permissions.canRead,
      canAdd: permissions.canAdd,
      canUpdate: permissions.canUpdate,
      canSee: permissions.canSee
    })
  }
  
  loading.value = true
  try {
    // 构建权限项列表：为所有选中的模块保存权限
    const permissionItems = checkedKeys.map(moduleId => {
      const savedPerms = modulePermissionsMap.value.get(moduleId)
      // 如果有保存的权限配置，使用保存的；否则如果是当前选中的模块，使用当前的；否则使用默认值
      if (savedPerms) {
        return {
          moduleId,
          canRead: savedPerms.canRead,
          canAdd: savedPerms.canAdd,
          canUpdate: savedPerms.canUpdate,
          canSee: savedPerms.canSee
        }
      } else if (moduleId === selectedModuleId.value) {
        return {
          moduleId,
          canRead: permissions.canRead,
          canAdd: permissions.canAdd,
          canUpdate: permissions.canUpdate,
          canSee: permissions.canSee
        }
      } else {
        // 默认权限：可浏览、可查看
        return {
          moduleId,
          canRead: true,
          canAdd: false,
          canUpdate: false,
          canSee: true
        }
      }
    })
    
    await api.post('/permissions', {
      roleId: selectedRoleId.value,
      permissions: permissionItems
    })
    
    ElMessage.success('保存成功')
    // 重新加载权限树以更新选中状态
    await loadPermissionTree()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '保存失败')
  } finally {
    loading.value = false
  }
}

watch(selectedRoleId, () => {
  loadPermissionTree()
})

onMounted(() => {
  loadRoles()
})
</script>

<style scoped>
.permission-config {
  height: 100%;
}

.tree-title {
  margin: 15px 0;
  font-weight: bold;
  font-size: 14px;
}

.permission-tree {
  min-height: 500px;
  max-height: 600px;
  overflow-y: auto;
}

.permission-settings {
  padding: 20px;
}

.grant-revoke {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.permission-items {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin: 20px 0;
}

.permission-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.save-button {
  margin-top: 30px;
  text-align: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

