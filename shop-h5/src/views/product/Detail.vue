<template>
  <div class="product-detail-page">
    <NavBar title="商品详情" />

    <div v-if="product" class="detail-content">
      <van-swipe :autoplay="3000" class="product-swipe">
        <van-swipe-item v-for="(img, index) in imageList" :key="index">
          <img :src="img" class="swipe-image" />
        </van-swipe-item>
      </van-swipe>

      <div class="info-card">
        <div class="product-name">{{ product.productName }}</div>
        <div class="price-row">
          <span class="price">¥{{ product.price }}</span>
          <span v-if="product.originalPrice" class="original-price">¥{{ product.originalPrice }}</span>
        </div>
      </div>

      <div v-if="product.stores && product.stores.length" class="section-card">
        <div class="section-title">适用门店</div>
        <van-cell-group :border="false">
          <van-cell
            v-for="store in product.stores"
            :key="store.id"
            :title="store.storeName"
            :label="store.address"
            is-link
            @click="goStoreDetail(store.id)"
          />
        </van-cell-group>
      </div>
    </div>

    <van-action-bar>
      <van-action-bar-button
        type="primary"
        text="立即购买"
        color="#1989fa"
        @click="goCreateOrder"
      />
    </van-action-bar>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '@/api/product'
import NavBar from '@/components/NavBar.vue'

const route = useRoute()
const router = useRouter()
const product = ref(null)

const imageList = computed(() => {
  if (!product.value || !product.value.images) return []
  try {
    const parsed = typeof product.value.images === 'string'
      ? JSON.parse(product.value.images)
      : product.value.images
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return typeof product.value.images === 'string' ? [product.value.images] : []
  }
})

const goStoreDetail = (id) => {
  router.push({ name: 'StoreDetail', params: { id } })
}

const goCreateOrder = () => {
  router.push({ name: 'OrderCreate', query: { productId: product.value.id } })
}

const fetchProduct = async () => {
  try {
    const res = await getProductDetail(route.params.id)
    product.value = res.data
  } catch {
    product.value = null
  }
}

onMounted(() => {
  fetchProduct()
})
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 60px;
}

.product-swipe {
  width: 100%;
}

.swipe-image {
  width: 100%;
  height: 375px;
  object-fit: cover;
  display: block;
}

.info-card {
  background: #fff;
  padding: 16px;
  margin-bottom: 12px;
}

.product-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  margin-bottom: 12px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: #ee0a24;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
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
</style>
