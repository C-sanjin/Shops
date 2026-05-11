<template>
  <div class="user-center-page">
    <div class="user-header">
      <div class="user-info" @click="goLogin">
        <van-image round width="60" height="60" :src="avatar" class="avatar" />
        <div class="user-name">{{ displayName }}</div>
      </div>
    </div>

    <div class="section-card">
      <van-cell title="我的订单" is-link to="/order" icon="orders-o" />
    </div>

    <div class="section-card">
      <van-cell title="门店列表" is-link to="/store" icon="shop-o" />
    </div>

    <div v-if="userStore.isLoggedIn" class="section-card">
      <van-button block plain type="danger" @click="handleLogout">
        退出登录
      </van-button>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { showDialog } from 'vant'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref(3)

const displayName = computed(() => {
  if (userStore.isLoggedIn && userStore.userInfo) {
    return userStore.userInfo.nickname || userStore.userInfo.phone || '用户'
  }
  return '请登录'
})

const avatar = computed(() => {
  if (userStore.userInfo?.avatar) {
    return userStore.userInfo.avatar
  }
  return 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'
})

const goLogin = () => {
  if (!userStore.isLoggedIn) {
    router.push({ name: 'Login' })
  }
}

const handleLogout = async () => {
  try {
    await showDialog({
      title: '提示',
      message: '确定要退出登录吗？'
    })
    userStore.logout()
  } catch {
    // user canceled
  }
}

onMounted(() => {
  if (userStore.isLoggedIn && !userStore.userInfo) {
    userStore.fetchUserInfo()
  }
})
</script>

<style scoped>
.user-center-page {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 60px;
}

.user-header {
  background: #fff;
  padding: 32px 16px;
  margin-bottom: 12px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  flex-shrink: 0;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.section-card {
  background: #fff;
  border-radius: 8px;
  margin: 0 12px 12px;
  overflow: hidden;
}
</style>
