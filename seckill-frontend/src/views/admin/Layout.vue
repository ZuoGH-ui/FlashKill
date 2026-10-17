<template>
  <el-container class="admin-layout">
    <el-aside width="200px" class="aside">
      <div class="logo">秒杀后台</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409eff"
      >
        <el-menu-item index="/admin/dashboard">数据看板</el-menu-item>
        <el-menu-item index="/admin/goods">商品管理</el-menu-item>
        <el-menu-item index="/admin/activity">活动管理</el-menu-item>
        <el-menu-item index="/admin/order">订单管理</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span>管理员</span>
        <el-button link @click="goUserHome">进入用户端</el-button>
        <el-button link @click="handleLogout">退出</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 返回用户端（不需要管理员权限）
function goUserHome() {
  router.push('/')
}

// 退出登录
function handleLogout() {
  localStorage.clear()
  ElMessage.success('已退出')
  router.replace('/admin/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}
.aside {
  background: #001529;
}
.logo {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid #1f2d3d;
}
.header {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
</style>