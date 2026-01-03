<template>
  <el-dialog
    v-model="dialogVisible"
    :title="customerId ? '编辑客户' : '新增客户'"
    width="1200px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      v-loading="loading"
    >
      <el-tabs v-model="activeTab">
        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="编号" prop="customerName">
                <el-input v-model="form.customerName" placeholder="请输入客户名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="单位名称" prop="customerName">
                <el-input v-model="form.customerName" placeholder="请输入单位名称" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="所属区域">
                <el-select v-model="form.customerTypeId" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="item in regionOptions"
                    :key="item.id"
                    :label="item.title"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属行业">
                <el-select v-model="form.industryId" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="item in industryOptions"
                    :key="item.id"
                    :label="item.title"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="公司地址">
                <el-input v-model="form.address" placeholder="请输入公司地址" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="地区">
                <el-select v-model="form.customerTypeId" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="item in regionOptions"
                    :key="item.id"
                    :label="item.title"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="买方属性">
                <el-select v-model="form.customerTypeId" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="item in buyerAttributeOptions"
                    :key="item.id"
                    :label="item.title"
                    :value="item.id"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="订货代表">
                <el-input v-model="form.contactPerson" placeholder="请输入订货代表" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 开票信息 -->
        <el-tab-pane label="开票信息" name="invoice">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="开票电话">
                <el-input v-model="form.contactPhone" placeholder="请输入开票电话" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="联系电话">
                <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="传真">
                <el-input v-model="form.contactEmail" placeholder="请输入传真" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮编">
                <el-input v-model="form.address" placeholder="请输入邮编" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="开票地址">
            <el-input v-model="form.address" placeholder="请输入开票地址" />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="开票银行">
                <el-input v-model="form.creditCode" placeholder="请输入开票银行" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="银行账号">
                <el-input v-model="form.creditCode" placeholder="请输入银行账号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="税号">
            <el-input v-model="form.creditCode" placeholder="请输入税号" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input
              v-model="form.remark"
              type="textarea"
              :rows="4"
              placeholder="请输入备注"
            />
          </el-form-item>
        </el-tab-pane>

        <!-- 信用信息 -->
        <el-tab-pane label="信用信息" name="credit">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="信用等级">
                <el-input v-model="form.creditCode" placeholder="请输入信用等级" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="欠款金额">
                <el-input v-model="form.legalRepresentative" placeholder="请输入欠款金额" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-tab-pane>

        <!-- 客户关键人物 -->
        <el-tab-pane label="客户关键人物" name="keyPerson">
          <CustomerKeyPerson :customer-id="props.customerId" />
        </el-tab-pane>

        <!-- 项目机会 -->
        <el-tab-pane label="项目机会" name="opportunity">
          <el-table :data="opportunityList" border style="width: 100%">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="opportunityName" label="项目名称" min-width="150" />
            <el-table-column label="项目所属片区" width="150">
              <template #default="{ row }">
                {{ row.opportunityStage?.title || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="expectedCloseDate" label="时间" width="120" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag v-if="row.status">{{ row.status.title }}</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" />
          </el-table>
        </el-tab-pane>

        <!-- 合同信息 -->
        <el-tab-pane label="合同信息" name="contract">
          <el-table :data="contractList" border style="width: 100%">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="contractName" label="合同名称" min-width="150" />
            <el-table-column prop="customerName" label="合同编号" width="150" />
            <el-table-column prop="deliveryDate" label="区域" width="120" />
            <el-table-column prop="scheduleDate" label="时间" width="120" />
            <el-table-column prop="contractRemark" label="备注" />
          </el-table>
        </el-tab-pane>

        <!-- 售后信息 -->
        <el-tab-pane label="售后信息" name="afterSales">
          <el-table :data="afterSalesList" border style="width: 100%">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="contractNo" label="合同编号" width="150" />
            <el-table-column prop="contractName" label="合同名称" min-width="150" />
            <el-table-column prop="region" label="区域" width="120" />
            <el-table-column prop="personnel" label="售后人员" width="120" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
        </el-tab-pane>

        <!-- 客户来访信息 -->
        <el-tab-pane label="客户来访信息" name="visit">
          <el-table :data="visitList" border style="width: 100%">
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="customer.customerName" label="客户名称" min-width="150" />
            <el-table-column prop="visitTime" label="日期" width="180" />
            <el-table-column prop="visitPurpose" label="来访目的" width="150" />
            <el-table-column prop="receptionist?.name" label="接待人" width="120" />
            <el-table-column prop="remark" label="备注" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">返回</el-button>
      <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import api from '@/utils/api'
import CustomerKeyPerson from './CustomerKeyPerson.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  customerId: {
    type: String,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const dialogVisible = ref(false)
const formRef = ref(null)
const loading = ref(false)
const saving = ref(false)
const activeTab = ref('basic')

const form = reactive({
  customerName: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  address: '',
  customerTypeId: null,
  industryId: null,
  creditCode: '',
  legalRepresentative: '',
  remark: ''
})

const rules = {
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const regionOptions = ref([])
const industryOptions = ref([])
const buyerAttributeOptions = ref([])
const opportunityList = ref([])
const contractList = ref([])
const afterSalesList = ref([])
const visitList = ref([])

watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
  if (val) {
    if (props.customerId) {
      loadCustomer()
      loadRelatedData()
    } else {
      resetForm()
    }
  }
})

watch(dialogVisible, (val) => {
  emit('update:modelValue', val)
})

const loadCustomer = async () => {
  if (!props.customerId) return
  loading.value = true
  try {
    const customers = await api.get('/basicinfo/customers')
    const data = customers.find(c => c.id === props.customerId)
    if (data) {
      Object.assign(form, {
        customerName: data.customerName || '',
        contactPerson: data.contactPerson || '',
        contactPhone: data.contactPhone || '',
        contactEmail: data.contactEmail || '',
        address: data.address || '',
        customerTypeId: data.customerType?.id || null,
        industryId: data.industry?.id || null,
        creditCode: data.creditCode || '',
        legalRepresentative: data.legalRepresentative || '',
        remark: data.remark || ''
      })
    }
  } catch (error) {
    ElMessage.error('加载客户信息失败')
  } finally {
    loading.value = false
  }
}

const loadRelatedData = async () => {
  if (!props.customerId) return
  try {
    // 加载项目机会
    const opportunities = await api.get('/sales/opportunities')
    opportunityList.value = opportunities.filter(o => o.customer?.id === props.customerId)

    // 加载客户来访信息
    const visits = await api.get('/sales/customer-visits', {
      params: { customerId: props.customerId }
    })
    visitList.value = visits || []
  } catch (error) {
    console.error('加载关联数据失败', error)
  }
}

const loadOptions = async () => {
  try {
    // 加载区域选项
    const regions = await api.get('/options', { params: { group: 'region' } })
    regionOptions.value = regions

    // 加载行业选项
    const industries = await api.get('/options', { params: { group: 'industry' } })
    industryOptions.value = industries

    // 加载买方属性选项
    const buyerAttrs = await api.get('/options', { params: { group: 'buyer_attribute' } })
    buyerAttributeOptions.value = buyerAttrs
  } catch (error) {
    console.error('加载选项失败', error)
  }
}

const resetForm = () => {
  Object.assign(form, {
    customerName: '',
    contactPerson: '',
    contactPhone: '',
    contactEmail: '',
    address: '',
    customerTypeId: null,
    industryId: null,
    creditCode: '',
    legalRepresentative: '',
    remark: ''
  })
  activeTab.value = 'basic'
  formRef.value?.clearValidate()
}

const handleSave = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        let savedCustomerId = props.customerId
        if (props.customerId) {
          await api.put(`/basicinfo/customers/${props.customerId}`, form)
          ElMessage.success('更新成功')
        } else {
          const response = await api.post('/basicinfo/customers', form)
          savedCustomerId = response.id
          ElMessage.success('创建成功')
        }
        emit('success', savedCustomerId)
        // 如果是新增，不关闭对话框，让用户可以继续添加关键人物等信息
        // 父组件会更新 customerId，CustomerKeyPerson 组件会自动接收到新的 customerId
        if (props.customerId) {
          handleClose()
        }
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        saving.value = false
      }
    }
  })
}

const handleClose = () => {
  dialogVisible.value = false
  resetForm()
}

onMounted(() => {
  loadOptions()
})
</script>

<style scoped>
.el-form {
  padding: 20px 0;
}
</style>

