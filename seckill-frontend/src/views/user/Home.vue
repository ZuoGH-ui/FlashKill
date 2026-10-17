<template>
  <div class="home">
    <!-- 顶部导航 -->
    <div class="header">
      <h2>商品秒杀系统</h2>
      <div class="user-info">
        <el-avatar :size="32">{{ userInfo.nickname?.charAt(0) }}</el-avatar>
        <span class="nickname">{{ userInfo.nickname }}</span>
        <el-tag size="small" :type="userInfo.role === 'admin' ? 'danger' : 'info'">
          {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
        </el-tag>
        <el-button link @click="goOrders">我的订单</el-button>
        <el-button v-if="userStore.role === 'admin'" link @click="goAdmin">管理后台</el-button>
        <el-button link @click="handleLogout">退出登录</el-button>
      </div>
    </div>
    <div class="search-bar">
      <el-input v-model="keyword" placeholder="搜索商品名称" clearable style="max-width: 400px" @keyup.enter="loadGoods"
        @clear="loadGoods">
        <template #append>
          <el-button @click="loadGoods">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 商品列表 -->
    <div class="goods-list">
      <el-card v-for="item in goodsList" :key="item.activityId" class="goods-card" shadow="hover"
        @click="goDetail(item.activityId)">
        <el-image :src="item.image" class="goods-img" fit="cover">
          <template #error>
            <div class="img-error">暂无图片</div>
          </template>
        </el-image>
        <div class="goods-info">
          <h3>{{ item.name }}</h3>
          <div class="price">
            <span class="seckill-price">¥{{ item.seckillPrice }}</span>
            <span class="original-price">¥{{ item.originalPrice }}</span>
          </div>
          <div class="stock">库存：{{ item.stock }}</div>
          <div class="status">
            <el-tag v-if="item.status === 0" type="warning">未开始</el-tag>
            <el-tag v-else-if="item.status === 1" type="success">进行中</el-tag>
            <el-tag v-else type="info">已结束</el-tag>
          </div>
          <el-button type="danger" :disabled="item.status !== 1" style="width: 100%; margin-top: 10px"
            @click.stop="goDetail(item.activityId)">
            {{ item.status === 1 ? '立即抢购' : '不可抢购' }}
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getSeckillList } from '../../api/goods'
import { getUserInfo } from '../../api/user'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()
const goodsList = ref([])

const userInfo = ref({
  nickname: userStore.nickname || '用户',
  role: userStore.role || 'user'
})

onMounted(async () => {
  await loadUserInfo()
  await loadGoods()
})

async function loadUserInfo() {
  // 管理员不走 /api/user/info
  if (userStore.role === 'admin') {
    userInfo.value = {
      nickname: '管理员',
      role: 'admin'
    }
    return
  }

  try {
    const res = await getUserInfo()
    userInfo.value = res.data
  } catch (e) {}
}

const keyword = ref('')

async function loadGoods() {
  try {
    const res = await getSeckillList(keyword.value)
    goodsList.value = res.data
  } catch (e) {}
}

function goDetail(activityId) {
  router.push(`/detail/${activityId}`)
}

function goOrders() {
  router.push('/orders')
}

function goAdmin() {
  if (userStore.role === 'admin') {
    router.push('/admin/dashboard')
  } else {
    router.push('/admin/login')
  }
}

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出')
  router.replace('/login')
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f7fa;
}

.search-bar {
  padding: 20px 30px 0;
  display: flex;
  justify-content: center;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.nickname {
  font-weight: 500;
  color: #333;
}

.goods-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  padding: 30px;
}

.goods-card {
  cursor: pointer;
}

.goods-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-radius: 4px;
}

.goods-info {
  padding-top: 10px;
}

.goods-info h3 {
  font-size: 16px;
  margin-bottom: 10px;
}

.price {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 10px;
}

.seckill-price {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.original-price {
  color: #999;
  text-decoration: line-through;
  font-size: 14px;
}

.stock {
  color: #666;
  font-size: 14px;
  margin-bottom: 10px;
}

.status {
  margin-bottom: 10px;
}

.img-error {
  width: 100%;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #999;
  border-radius: 4px;
}
</style>