<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>核销管理</span>
          <div>
            <el-button type="success" @click="openScanDialog">
              <el-icon><Camera /></el-icon> 扫码核销
            </el-button>
            <el-button type="warning" @click="openManualDialog">
              <el-icon><EditPen /></el-icon> 手动核销
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="code" label="核销码" width="180" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'">
              {{ row.status === 1 ? '已核销' : row.status === 2 ? '已作废' : '待核销' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="usedTime" label="核销时间" width="180">
          <template #default="{ row }">
            {{ row.usedTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="verifiedBy" label="核销人" width="100">
          <template #default="{ row }">
            {{ row.verifiedBy || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="过期时间" width="180" />
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchList"
          @current-change="fetchList"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="scanDialogVisible"
      title="扫码核销"
      width="480px"
      destroy-on-close
    >
      <el-form label-width="90px">
        <el-form-item label="核销码">
          <el-input
            v-model="scanCode"
            placeholder="请扫描或输入核销码"
            @keyup.enter="handleScanVerify"
          />
        </el-form-item>
        <el-form-item label="核销门店">
          <el-select v-model="scanStoreId" placeholder="请选择门店" style="width: 100%">
            <el-option
              v-for="s in storeOptions"
              :key="s.id"
              :label="s.storeName"
              :value="s.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scanDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="verifying" @click="handleScanVerify">确认核销</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="manualDialogVisible"
      title="手动核销"
      width="480px"
      destroy-on-close
    >
      <el-form
        ref="manualFormRef"
        :model="manualForm"
        :rules="manualRules"
        label-width="90px"
      >
        <el-form-item label="核销码" prop="code">
          <el-input v-model="manualForm.code" placeholder="请输入核销码" />
        </el-form-item>
        <el-form-item label="核销门店" prop="storeId">
          <el-select v-model="manualForm.storeId" placeholder="请选择门店" style="width: 100%">
            <el-option
              v-for="s in storeOptions"
              :key="s.id"
              :label="s.storeName"
              :value="s.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="manualDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="verifying" @click="handleManualVerify">确认核销</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const loading = ref(false)
const verifying = ref(false)
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const storeOptions = ref([])

const scanDialogVisible = ref(false)
const scanCode = ref('')
const scanStoreId = ref(null)

const manualDialogVisible = ref(false)
const manualFormRef = ref(null)
const manualForm = reactive({
  code: '',
  storeId: null
})

const manualRules = {
  code: [{ required: true, message: '请输入核销码', trigger: 'blur' }],
  storeId: [{ required: true, message: '请选择门店', trigger: 'change' }]
}

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get('/admin/verify/list', {
      params: { page: page.value, size: pageSize.value }
    })
    tableData.value = res.data?.list || res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

async function fetchStoreOptions() {
  try {
    const res = await request.get('/admin/store/list', { params: { size: 999 } })
    storeOptions.value = res.data?.list || res.data?.records || []
  } catch {
    // ignore
  }
}

function openScanDialog() {
  scanCode.value = ''
  scanStoreId.value = null
  scanDialogVisible.value = true
}

async function handleScanVerify() {
  if (!scanCode.value.trim()) {
    ElMessage.warning('请输入核销码')
    return
  }
  if (!scanStoreId.value) {
    ElMessage.warning('请选择核销门店')
    return
  }
  verifying.value = true
  try {
    await request.post('/admin/verify/scan', { code: scanCode.value.trim(), storeId: scanStoreId.value })
    ElMessage.success('核销成功')
    scanDialogVisible.value = false
    fetchList()
  } finally {
    verifying.value = false
  }
}

function openManualDialog() {
  Object.assign(manualForm, { code: '', storeId: null })
  manualDialogVisible.value = true
}

async function handleManualVerify() {
  const valid = await manualFormRef.value.validate().catch(() => false)
  if (!valid) return
  verifying.value = true
  try {
    await request.post('/admin/verify/manual', { code: manualForm.code, storeId: manualForm.storeId })
    ElMessage.success('核销成功')
    manualDialogVisible.value = false
    fetchList()
  } finally {
    verifying.value = false
  }
}

onMounted(() => {
  fetchList()
  fetchStoreOptions()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-wrap {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
