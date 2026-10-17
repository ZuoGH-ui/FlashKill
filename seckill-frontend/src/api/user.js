import request from '../utils/request'

/** 发送验证码 */
export function sendCode(phone) {
  return request.post('/user/sendCode', { phone })
}

/** 登录 */
export function login(phone, code) {
  return request.post('/user/login', { phone, code })
}

/** 获取用户信息 */
export function getUserInfo() {
  return request.get('/user/info')
}