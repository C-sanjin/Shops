<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 新增商品
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ Number(row.price).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="originalPrice" label="原价" width="100">
          <template #default="{ row }">
            {{ row.originalPrice ? '¥' + Number(row.originalPrice).toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="关联门店" width="120">
          <template #default="{ row }">
            {{ row.stores && row.stores.length > 0 ? row.stores.map(s => s.storeName).join('、') : (row.storeScope === 0 ? '全部门店' : '-') }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '新增商品'"
      width="560px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
      >
        <el-form-item label="商品名称" prop="productName">
          <el-input v-model="form.productName" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="form.price"
            :min="0"
            :precision="2"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number
            v-model="form.originalPrice"
            :min="0"
            :precision="2"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number
            v-model="form.stock"
            :min="0"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="门店范围" prop="storeScope">
          <el-select v-model="form.storeScope" placeholder="请选择" style="width: 100%">
            <el-option :value="0" label="全部门店" />
            <el-option :value="1" label="指定门店" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联门店" prop="storeIds" v-if="form.storeScope === 1">
          <el-select v-model="form.storeIds" multiple placeholder="请选择门店" style="width: 100%">
            <el-option
              v-for="s in storeOptions"
              :key="s.id"
              :label="s.storeName"
              :value="s.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option :value="1" label="上架" />
            <el-option :value="0" label="下架" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const storeOptions = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)

const form = reactive({
  productName: '',
  price: 0,
  originalPrice: null,
  stock: 0,
  storeScope: 0,
  storeIds: [],
  status: 1,
  description: ''
})

const rules = {
  productName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get('/admin/product/list', {
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

function openDialog(row) {
  isEdit.value = !!row
  editId.value = row?.id || null
  if (row) {
    Object.assign(form, {
      productName: row.productName || '',
      price: row.price ?? 0,
      originalPrice: row.originalPrice ?? null,
      stock: row.stock ?? 0,
      storeScope: row.storeScope ?? 0,
      storeIds: row.stores ? row.stores.map(s => s.id) : [],
      status: row.status ?? 1,
      description: row.description || ''
    })
  } else {
    Object.assign(form, {
      productName: '',
      price: 0,
      originalPrice: null,
      stock: 0,
      storeScope: 0,
      storeIds: [],
      status: 1,
      description: ''
    })
  }
  dialogVisible.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await request.put(`/admin/product/${editId.value}`, form)
      ElMessage.success('编辑成功')
    } else {
      await request.post('/admin/product/create', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除商品「${row.productName}」吗？`, '提示', {
    type: 'warning'
  })
  await request.delete(`/admin/product/${row.id}`)
  ElMessage.success('删除成功')
  fetchList()
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
