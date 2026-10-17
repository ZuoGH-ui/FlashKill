import request from '../utils/request'

/** 管理员登录 */
export function adminLogin(username, password) {
  return request.post('/admin/login', { username, password })
}

/** 数据看板 */
export function getDashboard() {
  return request.get('/admin/dashboard')
}

/** 商品列表 */
export function getGoodsList(page = 1, size = 10, keyword = '') {
  return request.get('/admin/goods/list', { params: { page, size, keyword } })
}

/** 新增商品 */
export function addGoods(data) {
  return request.post('/admin/goods/add', data)
}

/** 编辑商品 */
export function updateGoods(data) {
  return request.put('/admin/goods/update', data)
}

/** 删除商品 */
export function deleteGoods(id) {
  return request.delete(`/admin/goods/delete/${id}`)
}

/** 活动列表 */
export function getActivityList(page = 1, size = 10, status = null) {
  return request.get('/admin/activity/list', { params: { page, size, status } })
}

/** 新增活动 */
export function addActivity(data) {
  return request.post('/admin/activity/add', data)
}

/** 编辑活动 */
export function updateActivity(data) {
  return request.put('/admin/activity/update', data)
}

/** 结束活动 */
export function endActivity(id) {
  return request.post(`/admin/activity/end/${id}`)
}

/** 订单列表 */
export function getAdminOrderList(page = 1, size = 10, status = null) {
  return request.get('/admin/order/list', { params: { page, size, status } })
}