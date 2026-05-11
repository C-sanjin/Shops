<template>
  <div class="order-create-page">
    <NavBar title="确认订单" />

    <div v-if="product" class="form-content">
      <div class="section-card">
        <div class="product-info">
          <img v-if="firstImage" :src="firstImage" class="product-img" />
          <div class="product-detail">
            <div class="product-name">{{ product.productName }}</div>
            <div class="price-row">
              <span class="price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
            </div>
          </div>
        </div>
        <div class="quantity-row">
          <span class="label">数量</span>
          <van-stepper v-model="quantity" min="1" max="99" />
        </div>
      </div>

      <div class="section-card">
        <div class="section-title">选择核销门店</div>
        <van-radio-group v-model="storeId">
          <van-cell-group :border="false">
            <van-cell
              v-for="store in stores"
              :key="store.id"
              :title="store.storeName"
              :label="store.address"
              clickable
              @click="storeId = store.id"
            >
              <template #right-icon>
                <van-radio :name="store.id" />
              </template>
            </van-cell>
          </van-cell-group>
        </van-radio-group>
      </div>

      <div class="section-card">
        <div class="section-title">支付方式</div>
        <van-radio-group v-model="payMethod">
          <van-cell-group :border="false">
            <van-cell title="虚拟支付" clickable @click="payMethod = 'virtual'">
              <template #right-icon>
                <van-radio name="virtual" />
              </template>
            </van-cell>
            <van-cell title="门店代付" clickable @click="payMethod = 'store'">
              <template #right-icon>
                <van-radio name="store" />
              </template>
            </van-cell>
          </van-cell-group>
        </van-radio-group>

        <div v-if="payMethod === 'store'" class="sub-section">
          <div class="section-title">选择代付门店</div>
          <van-radio-group v-model="payStoreId">
            <van-cell-group :border="false">
              <van-cell
                v-for="store in stores"
                :key="store.id"
                :title="store.storeName"
                :label="store.address"
                clickable
                @click="payStoreId = store.id"
              >
                <template #right-icon>
                  <van-radio :name="store.id" />
                </template>
              </van-cell>
            </van-cell-group>
          </van-radio-group>
        </div>
      </div>
    </div>

    <van-action-bar>
      <div class="total-price">
        合计：<span class="price">¥{{ totalPrice }}</span>
      </div>
      <van-action-bar-button
        type="primary"
        text="提交订单"
        color="#1989fa"
        @click="handleSubmit"
      />
    </van-action-bar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant'
import { getProductDetail } from '@/api/product'
import { getStoreList } from '@/api/store'
import { createOrder } from '@/api/order'
import NavBar from '@/components/NavBar.vue'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const stores = ref([])
const quantity = ref(1)
const storeId = ref('')
const payMethod = ref('virtual')
const payStoreId = ref('')

const firstImage = computed(() => {
  if (!product.value || !product.value.images) return ''
  try {
    const parsed = typeof product.value.images === 'string'
      ? JSON.parse(product.value.images)
      : product.value.images
    return Array.isArray(parsed) && parsed.length > 0 ? parsed[0] : ''
  } catch {
    return ''
  }
})

const totalPrice = computed(() => {
  if (!product.value) return '0.00'
  return (product.value.price * quantity.value).toFixed(2)
})

const fetchProduct = async () => {
  try {
    const productId = route.query.productId
    if (!productId) return
    const res = await getProductDetail(productId)
    product.value = res.data
  } catch {
    product.value = null
  }
}

const fetchStores = async () => {
  try {
    const res = await getStoreList()
    stores.value = res.data?.list || res.data || []
  } catch {
    stores.value = []
  }
}

const handleSubmit = async () => {
  if (!storeId.value) {
    showToast('请选择核销门店')
    return
  }
  if (payMethod.value === 'store' && !payStoreId.value) {
    showToast('请选择代付门店')
    return
  }

  try {
    const data = {
      productId: product.value.id,
      quantity: quantity.value,
      storeId: storeId.value,
      payMethod: payMethod.value
    }
    if (payMethod.value === 'store') {
      data.payStoreId = payStoreId.value
    }
    const res = await createOrder(data)
    const orderNo = res.data?.orderNo || res.data?.id
    router.push({ name: 'Payment', params: { orderNo } })
  } catch {
    showToast('下单失败，请重试')
  }
}

onMounted(() => {
  fetchProduct()
  fetchStores()
})
</script>

<style scoped>
.order-create-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 60px;
}

.form-content {
  padding-bottom: 20px;
}

.section-card {
  background: #fff;
  border-radius: 8px;
  margin: 12px;
  padding: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.product-info {
  display: flex;
  gap: 12px;
}

.product-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}

.product-detail {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  line-height: 1.4;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: #ee0a24;
}

.original-price {
  font-size: 13px;
  color: #999;
  text-decoration: line-through;
}

.quantity-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.label {
  font-size: 14px;
  color: #333;
}

.sub-section {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.total-price {
  padding: 0 16px;
  font-size: 14px;
  color: #333;
  display: flex;
  align-items: center;
}

.total-price .price {
  font-size: 20px;
}
</style>
