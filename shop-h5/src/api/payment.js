import request from './request'

export function pay(data) {
  return request.post('/api/payment/pay', data)
}
