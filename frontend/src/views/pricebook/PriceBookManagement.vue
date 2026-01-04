<template>
  <div class="pricebook-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>价格本管理</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索产品名称、产品编码、价格版本号"
              style="width: 300px; margin-right: 10px"
              clearable
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="priceBookList"
        border
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="product.productCode" label="产品编码" width="120" />
        <el-table-column prop="product.productName" label="产品名称" min-width="150" />
        <el-table-column prop="versionNumber" label="价格版本号" width="120" />
        <el-table-column prop="priceType" label="价格类型" width="120" />
        <el-table-column prop="unitPrice" label="单价" width="120">
          <template #default="{ row }">
            {{ formatPrice(row.unitPrice) }}
          </template>
        </el-table-column>
        <el-table-column prop="currency" label="货币单位" width="100" />
        <el-table-column prop="effectiveDate" label="生效日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.effectiveDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="expiryDate" label="失效日期" width="120">
          <template #default="{ row }">
            {{ formatDate(row.expiryDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="active" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.active ? 'success' : 'info'">
              {{ row.active ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">修改</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="form.id ? '修改价格记录' : '新增价格记录'"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="产品" prop="productId">
          <el-select
            v-model="form.productId"
            placeholder="请选择产品"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="product in products"
              :key="product.id"
              :label="`${product.productCode} - ${product.productName}`"
              :value="product.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="价格版本号" prop="versionNumber">
          <el-input v-model="form.versionNumber" placeholder="请输入价格版本号" />
        </el-form-item>
        <el-form-item label="价格类型" prop="priceType">
          <el-input v-model="form.priceType" placeholder="如：标准价格、成本价格、销售价格" />
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number
            v-model="form.unitPrice"
            :precision="2"
            :step="0.01"
            :min="0"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="货币单位" prop="currency">
          <el-input v-model="form.currency" placeholder="如：CNY" />
        </el-form-item>
        <el-form-item label="生效日期" prop="effectiveDate">
          <el-date-picker
            v-model="form.effectiveDate"
            type="date"
            placeholder="选择生效日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="失效日期" prop="expiryDate">
          <el-date-picker
            v-model="form.expiryDate"
            type="date"
            placeholder="选择失效日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="状态" prop="active">
          <el-switch v-model="form.active" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import api from '@/utils/api'

const priceBookList = ref([])
const products = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const searchKeyword = ref('')
const formRef = ref(null)

const form = reactive({
  id: null,
  productId: null,
  versionNumber: '',
  priceType: '',
  unitPrice: null,
  currency: 'CNY',
  effectiveDate: null,
  expiryDate: null,
  active: true,
  remark: ''
})

const rules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  versionNumber: [{ required: true, message: '请输入价格版本号', trigger: 'blur' }],
  priceType: [{ required: true, message: '请输入价格类型', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const loadPriceBooks = async () => {
  loading.value = true
  try {
    const keyword = searchKeyword.value?.trim()
    const data = keyword
      ? await api.get('/pricebook', { params: { keyword } })
      : await api.get('/pricebook')
    priceBookList.value = data
  } catch (error) {
    ElMessage.error('加载价格列表失败')
    priceBookList.value = []
  } finally {
    loading.value = false
  }
}

const loadProducts = async () => {
  try {
    const data = await api.get('/basicinfo/products')
    products.value = data.filter(p => p.active)
  } catch (error) {
    console.error('加载产品列表失败', error)
  }
}

const handleSearch = () => {
  loadPriceBooks()
}

const handleAdd = () => {
  Object.assign(form, {
    id: null,
    productId: null,
    versionNumber: '',
    priceType: '',
    unitPrice: null,
    currency: 'CNY',
    effectiveDate: null,
    expiryDate: null,
    active: true,
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  Object.assign(form, {
    id: row.id,
    productId: row.product.id,
    versionNumber: row.versionNumber,
    priceType: row.priceType,
    unitPrice: parseFloat(row.unitPrice),
    currency: row.currency,
    effectiveDate: row.effectiveDate,
    expiryDate: row.expiryDate,
    active: row.active,
    remark: row.remark || ''
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      saving.value = true
      try {
        const requestData = {
          ...form,
          effectiveDate: form.effectiveDate ? new Date(form.effectiveDate + 'T00:00:00').toISOString().split('T')[0] : null,
          expiryDate: form.expiryDate ? new Date(form.expiryDate + 'T00:00:00').toISOString().split('T')[0] : null
        }
        if (form.id) {
          await api.put(`/pricebook/${form.id}`, requestData)
          ElMessage.success('修改成功')
        } else {
          await api.post('/pricebook', requestData)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        await loadPriceBooks()
      } catch (error) {
        ElMessage.error(error.response?.data?.message || '操作失败')
      } finally {
        saving.value = false
      }
    }
  })
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条价格记录吗？', '提示', {
      type: 'warning'
    })
    await api.delete(`/pricebook/${row.id}`)
    ElMessage.success('删除成功')
    await loadPriceBooks()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const formatPrice = (price) => {
  if (price == null) return '-'
  return `¥${parseFloat(price).toFixed(2)}`
}

const formatDate = (date) => {
  if (!date) return '-'
  return date
}

onMounted(() => {
  loadProducts()
  loadPriceBooks()
})
</script>

<style scoped>
.pricebook-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}
</style>

