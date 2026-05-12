<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单管理</span>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column label="商品名称" min-width="150">
          <template #default="{ row }">
            {{ row.items && row.items.length > 0 ? row.items.map(i => i.productName).join('、') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="数量" width="80">
          <template #default="{ row }">
            {{ row.items && row.items.length > 0 ? row.items.reduce((s, i) => s + i.quantity, 0) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            ¥{{ Number(row.payAmount || row.totalAmount).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.orderStatus)">
              {{ row.orderStatusDesc || statusLabel(row.orderStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="storeName" label="门店" width="120" />
        <el-table-column prop="createdAt" label="下单时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">详情</el-button>
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
      v-model="detailVisible"
      title="订单详情"
      width="650px"
      destroy-on-close
    >
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detail.orderStatus)">
            {{ detail.orderStatusDesc || statusLabel(detail.orderStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ Number(detail.payAmount || detail.totalAmount).toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="门店">{{ detail.storeName }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ detail.payTypeDesc || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">{{ detail.payStatusDesc || '-' }}</el-descriptions-item>
        <el-descriptions-item label="下单时间" :span="2">{{ detail.createdAt }}</el-descriptions-item>
        <el-descriptions-item label="商品明细" :span="2">
          <div v-if="detail.items && detail.items.length">
            <div v-for="(item, idx) in detail.items" :key="idx" style="margin-bottom:4px;">
              {{ item.productName }} × {{ item.quantity }} = ¥{{ Number(item.price * item.quantity).toFixed(2) }}
            </div>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="核销码" :span="2" v-if="detail.verifyCode">
          {{ detail.verifyCode.code }}（{{ detail.verifyCode.statusDesc }}）
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const detail = ref(null)

const statusMap = {
  0: '待支付',
  1: '待核销',
  2: '已核销',
  3: '已取消',
  4: '已退款'
}

const statusTagTypeMap = {
  0: 'warning',
  1: 'success',
  2: '',
  3: 'info',
  4: 'danger'
}

function statusLabel(status) {
  return statusMap[status] || '未知'
}

function statusTagType(status) {
  return statusTagTypeMap[status] || 'info'
}

async function fetchList() {
  loading.value = true
  try {
    const res = await request.get('/admin/order/list', {
      params: { page: page.value, size: pageSize.value }
    })
    tableData.value = res.data?.list || res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  try {
    const res = await request.get(`/admin/order/${row.id}`)
    detail.value = res.data
    detailVisible.value = true
  } catch {
    // error handled by interceptor
  }
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
