import request from './request'

export function getProductList(params) {
  return request.get('/api/product/list', { params })
}

export function getProductDetail(id) {
  return request.get(`/api/product/${id}`)
}
