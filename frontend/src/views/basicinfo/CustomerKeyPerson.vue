<template>
  <div class="customer-key-person">
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增
      </el-button>
      <el-button type="danger" :disabled="selectedRows.length === 0" @click="handleBatchDelete">
        <el-icon><Delete /></el-icon>
        删除
      </el-button>
    </div>

    <el-table
      :data="keyPersonList"
      border
      style="width: 100%"
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="birthday" label="生日" width="120" />
      <el-table-column prop="position" label="职位" width="120" />
      <el-table-column prop="directSuperior" label="直接上级" width="120" />
      <el-table-column prop="role" label="职务" width="120" />
      <el-table-column prop="contactInfo" label="联系方式" width="150" />
      <el-table-column label="是否主要联系人" width="120">
        <template #default="{ row }">
          <el-tag v-if="row.isPrimary" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="remark" label="备注" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 关键人物编辑对话框 -->
    <el-dialog
      v-model="formVisible"
      :title="currentId ? '编辑关键人物' : '新增关键人物'"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属客户">
              <el-input v-model="form.customerName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入联系人名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人编码">
              <el-input v-model="form.code" placeholder="请输入联系人编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.gender" placeholder="请选择" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="称呼">
              <el-input v-model="form.salutation" placeholder="请输入称呼" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="直接上级">
              <el-input v-model="form.directSuperior" placeholder="请输入直接上级" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="籍贯">
              <el-input v-model="form.placeOfOrigin" placeholder="请输入籍贯" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生日">
              <el-date-picker
                v-model="form.birthday"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="婚姻状况">
              <el-select v-model="form.maritalStatus" placeholder="请选择" style="width: 100%">
                <el-option label="未婚" value="未婚" />
                <el-option label="已婚" value="已婚" />
                <el-option label="离异" value="离异" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职务">
              <el-select v-model="form.position" placeholder="请选择" style="width: 100%">
                <el-option label="决策者" value="决策者" />
                <el-option label="部门主管" value="部门主管" />
                <el-option label="普通员工" value="普通员工" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="个人爱好">
              <el-input v-model="form.hobbies" placeholder="请输入个人爱好" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否主要联系人">
              <el-radio-group v-model="form.isPrimary">
                <el-radio :label="true">是</el-radio>
                <el-radio :label="false">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import api from '@/utils/api'

const props = defineProps({
  customerId: {
    type: String,
    default: null
  }
})

const keyPersonList = ref([])
const loading = ref(false)
const formVisible = ref(false)
const saving = ref(false)
const formRef = ref(null)
const currentId = ref(null)
const selectedRows = ref([])

const form = reactive({
  customerName: '',
  name: '',
  code: '',
  gender: '',
  salutation: '',
  directSuperior: '',
  placeOfOrigin: '',
  birthday: '',
  maritalStatus: '',
  position: '',
  hobbies: '',
  isPrimary: false,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入联系人名称', trigger: 'blur' }]
}

const loadKeyPersons = async () => {
  if (!props.customerId) return
  loading.value = true
  try {
    const response = await api.get(`/basicinfo/customers/${props.customerId}/key-persons`)
    keyPersonList.value = response.data || []
    // 格式化日期
    keyPersonList.value.forEach(item => {
      if (item.birthday) {
        item.birthday = item.birthday.split('T')[0]
      }
    })
  } catch (error) {
    ElMessage.error('加载关键人物列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = async () => {
  if (!props.customerId) {
    ElMessage.warning('请先保存客户信息，然后再添加关键人物')
    return
  }
  currentId.value = null
  resetForm()
  // 加载客户名称
  await loadCustomerName()
  formVisible.value = true
}

const handleEdit = (row) => {
  currentId.value = row.id
  Object.assign(form, {
    customerName: row.customer?.customerName || '',
    name: row.name || '',
    code: row.code || '',
    gender: row.gender || '',
    salutation: row.salutation || '',
    directSuperior: row.directSuperior || '',
    placeOfOrigin: row.placeOfOrigin || '',
    birthday: row.birthday ? (typeof row.birthday === 'string' ? row.birthday.split('T')[0] : row.birthday) : '',
    maritalStatus: row.maritalStatus || '',
    position: row.position || '',
    hobbies: row.hobbies || '',
    isPrimary: row.isPrimary || false,
    contactInfo: row.contactInfo || '',
    remark: row.remark || ''
  })
  formVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该关键人物吗？', '提示', {
      type: 'warning'
    })
    await api.delete(`/basicinfo/customers/${props.customerId}/key-persons/${row.id}`)
    ElMessage.success('删除成功')
    await loadKeyPersons()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 个关键人物吗？`, '提示', {
      type: 'warning'
    })
    const ids = selectedRows.value.map(row => row.id)
    await api.delete(`/basicinfo/customers/${props.customerId}/key-persons`, { data: ids })
    ElMessage.success('批量删除成功')
    await loadKeyPersons()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('批量删除失败')
    }
  }
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleSave = async () => {
  // 检查 customerId 是否存在
  if (!props.customerId) {
    ElMessage.warning('请先保存客户信息，然后再添加关键人物')
    return
  }

  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        const formData = {
          name: form.name,
          code: form.code,
          gender: form.gender,
          salutation: form.salutation,
          directSuperior: form.directSuperior,
          placeOfOrigin: form.placeOfOrigin,
          birthday: form.birthday || null,
          maritalStatus: form.maritalStatus,
          position: form.position,
          hobbies: form.hobbies,
          isPrimary: form.isPrimary,
          contactInfo: form.contactInfo,
          remark: form.remark
        }
        const url = currentId.value 
          ? `/basicinfo/customers/${props.customerId}/key-persons/${currentId.value}`
          : `/basicinfo/customers/${props.customerId}/key-persons`
        
        if (currentId.value) {
          await api.put(url, formData)
          ElMessage.success('更新成功')
        } else {
          await api.post(url, formData)
          ElMessage.success('创建成功')
        }
        formVisible.value = false
        await loadKeyPersons()
      } catch (error) {
        console.error('保存关键人物失败:', error)
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        saving.value = false
      }
    }
  })
}

const resetForm = () => {
  Object.assign(form, {
    customerName: '',
    name: '',
    code: '',
    gender: '',
    salutation: '',
    directSuperior: '',
    placeOfOrigin: '',
    birthday: '',
    maritalStatus: '',
    position: '',
    hobbies: '',
    isPrimary: false,
    remark: ''
  })
  formRef.value?.clearValidate()
}

watch(() => props.customerId, (newId) => {
  if (newId) {
    loadKeyPersons()
    // 更新表单中的客户名称
    loadCustomerName()
  }
}, { immediate: true })

const loadCustomerName = async () => {
  if (props.customerId) {
    try {
      const customers = await api.get('/basicinfo/customers')
      const customer = customers.find(c => c.id === props.customerId)
      if (customer) {
        form.customerName = customer.customerName
      }
    } catch (error) {
      console.error('加载客户信息失败', error)
    }
  }
}

onMounted(() => {
  if (props.customerId) {
    loadKeyPersons()
  }
})
</script>

<style scoped>
.customer-key-person {
  padding: 20px 0;
}

.toolbar {
  margin-bottom: 20px;
}
</style>

