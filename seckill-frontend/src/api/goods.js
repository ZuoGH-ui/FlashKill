import request from '../utils/request'

/** 秒杀商品列表 */
export function getSeckillList(keyword = '') {
  return request.get('/goods/seckillList', { params: { keyword } })
}

/** 商品详情 */
export function getGoodsDetail(activityId) {
  return request.get(`/goods/detail/${activityId}`)
}

