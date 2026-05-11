<template>
  <van-nav-bar
    :title="title"
    :left-arrow="leftArrow"
    @click-left="onClickLeft"
    fixed
    placeholder
  />
</template>

<script setup>
import { useRouter } from 'vue-router'

defineProps({
  title: {
    type: String,
    default: ''
  },
  leftArrow: {
    type: Boolean,
    default: true
  }
})

const router = useRouter()

const onClickLeft = () => {
  router.back()
}
</script>

<style scoped>
:deep(.van-nav-bar) {
  background: #fff;
}
:deep(.van-nav-bar__title) {
  color: #333;
  font-weight: 600;
}
</style>
