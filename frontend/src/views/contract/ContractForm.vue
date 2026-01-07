<template>
  <div class="contract-form">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '修改合同' : '新增合同' }}</span>
          <div class="header-operations">
            <el-button @click="handleCancel">返回</el-button>
            <el-button type="primary" @click="handleSave(false)">保存</el-button>
            <el-button type="success" @click="handleSave(true)">提交</el-button>
          </div>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="合同信息" name="info">
          <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" label-position="top">
            <!-- 基本信息 -->
            <el-row :gutter="20">
              <el-col :span="6">
                <el-form-item label="销售机会" prop="salesOpportunity">
                  <el-input v-model="form.salesOpportunity" placeholder="请输入销售机会" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="合同号" prop="contractNumber">
                  <el-input v-model="form.contractNumber" placeholder="请输入合同号" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="总价(元)" prop="totalPrice">
                  <el-input-number v-model="form.totalPrice" :precision="2" :step="1000" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="签订日期" prop="signingDate">
                  <el-date-picker v-model="form.signingDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="6">
                <el-form-item label="排产日期" prop="scheduleDate">
                  <el-date-picker v-model="form.scheduleDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="交货日期" prop="deliveryDate">
                  <el-date-picker v-model="form.deliveryDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="货物到站" prop="deliveryStation">
                  <el-input v-model="form.deliveryStation" placeholder="请输入货物到站" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="运费支付" prop="freightPayment">
                  <el-input v-model="form.freightPayment" placeholder="请输入运费支付" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="合同名称" prop="contractName">
                  <el-input v-model="form.contractName" placeholder="请输入合同名称" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="项目名称" prop="projectName">
                  <el-input v-model="form.projectName" placeholder="请输入项目名称" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="付款方式" prop="paymentMethod">
                  <el-input v-model="form.paymentMethod" type="textarea" :rows="3" placeholder="请输入付款方式" />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 订货单位信息 -->
            <el-divider content-position="left">订货单位信息</el-divider>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="订货单位" prop="orderingUnit">
                  <el-input v-model="form.orderingUnit" placeholder="请输入订货单位" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="订货代表" prop="orderingRepresentative">
                  <el-input v-model="form.orderingRepresentative" placeholder="请输入订货代表" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="订货电话" prop="orderingPhone">
                  <el-input v-model="form.orderingPhone" placeholder="请输入订货电话" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="8">
                <el-form-item label="订货地址" prop="orderingAddress">
                  <el-input v-model="form.orderingAddress" placeholder="请输入订货地址" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="订货邮编" prop="orderingPostcode">
                  <el-input v-model="form.orderingPostcode" placeholder="请输入订货邮编" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="订货片区" prop="orderingArea">
                  <el-select v-model="form.orderingArea" placeholder="请选择订货片区" style="width: 100%">
                    <el-option label="华东片区" value="华东" />
                    <el-option label="华南片区" value="华南" />
                    <el-option label="华北片区" value="华北" />
                    <el-option label="西北片区" value="西北" />
                    <el-option label="西南片区" value="西南" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 附件上传 -->
            <el-divider content-position="left">附件上传</el-divider>
            <el-form-item label="附件" prop="attachment">
              <el-input v-model="form.attachment" placeholder="请点击右侧按钮上传附件" readonly>
                <template #append>
                  <el-upload
                    action="/api/upload"
                    :show-file-list="false"
                    :on-success="handleUploadSuccess"
                    :headers="uploadHeaders"
                  >
                    <el-button type="primary">上传</el-button>
                  </el-upload>
                </template>
              </el-input>
            </el-form-item>

            <!-- 占比划分 -->
            <el-divider content-position="left">占比划分</el-divider>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-table :data="form.proportions" border size="small">
                  <el-table-column label="负责人" width="150">
                    <template #default="{ row }">
                      <el-input v-model="row.managerName" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column label="所属片区" width="150">
                    <template #default="{ row }">
                      <el-input v-model="row.area" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column label="占比划分">
                    <template #default="{ row }">
                      <el-input v-model="row.proportion" size="small" />
                    </template>
                  </el-table-column>
                  <el-table-column label="操作" width="60" align="center">
                    <template #header>
                      <el-button link type="primary" :icon="Plus" @click="addProportion" />
                    </template>
                    <template #default="{ $index }">
                      <el-button link type="danger" :icon="Delete" @click="removeProportion($index)" />
                    </template>
                  </el-table-column>
                </el-table>
              </el-col>
              <el-col :span="12">
                <el-form-item label="备注" prop="contractRemark">
                  <el-input v-model="form.contractRemark" type="textarea" :rows="4" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="20" style="margin-top: 20px">
              <el-col :span="8">
                <el-form-item label="经办部门" prop="handlerDepartment">
                  <el-input v-model="form.handlerDepartment" readonly />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="经办人" prop="handlerName">
                  <el-input v-model="form.handlerName" readonly />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="经办日期" prop="handleDate">
                  <el-input v-model="form.handleDate" readonly />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="细目" name="details">
          <el-table :data="form.details" border>
            <el-table-column label="类型" width="120">
              <template #default="{ row }">
                <el-input v-model="row.type" />
              </template>
            </el-table-column>
            <el-table-column label="产品型号" width="150">
              <template #default="{ row }">
                <el-input v-model="row.productModel" />
              </template>
            </el-table-column>
            <el-table-column label="产品类型" width="150">
              <template #default="{ row }">
                <el-input v-model="row.productType" />
              </template>
            </el-table-column>
            <el-table-column label="细分类型" width="150">
              <template #default="{ row }">
                <el-input v-model="row.subType" />
              </template>
            </el-table-column>
            <el-table-column label="产品名称" min-width="180">
              <template #default="{ row }">
                <el-input v-model="row.productName" />
              </template>
            </el-table-column>
            <el-table-column label="备注">
              <template #default="{ row }">
                <el-input v-model="row.remark" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #header>
                <el-button type="primary" link :icon="Plus" @click="addDetail">添加</el-button>
              </template>
              <template #default="{ $index }">
                <el-button type="danger" link :icon="Delete" @click="removeDetail($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="付款阶段" name="payments">
          <el-table :data="form.paymentStages" border>
            <el-table-column label="付款阶段" width="120">
              <template #default="{ row }">
                <el-input v-model="row.stage" />
              </template>
            </el-table-column>
            <el-table-column label="应付金额" width="150">
              <template #default="{ row }">
                <el-input-number v-model="row.amountPayable" :precision="2" style="width: 100%" />
              </template>
            </el-table-column>
            <el-table-column label="已付金额" width="150">
              <template #default="{ row }">
                <el-input-number v-model="row.amountPaid" :precision="2" style="width: 100%" />
              </template>
            </el-table-column>
            <el-table-column label="付款阶段名称" min-width="150">
              <template #default="{ row }">
                <el-input v-model="row.stageName" />
              </template>
            </el-table-column>
            <el-table-column label="付款日期" width="180">
              <template #default="{ row }">
                <el-date-picker v-model="row.paymentDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
              </template>
            </el-table-column>
            <el-table-column label="备注">
              <template #default="{ row }">
                <el-input v-model="row.remark" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" align="center">
              <template #header>
                <el-button type="primary" link :icon="Plus" @click="addPaymentStage">添加</el-button>
              </template>
              <template #default="{ $index }">
                <el-button type="danger" link :icon="Delete" @click="removePaymentStage($index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
