import request from './request'

export function createOrder(data) {
  return request.post('/api/order/create', data)
}

export function getOrderList(params) {
  return request.get('/api/order/list', { params })
}

export function getOrderDetail(id) {
  return request.get(`/api/order/${id}`)
}

export function cancelOrder(id) {
  return request.put(`/api/order/${id}/cancel`)
}
