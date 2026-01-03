<template>
  <div class="personnel-archive">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>人员档案</span>
          <el-button type="primary" @click="handleAdd" icon="Plus">新增人员</el-button>
        </div>
      </template>
      <!-- 搜索表单 -->
      <div class="search-form">
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="搜索">
            <el-input
              v-model="searchForm.keyword"
              placeholder="请输入姓名、身份证号或手机号（支持部分匹配）"
              clearable
              style="width: 300px"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch" icon="Search">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="personnelList" border style="width: 100%">
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="gender" label="性别" width="80" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column label="岗位" width="120">
          <template #default="{ row }">
            {{ row.position?.title || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="所属片区" width="120">
          <template #default="{ row }">
            {{ row.region?.title || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="birthDate" label="出生日期" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '编辑人员' : '新增人员'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input
            v-model="form.idCard"
            placeholder="请输入身份证号"
            :disabled="!!form.id"
            @blur="parseIdCard"
          />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="出生日期" prop="birthDate">
          <el-date-picker
            v-model="form.birthDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="1" :max="150" style="width: 100%" />
        </el-form-item>
        <el-form-item label="岗位" prop="positionId">
          <el-select v-model="form.positionId" placeholder="请选择岗位" style="width: 100%">
            <el-option
              v-for="item in positionOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属片区" prop="regionId">
          <el-select v-model="form.regionId" placeholder="请选择所属片区" style="width: 100%">
            <el-option
              v-for="item in regionOptions"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import api from '@/utils/api'

const personnelList = ref([])
const allPersonnelList = ref([]) // 保存所有数据，用于重置
const positionOptions = ref([])
const regionOptions = ref([])
const dialogVisible = ref(false)
const loading = ref(false)
const formRef = ref(null)

const searchForm = reactive({
  keyword: ''
})

const form = reactive({
  id: null,
  name: '',
  idCard: '',
  phone: '',
  birthDate: null,
  gender: '男',
  age: null,
  positionId: null,
  regionId: null
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  positionId: [{ required: true, message: '请选择岗位', trigger: 'change' }],
  regionId: [{ required: true, message: '请选择所属片区', trigger: 'change' }]
}

// 解析身份证号
const parseIdCard = () => {
  if (form.idCard && form.idCard.length === 18) {
    const year = form.idCard.substring(6, 10)
    const month = form.idCard.substring(10, 12)
    const day = form.idCard.substring(12, 14)
    const genderCode = parseInt(form.idCard.substring(16, 17))
    const gender = genderCode % 2 === 0 ? '女' : '男'
    const birthDate = `${year}-${month}-${day}`
    const age = new Date().getFullYear() - parseInt(year)
    
    if (!form.id) {
      // 只有新增时才自动填充
      form.birthDate = birthDate
      form.gender = gender
      form.age = age
    }
  }
}

const loadPersonnel = async (keyword = null) => {
  try {
    const params = keyword ? { keyword } : {}
    const data = await api.get('/accounts', { params })
    // 只显示已审核通过或已禁用的人员
    const filtered = data.filter(p => p.status === 'APPROVED' || p.status === 'DISABLED')
    personnelList.value = filtered
    // 如果没有搜索关键词，保存所有数据用于重置
    if (!keyword) {
      allPersonnelList.value = filtered
    }
  } catch (error) {
    ElMessage.error('加载人员列表失败')
  }
}

const handleSearch = () => {
  loadPersonnel(searchForm.keyword || null)
}

const handleReset = () => {
  searchForm.keyword = ''
  loadPersonnel()
}

const loadOptions = async () => {
  try {
    const [positions, regions] = await Promise.all([
      api.get('/options', { params: { group: 'POSITION' } }),
      api.get('/options', { params: { group: 'AREA' } })
    ])
    positionOptions.value = positions
    regionOptions.value = regions
  } catch (error) {
    console.error('加载选项失败', error)
  }
}

const handleAdd = () => {
  Object.assign(form, {
    id: null,
    name: '',
    idCard: '',
    phone: '',
    birthDate: null,
    gender: '男',
    age: null,
    positionId: null,
    regionId: null
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    name: row.name,
    idCard: row.idCard,
    phone: row.phone,
    birthDate: row.birthDate,
    gender: row.gender || '男',
    age: row.age,
    positionId: row.position?.id || null,
    regionId: row.region?.id || null
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (form.id) {
          // 更新
          await api.put(`/accounts/${form.id}`, {
            name: form.name,
            idCard: form.idCard,
            phone: form.phone,
            birthDate: form.birthDate,
            gender: form.gender,
            age: form.age,
            positionId: form.positionId,
            regionId: form.regionId
          })
          ElMessage.success('更新成功')
        } else {
          // 新增（使用注册接口）
          await api.post('/register', {
            name: form.name,
            idCard: form.idCard,
            phone: form.phone,
            birthDate: form.birthDate,
            gender: form.gender,
            age: form.age,
            positionId: form.positionId,
            regionId: form.regionId
          })
          ElMessage.success('新增成功，请前往账号管理进行审核')
        }
        dialogVisible.value = false
        await loadPersonnel()
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
    await ElMessageBox.confirm('确定要删除该人员吗？删除后将无法恢复。', '提示', {
      type: 'warning'
    })
    await api.delete(`/accounts/${row.id}`)
    ElMessage.success('删除成功')
    await loadPersonnel()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadPersonnel()
  loadOptions()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>

