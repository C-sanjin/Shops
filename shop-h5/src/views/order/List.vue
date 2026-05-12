<template>
  <div class="order-list-page">
    <NavBar title="我的订单" />

    <van-tabs v-model:active="activeTab" sticky>
      <van-tab title="全部" name="all" />
      <van-tab title="待付" name="0" />
      <van-tab title="待核销" name="1" />
      <van-tab title="已核销" name="2" />
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
            <van-tag :type="statusTagType(order.orderStatus)">{{ order.orderStatusDesc }}</van-tag>
          </div>
          <div class="order-body">
            <div class="order-product">{{ getOrderProductName(order) }}</div>
            <div class="order-amount">¥{{ order.payAmount }}</div>
          </div>
          <div class="order-footer">
            <span class="order-pay-type">{{ order.payTypeDesc }}</span>
            <span class="order-time">{{ order.createdAt }}</span>
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

const statusTagType = (status) => {
  const map = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'default'
  }
  return map[status] || 'default'
}

const getOrderProductName = (order) => {
  if (order.items && order.items.length > 0) {
    return order.items.map(i => `${i.productName}x${i.quantity}`).join('、')
  }
  return ''
}

const goDetail = (id) => {
  router.push({ name: 'OrderDetail', params: { id } })
}

const onLoad = async () => {
  try {
    const params = { page: page.value, pageSize }
    if (activeTab.value !== 'all') {
      params.orderStatus = activeTab.value
    }
    const res = await getOrderList(params)
    const list = res.data?.records || res.data || []
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

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  font-size: 12px;
  color: #999;
}
</style>
