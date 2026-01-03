<template>
  <div class="register-container">
    <div class="register-box">
      <h2>用户注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" @blur="checkIdCard" />
        </el-form-item>
        <el-form-item v-if="idCardChecked && idCardExists">
          <el-alert type="info" :closable="false">
            <p>该身份证已注册</p>
            <p>账号：{{ existingAccount.username }}</p>
            <p>姓名：{{ existingAccount.name }}</p>
          </el-alert>
        </el-form-item>
        <template v-if="!idCardExists">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="出生日期" prop="birthDate">
            <el-date-picker v-model="form.birthDate" type="date" placeholder="选择日期" style="width: 100%" />
          </el-form-item>
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="form.gender">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="年龄" prop="age">
            <el-input-number v-model="form.age" :min="1" :max="150" />
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
          <el-form-item>
            <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">
              提交注册
            </el-button>
          </el-form-item>
        </template>
        <el-form-item>
          <el-link type="primary" @click="$router.push('/login')">已有账号？去登录</el-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const idCardChecked = ref(false)
const idCardExists = ref(false)
const existingAccount = ref({})
const positionOptions = ref([])
const regionOptions = ref([])

const form = reactive({
  idCard: '',
  name: '',
  phone: '',
  birthDate: null,
  gender: '男',
  age: null,
  positionId: null,
  regionId: null
})

const rules = {
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  positionId: [{ required: true, message: '请选择岗位', trigger: 'change' }],
  regionId: [{ required: true, message: '请选择所属片区', trigger: 'change' }]
}

// 解析身份证号
const parseIdCard = (idCard) => {
  if (idCard.length === 18) {
    const year = idCard.substring(6, 10)
    const month = idCard.substring(10, 12)
    const day = idCard.substring(12, 14)
    const genderCode = parseInt(idCard.substring(16, 17))
    const gender = genderCode % 2 === 0 ? '女' : '男'
    const birthDate = `${year}-${month}-${day}`
    const age = new Date().getFullYear() - parseInt(year)
    return { birthDate, gender, age }
  }
  return null
}

const checkIdCard = async () => {
  if (!form.idCard || form.idCard.length !== 18) return
  
  try {
    const response = await api.get('/register/check', { params: { idCard: form.idCard } })
    idCardChecked.value = true
    idCardExists.value = response.registered
    
    if (response.registered) {
      existingAccount.value = {
        username: response.username,
        name: response.name
      }
    } else {
      // 解析身份证信息
      const parsed = parseIdCard(form.idCard)
      if (parsed) {
        form.birthDate = parsed.birthDate
        form.gender = parsed.gender
        form.age = parsed.age
      }
    }
  } catch (error) {
    ElMessage.error('身份证验证失败')
  }
}

const loadOptions = async () => {
  try {
    const [positions, regions] = await Promise.all([
      api.get('/options', { params: { group: 'position' } }),
      api.get('/options', { params: { group: 'region' } })
    ])
    positionOptions.value = positions
    regionOptions.value = regions
  } catch (error) {
    console.error('加载选项失败', error)
  }
}

const handleRegister = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await api.post('/register', {
          ...form,
          birthDate: form.birthDate ? new Date(form.birthDate).toISOString().split('T')[0] : null
        })
        ElMessage.success('注册成功，等待管理员审核')
        router.push('/login')
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}

onMounted(() => {
  loadOptions()
})
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 600px;
  max-width: 100%;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.register-box h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>

