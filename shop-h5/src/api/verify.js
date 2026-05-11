import request from './request'

export function getVerifyCode(orderId) {
  return request.get(`/api/verify/code/${orderId}`)
}

export function getVerifyQrcode(orderId) {
  return request.get(`/api/verify/qrcode/${orderId}`)
}
