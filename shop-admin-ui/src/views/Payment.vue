<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>虚拟支付配置</span>
          <el-button type="primary" @click="openDialog()">
            <el-icon><Plus /></el-icon> 新增配置
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="配置名称" min-width="150" />
        <el-table-column prop="channel" label="支付渠道" width="120">
          <template #default="{ row }">
            <el-tag>{{ channelLabel(row.channel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="merchantId" label="商户号" min-width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
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
      :title="isEdit ? '编辑支付配置' : '新增支付配置'"
      width="560px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="支付渠道" prop="channel">
          <el-select v-model="form.channel" placeholder="请选择支付渠道" style="width: 100%">
            <el-option value="wechat" label="微信支付" />
            <el-option value="alipay" label="支付宝" />
            <el-option value="unionpay" label="银联支付" />
          </el-select>
        </el-form-item>
        <el-form-item label="商户号" prop="merchantId">
          <el-input v-model="form.merchantId" placeholder="请输入商户号" />
        </el-form-item>
        <el-form-item label="应用ID" prop="appId">
          <el-input v-model="form.appId" placeholder="请输入应用ID" />
        </el-form-item>
        <el-form-item label="密钥" prop="secretKey">
          <el-input
            v-model="form.secretKey"
            type="password"
            show-password
            placeholder="请输入密钥"
          />
        </el-form-item>
        <el-form-item label="回调地址" prop="notifyUrl">
          <el-input v-model="form.notifyUrl" placeholder="请输入回调地址" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option :value="1" label="启用" />
            <el-option :value="0" label="禁用" />
          </el-select>
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
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const formRef = ref(null)

const channelMap = {
  wechat: '微信支付',
  alipay: '支付宝',
  unionpay: '银联支付'
}

function channelLabel(channel) {
  return channelMap[channel] || channel
}

const form = reactive({
  name: '',
  channel: '',
  merchantId: '',
  appId: '',
  secretKey: '',
  notifyUrl: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  channel: [{ required: true, message: '请选择支付渠道', trigger: 'change' }],
  merchantId: [{ required: true, message: '请输入商户号', trigger: 'blur' }],
  secretKey: [{ required: true, message: '请输入密钥', trigger: 'blur' }]
}

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get('/admin/virtual/list', {
      params: { page: page.value, size: pageSize.value }
    })
    tableData.value = res.data?.list || res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function openDialog(row) {
  isEdit.value = !!row
  editId.value = row?.id || null
  if (row) {
    Object.assign(form, {
      name: row.name || '',
      channel: row.channel || '',
      merchantId: row.merchantId || '',
      appId: row.appId || '',
      secretKey: row.secretKey || '',
      notifyUrl: row.notifyUrl || '',
      status: row.status ?? 1
    })
  } else {
    Object.assign(form, {
      name: '',
      channel: '',
      merchantId: '',
      appId: '',
      secretKey: '',
      notifyUrl: '',
      status: 1
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
      await request.put(`/admin/virtual/${editId.value}`, form)
      ElMessage.success('编辑成功')
    } else {
      await request.post('/admin/virtual/create', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除配置「${row.name}」吗？`, '提示', {
    type: 'warning'
  })
  await request.delete(`/admin/virtual/${row.id}`)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(() => {
  fetchList()
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
