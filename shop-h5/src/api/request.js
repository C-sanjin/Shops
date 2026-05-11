import axios from 'axios'
import { showToast } from 'vant'
import router from '@/router'

const request = axios.create({
  baseURL: '',
  timeout: 10000
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        localStorage.removeItem('token')
        router.push({ name: 'Login' })
        showToast('登录已过期，请重新登录')
      } else {
        const message = data?.message || '请求失败'
        showToast(message)
      }
    } else {
      showToast('网络异常，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
