import request from '../utils/request'

/** 我的订单列表 */
export function getOrderList() {
  return request.get('/order/list')
}

/** 订单详情 */
export function getOrderDetail(orderNo) {
  return request.get(`/order/detail/${orderNo}`)
}

/** 取消订单 */
export function cancelOrder(orderNo) {
  return request.post(`/order/cancel/${orderNo}`)
}

/** 支付订单 */
export function payOrder(orderNo) {
  return request.post(`/order/pay/${orderNo}`)
}