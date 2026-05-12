import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const adminId = ref(localStorage.getItem('adminId') || '')
  const username = ref(localStorage.getItem('username') || '')
  const realName = ref(localStorage.getItem('realName') || '')
  const role = ref(localStorage.getItem('role') || '')

  async function login(loginForm) {
    const res = await request.post('/admin/auth/login', loginForm)
    const data = res.data
    token.value = data.token
    adminId.value = data.adminId
    username.value = data.username
    realName.value = data.realName
    role.value = data.role
    localStorage.setItem('token', data.token)
    localStorage.setItem('adminId', data.adminId)
    localStorage.setItem('username', data.username)
    localStorage.setItem('realName', data.realName)
    localStorage.setItem('role', data.role)
    return data
  }

  function logout() {
    token.value = ''
    adminId.value = ''
    username.value = ''
    realName.value = ''
    role.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('adminId')
    localStorage.removeItem('username')
    localStorage.removeItem('realName')
    localStorage.removeItem('role')
  }

  return { token, adminId, username, realName, role, login, logout }
})
