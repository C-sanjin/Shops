<template>
  <div class="order-detail-page">
    <NavBar title="订单详情" />

    <div v-if="order" class="detail-content">
      <div class="status-card">
        <div class="status-text">{{ statusText(order.status) }}</div>
      </div>

      <div class="section-card">
        <div class="section-title">商品信息</div>
        <van-cell-group :border="false">
          <van-cell title="商品名称" :value="order.productName" />
          <van-cell title="数量" :value="order.quantity" />
          <van-cell title="金额" :value="`¥${order.totalAmount || order.amount}`" />
        </van-cell-group>
      </div>

      <div class="section-card">
        <div class="section-title">门店信息</div>
        <van-cell-group :border="false">
          <van-cell title="门店名称" :value="order.storeName" />
          <van-cell title="门店地址" :value="order.storeAddress" />
        </van-cell-group>
      </div>

      <div class="bottom-actions">
        <van-button
          v-if="order.status === 'paid'"
          type="primary"
          block
          round
          color="#1989fa"
          @click="goVerifyCode"
        >
          查看核销码
        </van-button>
        <van-button
          v-if="order.status === 'pending'"
          type="default"
          block
          round
          @click="handleCancel"
        >
          取消订单
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showDialog, showToast } from 'vant'
import { getOrderDetail, cancelOrder } from '@/api/order'
import NavBar from '@/components/NavBar.vue'

const route = useRoute()
const router = useRouter()
const order = ref(null)

const statusText = (status) => {
  const map = {
    pending: '待付款',
    paid: '待核销',
    verified: '已核销',
    cancelled: '已取消'
  }
  return map[status] || status
}

const goVerifyCode = () => {
  router.push({ name: 'VerifyCode', params: { orderId: order.value.id } })
}

const handleCancel = async () => {
  try {
    await showDialog({
      title: '提示',
      message: '确定要取消该订单吗？'
    })
    await cancelOrder(order.value.id)
    showToast('订单已取消')
    fetchOrder()
  } catch {
    // user canceled dialog or api error
  }
}

const fetchOrder = async () => {
  try {
    const res = await getOrderDetail(route.params.id)
    order.value = res.data
  } catch {
    order.value = null
  }
}

onMounted(() => {
  fetchOrder()
})
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.status-card {
  background: #fff;
  padding: 24px 16px;
  text-align: center;
  margin-bottom: 12px;
}

.status-text {
  font-size: 24px;
  font-weight: 700;
  color: #1989fa;
}

.section-card {
  background: #fff;
  border-radius: 8px;
  margin: 0 12px 12px;
  overflow: hidden;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  padding: 12px 16px 8px;
}

.bottom-actions {
  padding: 24px 16px;
}

.bottom-actions .van-button {
  margin-bottom: 10px;
}
</style>
