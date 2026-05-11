<template>
  <div class="store-detail-page">
    <NavBar title="门店详情" />

    <div v-if="store" class="detail-content">
      <div class="info-card">
        <div class="store-name">{{ store.storeName }}</div>
        <van-cell-group :border="false">
          <van-cell title="地址" :value="store.address" />
          <van-cell title="营业时间" :value="store.businessHours" />
          <van-cell title="联系电话" :value="store.contactPhone" />
        </van-cell-group>
      </div>

      <div v-if="store.products && store.products.length" class="section-card">
        <div class="section-title">可核销商品</div>
        <div class="product-list">
          <ProductCard
            v-for="item in store.products"
            :key="item.id"
            :product="item"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getStoreDetail } from '@/api/store'
import NavBar from '@/components/NavBar.vue'
import ProductCard from '@/components/ProductCard.vue'

const route = useRoute()
const store = ref(null)

const fetchStore = async () => {
  try {
    const res = await getStoreDetail(route.params.id)
    store.value = res.data
  } catch {
    store.value = null
  }
}

onMounted(() => {
  fetchStore()
})
</script>

<style scoped>
.store-detail-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.info-card {
  background: #fff;
  margin-bottom: 12px;
}

.store-name {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  padding: 16px;
}

.section-card {
  background: #fff;
  border-radius: 8px;
  margin: 0 12px 12px;
  padding: 12px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.product-list :deep(.van-card) {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 10px;
}
</style>