/**
 * 合同录入与修改页面
 */
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getContractById, createContract, updateContract, saveContractDraft } from '@/api/contract'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const activeTab = ref('info')
const formRef = ref(null)
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  salesOpportunity: '',
  contractNumber: '',
  contractName: '',
  totalPrice: 0,
  signingDate: '',
  scheduleDate: '',
  deliveryDate: '',
  deliveryStation: '',
  freightPayment: '',
  projectName: '',
  paymentMethod: '',
  orderingUnit: '',
  orderingRepresentative: '',
  orderingPhone: '',
  orderingAddress: '',
  orderingPostcode: '',
  orderingArea: '',
  attachment: '',
  contractRemark: '',
  handlerDepartment: authStore.user?.department || '销售部',
  handlerName: authStore.user?.name || '管理员',
  handleDate: new Date().toISOString().split('T')[0],
  proportions: [],
  details: [],
  paymentStages: []
})

const rules = {
  contractNumber: [{ required: true, message: '请输入合同编号', trigger: 'blur' }],
  contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }]
}

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${authStore.token}`
}))

/**
 * 获取详情数据
 */
const fetchData = async () => {
  if (isEdit.value) {
    try {
      const res = await getContractById(route.params.id)
      Object.assign(form, res)
    } catch (error) {
      console.error('获取详情失败:', error)
    }
  }
}

/**
 * 上传成功处理
 */
const handleUploadSuccess = (res) => {
  form.attachment = res.url || res.data?.url
  ElMessage.success('上传成功')
}

/**
 * 添加占比划分
 */
const addProportion = () => {
  form.proportions.push({
    managerName: '',
    area: '',
    proportion: ''
  })
}

/**
 * 删除占比划分
 */
const removeProportion = (index) => {
  form.proportions.splice(index, 1)
}

/**
 * 添加细目
 */
const addDetail = () => {
  form.details.push({
    type: '',
    productModel: '',
    productType: '',
    subType: '',
    productName: '',
    remark: ''
  })
}

/**
 * 删除细目
 */
const removeDetail = (index) => {
  form.details.splice(index, 1)
}

/**
 * 添加付款阶段
 */
const addPaymentStage = () => {
  form.paymentStages.push({
    stage: '',
    amountPayable: 0,
    amountPaid: 0,
    stageName: '',
    paymentDate: '',
    remark: ''
  })
}

/**
 * 删除付款阶段
 */
const removePaymentStage = (index) => {
  form.paymentStages.splice(index, 1)
}

/**
 * 保存或提交
 * @param {boolean} isSubmit - 是否提交审批
 */
const handleSave = async (isSubmit) => {
  // 必须先输入合同名称，即使是草稿
  if (!form.contractNumber || !form.contractName) {
    activeTab.value = 'info'
    ElMessage.warning('请填写必要的合同编号和名称')
    return
  }

  try {
    let res
    if (isEdit.value) {
      res = await updateContract(route.params.id, form)
    } else {
      res = await saveContractDraft(form)
    }
    
    ElMessage.success(isSubmit ? '正在进入审批配置...' : '保存成功')
    
    if (isSubmit) {
      router.push(`/contracts/${res.id}/approve`)
    } else {
      router.push('/contracts')
    }
  } catch (error) {
    console.error('保存失败:', error)
  }
}

/**
 * 取消并返回
 */
const handleCancel = () => {
  router.back()
}

onMounted(() => {
  fetchData()
  // 默认添加一行
  if (!isEdit.value) {
    addProportion()
    addDetail()
    addPaymentStage()
  }
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-divider {
  margin: 30px 0 20px;
}

:deep(.el-form-item__label) {
  font-weight: bold;
}
</style>










