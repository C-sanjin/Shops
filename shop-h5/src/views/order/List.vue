<template>
  <div class="order-list-page">
    <NavBar title="我的订单" />

    <van-tabs v-model:active="activeTab" sticky>
      <van-tab title="全部" name="all" />
      <van-tab title="待付" name="pending" />
      <van-tab title="待核销" name="paid" />
      <van-tab title="已核销" name="verified" />
    </van-tabs>

    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <div class="order-list">
        <div
          v-for="order in orders"
          :key="order.id"
          class="order-card"
          @click="goDetail(order.id)"
        >
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <van-tag :type="statusTagType(order.status)">{{ statusText(order.status) }}</van-tag>
          </div>
          <div class="order-body">
            <div class="order-product">{{ order.productName }}</div>
            <div class="order-amount">¥{{ order.totalAmount || order.amount }}</div>
          </div>
        </div>
      </div>
    </van-list>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList } from '@/api/order'
import NavBar from '@/components/NavBar.vue'

const router = useRouter()
const orders = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const pageSize = 10
const activeTab = ref('all')

const statusText = (status) => {
  const map = {
    pending: '待付款',
    paid: '待核销',
    verified: '已核销',
    cancelled: '已取消'
  }
  return map[status] || status
}

const statusTagType = (status) => {
  const map = {
    pending: 'warning',
    paid: 'primary',
    verified: 'success',
    cancelled: 'default'
  }
  return map[status] || 'default'
}

const goDetail = (id) => {
  router.push({ name: 'OrderDetail', params: { id } })
}

const onLoad = async () => {
  try {
    const params = { page: page.value, pageSize }
    if (activeTab.value !== 'all') {
      params.status = activeTab.value
    }
    const res = await getOrderList(params)
    const list = res.data?.list || res.data || []
    orders.value.push(...list)
    loading.value = false
    if (list.length < pageSize) {
      finished.value = true
    } else {
      page.value++
    }
  } catch {
    loading.value = false
    finished.value = true
  }
}

watch(activeTab, () => {
  orders.value = []
  page.value = 1
  finished.value = false
  loading.value = true
  onLoad()
})
</script>

<style scoped>
.order-list-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.order-list {
  padding: 12px;
}

.order-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 10px;
  cursor: pointer;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-size: 13px;
  color: #999;
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-product {
  font-size: 15px;
  color: #333;
  font-weight: 500;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 12px;
}

.order-amount {
  font-size: 16px;
  font-weight: 600;
  color: #ee0a24;
  flex-shrink: 0;
}
</style>
