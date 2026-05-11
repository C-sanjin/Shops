<template>
  <div class="product-card" @click="goDetail">
    <van-card
      :price="product.price"
      :origin-price="product.originalPrice"
      :desc="product.productName"
      :thumb="firstImage"
    >
      <template #title>
        <span class="product-name">{{ product.productName }}</span>
      </template>
    </van-card>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()

const firstImage = computed(() => {
  const images = props.product.images
  if (!images) return ''
  try {
    const parsed = typeof images === 'string' ? JSON.parse(images) : images
    return Array.isArray(parsed) && parsed.length > 0 ? parsed[0] : ''
  } catch {
    return typeof images === 'string' ? images : ''
  }
})

const goDetail = () => {
  router.push({ name: 'ProductDetail', params: { id: props.product.id } })
}
</script>

<style scoped>
.product-card {
  cursor: pointer;
}
.product-card :deep(.van-card) {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin: 0;
}
.product-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
