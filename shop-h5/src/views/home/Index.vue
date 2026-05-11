<template>
  <div class="home-page">
    <div class="search-bar">
      <van-search
        v-model="searchValue"
        shape="round"
        placeholder="搜索商品"
        readonly
        @click="goProductList"
      />
    </div>

    <div class="section-card">
      <van-grid :column-num="2" :border="false" :gutter="12">
        <van-grid-item icon="location-o" text="附近门店" @click="goStoreList" />
        <van-grid-item icon="shop-o" text="全部门店" @click="goStoreList" />
      </van-grid>
    </div>

    <div class="section-card">
      <div class="section-title">热门商品</div>
      <div class="product-grid">
        <ProductCard
          v-for="item in products"
          :key="item.id"
          :product="item"
        />
      </div>
    </div>

    <van-tabbar v-model="activeTab" route>
      <van-tabbar-item to="/" icon="home-o">首页</van-tabbar-item>
      <van-tabbar-item to="/product" icon="apps-o">商品</van-tabbar-item>
      <van-tabbar-item to="/order" icon="orders-o">订单</van-tabbar-item>
      <van-tabbar-item to="/user" icon="user-o">我的</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getProductList } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'

const router = useRouter()
const searchValue = ref('')
const activeTab = ref(0)
const products = ref([])

const goProductList = () => {
  router.push({ name: 'ProductList' })
}

const goStoreList = () => {
  router.push({ name: 'StoreList' })
}

const fetchProducts = async () => {
  try {
    const res = await getProductList({ page: 1, pageSize: 10 })
    products.value = res.data?.list || res.data || []
  } catch {
    products.value = []
  }
}

onMounted(() => {
  fetchProducts()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 60px;
}

.search-bar {
  padding: 8px 12px;
  background: #fff;
}

.section-card {
  margin: 12px;
  background: #fff;
  border-radius: 8px;
  padding: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.product-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.product-grid :deep(.van-card) {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.product-grid :deep(.van-card__content) {
  padding: 8px 0 0;
}

.product-grid :deep(.van-card__thumb) {
  width: 100%;
  padding-top: 100%;
  position: relative;
}

.product-grid :deep(.van-card__thumb img) {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}
</style>
