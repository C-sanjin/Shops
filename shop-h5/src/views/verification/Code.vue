<template>
  <div class="verify-code-page">
    <NavBar title="核销码" />

    <div v-if="codeData" class="code-content">
      <div class="code-card">
        <div class="code-number">{{ codeData.verifyCode }}</div>
        <div class="qrcode-wrapper">
          <img v-if="qrcodeUrl" :src="qrcodeUrl" class="qrcode-img" />
        </div>
      </div>

      <div class="section-card">
        <van-cell-group :border="false">
          <van-cell title="门店名称" :value="codeData.storeName" />
          <van-cell title="门店地址" :value="codeData.storeAddress" />
          <van-cell title="过期时间" :value="codeData.expireTime" />
        </van-cell-group>
      </div>

      <div class="status-row">
        <span class="status-label">使用状态</span>
        <van-tag :type="statusTagType" size="large">{{ statusText }}</van-tag>
      </div>

      <div class="tip">请到指定门店出示此码</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getVerifyCode, getVerifyQrcode } from '@/api/verify'
import { generateQRCode } from '@/utils/qrcode'
import NavBar from '@/components/NavBar.vue'

const route = useRoute()
const codeData = ref(null)
const qrcodeUrl = ref('')

const statusText = computed(() => {
  if (!codeData.value) return ''
  const map = {
    unused: '未使用',
    used: '已使用',
    expired: '已过期',
    invalid: '已作废'
  }
  return map[codeData.value.status] || codeData.value.status
})

const statusTagType = computed(() => {
  if (!codeData.value) return 'default'
  const map = {
    unused: 'primary',
    used: 'success',
    expired: 'warning',
    invalid: 'default'
  }
  return map[codeData.value.status] || 'default'
})

const fetchCodeData = async () => {
  try {
    const orderId = route.params.orderId
    const res = await getVerifyCode(orderId)
    codeData.value = res.data
  } catch {
    codeData.value = null
  }
}

const fetchQrcode = async () => {
  try {
    const orderId = route.params.orderId
    const res = await getVerifyQrcode(orderId)
    if (res.data?.url) {
      qrcodeUrl.value = res.data.url
    } else if (res.data?.content) {
      qrcodeUrl.value = await generateQRCode(res.data.content)
    }
  } catch {
    if (codeData.value?.verifyCode) {
      try {
        qrcodeUrl.value = await generateQRCode(codeData.value.verifyCode)
      } catch {
        qrcodeUrl.value = ''
      }
    }
  }
}

onMounted(async () => {
  await fetchCodeData()
  await fetchQrcode()
})
</script>

<style scoped>
.verify-code-page {
  min-height: 100vh;
  background: #f7f8fa;
}

.code-content {
  padding: 12px;
}

.code-card {
  background: #fff;
  border-radius: 8px;
  padding: 32px 16px;
  text-align: center;
  margin-bottom: 12px;
}

.code-number {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  letter-spacing: 6px;
  margin-bottom: 24px;
}

.qrcode-wrapper {
  display: flex;
  justify-content: center;
}

.qrcode-img {
  width: 200px;
  height: 200px;
}

.section-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
}

.status-row {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.status-label {
  font-size: 15px;
  color: #333;
}

.tip {
  text-align: center;
  font-size: 14px;
  color: #999;
  padding: 16px 0;
}
</style>
