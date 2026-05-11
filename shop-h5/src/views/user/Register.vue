<template>
  <div class="register-page">
    <div class="register-header">
      <div class="logo-text">欢迎注册</div>
    </div>

    <div class="form-card">
      <van-form @submit="handleRegister">
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
          <van-field
            v-model="confirmPassword"
            name="confirmPassword"
            label="确认密码"
            placeholder="请再次输入密码"
            type="password"
            :rules="[{ required: true, message: '请确认密码' }, { validator: validateConfirm, message: '两次密码不一致' }]"
          />
        </van-cell-group>

        <div class="submit-btn">
          <van-button round block type="primary" color="#1989fa" native-type="submit" :loading="loading">
            注册
          </van-button>
        </div>
      </van-form>

      <div class="link-row">
        已有账号？
        <router-link to="/login" class="link">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { register } from '@/api/user'

const router = useRouter()

const phone = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)

const validateConfirm = () => {
  return password.value === confirmPassword.value
}

const handleRegister = async () => {
  loading.value = true
  try {
    await register(phone.value, password.value)
    showToast('注册成功')
    router.push({ name: 'Login' })
  } catch {
    showToast('注册失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: #fff;
}

.register-header {
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
