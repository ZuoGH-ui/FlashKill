import request from '../utils/request'

/** 执行秒杀 */
export function executeSeckill(activityId) {
  return request.post(`/seckill/execute/${activityId}`)
}

/** 查询秒杀结果 */
export function getSeckillResult(activityId) {
  return request.get(`/seckill/result/${activityId}`)
}