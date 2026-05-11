import request from './request'

export function getStoreList(params) {
  return request.get('/api/store/list', { params })
}

export function getStoreDetail(id) {
  return request.get(`/api/store/${id}`)
}
