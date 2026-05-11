import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo } from '@/api/user'
import router from '@/router'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token
  },

  actions: {
    async login(phone, password) {
      const res = await loginApi(phone, password)
      this.token = res.data.token
      localStorage.setItem('token', res.data.token)
      await this.fetchUserInfo()
    },

    async fetchUserInfo() {
      if (!this.token) return
      const res = await getUserInfo()
      this.userInfo = res.data
    },

    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      router.push({ name: 'Login' })
    }
  }
})
