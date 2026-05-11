<template>
  <div class="login-page">
    <div class="login-header">
      <div class="logo-text">欢迎登录</div>
    </div>

    <div class="form-card">
      <van-form @submit="handleLogin">
        <van-cell-group :border="false" inset>
          <van-field
            v-model="phone"
            name="phone"
            label="手机号"
            placeholder="请输入手机号"
            type="tel"
            maxlength="11"
            :rules="[{ required: true, message: '请输入手机号' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }]"
          />
          <van-field
            v-model="password"
            name="password"
            label="密码"
            placeholder="请输入密码"
            type="password"
            :rules="[{ required: true, message: '请输入密码' }]"
          />
        </van-cell-group>

        <div class="submit-btn">
          <van-button round block type="primary" color="#1989fa" native-type="submit" :loading="loading">
            登录
          </van-button>
        </div>
      </van-form>

      <div class="link-row">
        还没有账号？
        <router-link to="/register" class="link">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { showToast } from 'vant'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const phone = ref('')
const password = ref('')
const loading = ref(false)

const handleLogin = async () => {
  loading.value = true
  try {
    await userStore.login(phone.value, password.value)
    showToast('登录成功')
    const redirect = route.query.redirect || '/'
    router.replace(redirect)
  } catch {
    showToast('登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #fff;
}

.login-header {
  padding: 60px 24px 32px;
  text-align: center;
}

.logo-text {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.form-card {
  padding: 0 16px;
}

.submit-btn {
  margin: 24px 16px 0;
}

.link-row {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #999;
}

.link {
  color: #1989fa;
  text-decoration: none;
}
</style>
