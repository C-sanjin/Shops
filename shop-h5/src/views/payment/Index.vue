<template>
  <div class="payment-page">
    <NavBar title="订单支付" />

    <div class="payment-content">
      <div class="amount-card">
        <div class="amount-label">支付金额</div>
        <div class="amount-value">¥{{ amount }}</div>
      </div>

      <div class="section-card">
        <div class="section-title">支付方式</div>
        <van-cell-group :border="false">
          <van-cell
            v-if="payMethod === 'virtual'"
            title="虚拟支付"
            icon="balance-o"
          />
          <van-cell
            v-if="payMethod === 'store'"
            title="门店代付"
            icon="shop-o"
          />
        </van-cell-group>
      </div>

      <div class="pay-action">
        <van-button
          type="primary"
          block
          round
          color="#1989fa"
          size="large"
          :loading="paying"
          @click="handlePay"
        >
          确认支付
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { pay } from '@/api/payment'
import { getOrderDetail } from '@/api/order'
import NavBar from '@/components/NavBar.vue'

const route = useRoute()
const router = useRouter()

const amount = ref('0.00')
const payMethod = ref('virtual')
const paying = ref(false)
const orderNo = ref('')

const handlePay = async () => {
  paying.value = true
  try {
    await pay({ orderNo: orderNo.value, payMethod: payMethod.value })
    showToast('支付成功')
    router.replace({ name: 'VerifyCode', params: { orderId: orderNo.value } })
  } catch {
    showToast('支付失败，请重试')
  } finally {
    paying.value = false
  }
}

const fetchOrder = async () => {
  try {
    orderNo.value = route.params.orderNo
    const res = await getOrderDetail(orderNo.value)
    const order = res.data
    amount.value = order.totalAmount || order.amount || '0.00'
    payMethod.value = order.payMethod || 'virtual'
  } catch {
    amount.value = '0.00'
  }
}

onMounted(() => {
  fetchOrder()
})
</script>

<style scoped>
.payment-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.amount-card {
  background: #fff;
  padding: 32px 16px;
  text-align: center;
  margin-bottom: 12px;
}

.amount-label {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.amount-value {
  font-size: 36px;
  font-weight: 700;
  color: #ee0a24;
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

.pay-action {
  padding: 32px 16px;
}
</style>
