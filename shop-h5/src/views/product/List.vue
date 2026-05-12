<template>
  <div class="product-list-page">
    <NavBar title="商品列表" />

    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <div class="product-list">
        <ProductCard
          v-for="item in products"
          :key="item.id"
          :product="item"
        />
      </div>
    </van-list>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getProductList } from '@/api/product'
import NavBar from '@/components/NavBar.vue'
import ProductCard from '@/components/ProductCard.vue'

const products = ref([])
const loading = ref(false)
const finished = ref(false)
const page = ref(1)
const pageSize = 10

const onLoad = async () => {
  try {
    const res = await getProductList({ page: page.value, pageSize })
    const list = res.data?.records || res.data || []
    products.value.push(...list)
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
</script>

<style scoped>
.product-list-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.product-list {
  padding: 12px;
}

.product-list :deep(.product-card) {
  margin-bottom: 10px;
}

.product-list :deep(.van-card) {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}
</style>
