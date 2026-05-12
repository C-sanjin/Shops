<template>
  <div class="order-detail-page">
    <NavBar title="订单详情" />

    <div v-if="order" class="detail-content">
      <div class="status-card">
        <div class="status-text">{{ order.orderStatusDesc }}</div>
      </div>

      <div class="section-card">
        <div class="section-title">商品信息</div>
        <van-cell-group :border="false">
          <van-cell
            v-for="item in order.items"
            :key="item.productId"
            :title="item.productName"
            :value="`x${item.quantity}`"
            :label="`¥${item.price}`"
          />
        </van-cell-group>
        <van-cell title="实付金额" :value="`¥${order.payAmount}`" class="amount-cell" />
      </div>

      <div class="section-card">
        <div class="section-title">门店信息</div>
        <van-cell-group :border="false">
          <van-cell title="门店名称" :value="order.storeName || '-'" />
        </van-cell-group>
      </div>

      <div class="section-card">
        <div class="section-title">订单信息</div>
        <van-cell-group :border="false">
          <van-cell title="订单号" :value="order.orderNo" />
          <van-cell title="支付方式" :value="order.payTypeDesc || '-'" />
          <van-cell title="下单时间" :value="formatTime(order.createdAt)" />
          <van-cell v-if="order.payTime" title="支付时间" :value="formatTime(order.payTime)" />
          <van-cell v-if="order.remark" title="备注" :value="order.remark" />
        </van-cell-group>
      </div>

      <div v-if="order.verifyCode" class="section-card">
        <div class="section-title">核销码</div>
        <van-cell-group :border="false">
          <van-cell title="核销码" :value="order.verifyCode.code" />
          <van-cell title="状态" :value="order.verifyCode.statusDesc || '-'" />
        </van-cell-group>
      </div>

      <div class="bottom-actions">
        <van-button
          v-if="order.orderStatus === 1"
          type="primary"
          block
          round
          color="#1989fa"
          @click="goVerifyCode"
        >
          查看核销码
        </van-button>
        <van-button
          v-if="order.orderStatus === 0"
          type="default"
          block
          round
          @click="handleCancel"
        >
          取消订单
        </van-button>
      </div>
    </div>

    <div v-else class="loading">
      <van-loading size="24px">加载中...</van-loading>
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

const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 19)
}

const goVerifyCode = () => {
  router.push({ name: 'VerifyCode', params: { orderId: route.params.id } })
}

const handleCancel = async () => {
  try {
    await showDialog({
      title: '提示',
      message: '确定要取消该订单吗？'
    })
    await cancelOrder(route.params.id)
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

.amount-cell :deep(.van-cell__value) {
  color: #ee0a24;
  font-weight: 600;
  font-size: 16px;
}

.bottom-actions {
  padding: 24px 16px;
}

.bottom-actions .van-button {
  margin-bottom: 10px;
}

.loading {
  display: flex;
  justify-content: center;
  padding-top: 100px;
}
</style>
