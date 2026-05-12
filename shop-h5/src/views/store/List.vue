<template>
  <div class="store-list-page">
    <NavBar title="门店列表" />

    <div class="store-list">
      <div
        v-for="store in stores"
        :key="store.id"
        class="store-card"
        @click="goDetail(store.id)"
      >
        <van-card>
          <template #title>
            <span class="store-name">{{ store.storeName }}</span>
          </template>
          <template #desc>
            <div class="store-info">
              <div class="info-item">
                <van-icon name="location-o" />
                <span>{{ store.address }}</span>
              </div>
              <div class="info-item">
                <van-icon name="clock-o" />
                <span>{{ store.businessHours }}</span>
              </div>
              <div class="info-item">
                <van-icon name="phone-o" />
                <span>{{ store.contactPhone }}</span>
              </div>
            </div>
          </template>
        </van-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getStoreList } from '@/api/store'
import NavBar from '@/components/NavBar.vue'

const router = useRouter()
const stores = ref([])

const goDetail = (id) => {
  router.push({ name: 'StoreDetail', params: { id } })
}

const fetchStores = async () => {
  try {
    const res = await getStoreList()
    stores.value = res.data?.records || res.data || []
  } catch {
    stores.value = []
  }
}

onMounted(() => {
  fetchStores()
})
</script>

<style scoped>
.store-list-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.store-list {
  padding: 12px;
}

.store-card {
  margin-bottom: 10px;
  cursor: pointer;
}

.store-card :deep(.van-card) {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin: 0;
}

.store-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.store-info {
  margin-top: 8px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
  margin-bottom: 4px;
}

.info-item .van-icon {
  color: #1989fa;
}
</style>
