import request from './request'

export function login(phone, password) {
  return request.post('/api/user/login', { phone, password })
}

export function register(phone, password) {
  return request.post('/api/user/register', { phone, password })
}

export function getUserInfo() {
  return request.get('/api/user/info')
}

export function updateUser(data) {
  return request.put('/api/user/info', data)
}
