import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 用户端
  { path: '/login', component: () => import('../views/user/Login.vue') },
  { path: '/', component: () => import('../views/user/Home.vue') },
  { path: '/detail/:activityId', component: () => import('../views/user/Detail.vue') },
  { path: '/orders', component: () => import('../views/user/Orders.vue') },
  { path: '/order/:orderNo', component: () => import('../views/user/OrderDetail.vue') },

  // 管理端
  { path: '/admin/login', component: () => import('../views/admin/AdminLogin.vue') },
  {
    path: '/admin',
    component: () => import('../views/admin/Layout.vue'),
    meta: { requiresAdmin: true },
    children: [
      { path: 'dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'goods', component: () => import('../views/admin/Goods.vue') },
      { path: 'activity', component: () => import('../views/admin/Activity.vue') },
      { path: 'order', component: () => import('../views/admin/Order.vue') }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  // 登录页直接放行
  if (to.path === '/login' || to.path === '/admin/login') {
    return true
  }

  // 需要登录
  if (!token) {
    return '/login'
  }

  // 需要管理员
  if (to.meta.requiresAdmin && role !== 'admin') {
    return '/admin/login'
  }

  return true
})

router.onError((error) => {
  console.error('路由错误：', error)
  // 不阻止跳转
})

export default router